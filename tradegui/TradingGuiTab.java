package com.eurexchange.clear.tradegui;

import com.eurexchange.clear.frontend.PopClickListener;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Vector;

public class TradingGuiTab {

    private JButton jButton1;
    private JButton jButton2;
    private JTextField jTextField1;
    private JTextPane jTextPane1;
    private JTextField jTextField2;
    private JProgressBar progress;

    public TradingGuiTab(JProgressBar progressBar) {
        this.progress = progressBar;
    }

    public JPanel invoke() {
        DefaultTableModel instrumentModel = new DefaultTableModel();
        DefaultTableModel sellerModel = new DefaultTableModel();
        DefaultTableModel buyerModel = new DefaultTableModel();
        JPanel tradingPanel = new JPanel();
        jButton1 = new JButton();
        JButton startStopButton = new JButton("Start Connection");
        JLabel jLabel1 = new JLabel();
        jTextField1 = new JTextField();
        jButton2 = new JButton();
        JLabel jLabel2 = new JLabel();
        JLabel jLabel5 = new JLabel();
        JLabel jLabel6 = new JLabel();
        JLabel jLabel7 = new JLabel();
        JLabel jLabel3 = new JLabel();
        JSpinner jSpinner1 = new JSpinner();
        jTextField2 = new JTextField();
        jTextField2.setText("1");
        jTextPane1 = new JTextPane();
        jSpinner1.setValue(1);

        JTable buyerTable = new JTable(buyerModel);
        JTable instrumentTable = new JTable(instrumentModel);
        JSeparator jSeparator2 = new JSeparator();
        JScrollPane jScrollPane1 = new JScrollPane();
        JScrollPane jScrollPane2 = new JScrollPane();
        JScrollPane jScrollPane3 = new JScrollPane();
        JScrollPane jScrollPane4 = new JScrollPane();

        jButton1.setText("Press To Start");

        jLabel1.setText("Load Config File");
        TradeBuilderInterface tradeBuilderInterface = new TradeBuilder(this);
        jButton1.addActionListener(tradeBuilderInterface);
        jButton2.addActionListener(tradeBuilderInterface);
        startStopButton.addActionListener(tradeBuilderInterface);
        jSpinner1.addChangeListener(tradeBuilderInterface);
        jTextPane1.setBorder(BorderFactory.createLineBorder(new java.awt.Color(140, 140, 140)));
        jScrollPane1.setViewportView(jTextPane1);
        JTable sellerTable = new JTable(sellerModel);

        Object cols = new Object[] { "Member", "Clearer", "Account", "TradeId" };
        Vector<Object> rows = new Vector<>();
        new TradeDataTable(sellerModel, (Object[]) cols, rows);
        tradeBuilderInterface.setSellerModel(sellerModel);

        sellerTable.getTableHeader().setReorderingAllowed(true);
        sellerTable.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);

        sellerTable.addMouseListener(new PopClickListener());
        sellerTable.setShowHorizontalLines(true);
        sellerTable.setShowVerticalLines(true);
        sellerTable.setForeground(Color.RED);
        jScrollPane2.setViewportView(sellerTable);

        jButton2.setText("Send Trade");

        jLabel2.setText("Feed Trade #");

        cols = new Object[] { "Member", "Clearer", "Account", "TradeId" };
        rows = new Vector<>();
        new TradeDataTable(buyerModel, (Object[]) cols, rows);
        tradeBuilderInterface.setBuyerModel(buyerModel);
        buyerTable.getTableHeader().setReorderingAllowed(true);
        buyerTable.addMouseListener(new PopClickListener());
        buyerTable.setShowHorizontalLines(true);
        buyerTable.setShowVerticalLines(true);
        buyerTable.getColumnModel().getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        buyerTable.setForeground(Color.BLUE);
        jScrollPane3.setViewportView(buyerTable);

        jLabel5.setFont(new Font("Times New Roman", 0, 18)); // NOI18N
        jLabel5.setText("Buyer");

