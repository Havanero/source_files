package com.eurexchange.clear.tradegui;

import com.eurexchange.clear.domain.AmqpQueue;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public interface TradeBuilderInterface extends ActionListener, ChangeListener, TableModelListener {

    void setInstrumentModel(DefaultTableModel instrumentModel);
    void setBuyerModel(DefaultTableModel buyerModel);
    void setSellerModel(DefaultTableModel sellerModel);

    DefaultTableModel getInstrumentModel();
    DefaultTableModel getBuyerModel();
    DefaultTableModel getSellerModel();
    String getBuildTrade();
    void setLatestAppSecNum(String secNum);
    void updateApplSecCtrl();
    void setBusinessDate(String businessDate);
    List<AmqpQueue> getListOfQueuesFromBrokerLike(String brokerType);

    @Override void actionPerformed(ActionEvent e);

    @Override void stateChanged(ChangeEvent e);

    @Override void tableChanged(TableModelEvent e);

}
