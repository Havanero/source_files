package com.eurexchange.clear.tradegui;

import com.eurexchange.clear.frontend.TradeRequest;
import org.apache.qpid.client.AMQConnection;

import java.util.ArrayList;

public interface QpidMsgService {

    void setBrokerUrl(String url);

    String getBrokerUrl();

    void setBrokerConnection(AMQConnection connection);

    AMQConnection getBrokerConnection();

    void startConnection(final int threadNo, final ArrayList<String> list_of_queues);

    void stopConnection();

    void sendOut(TradeRequest tradeRequest);

}
