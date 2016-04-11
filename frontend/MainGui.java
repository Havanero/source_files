package com.eurexchange.clear.frontend;

import com.eurexchange.clear.common.service.CommonSenderService;
import com.eurexchange.clear.common.value.ClearConstants;
import com.eurexchange.clear.domain.AmqpQueue;
import com.eurexchange.clear.frontend.dao.*;
import com.eurexchange.clear.frontend.trades.TradeDataTable;
import org.apache.qpid.AMQException;
import org.apache.qpid.url.URLSyntaxException;

import javax.jms.JMSException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

public class MainGui extends JFrame {

    private static final long serialVersionUID = 1L;
    private String queue;
    private String reply_address;
    private String response_address;
    private HashMap<String, String> request_broker;
    private HashMap<String, String> trade_import_broker;
    private HashMap<String, String> cil_broadcast_broker;
    private final List<String> members_queue_list = new ArrayList<>();
    private List<String> files_for_combo = new ArrayList<>();
    private String request_queue_in_use = null;
    private final transient BrokerConnection brokerConnection;
    private List<String> list_of_cil_queues = new ArrayList<>();
    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(MainGui.class);
    QpidClient qpidClient;
    private DefaultTableModel model = new DefaultTableModel();
    Properties prop = new Properties();
    InputStream input = null;

