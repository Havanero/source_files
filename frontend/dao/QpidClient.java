package com.eurexchange.clear.frontend.dao;

import com.eurexchange.clear.frontend.ClearingRequest;
import org.apache.qpid.AMQException;
import org.apache.qpid.url.URLSyntaxException;

import javax.jms.JMSException;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public interface QpidClient {

    void setConnectionURL(String connectionURL);

    void SendMessage(String requestQueue, String message, String msg_count, String broker);

    List<String> getTradeConfirmation(String queueName);

    void ReadCILMessages(String queueName);

    void startConsumer(final String responseAddress) throws AMQException, URLSyntaxException, JMSException;

    void setDisconnectConsumer(boolean status);

    void setAmqpConnection(String connectionUrl, String brokerType) throws URLSyntaxException,
            AMQException;

    void setClearingMessagesParameters(ClearingRequest clearingRequest);

    void setLatestAppSecNum(String secNum);

    void setIncomingData(DefaultTableModel model);

}
