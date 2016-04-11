package com.eurexchange.clear.tradegui;

import com.eurexchange.clear.frontend.TradeRequest;
import org.apache.qpid.AMQException;
import org.apache.qpid.client.AMQConnection;
import org.apache.qpid.client.AMQSession;
import org.apache.qpid.url.URLSyntaxException;

import javax.jms.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class QpidMsgServiceImpl implements QpidMsgService {

    String url;
    AMQConnection connection;
    private ExecutorService executor;
    private ArrayList<AMQSession<?, ?>> sessionList;
    Boolean trackErrors = false;
    protected ArrayList<AMQConnection> live_connection;
    protected ArrayList<MessageProducer> producerList = new ArrayList<>();
    private TradingGuiTab tradingGuiTab;

    public QpidMsgServiceImpl(TradingGuiTab jComponent) {
        live_connection = new ArrayList<>();
        sessionList = new ArrayList<>();
        this.tradingGuiTab = jComponent;
    }

    static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(QpidMsgServiceImpl.class);

    @Override public void setBrokerUrl(String url) {
        LOGGER.info("Setting Broker Address {}", url);
        this.url = url;

    }

    @Override public String getBrokerUrl() {
        return url;
    }

    @Override public void setBrokerConnection(AMQConnection connection) {
        this.connection = connection;

    }

    @Override public AMQConnection getBrokerConnection() {
        return connection;
    }


    @Override public void sendOut(TradeRequest tradeRequest) {
        int i = 0;
        for (Session session : sessionList) {
            LOGGER.info("Session #" + sessionList.get(i).getAMQConnection().getConnectionNumber().toString());
            LOGGER.info("Sending out " + tradeRequest.getFixmlMessage());
            tradingGuiTab.getTextPane1().setText(tradeRequest.getFixmlMessage() + "\n");
            tradingGuiTab.getTextPane1().setBackground(Color.BLUE);
            tradeRequest.upDateApplseqCtrl();
            TextMessage textMessage = null;
            try {
                textMessage = session.createTextMessage();
            } catch (JMSException e) {
                e.printStackTrace();
            }
            try {
                assert textMessage != null;
                textMessage.setStringProperty("qpid.subject", tradeRequest.getRoutingKey());
                textMessage.setStringProperty("qpid.id", tradeRequest.getRoutingKey());
                textMessage.setText(tradeRequest.getFixmlMessage());
            } catch (JMSException e) {
                e.printStackTrace();
            }

            try {
                producerList.get(i).send(textMessage);
            } catch (JMSException e) {
                e.printStackTrace();
            }
            i++;
        }

    }

    @Override
    public void startConnection(final int threadNo, final ArrayList<String> list_of_queues) {
        boolean wait = false;
        executor = Executors.newFixedThreadPool(threadNo);
        final CountDownLatch latch = new CountDownLatch(threadNo);
        for (int t = 0; t < threadNo; t++) {

            Runnable worker = () -> {
                for (String list_of_queue : list_of_queues) {
                    try {
                        connection = new AMQConnection(getBrokerUrl());
                        connection.start();
                        Session session =
                                connection
                                        .createSession(false, Session.CLIENT_ACKNOWLEDGE);
                        sessionList.add((AMQSession<?, ?>) session);
                        createProducer(session, list_of_queue);
                        live_connection.add(connection);
                    } catch (JMSException | URLSyntaxException | AMQException e) {
                        e.printStackTrace();
                        trackErrors = true;
                        try {
                            connection.close();
                        } catch (JMSException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
                latch.countDown();
            };
            executor.execute(worker);
        }
        try {
            wait = latch.await(120, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (wait) {
            String status = "Complete";
            if (trackErrors) {
                status = "Complete with Error(s)";
                tradingGuiTab.getTextPane1().setForeground(Color.RED);
                tradingGuiTab.getTextPane1().setBackground(Color.YELLOW);
                trackErrors = false;
            }
            tradingGuiTab.getTextPane1().setText("----------------------------------------\n");
            LOGGER.info("All Connection to Queue(s)  " + status + "\n");
            tradingGuiTab.getTextPane1().setText("All Connection to Queue(s)  " + status + "\n");
            trackErrors = false;
        }
        executor.shutdown();
        if (!executor.isTerminated()) {
            executor.shutdownNow();
        }
    }

    private synchronized void createProducer(Session session, String list_of_queue) {
        try {
            Destination destination = session.createQueue(list_of_queue);
            producerList.add(session.createProducer(destination));
            LOGGER.info("Creating Producer on " + list_of_queue);
            tradingGuiTab.getTextPane1().setText("Creating Producer on " + list_of_queue);
            tradingGuiTab.getTextPane1().setForeground(Color.BLUE);
        } catch (JMSException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void stopConnection() {
        boolean wait = false;
        executor = Executors.newFixedThreadPool(sessionList.size());
        final CountDownLatch latch = new CountDownLatch(sessionList.size());
        for (int i = 0; i < sessionList.size(); i++) {
            final int finalI = i;
            Runnable worker = () -> {
                try {

                    sessionList.get(finalI).getAMQConnection().close();
                    if (sessionList.get(finalI).getAMQConnection().isClosed()) {
                        LOGGER.info(
                                "Successfully Stopped & Closed Connection#" + sessionList.get(finalI)
                                        .getAMQConnection().getConnectionNumber());
                        LOGGER.info(
                                "Successfully Stopped & Closed Session#" +
                                        sessionList.get(finalI).getAMQConnection()
                                                .getConnectionNumber()
                                        + "\n");

                    }
                    LOGGER.info(Thread.currentThread().getName() + " is "
                            + "Waiting to close - Please wait \n");
                    latch.countDown();

                } catch (JMSException e) {
                    LOGGER.warn("Caught an exception: {}", e);
                }

            };
            executor.execute(worker);
        }
        try {
            wait = latch.await(120, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (wait) {
            LOGGER.info("----------------------------------\n");
            LOGGER.info("All Connections Closed \n");
            LOGGER.info("----------------------------------\n");
            tradingGuiTab.getTextPane1().setText("All Connections Closed \n");
            sessionList.clear();
            producerList.clear();
        }
        executor.shutdown();
        if (!executor.isTerminated()) {
            executor.shutdownNow();
        }
    }

}
