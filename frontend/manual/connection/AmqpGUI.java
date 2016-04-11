package com.eurexchange.clear.frontend.manual.connection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * @author carvcal
 */
public class AmqpGUI extends JFrame {

    public boolean run_mode = true;
    private static final Logger LOGGER = LoggerFactory.getLogger(AmqpGUI.class);
    private SimpleJmsConnection jmsConnection = new SimpleJmsConnection(this);
    String dev_host = "cbgd01.xeop.de:20282";
    String connetionURL = "amqp://admin:admin@JavaTool/?brokerlist='tcp://" + dev_host +
            "?heartbeat='5''&sasl_mechs='PLAIN''&sync_publish='all'&sync_ack='true'";
    private ArrayList<String> list_of_queues = new ArrayList<>();
    private String userName;
    private char[] password;
    private String hostName;
    private String port;

    /**
     * Creates new form AmqpSimpleGUI
     */
    public AmqpGUI() {
        initComponents();
        list_of_queues = jmsConnection.getListOfQueueForBroker(connetionURL);

    }

    public void setTextArea1(String text) {
        jTextArea1.append(text);
        jTextArea1.setCaretPosition(jTextArea1.getDocument().getLength());
    }

    public JTextArea getTextArea1() {
        return jTextArea1;
    }

    public JTextArea getTextArea2() {
        return jTextArea2;
    }

