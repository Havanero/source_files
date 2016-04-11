package com.eurexchange.clear.frontend.trades;

import javax.swing.table.DefaultTableModel;

public class TradeDataTable extends DefaultTableModel {

    public TradeDataTable(DefaultTableModel model) {

        setUpTradeData(model);

    }

    private void setUpTradeData(DefaultTableModel model) {
        model.addColumn("Side");
        model.addColumn("Member");
        model.addColumn("Clearer");
        model.addColumn("Account");
        model.addColumn("Sym");
        model.addColumn("MMY");
        model.addColumn("StrkPx");
        model.addColumn("OptAt");
        model.addColumn("PutCall");
        model.addColumn("Qty");
        model.addRow(
                new Object[] { "Seller", "CBKFR", "CBKFR", "A1", "OGBL", "201401", "101", "0", "1", "2" });
        model.addRow(
                new Object[] { "Buyer", "TUBDU", "TUBDU", "A1", "OGBL", "201401", "101", "0", "1", "2" });

    }

}
