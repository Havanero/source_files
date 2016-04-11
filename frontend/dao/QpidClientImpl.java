package com.eurexchange.clear.frontend.dao;

import com.eurexchange.clear.frontend.ClearingRequest;
import com.eurexchange.clear.frontend.MainGui;
import com.eurexchange.clear.frontend.TradeRequest;
import com.eurexchange.clear.frontend.XmlFormatter;
import com.eurexchange.clear.frontend.tradematch.obj.InstrmtType;
import com.eurexchange.clear.frontend.tradematch.obj.PtyType;
import com.eurexchange.clear.frontend.tradematch.obj.TrdMtchRptType;
import com.eurexchange.clear.frontend.tradematch.obj.TrdMtchSideType;
import com.eurexchange.clear.interfaces.cil.generated.ObjectList;
import com.eurexchange.clear.interfaces.cil.generated.c7.v001.GO60006ProductPhase;
import com.eurexchange.clear.interfaces.cil.generated.c7.v001.GO60008MemberStatus;
import com.eurexchange.clear.interfaces.cil.generated.c7.v001.HeaderClasses;
import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.InvalidProtocolBufferException;
import org.apache.qpid.AMQException;
import org.apache.qpid.client.AMQConnection;
import org.apache.qpid.client.AMQSession;
import org.apache.qpid.url.URLSyntaxException;

import javax.jms.*;
import javax.jms.Queue;
import javax.swing.table.DefaultTableModel;
import javax.xml.bind.JAXBException;
import java.awt.*;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;
import java.util.List;

public class QpidClientImpl implements QpidClient, Serializable {

    private MainGui mainGui;
    private String connectionUrl;
    private Session session;
    private ExtensionRegistry registry;
    private boolean disconnectConsumer = true;
    private LinkedHashMap<String, AMQConnection> brokerConnection;
    private ClearingRequest clearingRequest;
    private TradeRequest tradeRequest;
    private DefaultTableModel dataModel;

    static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(QpidClientImpl.class);

    public QpidClientImpl(MainGui mainGui) {
        brokerConnection = new LinkedHashMap<>();
        TrdMtchRptType trdMtchRptType = new TrdMtchRptType();
        tradeRequest = new TradeRequest(trdMtchRptType);
        this.mainGui = mainGui;

    }

    @Override public void setConnectionURL(String connectionURL) {
        this.connectionUrl = connectionURL;

    }

    @Override public synchronized void SendMessage(String requestQueue, String message, String msg_count,
            String broker) {
        try {

            session = getAmqConnection(broker).createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination destination;
            destination = session.createQueue(requestQueue);
            MessageProducer messageProducer = session.createProducer(destination);
            TextMessage textMessage;
            textMessage = session.createTextMessage();

            textMessage.setText(message);
            processClearingMessage(broker, textMessage, session);

            populateSendTrade(requestQueue, msg_count, broker, textMessage);

            messageProducer.send(textMessage);
            mainGui.getJTextArea2().setCaretPosition(mainGui.getJTextArea2().getDocument().getLength());
            messageProducer.close();
            session.close();

        } catch (JMSException e) {
            e.printStackTrace();
        }

    }

