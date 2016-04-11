package com.eurexchange.clear.frontend.dao;

import com.eurexchange.clear.common.service.CommonSenderService;
import com.eurexchange.clear.domain.AmqpQueue;

import java.util.HashMap;
import java.util.List;

public interface BrokerConnection {

    HashMap<String, String> getConnectionAndQueue(CommonSenderService.Broker _broker,
            String queueName);

    List<AmqpQueue> getMembersQueue();

    List<String> getListOfQueueForBroker(String brokerConnection);

    void setUpConnection(String connectionUrl);

    void setUpQueue(String queueName);
    
}
