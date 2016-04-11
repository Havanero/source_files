package com.eurexchange.clear.frontend;

import com.eurexchange.clear.frontend.tradematch.obj.*;
import com.eurexchange.clear.frontend.trades.MarshallerUnmarshaller;
import com.eurexchange.clear.frontend.trades.Sides;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TradeRequest extends Sides{

    private MarshallerUnmarshaller marshallerUnmarshaller;
    private TrdMtchRptType trdMtchRptType;
    private String id, businessDate;
    private String appSecNum;
    static final Logger LOGGER = LoggerFactory.getLogger(TradeRequest.class);

    public TradeRequest(TrdMtchRptType trdMtchRptType) {
        this.trdMtchRptType = trdMtchRptType;
        this.marshallerUnmarshaller = new MarshallerUnmarshaller();
    }

    public void setFixmlMessage(String fixmlMessage) {

        this.trdMtchRptType = MarshallerUnmarshaller.unmrshallTradeMatchReport(fixmlMessage);

    }

    public TrdMtchRptType getFixmlType(){
        return trdMtchRptType;
    }

    public String getFixmlMessage() {
        return this.marshallerUnmarshaller.createFiXML(this.trdMtchRptType);
    }

    public void setTradeId(String id) {
        this.id = id;
    }

    public String getTradeId() {
        return id;
    }

    public void setAppSecNum(String appSecNum) {
        LOGGER.info("Setting AppSecNum to  {} ", appSecNum);
        this.appSecNum = appSecNum;
    }

    public String getAppSecNum() {
        return appSecNum;
    }

    public String getRoutingKey() {
        return createRoutingKey();
    }

    public void upDateApplseqCtrl(){
        String getApplSeqCtrl = trdMtchRptType.getApplSeqCtrl().getApplSeqNum().replaceFirst("^0+(?!$)", "");
        Integer increment = Integer.parseInt(getAppSecNum());
        increment++;
        String getApplSeqCtrl1 = trdMtchRptType.getApplSeqCtrl().getApplSeqNum().replaceFirst(getApplSeqCtrl,
                increment.toString());

        setAppSecNum(getApplSeqCtrl1);

        trdMtchRptType.getApplSeqCtrl().setApplSeqNum(getApplSeqCtrl1);
    }

    public void setUpApplseqCtrl() {
        System.out.println("ApplID: " + trdMtchRptType.getApplSeqCtrl().getApplID() +
                " ApplSeqNum: " + trdMtchRptType.getApplSeqCtrl().getApplSeqNum());
        String getApplSeqCtrl = trdMtchRptType.getApplSeqCtrl().getApplSeqNum().replaceFirst("^0+(?!$)", "");
        Integer increment = Integer.parseInt(getAppSecNum());
        increment++;
        String getApplSeqCtrl1 = trdMtchRptType.getApplSeqCtrl().getApplSeqNum().replaceFirst(getApplSeqCtrl,
                increment.toString());

        setAppSecNum(getApplSeqCtrl1);

        trdMtchRptType.getApplSeqCtrl().setApplSeqNum(getApplSeqCtrl1);
        List<TrdMtchSideType> trdMtchSide = trdMtchRptType.getInstrmtMtchSide().getTrdMtchSide();
//        for(TrdMtchSideType trdMtchSideType:trdMtchSide){
//            System.out.println("Side \t" + trdMtchSideType.getSide());
//            System.out.println("Clearing AccountType \t" +trdMtchSideType.getClrAcctTyp());
//            System.out.println("posEffect \t" + trdMtchSideType.getPosEfct());
//            System.out.println("position of member zero" + trdMtchSideType.getPty().get(0).getID());
//            System.out.println("position of member zero1" + trdMtchSideType.getPty().get(1).getID());
//            System.out.println("position of member zero2" + trdMtchSideType.getPty().get(2).getID());
//            System.out.println("position of member zero3" + trdMtchSideType.getPty().get(3).getID());
//            System.out.println("position of member zero4" + trdMtchSideType.getPty().get(4).getID());
//            System.out.println("position of member zero5" + trdMtchSideType.getPty().get(5).getID());
//
//            System.out.println("position of member 1" + trdMtchSideType.getPty().get(1).getR());
//            System.out.println("position of member 2" + trdMtchSideType.getPty().get(2).getSrc());
//            System.out.println("position of member 3" + trdMtchSideType.getPty().get(3).getValue());
//            List<PtyType> ptyType = trdMtchSideType.getPty();
//            for(PtyType ptyType1:ptyType){
//                System.out.println("Pty Source\t" + ptyType1.getSrc());
//                System.out.println("Pty R \t" + ptyType1.getR());
//                System.out.println("Pty ID \t" + ptyType1.getID());
//                System.out.println("Pty Value\t" + ptyType1.getValue());
//
//            }
//
//            System.out.println("qty \t" + trdMtchSideType.getSideQty());
//            System.out.println("trade Id \t" +trdMtchSideType.getTrdID());
//            System.out.println("TS \t" +trdMtchSideType.getTrdRegTS().getTS());
//
//        }
    }

    private String createRoutingKey() {
        InstrmtMtchSideType instrmtMtchSideType = trdMtchRptType.getInstrmtMtchSide();
        System.out.println("LastPx: " + instrmtMtchSideType.getLastPx());
        InstrmtType instr;
        instr = instrmtMtchSideType.getInstrmt();

        LocalDate datetime = LocalDate.parse(trdMtchRptType.getTrdDt(),
                DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        String formatted_date = datetime.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String nonZeros = trdMtchRptType.getApplSeqCtrl().getApplSeqNum();
        nonZeros = nonZeros.replaceFirst("^0+(?!$)", "");
        String appIdDigits = trdMtchRptType.getApplSeqCtrl().getApplID().replaceAll("[^0-9]", "");
        appIdDigits = appIdDigits.replaceFirst("^0+(?!$)", "");
        System.out.println(appIdDigits);

        String queue_name = "broadcast.RAPPIDD_Trade";

        return queue_name + "." + instr.getSym() + "." + trdMtchRptType.getApplSeqCtrl().getApplID()
                + "." + trdMtchRptType.getApplSeqCtrl().getApplSeqNum() + "." + appIdDigits + "." +
                formatted_date + "." + nonZeros;

    }

    public void setSides(Sides sides){
        List<Sides> sidesList = sides.getTradeSides();
        TrdMtchSideType trdMtchSideType = new TrdMtchSideType();

        for(Sides buyers:sidesList){

            System.out.println("sides + \t " +buyers.getSides());

        }


    }
    public String getBusinessDate(){
        return businessDate;
    }

    public void setBusinessDate(String businessDate) {
        this.businessDate = businessDate;
        LOGGER.info("Setting businessDate to  {} ", businessDate);
    }
}