        jLabel6.setFont(new Font("Times New Roman", 0, 18)); // NOI18N
        jLabel6.setText("Seller");

        cols = new Object[] { "Symbol", "MMY", "StkPrice", "Qty", "Price"};
        rows = new Vector<>();
        new TradeDataTable(instrumentModel, (Object[]) cols, rows);
        tradeBuilderInterface.setInstrumentModel(instrumentModel);

        instrumentTable.getTableHeader().setReorderingAllowed(true);
        instrumentTable.addMouseListener(new PopClickListener());
        instrumentTable.setShowHorizontalLines(true);
        instrumentTable.setShowVerticalLines(true);
        instrumentTable.getColumnModel().getSelectionModel().setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION);

        instrumentTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        instrumentTable.setForeground(Color.BLUE);

        jScrollPane4.setViewportView(instrumentTable);
        instrumentTable.setSelectionForeground(Color.cyan);
        instrumentTable.getModel().addTableModelListener(tradeBuilderInterface);

//        instrumentTable.getModel().addTableModelListener(tradeBuilderInterface);
//        buyerTable.getModel().addTableModelListener(tradeBuilderInterface);
//        sellerTable.getModel().addTableModelListener(tradeBuilderInterface);

        jLabel7.setFont(new Font("Times New Roman", 0, 18)); // NOI18N
        jLabel7.setText("Instrument");

        jLabel3.setText("Thread(s) #");

        GroupLayout tradingPanelLayout = new GroupLayout(tradingPanel);
        tradingPanel.setLayout(tradingPanelLayout);
        tradingPanelLayout.setHorizontalGroup(
                tradingPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(jSeparator2, GroupLayout.Alignment.TRAILING)
                        .addComponent(jScrollPane1)
                        .addGroup(tradingPanelLayout.createSequentialGroup()
                                .addGap(55, 55, 55)
                                .addGroup(
                                        tradingPanelLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                                .addGroup(tradingPanelLayout.createSequentialGroup()
                                                        .addComponent(startStopButton,
                                                                GroupLayout.PREFERRED_SIZE, 138,
                                                                GroupLayout.PREFERRED_SIZE)
                                                        .addPreferredGap(
                                                                LayoutStyle.ComponentPlacement.RELATED,
                                                                GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addComponent(jLabel1, GroupLayout.PREFERRED_SIZE,
                                                                123, GroupLayout.PREFERRED_SIZE))
                                                .addGroup(tradingPanelLayout.createSequentialGroup()
                                                        .addGroup(tradingPanelLayout.createParallelGroup(
                                                                GroupLayout.Alignment.LEADING)
                                                                .addComponent(jScrollPane4,
                                                                        GroupLayout.PREFERRED_SIZE, 330,
                                                                        GroupLayout.PREFERRED_SIZE)
                                                                .addComponent(jLabel7,
                                                                        GroupLayout.PREFERRED_SIZE, 185,
                                                                        GroupLayout.PREFERRED_SIZE)
                                                                .addGroup(tradingPanelLayout
                                                                        .createSequentialGroup()
                                                                        .addGroup(tradingPanelLayout
                                                                                .createParallelGroup(
                                                                                        GroupLayout.Alignment.TRAILING,
                                                                                        false)
                                                                                .addComponent(jLabel3,
                                                                                        GroupLayout.Alignment.LEADING,
                                                                                        GroupLayout.DEFAULT_SIZE,
                                                                                        GroupLayout.DEFAULT_SIZE,
                                                                                        Short.MAX_VALUE)
                                                                                .addComponent(jLabel2,
                                                                                        GroupLayout.Alignment.LEADING,
                                                                                        GroupLayout.DEFAULT_SIZE,
                                                                                        78, Short.MAX_VALUE))
                                                                        .addPreferredGap(
                                                                                LayoutStyle.ComponentPlacement.RELATED)
                                                                        .addComponent(jTextField2,
                                                                                GroupLayout.PREFERRED_SIZE,
                                                                                68,
                                                                                GroupLayout.PREFERRED_SIZE)
                                                                        .addPreferredGap(
                                                                                LayoutStyle.ComponentPlacement.UNRELATED)
                                                                        .addGroup(tradingPanelLayout
                                                                                .createParallelGroup(
                                                                                        GroupLayout.Alignment.LEADING)
                                                                                .addComponent(jButton2)
                                                                                .addComponent(jSpinner1,
                                                                                        GroupLayout.PREFERRED_SIZE,
                                                                                        53,
                                                                                        GroupLayout.PREFERRED_SIZE))))
                                                        .addGap(0, 0, Short.MAX_VALUE)))
                                .addGap(18, 18, 18)
                                .addGroup(
                                        tradingPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                .addComponent(jTextField1, GroupLayout.PREFERRED_SIZE, 328,
                                                        GroupLayout.PREFERRED_SIZE)
                                                .addComponent(jButton1, GroupLayout.PREFERRED_SIZE, 168,
                                                        GroupLayout.PREFERRED_SIZE))
                                .addContainerGap())
                        .addGroup(GroupLayout.Alignment.TRAILING, tradingPanelLayout.createSequentialGroup()
                                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel5, GroupLayout.PREFERRED_SIZE, 185,
                                        GroupLayout.PREFERRED_SIZE)
                                .addGap(226, 226, 226)
                                .addComponent(jLabel6, GroupLayout.PREFERRED_SIZE, 185,
                                        GroupLayout.PREFERRED_SIZE)
                                .addGap(188, 188, 188))
                        .addGroup(GroupLayout.Alignment.TRAILING, tradingPanelLayout.createSequentialGroup()
                                .addGap(0, 430, Short.MAX_VALUE)
                                .addComponent(jScrollPane3, GroupLayout.PREFERRED_SIZE, 400,
                                        GroupLayout.PREFERRED_SIZE)
                                .addGap(62, 62, 62)
                                .addComponent(jScrollPane2, GroupLayout.PREFERRED_SIZE, 437,
                                        GroupLayout.PREFERRED_SIZE)
                                .addGap(97, 97, 97))
        );
        tradingPanelLayout.setVerticalGroup(
                tradingPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(tradingPanelLayout.createSequentialGroup()
                                .addGap(24, 24, 24)
                                .addGroup(
                                        tradingPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                .addGroup(tradingPanelLayout.createSequentialGroup()
                                                        .addGroup(tradingPanelLayout.createParallelGroup(
                                                                GroupLayout.Alignment.BASELINE)
                                                                .addComponent(jTextField1,
                                                                        GroupLayout.PREFERRED_SIZE,
                                                                        GroupLayout.DEFAULT_SIZE,
                                                                        GroupLayout.PREFERRED_SIZE)
                                                                .addComponent(jLabel1))
                                                        .addGap(18, 18, 18)
                                                        .addComponent(jButton1))
                                                .addGroup(tradingPanelLayout.createSequentialGroup()
                                                        .addComponent(startStopButton,
                                                                GroupLayout.PREFERRED_SIZE,
                                                                GroupLayout.DEFAULT_SIZE,
                                                                GroupLayout.PREFERRED_SIZE)
                                                        .addGap(34, 34, 34)
                                                        .addGroup(tradingPanelLayout.createParallelGroup(
                                                                GroupLayout.Alignment.BASELINE)
                                                                .addComponent(jTextField2,
                                                                        GroupLayout.PREFERRED_SIZE, 23,
                                                                        GroupLayout.PREFERRED_SIZE)
                                                                .addComponent(jButton2)
                                                                .addComponent(jLabel2,
                                                                        GroupLayout.PREFERRED_SIZE, 23,
                                                                        GroupLayout.PREFERRED_SIZE))))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 42, Short.MAX_VALUE)
                                .addGroup(
                                        tradingPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                .addComponent(jLabel3, GroupLayout.PREFERRED_SIZE, 34,
                                                        GroupLayout.PREFERRED_SIZE)
                                                .addGroup(tradingPanelLayout.createSequentialGroup()
                                                        .addGap(7, 7, 7)
                                                        .addComponent(jSpinner1, GroupLayout.PREFERRED_SIZE,
                                                                27, GroupLayout.PREFERRED_SIZE)))
                                .addGap(18, 18, 18)
                                .addGroup(
                                        tradingPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                .addComponent(jLabel5, GroupLayout.Alignment.TRAILING,
                                                        GroupLayout.PREFERRED_SIZE, 32,
                                                        GroupLayout.PREFERRED_SIZE)
                                                .addComponent(jLabel6, GroupLayout.Alignment.TRAILING,
                                                        GroupLayout.PREFERRED_SIZE, 32,
                                                        GroupLayout.PREFERRED_SIZE)
                                                .addComponent(jLabel7, GroupLayout.Alignment.TRAILING,
                                                        GroupLayout.PREFERRED_SIZE, 32,
                                                        GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(
                                        tradingPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                .addGroup(tradingPanelLayout.createSequentialGroup()
                                                        .addGroup(tradingPanelLayout.createParallelGroup(
                                                                GroupLayout.Alignment.LEADING)
                                                                .addComponent(jScrollPane4,
                                                                        GroupLayout.PREFERRED_SIZE, 106,
                                                                        GroupLayout.PREFERRED_SIZE)
                                                                .addComponent(jScrollPane2,
                                                                        GroupLayout.Alignment.TRAILING,
                                                                        GroupLayout.PREFERRED_SIZE, 106,
                                                                        GroupLayout.PREFERRED_SIZE))
                                                        .addPreferredGap(
                                                                LayoutStyle.ComponentPlacement.UNRELATED)
                                                        .addComponent(jSeparator2, GroupLayout.PREFERRED_SIZE,
                                                                10, GroupLayout.PREFERRED_SIZE)
                                                        .addPreferredGap(
                                                                LayoutStyle.ComponentPlacement.RELATED, 28,
                                                                Short.MAX_VALUE)
                                                        .addComponent(jScrollPane1,
                                                                GroupLayout.PREFERRED_SIZE, 41,
                                                                GroupLayout.PREFERRED_SIZE))
                                                .addGroup(GroupLayout.Alignment.TRAILING,
                                                        tradingPanelLayout.createSequentialGroup()
                                                                .addComponent(jScrollPane3,
                                                                        GroupLayout.PREFERRED_SIZE, 106,
                                                                        GroupLayout.PREFERRED_SIZE)
                                                                .addGap(0, 0, Short.MAX_VALUE)))
                                .addContainerGap())
        );
        return tradingPanel;
    }

    public JButton getButton1() {
        return jButton1;
    }

    public JButton getButton2() {
        return jButton2;
    }

    public JTextField getTextField1() {
        return jTextField1;
    }

    public JTextField getTextField2() {
        return jTextField2;
    }

    public JTextPane getTextPane1() {
        return jTextPane1;
    }

    public JProgressBar getProgressBar() {
        return progress;
    }

    class TradeDataTable extends DefaultTableModel {

        private Object[] cols;
        private Vector rowData;

        public TradeDataTable(DefaultTableModel model, Object[] cols, Vector rowData) {
            this.cols = cols;
            this.rowData = rowData;
            setUpTradeData(model);

        }

        private void setUpTradeData(DefaultTableModel model) {
            for (Object obj : cols)
                model.addColumn(obj);
            model.addRow(rowData);
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            if (getRowCount() == 0) {
                return super.getColumnClass(columnIndex);
            }
            Object value = getValueAt(0, columnIndex);
            if (value == null) {
                return super.getColumnClass(columnIndex);
            }

            return value.getClass();
        }

    }
}