    private void populateSendTrade(String requestQueue, String msg_count, String broker,
            TextMessage textMessage) throws JMSException {
        tradeRequest.setFixmlMessage(textMessage.getText());
        if (tradeRequest.getFixmlType() != null) {
            TrdMtchRptType trdMtchSideType = tradeRequest.getFixmlType();
            InstrmtType instrmt = trdMtchSideType.getInstrmtMtchSide().getInstrmt();
            List<TrdMtchSideType> listSides = trdMtchSideType.getInstrmtMtchSide().getTrdMtchSide();

            int newSize = dataModel.getRowCount() - listSides.size();

            if (listSides.size() < dataModel.getRowCount()) {

                for (int size = 0; size < newSize; size++) {
                    TrdMtchSideType addTradeSide = new TrdMtchSideType();
                    addTradeSide.setSide("1");
                    addTradeSide.setSideQty("3");
                    addTradeSide.setTrdID("433");
                    String[] Ids = new String[] { "CBKFR", "TRD002", "CBKFR", "A1", "TRD001", "EUREX" };
                    String[] Rs = new String[] { "1", "12", "4", "38", "36", "7" };
                    int i = 0;
                    for (String id : Ids) {
                        PtyType member = new PtyType();
                        member.setID(id);
                        member.setSrc("D");
                        member.setR(Rs[i]);
                        addTradeSide.getPty().add(member);
                        i++;

                    }
                    trdMtchSideType.getInstrmtMtchSide().getTrdMtchSide().add(addTradeSide);
                    listSides = trdMtchSideType.getInstrmtMtchSide().getTrdMtchSide();
                }
            }

            Vector vector = dataModel.getDataVector();
            Enumeration enumeration = vector.elements();
            int cols, rows;
            rows = 0;
            while (enumeration.hasMoreElements()) {
                enumeration.nextElement();
                Vector row = (Vector) vector.elementAt(rows);
                HashMap<String, String> sides = new LinkedHashMap<>();
                for (cols = 0; cols < dataModel.getColumnCount(); cols++) {
                    sides.put(dataModel.getColumnName(cols), String.valueOf(row.get(cols)));
                }

                if (sides.get("Side").equals("Seller")) {
                    instrmt.setMMY(sides.get("MMY"));
                    instrmt.setSym(sides.get("Sym"));
                    instrmt.setStrkPx(sides.get("StrkPx"));
                    instrmt.setPutCall(sides.get("PutCall"));
                    instrmt.setOptAt(sides.get("OptAt"));
                }
                listSides.get(rows).setSide(sellerBuyer(sides.get("Side")));
                listSides.get(rows).getPty().get(0).setID(sides.get("Member"));
                listSides.get(rows).getPty().get(2).setID(sides.get("Clearer"));
                listSides.get(rows).getPty().get(5).setID(sides.get("Account"));
                listSides.get(rows).setSideQty(sides.get("Qty"));
                rows++;
            }
            textMessage.setText(tradeRequest.getFixmlMessage());
            processTrade(requestQueue, msg_count, broker, textMessage);
        }
    }

    private void processClearingMessage(String broker, TextMessage textMessage, Session sess)
            throws JMSException {
        if (broker.equals("REQUEST")) {
            Destination tempResponseQueue = sess.createQueue(getClearingRequestMessage().getReplyToQueue());
            textMessage.setStringProperty("qpid.subject", getClearingRequestMessage().getKey());
            textMessage.setStringProperty("qpid.reply_to", getClearingRequestMessage().getReplyToQueue());
            textMessage.setJMSReplyTo(tempResponseQueue);

        }
    }

    private void processTrade(String requestQueue, String msg_count, String broker, TextMessage textMessage)
            throws JMSException {
        if (broker.equals("TRADE")) {

            tradeRequest.setFixmlMessage(textMessage.getText());
            System.out.println(tradeRequest.getRoutingKey());
            tradeRequest.setUpApplseqCtrl();
            System.out.println("Print " + tradeRequest.getFixmlMessage());

            //            TrdMtchRptType trdMtchRptType;
            //            trdMtchRptType = MarshallerUnmarshaller.unmrshallTradeMatchReport(textMessage.getText());

            //            String queue_name = "broadcast.RAPPIDD_Trade";
            //
            //            InstrmtMtchSideType instrmtMtchSideType;
            //            instrmtMtchSideType = trdMtchRptType.getInstrmtMtchSide();
            //            System.out.println("LastPx: " + instrmtMtchSideType.getLastPx());
            //            InstrmtType instr;
            //            instr = instrmtMtchSideType.getInstrmt();
            //            System.out.println("MMY: " +instr.getMMY() + " SYM: " +instr.getSym() + " OptAt: " +instr.getOptAt() +
            //                    " PutCall: " + instr.getPutCall() + " StrkPx " + instr.getStrkPx());
            //
            ////            broadcast.RAPPIDD_Trade.OGBL.GTS .001 .0000281762 .1 .20131218 .281762
            //
            //            String routingKey = queue_name+"."+instr.getSym()+"."+trdMtchRptType.getApplSeqCtrl().getApplID()
            //                    +"."+trdMtchRptType.getApplSeqCtrl().getApplSeqNum()+"."+trdMtchRptType.getTrdDt();
            //
            //                    //ALV.GTS.002.0000282183.1.20131218.282184";
            //            LOGGER.info("RoutingKey" + routingKey);
            //

            textMessage.setStringProperty("qpid.subject", tradeRequest.getRoutingKey());
            textMessage.setStringProperty("qpid.id", tradeRequest.getRoutingKey());
            textMessage.setText(tradeRequest.getFixmlMessage());

        }
    }

