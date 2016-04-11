package com.eurexchange.clear.tradegui;

import javax.swing.table.DefaultTableModel;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Vector;

public class LinkedTradeDataRow  {
    private  DefaultTableModel instrument = new DefaultTableModel();
    private DefaultTableModel buyer = new DefaultTableModel();
    private DefaultTableModel seller = new DefaultTableModel();
    private LinkedHashMap<String, HashMap<String, String>> instrumentRow = new LinkedHashMap<>();
    private LinkedHashMap<String, HashMap<String, String>> buyerRow = new LinkedHashMap<>();
    private LinkedHashMap<String, HashMap<String, String>> sellerRow = new LinkedHashMap<>();


    public LinkedTradeDataRow(DefaultTableModel instrument, DefaultTableModel buyer, DefaultTableModel seller){
        this.instrument = instrument;
        this.buyer = buyer;
        this.seller = seller;

        processData();
        processBuyer();
        processSeller();
    }
    private void processData(){
        Vector vector = instrument.getDataVector();
        Enumeration enumeration = vector.elements();
        int cols, rows;
        rows = 0;

        while (enumeration.hasMoreElements()) {
            enumeration.nextElement();
            Vector row = (Vector) vector.elementAt(rows);
            LinkedHashMap<String,String> instrumentRowData = new LinkedHashMap<>();
            for (cols = 0; cols < instrument.getColumnCount(); cols++) {
                instrumentRowData.put(instrument.getColumnName(cols), String.valueOf(row.get(cols)));
            }
            instrumentRow.put(String.valueOf(rows),instrumentRowData);
            rows++;
        }

    }

    private void processBuyer(){
        Vector vector = buyer.getDataVector();
        Enumeration enumeration = vector.elements();
        int cols, rows;
        rows = 0;

        while (enumeration.hasMoreElements()) {
            enumeration.nextElement();
            Vector row = (Vector) vector.elementAt(rows);
            LinkedHashMap<String,String> buyerRowData = new LinkedHashMap<>();
            for (cols = 0; cols < buyer.getColumnCount(); cols++) {
                buyerRowData.put(buyer.getColumnName(cols), String.valueOf(row.get(cols)));
            }
            buyerRow.put(String.valueOf(rows),buyerRowData);
            rows++;
        }


    }
    private void processSeller(){
        Vector vector = seller.getDataVector();
        Enumeration enumeration = vector.elements();
        int cols, rows;
        rows = 0;

        while (enumeration.hasMoreElements()) {
            enumeration.nextElement();
            Vector row = (Vector) vector.elementAt(rows);
            LinkedHashMap<String,String> sellerRowData = new LinkedHashMap<>();
            for (cols = 0; cols < seller.getColumnCount(); cols++) {
                sellerRowData.put(seller.getColumnName(cols), String.valueOf(row.get(cols)));
            }
            sellerRow.put(String.valueOf(rows),sellerRowData);
            rows++;
        }

    }
    public LinkedHashMap getInstruments(){
        return instrumentRow;
    }

    public LinkedHashMap getBuyers(){
        return buyerRow;
    }
    public LinkedHashMap getSeller(){
        return sellerRow;
    }

}
