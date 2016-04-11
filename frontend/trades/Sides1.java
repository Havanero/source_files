//package com.eurexchange.clear.frontend.trades;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//
//public class Sides1 {
//
//    private String side;
//    private String member;
//    private String account;
//    private String symbol;
//    private String mmy;
//    private String product;
//    private List<Sides> allTradeSides = new ArrayList<>();
//    private List<Sides> buyerSide = new ArrayList<>();
//    private List<Sides> sellerSide = new ArrayList<>();
//    List<String> sides = new ArrayList<>();
//
//    public Sides1() {
//
//    }
//
//    public void setSide(String side) {
//        this.side = side;
//
//    }
//
//    public void setMember(String member) {
//        this.member = member;
//    }
//
//    public void setAccount(String account) {
//        this.account = account;
//    }
//
//    public void setSymbol(String sym){
//        this.symbol = sym;
//    }
//    public void setMmy(String mmy) {
//        this.mmy = mmy;
//    }
//
//    public void setProduct(String product) {
//        this.product = product;
//    }
//
//    public String getSide() {
//        return side;
//    }
//
//    public String getMember() {
//        return member;
//    }
//    public String getSymbol(){
//        return symbol;
//    }
//
//    public String getAccount() {
//        return account;
//    }
//
//    public String getMmy() {
//        return mmy;
//    }
//
//    public String getProduct() {
//        return product;
//    }
//
//    public void addSides(Sides tradeSides) {
//        sides.add(tradeSides.getSide());
//        sides.add(tradeSides.getMember());
//        sides.add(tradeSides.getAccount());
//        sides.add(tradeSides.getMmy());
//        sides.add(tradeSides.getSymbol());
//        sides.add(tradeSides.getProduct());
//
//    }
//
//    public List<String> getSides() {
//        return sides;
//    }
//
//    public void setAllTradeSides(Sides tradeSides) {
//        allTradeSides.add(tradeSides);
//    }
//
//    public void addBuyerSide(Sides tradeSides){
//        buyerSide.add(tradeSides);
//    }
//
//    public void addSellerSide(Sides tradeSides){
//        sellerSide.add(tradeSides);
//    }
//
//    public List<Sides>getBuyerSide(){
//        return buyerSide;
//    }
//    public List<Sides>getSellerSide(){
//        return sellerSide;
//    }
//
//    public List<Sides> getTradeSides() {
//        return allTradeSides;
//    }
//
//    @Override
//    public String toString() {
//        return member + " " + account + " " + mmy + " " + product + " " + symbol;
//    }
//
//    private HashMap<Integer, HashMap<String, String>> rowSide = new HashMap<>();
//
//    public void setRowSide(int rowNo, HashMap<String, String> dataRow) {
//        rowSide.put(rowNo, dataRow);
//
//    }
//
//    public HashMap getRowSide() {
//        return rowSide;
//    }
//
//}
//