    @Override public List<String> getTradeConfirmation(String queueName) {
        List<String> names = new ArrayList<>();
        try {
            CreateSession("REQUEST");

            Queue queue = session.createQueue(queueName);
            MessageConsumer messageConsumer = session.createConsumer(queue);
            Message message;
            boolean end = false;
            while (!end) {
                message = messageConsumer.receive(5);
                if (message != null) {
                    String stringMessage = getMessage(message);
                    mainGui.getJTextArea2().append("\n" + new XmlFormatter().format(stringMessage));
                    mainGui.getJTextArea2()
                            .setCaretPosition(mainGui.getJTextArea2().getDocument().getLength());
                    names.add(stringMessage);
                } else {
                    end = true;
                }
            }
        } catch (Exception s) {
            s.printStackTrace();
        }
        return names;
    }

    private void CreateSession(String brokerType) throws JMSException {
        if (!getAmqConnection(brokerType).isConnected()) {
            LOGGER.info(brokerType + " need to set connection otherwise it wont work - We wont reach here");
            getAmqConnection(brokerType).start();
        } else {

            LOGGER.info(brokerType + " Connection already set" + getAmqConnection(brokerType).toURL());
            getAmqConnection(brokerType).start();
        }

        //        ChannelToSessionMap sess = getAmqConnection(brokerType).getSessions();
        //
        //        for(AMQSession s:sess.values()){
        //            s.getAMQConnection().getSession(s.getChannelId());
        //        }

        session = getAmqConnection(brokerType).createSession(false, Session.AUTO_ACKNOWLEDGE);

    }

    @Override public void ReadCILMessages(String queueName) {
        Queue consumerQueue = null;
        List<String> messages = new ArrayList<>();
        try {

            registry = ExtensionRegistry.newInstance();
            setDataType(queueName);
            HeaderClasses.registerAllExtensions(registry);
            CreateSession("CIL");

            consumerQueue = session.createQueue(queueName);
        } catch (JMSException e) {
            e.printStackTrace();
        }
        try {
            MessageConsumer messageConsumer = session.createConsumer(consumerQueue);
            Message message;
            ObjectList.GPBObjectList objectList = null;
            boolean end = false;
            while (!end) {
                message = messageConsumer.receive(5);
                if (message != null) {
                    BytesMessage bm = (BytesMessage) message;
                    byte data[] = new byte[(int) bm.getBodyLength()];
                    bm.readBytes(data);
                    try {
                        objectList = ObjectList.GPBObjectList.parseFrom(data, registry);
                    } catch (InvalidProtocolBufferException e) {
                        e.printStackTrace();
                    }

                    assert objectList != null;
                    String stringMessage = objectList.toString();

                    mainGui.getJTextArea2().append("\n" + stringMessage);
                    mainGui.getJTextArea2()
                            .setCaretPosition(mainGui.getJTextArea2().getDocument().getLength());
                    messages.add(stringMessage);
                    mainGui.getJTextArea1().setText("[ " + messages.size() + " ] MESSAGE(S) FOUND");
                    mainGui.getJTextArea2().setBackground(Color.gray);
                    mainGui.getJTextArea2().setForeground(Color.BLUE);
                } else {
                    end = true;
                    mainGui.getJTextArea2().append("NO MORE MESSAGES FOUND");
                    mainGui.getJTextArea2().append("\n");
                    mainGui.getJTextArea2().setBackground(Color.gray);
                    mainGui.getJTextArea2().setForeground(Color.BLACK);
                }
            }

        } catch (JMSException e) {
            e.printStackTrace();
        }
        try {
            session.close();
        } catch (JMSException e) {
            e.printStackTrace();
        }

    }