    public MainGui() {
        try {
            LOGGER.info("Loading GUI Controls and Connection Details.. Hang on!");
  //          input = new FileInputStream("clear_tools.properties");
            input = new FileInputStream("clear-tools/src/main/resources/clear_tools.properties");
//            InputStream uri = MainGui.class.getResourceAsStream("/clear_tools.properties");
////            System.out.println("Resources = /templates/" + uri);
//            assert uri != null;
//            BufferedInputStream in = new BufferedInputStream(uri);
            prop.load(input);

        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("clearingLivePU", prop);
        EntityManager em = emf.createEntityManager();
        brokerConnection = new BrokerConnectionImpl(em);
        DataAccessImpl dataAccess = new DataAccessImpl(em);
        qpidClient = new QpidClientImpl(this);
        qpidClient.setLatestAppSecNum(String.valueOf(dataAccess.getMaximumCount()));
        LoadContentInformation();
        initComponents();

    }

    private void LoadContentInformation() {

        try {

            setBrokerConnection();
        } catch (AMQException | URLSyntaxException e) {
            e.printStackTrace();
        }

        List<AmqpQueue> queueList = brokerConnection.getMembersQueue();
        members_queue_list
                .addAll(queueList.stream().map(list -> new XmlFormatter().extract_member(list.getQueueName()))
                        .collect(Collectors.toList()));

        request_queue_in_use = members_queue_list.get(0);
        list_of_cil_queues = brokerConnection.getListOfQueueForBroker(
                cil_broadcast_broker.get("url"));
    }

    private void setBrokerConnection() throws AMQException, URLSyntaxException {
        trade_import_broker = brokerConnection.getConnectionAndQueue(
                CommonSenderService.Broker.TRADE_IMPORT_FROM_T7,
                ClearConstants.TRADE_IMPORT_FROM_T7_QUEUE);
        qpidClient.setAmqpConnection(trade_import_broker.get("url"), "TRADE");

        request_broker =
                brokerConnection.getConnectionAndQueue(CommonSenderService.Broker.FIXML_BROADCAST,
                        String.format("%s%s", ClearConstants.TRADE_CONFIRMATION_OWNER_QUEUE_PREFIX, "CBKFR"));
        qpidClient.setAmqpConnection(request_broker.get("url"), "REQUEST");

        cil_broadcast_broker = brokerConnection.getConnectionAndQueue(CommonSenderService.Broker.CIL,
                ClearConstants.CIL_MEMBER_IN_QUEUE);
        qpidClient.setAmqpConnection(cil_broadcast_broker.get("url"), "CIL");
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private void initComponents() {

        JToolBar jToolBar1 = new JToolBar();
        jButton1 = new javax.swing.JButton();
        JScrollPane jScrollPane1 = new JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jProgressBar1 = new javax.swing.JProgressBar();
        jRadioButton1 = new javax.swing.JRadioButton();
        jRadioButton2 = new javax.swing.JRadioButton();
        jRadioTrade = new javax.swing.JRadioButton();
        jRadioButton4 = new javax.swing.JRadioButton();
        jRadioButton5 = new javax.swing.JRadioButton();
        JSeparator jSeparator1 = new JSeparator();
        JScrollPane jScrollPane2 = new JScrollPane();
        jTextArea2 = new javax.swing.JTextArea();
        JLabel jLabel2 = new JLabel();
        jTextField2 = new javax.swing.JTextField();
        JLabel jLabel1 = new JLabel();
        JLabel jLabel3 = new JLabel();
        jTextField3 = new javax.swing.JTextField();
        JComboBox<?> jComboBox1 = new JComboBox<String>();
        lTrade_type = new JLabel();
        JLabel jLabel5 = new JLabel("AMQP:");
        jComboBox2 = new javax.swing.JComboBox();
        jComboBox3 = new javax.swing.JComboBox();
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jToolBar1.setRollover(true);

        jButton1.setText("SEND PAYLOAD");
        model = new DefaultTableModel();
        JTable table = new JTable(model);
        table.getTableHeader().setReorderingAllowed(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        table.addMouseListener(new PopClickListener());

        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(true);

        jScrollPane1.setViewportView(table);

        jButton1.addActionListener(this::processPayloadInput);

        //        jTextArea1.setColumns(20);
        //        jTextArea1.setRows(5);
        //        jScrollPane1.setViewportView(jTextArea1);

        jProgressBar1.addPropertyChangeListener(this::jProgressBar1PropertyChange

        );

        jRadioButton1.setText("Clearing");
        jRadioButton1.addActionListener(evt -> {
                    processClearingRequest(evt);
                    jComboBox3.setEnabled(true);
                    jComboBox3.setModel(new DefaultComboBoxModel(
                            files_for_combo.toArray()));
                    jComboBox2.setModel(new DefaultComboBoxModel(
                            members_queue_list.toArray()));
                    request_queue_in_use = (String) jComboBox2.getSelectedItem();
                }

        );

        jRadioButton2.setText("Trade Confirmation");
        jRadioButton2.addActionListener(evt -> {
                    jComboBox3.setEnabled(false);
                    processTradeConfirmation(evt);
                    jComboBox2.setModel(new DefaultComboBoxModel(
                            members_queue_list.toArray()));
                    request_queue_in_use = (String) jComboBox2.getSelectedItem();
                }

        );

        jRadioButton4.setText("Workflow BroadCast");
        jRadioButton4.addActionListener(evt -> {
                    jComboBox3.setEnabled(false);
                    processTradeConfirmation(evt);
                }

        );

        jRadioTrade.setText("Trade");
        jRadioTrade.addActionListener(evt -> {
                    if (model.getColumnCount() == 0) {
                        new TradeDataTable(model);
                    }
                    selectTradeOption(evt);
                    jComboBox3.setEnabled(true);
                    jComboBox3.setModel(new DefaultComboBoxModel(
                            files_for_combo.toArray()));
                    jComboBox2.setModel(new DefaultComboBoxModel(
                            members_queue_list.toArray()));

                }

        );

        jSeparator1.setBackground(java.awt.SystemColor.info);
        jSeparator1.setForeground(new java.awt.Color(136, 45, 143));

        jTextArea2.setColumns(10);
        jTextArea2.setRows(4);
        jScrollPane2.setViewportView(jTextArea2);

        jTextArea2.addMouseListener(new PopClickListener());
        jTextArea1.addMouseListener(new PopClickListener());

        jLabel2.setIcon(new ImageIcon(String.valueOf(new
                ImageIcon("/clear-tools/eurex_clearing_logo1.png"))));

        jLabel1.setText("Member Account");
        jLabel3.setText("Iteration #");
        jTextField3.setText(trade_import_broker.get("url"));
        jTextField3.setForeground(Color.blue);
        jTextField3.setBackground(Color.lightGray);
        jTextField3.setEditable(false);
        jTextField3.setComponentPopupMenu(new PopupMenu(jTextField3));

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "ACCOUNT TRANSFER",
                "TRADE SPLIT", "AVERAGE PRICE", "DE-MERGE", "OC ADJUSTMENT", "TEXT ADJUSTMENT" }));

        jComboBox1.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                lTrade_type.setText(String.format("Sending Trade Type %s", e.getItem().toString()));
                lTrade_type.setForeground(Color.BLUE);
            }

        });

        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel(members_queue_list.toArray()));
        jComboBox2.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                LOGGER.info(String.format("Sending to Member Queue Type %s", e.getItem().toString()));
                request_queue_in_use = e.getItem().toString();
            }

        });

        jComboBox3.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Select Template" }));
        jComboBox3.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                LOGGER.info(String.format("Selected Template %s", e.getItem().toString()));
                try {
                    if (jRadioButton1.isSelected()) {
                        jTextArea1.setText(getTemplate("clearing/" + e.getItem().toString()));
                    }
                    if (jRadioTrade.isSelected()) {
                        jTextArea1.setText(getTemplate("trades/" + e.getItem().toString()));
                    }
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }

        });
        jRadioButton5.setText("CIL Message");
        jRadioButton5.addActionListener(evt -> {
                    processCILMessages(evt);
                    jComboBox3.setEnabled(true);
                    jComboBox3.setModel(new DefaultComboBoxModel(
                            files_for_combo.toArray()));
                    jComboBox2.setModel(new DefaultComboBoxModel(
                            list_of_cil_queues.toArray()));
                    request_queue_in_use = (String) jComboBox2.getSelectedItem();
                }

        );
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setPreferredSize(new Dimension(1292, 700));

        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                Short.MAX_VALUE)
                        .addComponent(jProgressBar1, javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                Short.MAX_VALUE)
                        .addGroup(
                                layout.createSequentialGroup()
                                        .addContainerGap()
                                        .addGroup(
                                                layout.createParallelGroup(
                                                        javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addGroup(
                                                                layout.createSequentialGroup()
                                                                        .addGroup(
                                                                                layout.createParallelGroup(
                                                                                        javax.swing.GroupLayout.Alignment.LEADING)
                                                                                        .addComponent(
                                                                                                jLabel1,
                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                128,
                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                        .addComponent(
                                                                                                jLabel5,
                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                108,
                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                        .addGap(18, 18, 18)
                                                                        .addGroup(
                                                                                layout.createParallelGroup(
                                                                                        javax.swing.GroupLayout.Alignment.LEADING)
                                                                                        .addGroup(
                                                                                                layout.createSequentialGroup()
                                                                                                        .addComponent(
                                                                                                                jComboBox3,
                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                                355,
                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                                        .addGap(60,
                                                                                                                60,
                                                                                                                60)
                                                                                                        .addComponent(
                                                                                                                jRadioButton5)
                                                                                                        .addGap(18,
                                                                                                                18,
                                                                                                                18)
                                                                                                        .addGroup(
                                                                                                                layout.createParallelGroup(
                                                                                                                        javax.swing.GroupLayout.Alignment.LEADING)
                                                                                                                        .addGroup(
                                                                                                                                javax.swing.GroupLayout.Alignment.TRAILING,
                                                                                                                                layout.createSequentialGroup()
                                                                                                                                        .addComponent(
                                                                                                                                                jComboBox1,
                                                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                                                                161,
                                                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                                                                        .addGap(47,
                                                                                                                                                47,
                                                                                                                                                47))
                                                                                                                        .addGroup(
                                                                                                                                javax.swing.GroupLayout.Alignment.TRAILING,
                                                                                                                                layout.createSequentialGroup()
                                                                                                                                        .addGroup(
                                                                                                                                                layout.createParallelGroup(
                                                                                                                                                        javax.swing.GroupLayout.Alignment.LEADING)
                                                                                                                                                        .addComponent(
                                                                                                                                                                jRadioTrade)
                                                                                                                                                        .addComponent(
                                                                                                                                                                jRadioButton1))
                                                                                                                                        .addGap(29,
                                                                                                                                                29,
                                                                                                                                                29)
                                                                                                                                        .addGroup(
                                                                                                                                                layout.createParallelGroup(
                                                                                                                                                        javax.swing.GroupLayout.Alignment.LEADING)
                                                                                                                                                        .addComponent(
                                                                                                                                                                jRadioButton4)
                                                                                                                                                        .addComponent(
                                                                                                                                                                jRadioButton2))
                                                                                                                                        .addGap(371,
                                                                                                                                                371,
                                                                                                                                                371))))
                                                                                        .addGroup(
                                                                                                layout.createSequentialGroup()
                                                                                                        .addGroup(
                                                                                                                layout.createParallelGroup(
                                                                                                                        javax.swing.GroupLayout.Alignment.LEADING,
                                                                                                                        false)
                                                                                                                        .addGroup(
                                                                                                                                layout.createSequentialGroup()
                                                                                                                                        .addComponent(
                                                                                                                                                jComboBox2,
                                                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                                                                355,
                                                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                                                                        .addGap(119,
                                                                                                                                                119,
                                                                                                                                                119)
                                                                                                                                        .addComponent(
                                                                                                                                                jLabel3,
                                                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                                                                117,
                                                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                                                                        .addGap(4,
                                                                                                                                                4,
                                                                                                                                                4)
                                                                                                                                        .addComponent(
                                                                                                                                                jTextField2,
                                                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                                                                64,
                                                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                                                                        .addComponent(
                                                                                                                                jTextField3))
                                                                                                        .addPreferredGap(
                                                                                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED,
                                                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                                                Short.MAX_VALUE)
                                                                                                        .addComponent(
                                                                                                                jButton1)
                                                                                                        .addGap(65,
                                                                                                                65,
                                                                                                                65))))
                                                        .addGroup(
                                                                layout.createSequentialGroup()
                                                                        .addGroup(
                                                                                layout.createParallelGroup(
                                                                                        javax.swing.GroupLayout.Alignment.LEADING)
                                                                                        .addGroup(
                                                                                                layout.createSequentialGroup()
                                                                                                        .addComponent(
                                                                                                                jScrollPane1,
                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                                666,
                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                                        .addGap(18,
                                                                                                                18,
                                                                                                                18))
                                                                                        .addGroup(
                                                                                                javax.swing.GroupLayout.Alignment.TRAILING,
                                                                                                layout.createSequentialGroup()
                                                                                                        .addComponent(
                                                                                                                lTrade_type)
                                                                                                        .addGap(62,
                                                                                                                62,
                                                                                                                62)))
                                                                        .addComponent(jSeparator1,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                        .addPreferredGap(
                                                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                        .addComponent(jScrollPane2,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                561,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                        .addContainerGap(
                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                Short.MAX_VALUE))))
                        .addGroup(
                                layout.createSequentialGroup()
                                        .addGap(491, 491, 491)
                                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 212,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(
                                layout.createSequentialGroup()
                                        .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 37,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 48,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(63, 63, 63)
                                        .addGroup(
                                                layout.createParallelGroup(
                                                        javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(jTextField3,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(jLabel5))
                                        .addGap(18, 18, 18)
                                        .addGroup(
                                                layout.createParallelGroup(
                                                        javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(jTextField2,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(jLabel1)
                                                        .addComponent(jLabel3)
                                                        .addComponent(jComboBox2,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE, 31,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(jButton1))
                                        .addGroup(
                                                layout.createParallelGroup(
                                                        javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addGroup(
                                                                layout.createSequentialGroup()
                                                                        .addGap(23, 23, 23)
                                                                        .addGroup(
                                                                                layout.createParallelGroup(
                                                                                        javax.swing.GroupLayout.Alignment.BASELINE)
                                                                                        .addComponent(
                                                                                                jRadioButton2)
                                                                                        .addComponent(
                                                                                                jRadioTrade)
                                                                                        .addComponent(
                                                                                                jComboBox1,
                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                        .addComponent(
                                                                                                jRadioButton5))
                                                                        .addGap(3, 3, 3)
                                                                        .addGroup(
                                                                                layout.createParallelGroup(
                                                                                        javax.swing.GroupLayout.Alignment.BASELINE)
                                                                                        .addComponent(
                                                                                                jRadioButton4)
                                                                                        .addComponent(
                                                                                                jRadioButton1))
                                                                        .addPreferredGap(
                                                                                javax.swing.LayoutStyle.ComponentPlacement.UNRELATED))
                                                        .addGroup(
                                                                javax.swing.GroupLayout.Alignment.TRAILING,
                                                                layout.createSequentialGroup()
                                                                        .addPreferredGap(
                                                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                        .addComponent(jComboBox3,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                        .addGap(19, 19, 19)))
                                        .addGroup(
                                                layout.createParallelGroup(
                                                        javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(jSeparator1,
                                                                javax.swing.GroupLayout.Alignment.TRAILING)
                                                        .addGroup(
                                                                layout.createSequentialGroup()
                                                                        .addGroup(
                                                                                layout.createParallelGroup(
                                                                                        javax.swing.GroupLayout.Alignment.LEADING)
                                                                                        .addComponent(
                                                                                                jScrollPane2,
                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                306,
                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                        .addComponent(
                                                                                                jScrollPane1,
                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                303,
                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                        .addPreferredGap(
                                                                                javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                                        .addComponent(lTrade_type)
                                                                        .addGap(0, 0, Short.MAX_VALUE)))
                                        .addGap(18, 18, 18)
                                        .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(17, 17, 17))
        );
        pack();
    }

    private void processPayloadInput(ActionEvent evt) {
        final String txt = evt.getActionCommand();

        if (!verifyRadioIsSelected()) {
            jTextArea1.append("PLEASE MAKE SELECTION ON RADIO BUTTON \n");
            jTextArea1.setForeground(Color.red);
            return;
        }

        jProgressBar1.setValue(0);
        jProgressBar1.setStringPainted(true);

        if (jTextField2.getText().isEmpty())
            jTextField2.setText("1");
        final int iteration = Integer.parseInt(jTextField2.getText());
        jButton1.setEnabled(false);

        if (txt.contains("SEND") && (txt.contains("PAYLOAD"))) {
            processTrade(iteration);
        }
        if (txt.contains("CIL")) {
            processCILPayload();

        }
        if (txt.contains("GET") && (txt.contains("PAYLOAD"))) {
            processMemberPayload();
        }
        if (txt.contains("SEND") && (txt.contains("CLEARING"))) {
            processMembersSendPayload(iteration);
        }

        jButton1.setEnabled(true);

    }

    private void processMembersSendPayload(final int iteration) {
        final SwingWorker<Object, float[]> worker;
        final ClearingRequest clearingRequest = new ClearingRequest();
        worker = new SwingWorker<Object, float[]>() {

            @Override
            protected void process(List<float[]> chunks) {
                float[] progress = chunks.get(chunks.size() - 1); // only want the last one
                jProgressBar1.setValue(Math.round(progress[0] * 100f));

            }

            @Override
            protected Object doInBackground() throws Exception {
                qpidClient.setConnectionURL(request_broker.get("url"));

                for (int i = 1; i <= iteration; i++) {
                    setRequestData();
                    clearingRequest.setKey(String.format("request.%s.Request", request_queue_in_use));
                    clearingRequest.setReplyToQueue(reply_address);
                    clearingRequest.setResponseQueue(response_address);
                    qpidClient.setClearingMessagesParameters(clearingRequest);
                    qpidClient.SendMessage(queue, jTextArea1.getText(),
                            Integer.toString(i), "REQUEST");
                    publish(new float[] {
                            getProgress1(i, iteration),
                    });

                    Thread.sleep(5);

                }
                return null;
            }

        };
        worker.execute();
    }

    private void processMemberPayload() {
        qpidClient.setConnectionURL(request_broker.get("url"));

        SwingWorker<Object, String> swingWorker = new SwingWorker<Object, String>() {

            @Override protected Object doInBackground() throws Exception {
                List<String> total =
                        qpidClient.getTradeConfirmation(getQueue());
                if (total.isEmpty()) {
                    jTextArea2.setText("NO MORE MESSAGES FOUND");
                    jTextArea2.append("\n");
                    jTextArea2.setBackground(Color.black);
                    jTextArea2.setForeground(Color.WHITE);
                } else {
                    jTextArea1.setText("[ " + total.size() + " ] MESSAGE(S) FOUND");
                    jTextArea2.setBackground(Color.LIGHT_GRAY);
                    jTextArea2.setForeground(Color.BLUE);
                }
                return null;
            }
        };
        swingWorker.execute();
        jButton1.setEnabled(true);
    }

    private void processCILPayload() {
        SwingWorker<Object, String> swingWorker = new SwingWorker<Object, String>() {

            @Override protected Object doInBackground() throws Exception {
                qpidClient.ReadCILMessages(request_queue_in_use);
                jButton1.setEnabled(true);
                return null;
            }
        };
        swingWorker.execute();

    }

    private void processTrade(final int iteration) {
        final SwingWorker<Object, float[]> worker;
        qpidClient.setIncomingData(model);
        worker = new SwingWorker<Object, float[]>() {

            @Override
            protected void process(List<float[]> chunks) {
                float[] progress = chunks.get(chunks.size() - 1); // only want the last one
                jProgressBar1.setValue(Math.round(progress[0] * 100f));

            }

            @Override
            protected Object doInBackground() throws Exception {
                for (int i = 1; i <= iteration; i++) {
                    qpidClient.SendMessage(
                            String.format("%s.%s", "broadcast/broadcast",
                                    trade_import_broker.get("queue")),
                            jTextArea1.getText(),
                            Integer.toString(i), "TRADE");
                    jTextArea2.setText("Loading... [ " + i + " ] MESSAGES");
                    jTextArea2.append("\n");
                    jTextArea2.setBackground(Color.LIGHT_GRAY);
                    jTextArea2.setForeground(Color.BLUE);
                    publish(new float[] {
                            getProgress1(i, iteration),
                    });
                    Thread.sleep(5);

                }
                return null;
            }

        };
        worker.execute();
    }

    private void processClearingRequest(ActionEvent evt) {
        LOGGER.info(evt.getActionCommand());
        jRadioButton2.setSelected(false);
        jRadioTrade.setSelected(false);
        jRadioButton4.setSelected(false);
        jRadioButton5.setSelected(false);
        jTextField3.setForeground(Color.blue);
        jTextField3.setText(request_broker.get("url"));
        qpidClient.setDisconnectConsumer(true);
        SwingWorker<Object, String> swingWorker = new SwingWorker<Object, String>() {

            @Override protected Object doInBackground() throws Exception {
                setRequestData();
                try {
                    qpidClient.startConsumer(response_address);
                } catch (JMSException | URLSyntaxException | AMQException e) {
                    e.printStackTrace();
                }

                return null;
            }
        };
        swingWorker.execute();
        files_for_combo = getTemplatesFromFolder("clearing/");

        try {
            jTextArea1.setText(getTemplate("clearing/ClearingRequest.xml"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        jTextArea1.setForeground(Color.black);
        jTextArea2.setBackground(Color.white);
        // jTextArea2.setText("");
        jButton1.setText("SEND CLEARING");

    }

    private void selectTradeOption(ActionEvent evt) {
        LOGGER.info(evt.getActionCommand());
        jTextField3.setText(trade_import_broker.get("url"));
        qpidClient.setDisconnectConsumer(false);
        jTextField3.setForeground(Color.blue);
        jRadioButton1.setSelected(false);
        jRadioButton2.setSelected(false);
        jRadioButton4.setSelected(false);
        jRadioButton5.setSelected(false);
        jTextArea1.setForeground(Color.black);
        jTextArea2.setBackground(Color.white);
        files_for_combo = getTemplatesFromFolder("trades/");
        try {
            jTextArea1.setText(getTemplate("trades/TradeMatchReport.xml"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        jButton1.setText("SEND PAYLOAD");

    }

    private void processTradeConfirmation(ActionEvent evt) {
        jTextField3.setText(request_broker.get("url"));

        qpidClient.setDisconnectConsumer(false);

        jRadioButton1.setSelected(false);
        jRadioTrade.setSelected(false);
        jRadioButton4.setSelected(false);
        jRadioButton5.setSelected(false);
        jTextArea1.setText("");
        jTextArea2.setText("");
        jButton1.setText("GET PAYLOAD");
        jTextArea2.setBackground(Color.white);
        jTextArea2.setForeground(Color.BLUE);
        String cmd = evt.getActionCommand();
        if (cmd.contains("Workflow BroadCast")) {
            jRadioButton1.setSelected(false);
            jRadioTrade.setSelected(false);
            jRadioButton2.setSelected(false);
            jRadioButton4.setSelected(true);
            jRadioButton5.setSelected(false);
            queue = String.format("broadcast.%s.%s ;{mode: consume}", request_queue_in_use, "Workflow");
        } else {
            queue = String.format("broadcast.%s.%s ;{mode: consume}", request_queue_in_use,
                    "TradeConfirmation");
        }
    }

    private void processCILMessages(ActionEvent event) {
        LOGGER.info(event.getActionCommand() + "Running");
        jTextField3.setText(cil_broadcast_broker.get("url"));
        qpidClient.setDisconnectConsumer(false);
        jRadioButton1.setSelected(false);
        jRadioButton2.setSelected(false);
        jRadioTrade.setSelected(false);
        jRadioButton4.setSelected(false);
        jButton1.setText("CIL PAYLOAD");
        jTextArea1.setText("");
        jTextArea2.setText("");
        jTextArea2.setBackground(Color.white);
        jTextArea2.setForeground(Color.BLUE);
    }

    private void jProgressBar1PropertyChange(java.beans.PropertyChangeEvent evt) {
        LOGGER.info(evt.getPropertyName());
        // TODO add your handling code here:
    }

    private String getQueue() {

        return queue;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager
                    .getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainGui.class.getName()).log(java.util.logging.Level.SEVERE,
                    null, ex);
        }

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {

            final MainGui mainGui = new MainGui();

            public void run() {
                mainGui.setTitle("Trade And Clearing Tool");
                mainGui.setVisible(true);
            }
        });
    }

    private float getProgress1(int value, int max) {
        return (float) value / (float) max;
    }

    private void setRequestData() {
        queue =
                String.format("request.%s/request; { node: { type: topic }, create: never }",
                        request_queue_in_use);
        reply_address =
                String.format("response/response.%s.queue_1; { create: receiver, node: { type: topic } }",
                        request_queue_in_use);
        response_address =
                String.format(
                        "response.%s.queue_1; {create: receiver, assert: never,node: { type: queue, x-declare:"
                                +
                                "{ auto-delete: true, exclusive: false,"
                                +
                                "arguments: { 'qpid.policy_type':"
                                +
                                "ring,'qpid.max_count': 1000,"
                                +
                                "'qpid.max_size': 1000000 } }, x-bindings: [ { exchange: 'response', queue:'response.%s.queue_1',key:"
                                +
                                "'response.%s.queue_1', } ] } }", request_queue_in_use,
                        request_queue_in_use, request_queue_in_use);

    }

    private String getTemplate(String templateType) throws IOException {
        ReadTemplate template = new ReadTemplate(templateType);
        assert template.getTemplate() != null;
        return template.getTemplate();
    }

    private List<String> getTemplatesFromFolder(String folderName) {
        List<String> files = new ArrayList<>();
        InputStream uri = MainGui.class.getResourceAsStream("/templates/" + folderName);
        System.out.println("Resources = /templates/" + folderName);
        assert uri != null;
        BufferedInputStream in = new BufferedInputStream(uri);
        BufferedReader rdr = new BufferedReader(new InputStreamReader(in));
        String line;
        try {
            while ((line = rdr.readLine()) != null) {
                System.out.println("Files.." + line);
                files.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            rdr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Files2..");
        return files;
    }

    private boolean verifyRadioIsSelected() {

        if (jRadioButton4.isSelected()) {
            queue = String.format("broadcast.%s.%s ;{mode: consume}", request_queue_in_use, "Workflow");
        }
        if (jRadioButton2.isSelected()) {
            queue = String.format("broadcast.%s.%s ;{mode: consume}", request_queue_in_use,
                    "TradeConfirmation");
        }

        return jRadioButton1.isSelected() || jRadioButton2.isSelected() || jRadioTrade.isSelected()
                || jRadioButton4.isSelected() || jRadioButton5.isSelected();
    }

    public JTextArea getJTextArea1() {
        return jTextArea1;
    }

    public JTextArea getJTextArea2() {
        return jTextArea2;
    }

    // Variables declaration - do not modify
    private javax.swing.JButton jButton1;
    private javax.swing.JComboBox<?> jComboBox2;
    private javax.swing.JComboBox<?> jComboBox3;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JRadioButton jRadioButton2;
    private javax.swing.JRadioButton jRadioTrade;
    private javax.swing.JRadioButton jRadioButton4;
    private javax.swing.JRadioButton jRadioButton5;
    private javax.swing.JTextArea jTextArea1;
    private JTextArea jTextArea2;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private JLabel lTrade_type;

}
