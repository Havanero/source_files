package com.eurexchange.clear.frontend.manual.connection;

import org.apache.qpid.AMQException;
import org.apache.qpid.client.AMQConnection;
import org.apache.qpid.client.AMQSession;
import org.apache.qpid.qmf2.common.QmfException;
import org.apache.qpid.qmf2.console.Console;
import org.apache.qpid.qmf2.console.QmfConsoleData;
import org.apache.qpid.qmf2.util.ConnectionHelper;
import org.apache.qpid.url.URLSyntaxException;

import javax.jms.*;
import javax.jms.Queue;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SimpleJmsConnection implements SimpleJmsConnectionInterface {

    private ExecutorService executor;
    String dev_host = "cbgd01.xeop.de:20282";
    //    String acceptance_host = "localhost:9000";

    static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(SimpleJmsConnection.class);
    private String connection = "amqp://amqpsrv_C7:amqpsrv_C7@JavaTool/?brokerlist='tcp://" + dev_host +
            "?heartbeat='5''&sasl_mechs='PLAIN''&sync_publish='all'&sync_ack='true'";

    private String destinationQueue;
    private AmqpGUI gui;
    private MessageConsumer consume;
    private AMQConnection amqConnection;
    private Session session;
    private ArrayList<AMQSession<?,?>> sessionList;
    protected ArrayList<AMQConnection> live_connection;
    private boolean trackErrors = false;

    public SimpleJmsConnection(AmqpGUI gui) {
        this.gui = gui;
        live_connection = new ArrayList<>();
        sessionList = new ArrayList<>();
    }

    public SimpleJmsConnection() {
        live_connection = new ArrayList<>();
        sessionList = new ArrayList<>();
    }

    public void setConnection(String url) {
        this.connection = url;
    }

    public String getConnection() {
        return connection;
    }

    public void setDestinationQueue(String destinationQueue) {
        this.destinationQueue = destinationQueue;
    }

    public String getDestinationQueue() {
        return destinationQueue;
    }

    public void startConnection(final int threadNo, final ArrayList<String> list_of_queues) {
        boolean wait = false;
        executor = Executors.newFixedThreadPool(threadNo);
        final CountDownLatch latch = new CountDownLatch(threadNo);
        for (int t = 0; t < threadNo; t++) {

            Runnable worker = new Runnable() {

                @Override public void run() {
                    for (String list_of_queue : list_of_queues) {
                        try {
                            amqConnection = new AMQConnection(getConnection());
                            amqConnection.start();
                            session =
                                    amqConnection
                                            .createSession(false, Session.CLIENT_ACKNOWLEDGE);
                            sessionList.add((AMQSession<?,?>) session);
                            gui.setTextArea1("waiting to connect....\n");
                            Consumer(session, list_of_queue);
                            live_connection.add(amqConnection);
                        } catch (JMSException | URLSyntaxException | AMQException e) {
                            trackErrors = true;
                            e.printStackTrace();
                            try {
                                amqConnection.close();
                            } catch (JMSException e1) {
                                e1.printStackTrace();
                            }
                            gui.setTextArea1("\n" + list_of_queue + e.getMessage() + "\n");
                        }
                    }
                    LOGGER.info("Waiting for connection....");
                    gui.setTextArea1(Thread.currentThread().getName() + " is "
                            + "Waiting to Connect - Please wait \n");
                    gui.getTextArea2().setBackground(Color.BLUE);
                    latch.countDown();
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
            String status = "Complete";
            if (trackErrors) {
                status = "Complete with Error(s)";
                gui.getTextArea1().setForeground(Color.RED);
                gui.getTextArea2().setBackground(Color.YELLOW);
                trackErrors = false;
            }
            gui.setTextArea1("----------------------------------------\n");
            gui.setTextArea1("All Connection to Queue(s)  " + status + "\n");
            gui.setTextArea1("-----------------------------------------\n");
            gui.getTextArea2().setBackground(Color.GREEN);
            LOGGER.info("All Connection to Queue(s)  " + status + "\n");
            trackErrors = false;
        }
        executor.shutdown();
        if (!executor.isTerminated()) {
            executor.shutdownNow();
        }
    }

    public void stopConnection() {
        boolean wait = false;
        executor = Executors.newFixedThreadPool(sessionList.size());
        final CountDownLatch latch = new CountDownLatch(sessionList.size());
        for (int i = 0; i < sessionList.size(); i++) {
            final int finalI = i;
            Runnable worker = new Runnable() {

                @Override public void run() {
                    try {

                        sessionList.get(finalI).getAMQConnection().close();
                        if (sessionList.get(finalI).getAMQConnection().isClosed()) {
                            LOGGER.info(
                                    "Successfully Stopped & Closed #" + sessionList.get(finalI)
                                            .getAMQConnection().getConnectionNumber());
                            gui.setTextArea1(
                                    "Successfully Stopped & Closed #" +
                                            sessionList.get(finalI).getAMQConnection()
                                                    .getConnectionNumber()
                                            + "\n");
                            gui.getTextArea1().setForeground(Color.BLUE);
                        }
                        gui.setTextArea1(Thread.currentThread().getName() + " is "
                                + "Waiting to close - Please wait \n");
                        latch.countDown();
                    } catch (JMSException e) {
                        LOGGER.warn("Caught an exception: {}", e);
                    }

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
            gui.setTextArea1("----------------------------------\n");
            gui.setTextArea1("All Connections Closed \n");
            gui.setTextArea1("----------------------------------\n");
            gui.getTextArea2().setBackground(Color.BLACK);
            sessionList.clear();
        }
        executor.shutdown();
        if (!executor.isTerminated()) {
            executor.shutdownNow();
        }
    }

    public synchronized void Consumer(javax.jms.Session session, String destination_queue) {
        Queue queue = null;
        try {
            gui.setTextArea1("waiting on connection to :<<<>>" + destination_queue + "\n");
            queue = session.createQueue(destination_queue);

        } catch (JMSException e) {
            gui.setTextArea1("Error" + e.getMessage());
        }
        try {
            consume = session.createConsumer(queue);
            LOGGER.info("Connection---> "
                    + Thread.currentThread().getName());
            gui.getTextArea1().setForeground(Color.BLUE);
            gui.getTextArea2().setVisible(true);
            System.out.println("Con # " + amqConnection.getConnectionNumber());
            gui.setTextArea1("Connected to Queue " + destination_queue + "\n");
        } catch (JMSException e) {
            e.printStackTrace();
            gui.setTextArea1("Create Consumer Error for Address " + destination_queue + ":" +
                    e.getMessage() + "\n");
            gui.getTextArea1().setForeground(Color.RED);
            Thread.currentThread().interrupt();
            gui.run_mode = false;
            gui.getTextArea2().setBackground(Color.RED);
            try {
                consume.close();
            } catch (JMSException e1) {
                e1.printStackTrace();
            }
        }

    }

    public ArrayList<String> getListOfQueueForBroker(String brokerConnection) {
        Connection connection = ConnectionHelper.createConnection(brokerConnection);
        Console console = null;
        try {
            console = new Console();
        } catch (QmfException e) {
            e.printStackTrace();
        }
        try {
            assert console != null;
            console.addConnection(connection);
        } catch (QmfException e) {
            e.printStackTrace();
            gui.setTextArea1("Q load error " + e.getMessage() + "\n");
            gui.getTextArea1().setForeground(Color.RED);
        }
        ArrayList<String> list_of_queues = new ArrayList<>();
        List<QmfConsoleData> queues = console.getObjects("org.apache.qpid.broker", "queue");

        Pattern pattern = Pattern.compile("request(.*?)C7");
        for (QmfConsoleData q : queues) {
            String name = q.getStringValue("name");
            Matcher extractedMember = pattern.matcher(name);
            if (extractedMember.find()) {
                LOGGER.info(extractedMember.group(0));
                gui.setTextArea1(
                        "Loading Queue Info from the broker " + extractedMember.group(0) + "\n");
                list_of_queues.add(extractedMember.group(0));
                LOGGER.info("Loading Queue Info from the broker " + extractedMember.group(0));

                System.out.println();
            }
        }
        try {
            connection.close();

        } catch (JMSException e) {
            e.printStackTrace();
        }
        return list_of_queues;
    }

}