    @Override public synchronized void startConsumer(final String responseAddress) {
        Thread t = new Thread(() -> {
            System.out.println("Checking connection");
            if (getAmqConnection("REQUEST").isConnected()) {
                LOGGER.info("We Still have live connection just need to start it..." + getAmqConnection(
                        "REQUEST").toURL());
                try {

                    getAmqConnection("REQUEST").start();
                    LOGGER.info("Broker Started");
                } catch (JMSException e) {
                    e.printStackTrace();
                    if (e.getMessage().contains("closed")) {
                        System.out.println("lost connection");
                        try {
                            getAmqConnection("REQUEST").makeBrokerConnection(
                                    getAmqConnection("REQUEST").getActiveBrokerDetails());
                        } catch (IOException | AMQException e1) {
                            e1.printStackTrace();
                        }
                    }

                }

            } else {
                LOGGER.warn("We don't have live connection handler to REQUEST BROKER");
                try {
                    setAmqpConnection(connectionUrl, "REQUEST");
                    getAmqConnection("REQUEST").start();
                } catch (JMSException | AMQException | URLSyntaxException e) {
                    e.printStackTrace();
                }
            }
            try {
                LOGGER.info("Assigning Session for consumer");

                session = getAmqConnection("REQUEST").createSession(false, Session.AUTO_ACKNOWLEDGE);
            } catch (JMSException e) {
                e.printStackTrace();
            }

            try {
                LOGGER.info("Starting Consumer " + Thread.currentThread().getName());
                createConsumer(session, responseAddress);

            } catch (JMSException | URLSyntaxException | AMQException e) {
                LOGGER.error("------------------" + e.getMessage());
            }
        });

        t.start();

    }

    @Override public void setDisconnectConsumer(boolean status) {

        this.disconnectConsumer = status;

    }

    @Override
    public void setAmqpConnection(String connectionUrl, String brokerType) throws URLSyntaxException,
            AMQException {
        this.connectionUrl = connectionUrl;
        AMQConnection amqConnection = new AMQConnection(this.connectionUrl);
        LOGGER.info(amqConnection.getBrokerUUID() + " Successfully connected ");
        this.brokerConnection.put(brokerType, amqConnection);
        LOGGER.info("Setting up connection for " + brokerType + " " + amqConnection.toURL());
    }

    @Override public void setClearingMessagesParameters(ClearingRequest clearingRequest) {
        this.clearingRequest = clearingRequest;
    }

    @Override public void setLatestAppSecNum(String secNum) {
        tradeRequest.setAppSecNum(secNum);
    }

    @Override public void setIncomingData(DefaultTableModel model) {
        this.dataModel = model;

    }

    private ClearingRequest getClearingRequestMessage() {
        return clearingRequest;

    }

    private AMQConnection getAmqConnection(String brokerType) {
        return brokerConnection.get(brokerType);
    }

    public void createConsumer(Session session, String queue)
            throws JMSException, AMQException, URLSyntaxException {
        Queue consumerQueue = session.createQueue(queue);
        MessageConsumer messageConsumer = session.createConsumer(consumerQueue);
        LOGGER.info("incoming consumer starting......");
        while (disconnectConsumer) {
            Message incomingMsg = messageConsumer.receive(4);
            try {
                if (incomingMsg != null) {
                    mainGui.getJTextArea2().setForeground(Color.BLUE);
                    mainGui.getJTextArea2().append(new XmlFormatter().format(getMessage(incomingMsg)) + "\n");
                    mainGui.getJTextArea2()
                            .setCaretPosition(mainGui.getJTextArea2().getDocument().getLength());
                }
                getDisconnectConsumerStatus();
            } catch (JAXBException e) {
                e.printStackTrace();
            }
        }
        getAmqConnection("REQUEST").stop();
        for (AMQSession<?, ?> s : getAmqConnection("REQUEST").getSessions().values()) {
            s.getAMQConnection().deregisterSession(s.getChannelId());

        }
    }

    private String getMessage(Message message)
            throws JMSException {
        String textMessage = null;
        if (message != null) {
            if (message instanceof TextMessage) {
                String text = ((TextMessage) message).getText();
                LOGGER.info(text);
                textMessage = text;
            } else if (message instanceof BytesMessage) {
                BytesMessage bm = (BytesMessage) message;
                byte data[] = new byte[(int) bm.getBodyLength()];
                bm.readBytes(data);
                textMessage = new String(data);
                LOGGER.info(textMessage);
            }

            message.acknowledge();
            LOGGER.info("Acknowledgement Sent");
            return textMessage;
        }
        return null;

    }

    public String sellerBuyer(String value) {
        switch (value) {
            case "Seller":
                return "1";
            case "Buyer":
                return "2";
        }

        return null;
    }

    boolean getDisconnectConsumerStatus() {
        return disconnectConsumer;
    }

    private void setDataType(String queue) {

        if (queue.contains("CLEAR_MemberState"))
            GO60008MemberStatus.registerAllExtensions(registry);
        if (queue.contains("CLEAR_ProductPhase"))
            GO60006ProductPhase.registerAllExtensions(registry);

    }

}