    private void initComponents() {

        JSeparator jSeparator1 = new JSeparator();
        JScrollPane jScrollPane1 = new JScrollPane();
        jTextArea1 = new JTextArea();
        jTextField1 = new JTextField();
        JLabel jLabel1 = new JLabel();
        JLabel jLabel2 = new JLabel();
        jTextField2 = new JTextField();
        jButton1 = new JButton();
        JScrollPane jScrollPane2 = new JScrollPane();
        jTextArea2 = new JTextArea();
        jTextField3 = new JTextField();
        JLabel jLabel3 = new JLabel();
        jCheckBox2 = new JCheckBox();
        jRadioButton2 = new JRadioButton();

        jTextField1.setText("request_be.OKAOS_TESTCALMMACC1.C7");
        jTextField1.setEnabled(false);
        jTextField2.setText(connetionURL);

        jCheckBox1 = new JCheckBox();
        JLabel jLabel4 = new JLabel();
        JLabel jLabel5 = new JLabel();
        JLabel jLabel6 = new JLabel();
        JLabel jLabel7 = new JLabel();
        JLabel jLabel8 = new JLabel();
        JLabel jLabel9 = new JLabel();

        final JRadioButton jRadioButton1 = new JRadioButton();
        jTextField4 = new JTextField();
        jTextField5 = new JTextField();
        jPasswordField1 = new JPasswordField();
        jTextField6 = new JTextField();

        jRadioButton1.setSelected(true);
        jCheckBox2.setSelected(true);
        jCheckBox1.addActionListener(new ActionListener() {

            @Override public void actionPerformed(ActionEvent e) {
                if (jCheckBox1.isSelected()) {
                    jTextField1.setEnabled(true);
                    jTextField1.setVisible(true);
                    list_of_queues.clear();
                    list_of_queues.add(jTextField1.getText());
                    jRadioButton1.setSelected(false);
                    jTextArea1.append("Loading Single Queue Option " + jTextField1.getText() + "\n");
                    jCheckBox1.setSelected(true);
                }
            }
        });

        jCheckBox2.addActionListener(new ActionListener() {

            @Override public void actionPerformed(ActionEvent e) {

                if (jCheckBox2.isSelected()) {
                    jTextArea1.append("Connection String selection added \n");
                    jTextField2.setEnabled(true);
                    jRadioButton2.setSelected(false);
                    jTextField4.setEnabled(false);
                    jTextField5.setEnabled(false);
                    jTextField6.setEnabled(false);
                    jPasswordField1.setEnabled(false);

                }
            }
        });

        jRadioButton1.addActionListener(new ActionListener() {

            @Override public void actionPerformed(ActionEvent e) {
                if (jRadioButton1.isSelected()) {
                    jTextArea1.append("Loading from list of queues \n");
                    checkConnectionOption();
                    checkCredentials(jTextField4, jTextField5, jTextField6, jPasswordField1);
                    list_of_queues = jmsConnection.getListOfQueueForBroker(jmsConnection.getConnection());
                    jCheckBox1.setSelected(false);
                    jTextField1.setEnabled(false);
                }

            }
        });

        jRadioButton2.addActionListener(new ActionListener() {

            @Override public void actionPerformed(ActionEvent e) {
                jTextArea1.append("Using User provided Credentials");
                if (jRadioButton2.isSelected()) {
                    jCheckBox2.setSelected(false);
                    jTextField4.setEnabled(true);
                    jTextField5.setEnabled(true);
                    jTextField6.setEnabled(true);
                    jPasswordField1.setEnabled(true);
                    jTextField2.setEnabled(false);
                    checkCredentials(jTextField4, jTextField5, jTextField6, jPasswordField1);

                }
            }
        });

        jTextField4.setEnabled(false);
        jTextField5.setEnabled(false);
        jTextField6.setEnabled(false);
        jPasswordField1.setEnabled(false);

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        jTextField3.setText("1");
        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane1.setViewportView(jTextArea1);

        jLabel1.setText("Single QUEUE");

        jLabel2.setText("Connection String");

        jButton1.setText("Connect");
        jButton1.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jScrollPane2.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane2.setToolTipText("");
        jScrollPane2.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

        jTextArea2.setEditable(false);
        jTextArea2.setBackground(new java.awt.Color(240, 240, 240));
        jTextArea2.setColumns(20);
        jTextArea2.setRows(5);
        jTextArea2.setAutoscrolls(false);
        jTextArea2.setCaretColor(new java.awt.Color(240, 240, 240));
        jTextArea2.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jScrollPane2.setViewportView(jTextArea2);

        jLabel3.setText("Number Of Threads");
        jLabel4.setText("List of QUEUES");

        jLabel5.setText("UserName");

        jLabel6.setText("Password");

        jLabel7.setText("Host");

        jLabel8.setText("Port");
        jLabel9.setText("Credentials");
        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(jScrollPane1)
                        .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(jSeparator1, GroupLayout.Alignment.TRAILING)
                                        .addComponent(jScrollPane2, GroupLayout.PREFERRED_SIZE, 67,
                                                GroupLayout.PREFERRED_SIZE)
                                        .addGroup(layout.createSequentialGroup()
                                                .addGap(16, 16, 16)
                                                .addGroup(layout.createParallelGroup(
                                                        GroupLayout.Alignment.LEADING)
                                                        .addGroup(layout.createParallelGroup(
                                                                GroupLayout.Alignment.LEADING, false)
                                                                .addComponent(jLabel1,
                                                                        GroupLayout.DEFAULT_SIZE,
                                                                        GroupLayout.DEFAULT_SIZE,
                                                                        Short.MAX_VALUE)
                                                                .addComponent(jLabel3,
                                                                        GroupLayout.DEFAULT_SIZE, 114,
                                                                        Short.MAX_VALUE)
                                                                .addComponent(jLabel2,
                                                                        GroupLayout.DEFAULT_SIZE,
                                                                        GroupLayout.DEFAULT_SIZE,
                                                                        Short.MAX_VALUE)
                                                                .addComponent(jLabel4,
                                                                        GroupLayout.DEFAULT_SIZE,
                                                                        GroupLayout.DEFAULT_SIZE,
                                                                        Short.MAX_VALUE))
                                                        .addComponent(jLabel9, GroupLayout.PREFERRED_SIZE, 79,
                                                                GroupLayout.PREFERRED_SIZE))
                                                .addGap(22, 22, 22)
                                                .addGroup(layout.createParallelGroup(
                                                        GroupLayout.Alignment.LEADING)
                                                        .addComponent(jRadioButton1)
                                                        .addComponent(jCheckBox2, GroupLayout.PREFERRED_SIZE,
                                                                33, GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(jCheckBox1, GroupLayout.PREFERRED_SIZE,
                                                                31, GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(jRadioButton2))
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(layout.createParallelGroup(
                                                        GroupLayout.Alignment.LEADING)
                                                        .addGroup(layout.createSequentialGroup()
                                                                .addComponent(jLabel5)
                                                                .addGap(38, 38, 38)
                                                                .addComponent(jTextField4,
                                                                        GroupLayout.PREFERRED_SIZE, 148,
                                                                        GroupLayout.PREFERRED_SIZE)
                                                                .addGap(18, 18, 18)
                                                                .addComponent(jLabel6)
                                                                .addGap(18, 18, 18)
                                                                .addComponent(jPasswordField1,
                                                                        GroupLayout.PREFERRED_SIZE, 149,
                                                                        GroupLayout.PREFERRED_SIZE)
                                                                .addPreferredGap(
                                                                        LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(jLabel7,
                                                                        GroupLayout.PREFERRED_SIZE, 48,
                                                                        GroupLayout.PREFERRED_SIZE)
                                                                .addGap(18, 18, 18)
                                                                .addComponent(jTextField5,
                                                                        GroupLayout.PREFERRED_SIZE, 204,
                                                                        GroupLayout.PREFERRED_SIZE)
                                                                .addPreferredGap(
                                                                        LayoutStyle.ComponentPlacement.UNRELATED)
                                                                .addComponent(jLabel8)
                                                                .addPreferredGap(
                                                                        LayoutStyle.ComponentPlacement.UNRELATED)
                                                                .addComponent(jTextField6,
                                                                        GroupLayout.PREFERRED_SIZE, 38,
                                                                        GroupLayout.PREFERRED_SIZE))
                                                        .addGroup(layout.createSequentialGroup()
                                                                .addComponent(jTextField3,
                                                                        GroupLayout.PREFERRED_SIZE, 64,
                                                                        GroupLayout.PREFERRED_SIZE)
                                                                .addGap(100, 100, 100)
                                                                .addComponent(jButton1,
                                                                        GroupLayout.PREFERRED_SIZE, 337,
                                                                        GroupLayout.PREFERRED_SIZE))
                                                        .addComponent(jTextField2, GroupLayout.PREFERRED_SIZE,
                                                                960, GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(jTextField1, GroupLayout.PREFERRED_SIZE,
                                                                718, GroupLayout.PREFERRED_SIZE))))
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(jScrollPane2, GroupLayout.PREFERRED_SIZE, 23,
                                        GroupLayout.PREFERRED_SIZE)
                                .addGap(29, 29, 29)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(
                                                        GroupLayout.Alignment.BASELINE)
                                                        .addComponent(jTextField1, GroupLayout.PREFERRED_SIZE,
                                                                GroupLayout.DEFAULT_SIZE,
                                                                GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(jCheckBox1)
                                                        .addComponent(jLabel1))
                                                .addGap(18, 18, 18)
                                                .addComponent(jLabel4))
                                        .addComponent(jRadioButton1))
                                .addGap(10, 10, 10)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel2)
                                        .addComponent(jTextField2, GroupLayout.PREFERRED_SIZE,
                                                GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jCheckBox2))
                                .addGap(26, 26, 26)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                .addComponent(jLabel5)
                                                .addComponent(jTextField4, GroupLayout.PREFERRED_SIZE,
                                                        GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                                .addComponent(jLabel6)
                                                .addComponent(jPasswordField1, GroupLayout.PREFERRED_SIZE,
                                                        GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                                .addComponent(jLabel7)
                                                .addComponent(jTextField5, GroupLayout.PREFERRED_SIZE,
                                                        GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                                .addComponent(jLabel8)
                                                .addComponent(jTextField6, GroupLayout.PREFERRED_SIZE,
                                                        GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                                .addComponent(jRadioButton2))
                                        .addComponent(jLabel9, GroupLayout.PREFERRED_SIZE, 25,
                                                GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 69, Short.MAX_VALUE)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(jButton1)
                                        .addComponent(jLabel3)
                                        .addComponent(jTextField3, GroupLayout.PREFERRED_SIZE,
                                                GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addGap(80, 80, 80)
                                .addComponent(jSeparator1, GroupLayout.PREFERRED_SIZE, 10,
                                        GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jScrollPane1, GroupLayout.PREFERRED_SIZE, 79,
                                        GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>

    private void checkConnectionOption() {
        if (jCheckBox2.isSelected()) {
            jmsConnection.setConnection(jTextField2.getText());
        }
        if (jRadioButton2.isSelected()) {
            checkCredentials(jTextField4, jTextField5, jTextField6, jPasswordField1);
        }
        if ((jRadioButton2.isSelected()) && (jCheckBox2.isSelected())) {
            jmsConnection.setConnection(jTextField2.getText());
            jCheckBox2.setSelected(false);
        }
        if (jCheckBox1.isSelected()) {
            list_of_queues.clear();
            list_of_queues.add(jTextField1.getText());
            jmsConnection.setDestinationQueue(jTextField1.getText());
        }
    }

    private void checkCredentials(JTextField jTextField4, JTextField jTextField5, JTextField jTextField6,
            JPasswordField jPasswordField1) {
        if ((!jTextField4.getText().equals(""))
                && (!jTextField5.getText().equals("")) && (!jTextField6.getText().equals(""))) {
            setUserDefinedLogon(jTextField4.getText(), jPasswordField1.getPassword(),
                    jTextField5.getText(), jTextField6.getText());
            jmsConnection.setConnection(getUserDefinedLogon());

        }
    }

    private void setUserDefinedLogon(String userName, char[] password, String host, String port) {
        this.userName = userName;
        this.password = password;
        this.hostName = host;
        this.port = port;
    }

    private String getUserDefinedLogon() {
        return String.format("amqp://%s:%s@JavaTool/?brokerlist='tcp://%s:%s"
                        + "?heartbeat='5''&sasl_mechs='PLAIN''&sync_publish='all'&sync_ack='true'",
                userName, new String(password), hostName, port);
    }

    private void jButton1ActionPerformed(final ActionEvent evt) {
        checkConnectionOption();
        final int no_of_threads = Integer.parseInt(jTextField3.getText());
        if (evt.getActionCommand().equals("Connect")) {
            run_mode = true;
            jButton1.setText("Disconnect");
            Thread t = new Thread(new Runnable() {

                @Override public void run() {
                    jmsConnection.startConnection(no_of_threads, list_of_queues);
                    while (run_mode) {
                        try {
                            jTextArea1.setCaretPosition(jTextArea1.getDocument().getLength());
                            if (jTextArea1.getText().contains("Started")) {
                                LOGGER.info("connection at started mode");
                                jTextArea1.append("Connected Threads #" + jTextField3.getText() + "\n");
                                jTextArea2.setVisible(true);
                                jTextArea2.setBackground(Color.GREEN);
                            }
                            Thread.sleep(3000);

                        } catch (InterruptedException e) {
                            LOGGER.error(e.getMessage());
                        }
                    }
                }

            });
            t.start();

        }
        if (evt.getActionCommand().equals("Disconnect")) {
            run_mode = false;
            jButton1.setText("Connect");
            Thread t2 = new Thread(new Runnable() {

                @Override public void run() {
                    run_mode = false;
                    jTextArea1.append("Disconnected \n");
                    jTextArea2.setVisible(true);
                    jTextArea2.setBackground(Color.YELLOW);
                    jmsConnection.stopConnection();

                }
            });
            t2.start();
        }
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
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                javax.swing.UnsupportedLookAndFeelException ex) {
            LOGGER.error(ex.getMessage());
        }

        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                new AmqpGUI().setVisible(true);
            }
        });
    }

    private JCheckBox jCheckBox1;
    private JTextField jTextField4;
    private JTextField jTextField5;
    private JPasswordField jPasswordField1;
    private JTextField jTextField6;
    private JCheckBox jCheckBox2;
    private JRadioButton jRadioButton2;
    private JButton jButton1;
    private JTextArea jTextArea1;
    private JTextArea jTextArea2;
    private JTextField jTextField1;
    private JTextField jTextField2;
    private JTextField jTextField3;

    // End of variables declaration
}
