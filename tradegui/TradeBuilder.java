package com.eurexchange.clear.tradegui;

import com.eurexchange.clear.common.service.CommonSenderService;
import com.eurexchange.clear.domain.Account;
import com.eurexchange.clear.domain.AmqpQueue;
import com.eurexchange.clear.domain.ClearedInstrument;
import com.eurexchange.clear.frontend.ReadTemplate;
import com.eurexchange.clear.frontend.TradeRequest;
import com.eurexchange.clear.frontend.dao.DataAccessImpl;
import com.eurexchange.clear.frontend.tradematch.obj.InstrmtType;
import com.eurexchange.clear.frontend.tradematch.obj.PtyType;
import com.eurexchange.clear.frontend.tradematch.obj.TrdMtchRptType;
import com.eurexchange.clear.frontend.tradematch.obj.TrdMtchSideType;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.swing.*;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class TradeBuilder implements TradeBuilderInterface {

    DefaultTableModel instrumentModel = new DefaultTableModel();
    DefaultTableModel buyerModel = new DefaultTableModel();
    DefaultTableModel sellerModel = new DefaultTableModel();
    private TradingGuiTab tradingGuiTab;
    private Properties prop = new Properties();
    private InputStream input = null;
    private DataAccessImpl dataAccess;
    QpidMsgService qpidMsgService;
    private TrdMtchRptType trdMtchRptType;
    private TradeRequest tradeRequest;
    private Boolean isConfigurationLoaded = false;
    private EntityManager em;
    EntityManagerFactory emf;
    JButton stopStartButton = null;
    private String totalThreads = "1";
    private DefaultTableModel table;
    Timer timer;
    int progressValue = 0;
    static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(TradeBuilder.class);

    TradeBuilder(TradingGuiTab tradingGuiTab) {
        this.tradingGuiTab = tradingGuiTab;
        trdMtchRptType = new TrdMtchRptType();
        tradeRequest = new TradeRequest(trdMtchRptType);
        qpidMsgService = new QpidMsgServiceImpl(tradingGuiTab);
        timer = new Timer(50, updateProBar);

    }

    @Override public void setInstrumentModel(DefaultTableModel instrumentModel) {
        this.instrumentModel = instrumentModel;

    }

    @Override public void setBuyerModel(DefaultTableModel buyerModel) {
        this.buyerModel = buyerModel;

    }

    @Override public void setSellerModel(DefaultTableModel sellerModel) {
        this.sellerModel = sellerModel;

    }

    @Override public DefaultTableModel getInstrumentModel() {
        return instrumentModel;
    }

    @Override public DefaultTableModel getBuyerModel() {
        return buyerModel;
    }

    @Override public DefaultTableModel getSellerModel() {
        return sellerModel;
    }

    @Override public String getBuildTrade() {
        LinkedTradeDataRow linkedTradeDataRow = new LinkedTradeDataRow(getInstrumentModel(),
                getBuyerModel(), getSellerModel());

        String lastMessage = null;

        if (!isConfigurationLoaded) {
            LOGGER.error("Missing Config - button \t" + tradingGuiTab.getButton1().getText());
            return null;
        }

        for (Object o : linkedTradeDataRow.getInstruments().entrySet()) {
            Map.Entry pair = (Map.Entry) o;

            tradeRequest.setFixmlMessage(getTemplate("trades/TradeMatchReport.xml"));
            trdMtchRptType = tradeRequest.getFixmlType();
            updateApplSecCtrl();

            TrdMtchRptType trdMtchSideType = tradeRequest.getFixmlType();
            InstrmtType instrmt = trdMtchSideType.getInstrmtMtchSide().getInstrmt();
            List<TrdMtchSideType> listSides = trdMtchSideType.getInstrmtMtchSide().getTrdMtchSide();

            HashMap instrumentData;

            instrumentData = (HashMap) linkedTradeDataRow.getInstruments().get(pair.getKey());

            instrmt.setMMY(String.valueOf(instrumentData.get("MMY")));
            instrmt.setSym(String.valueOf(instrumentData.get("Symbol")));
            instrmt.setStrkPx(String.valueOf(instrumentData.get("StkPrice")));
            instrmt.setPutCall("1");
            instrmt.setOptAt("0");
            trdMtchSideType.getInstrmtMtchSide().setLastPx(String.valueOf(instrumentData.get("Price")));

            String date = String.valueOf
                    (dataAccess.getBusinessDateForSymbol(String.valueOf(instrumentData.get("Symbol"))));
            setBusinessDate(date);

            trdMtchSideType.setTrdDt(tradeRequest.getBusinessDate());

            HashMap buyerData = (HashMap) linkedTradeDataRow.getBuyers().get(pair.getKey());

            listSides.get(0).setSideQty(String.valueOf(instrumentData.get("Qty")));
            List<PtyType> buyerSide = listSides.get(0).getPty();
            buyerSide.get(0).setID(String.valueOf(buyerData.get("Member")));
            buyerSide.get(2).setID(String.valueOf(buyerData.get("Clearer")));
            buyerSide.get(5).setID(String.valueOf(buyerData.get("Account")));

            HashMap sellerData = (HashMap) linkedTradeDataRow.getSeller().get(pair.getKey());
            listSides.get(1).setSideQty(String.valueOf(instrumentData.get("Qty")));
            List<PtyType> sellerSide = listSides.get(1).getPty();

            sellerSide.get(0).setID(String.valueOf(sellerData.get("Member")));
            sellerSide.get(2).setID(String.valueOf(sellerData.get("Clearer")));
            sellerSide.get(5).setID(String.valueOf(sellerData.get("Account")));
            qpidMsgService.sendOut(tradeRequest);
            lastMessage = tradeRequest.getFixmlMessage();

        }
        tradeRequest.setFixmlMessage(lastMessage);
        return null;
    }

    @Override public void setLatestAppSecNum(String secNum) {
        tradeRequest.setAppSecNum(secNum);

    }

    @Override public void updateApplSecCtrl() {
        tradeRequest.upDateApplseqCtrl();
    }

    @Override public void setBusinessDate(String businessDate) {
        tradeRequest.setBusinessDate(businessDate);
    }

    @Override public List<AmqpQueue> getListOfQueuesFromBrokerLike(String brokerType) {
        return null;
    }

    ActionListener updateProBar = new ActionListener() {

        public void actionPerformed(ActionEvent actionEvent) {
            int processValue = tradingGuiTab.getProgressBar().getValue();
            if (processValue >= 100) {
                timer.stop();
                return;
            }
            tradingGuiTab.getProgressBar().setValue(++processValue);
        }
    };

    @Override public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().contains("Press")) {
            if (timer.isRunning())
                timer.stop();
            timer.start();
            loadConfiguration(tradingGuiTab.getTextField1().getText());
            return;
        }
        if (e.getActionCommand().contains("Send")) {
            int totalTrades = Integer.parseInt(tradingGuiTab.getTextField2().getText());
            processTrades(totalTrades);
            return;
        }

        if (e.getActionCommand().contains("ShutDown")) {
            em.close();
            emf.close();
            dataAccess = null;
            if (!em.isOpen())
                LOGGER.info("Entity Manager Closed Successfully.");
            tradingGuiTab.getButton1().setText("Press To Start");
            tradingGuiTab.getButton1().setBackground(Color.GRAY);
            tradingGuiTab.getProgressBar().setValue(0);
            isConfigurationLoaded = false;
            timer.stop();
            return;
        }
        if (e.getActionCommand().contains("Start")) {
            if (timer.isRunning())
                timer.stop();
            timer.start();
            if (setConnection()) {
                stopStartButton = (JButton) e.getSource();
                stopStartButton.setText("Stop Connection");
                stopStartButton.setBackground(Color.GREEN);
                tradingGuiTab.getProgressBar().setValue(tradingGuiTab.getProgressBar().getMaximum());
                timer.stop();
                return;
            }
            timer.stop();

        }
        if (e.getActionCommand().contains("Stop")) {
            stopStartButton = (JButton) e.getSource();
            stopStartButton.setText("Start Connection");
            stopConnection();
            stopStartButton.setBackground(Color.GRAY);
            tradingGuiTab.getProgressBar().setValue(tradingGuiTab.getProgressBar().getMinimum());
            return;
        }

        LOGGER.info(e.getActionCommand() + "\t Button Pressed");

    }

    @Override public void stateChanged(ChangeEvent e) {
        if (e.getSource() instanceof JSpinner) {
            JSpinner spinner = (JSpinner) e.getSource();
            totalThreads = String.valueOf(spinner.getValue());
            LOGGER.info("Using Thread(s) " + String.valueOf(spinner.getValue()));

        }

    }

    @Override public void tableChanged(TableModelEvent e) {

        System.out.println(e.getColumn() + "--" + e.getLastRow() + "--" + e.getType());
        DefaultTableModel tableModel = (DefaultTableModel) e.getSource();
        Vector vector = tableModel.getDataVector();
        if (getInstrumentModel().getRowCount() != tableModel.getRowCount()) {
            System.out.println(
                    "Both mode don't match" + getInstrumentModel().getRowCount() + tableModel.getRowCount());
        }
        Boolean ff = false;
        int v = e.getType();
        if (v == TableModelEvent.UPDATE) {
            System.out.println("is an update only");
            ff = true;

        }

        //        if (e.getType() == TableModelEvent.UPDATE) {
        //            System.out.println("watch out for outof index error" + vector.get(e.getFirstRow()));
        //            System.out.println("update selecteed");
        //        }
    }

    private void loadConfiguration(String path) {
        processRequest(path);

    }

    void processTrades(int iteration) {
        LOGGER.info("Processing Iteration" + iteration);
        SwingWorker worker = new SwingWorker<Object, float[]>() {

            @Override
            protected void process(List<float[]> chunks) {
                float[] progress = chunks.get(chunks.size() - 1);
                tradingGuiTab.getProgressBar().setValue(Math.round(progress[0] * 100f));

            }

            @Override
            protected Object doInBackground() throws Exception {
                for (int i = 1; i <= iteration; i++) {
                    LOGGER.info("Processing Trades" + i);
                    getBuildTrade();
                    tradingGuiTab.getButton2().setBackground(Color.GREEN);
                    publish(new float[] {
                            getProgressCount(i, iteration),
                    });
                }

                return null;

            }

            @Override protected void done() {
                super.done();
                timer.stop();
                tradingGuiTab.getButton2().setBackground(Color.GRAY);
            }
        };
        worker.execute();

    }

    private void processRequest(String path) {

        SwingWorker<Object, String> swingWorker = new SwingWorker<Object, String>() {

            @Override protected Object doInBackground() throws Exception {
                try {
                    input = new FileInputStream(path);
                    prop.load(input);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                tradingGuiTab.getButton1().setBackground(Color.YELLOW);
                tradingGuiTab.getTextPane1().setText("Loading Started \n");
                tradingGuiTab.getButton1().setText("Loading EM...Pls Wait");
                emf = Persistence.createEntityManagerFactory("clearingLivePU", prop);
                em = emf.createEntityManager();
                dataAccess = new DataAccessImpl(em);
                setLatestAppSecNum(String.valueOf(dataAccess.getMaximumCount()));
                String date = String.valueOf(dataAccess.getBusinessDateForSymbol("ODAX"));
                LOGGER.info("Loading Date for Product to .." + date);
                setBusinessDate(date);
                updateInstrumentTable();
                updateBuyerTable();
                updateSellerTable();
                return null;
            }

            @Override
            protected void done() {
                LOGGER.info("Complete loading");
                isConfigurationLoaded = true;
                tradingGuiTab.getProgressBar().setValue(tradingGuiTab.getProgressBar().getMaximum());
                timer.stop();
                tradingGuiTab.getTextPane1().setText("Loading Complete \n");
                tradingGuiTab.getButton1().setBackground(Color.GREEN);
                tradingGuiTab.getButton1().setText("ShutDown Em");

            }
        };
        swingWorker.execute();

    }

    private void updateInstrumentTable() {
        List<ClearedInstrument> instr = dataAccess.getSingleClearedInstrument("ODAX");
        instrumentModel.setValueAt(instr.get(0).getSymbol(), 0, 0);
        instrumentModel.setValueAt(instr.get(0).getMmy(), 0, 1);
        instrumentModel.setValueAt(instr.get(0).getStrikePrice(), 0, 2);
        instrumentModel.setValueAt("100", 0, 3);
        instrumentModel.setValueAt(instr.get(0).getItmPrice().getPrice().doubleValue(), 0, 4);
        instrumentModel.fireTableDataChanged();

    }

    private void updateBuyerTable() {
        LOGGER.info("updating bueyer");
        Account account = dataAccess.getClearerFor("CBKFR").get(0);
        buyerModel.setValueAt(account.getOwner().getSymbol(), 0, 0);
        buyerModel.setValueAt(account.getSponsor().getSymbol(), 0, 1);
        buyerModel.setValueAt(account.getName(), 0, 2);
        buyerModel.setValueAt("Trade01", 0, 3);
        buyerModel.fireTableDataChanged();

    }

    private void updateSellerTable() {
        LOGGER.info("Updating seller");
        Account account = dataAccess.getClearerFor("TUBDU").get(0);
        sellerModel.setValueAt(account.getOwner().getSymbol(), 0, 0);
        sellerModel.setValueAt(account.getSponsor().getSymbol(), 0, 1);
        sellerModel.setValueAt(account.getName(), 0, 2);
        sellerModel.setValueAt("Trade01", 0, 3);
        sellerModel.fireTableDataChanged();
    }

    private String getTemplate(String templateType) {
        ReadTemplate template = new ReadTemplate(templateType);
        try {
            assert template.getTemplate() != null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            return template.getTemplate();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Boolean setConnection() {
        if (dataAccess == null) {
            LOGGER.info("error connecting");
            return false;
        }
        qpidMsgService.setBrokerUrl(dataAccess.getBrokerConnectionUrlFor(
                CommonSenderService.Broker.TRADE_IMPORT_FROM_T7));
        List<String> highValueQueues = getListOfQueue();
        Set<String> setUniqueArray = highValueQueues.stream().collect(Collectors.toSet());
        ArrayList<String> arrayList = new ArrayList<>(setUniqueArray);
        qpidMsgService.startConnection(Integer.parseInt(totalThreads), arrayList);
        return true;
    }

    private ArrayList<String> getListOfQueue() {
        ArrayList<String> updateQueueList = new ArrayList<>();
        String[] highValuesQueue = new String[] { "ODAX", "OESX", "FDAX", "FESX", "FGBL", "OGBL" };
        Integer rowCount = instrumentModel.getRowCount();
        for (int i = 0; i < rowCount; i++) {
            for (String queue : highValuesQueue) {

                if (queue.equals(String.valueOf(instrumentModel.getValueAt(i, 0)))) {
                    updateQueueList.add(String.format("broadcast.C7_TRADEFEEDRCV.RAPPIDD_Trade.%s", queue));
                    LOGGER.info("Checking and Updating high value Queue \t" + queue);
                    break;
                } else {
                    updateQueueList.add("broadcast.C7_TRADEFEEDRCV.RAPPIDD_Trade.RestProd");
                }
            }

        }
        return updateQueueList;
    }

    public void stopConnection() {
        qpidMsgService.stopConnection();

    }

    private float getProgressCount(int value, int max) {
        return (float) value / (float) max;
    }

}
