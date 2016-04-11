package com.eurexchange.clear.frontend.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.ejb.Local;
import javax.jms.Connection;
import javax.persistence.EntityManager;

import org.apache.qpid.qmf2.common.QmfException;
import org.apache.qpid.qmf2.console.Console;
import org.apache.qpid.qmf2.console.QmfConsoleData;
import org.apache.qpid.qmf2.util.ConnectionHelper;

import com.eurexchange.clear.common.service.CommonSenderService;
import com.eurexchange.clear.common.service.CommonSenderService.Broker;
import com.eurexchange.clear.dao.AmqpQueueDao;
import com.eurexchange.clear.dao.AmqpQueueDaoImpl;
import com.eurexchange.clear.dao.ConfigurationDao;
import com.eurexchange.clear.dao.ConfigurationDaoImpl;
import com.eurexchange.clear.domain.AmqpQueue;
import com.eurexchange.clear.systemconfig.service.SystemConfigService;
import com.eurexchange.clear.systemconfig.service.SystemConfigServiceImpl;

@Local(BrokerConnection.class)
public class BrokerConnectionImpl implements BrokerConnection {

    static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(BrokerConnectionImpl.class);

    private AmqpQueueDao amqpDao;
    SystemConfigService systemConfigService;
    ConfigurationDao configDao;

    public BrokerConnectionImpl(EntityManager em) {
        this.amqpDao = new AmqpQueueDaoImpl(em);
        this.configDao = new ConfigurationDaoImpl(em);
        this.systemConfigService = new SystemConfigServiceImpl(configDao);

    }

    @Override
    public HashMap<String, String> getConnectionAndQueue(Broker _broker, String queueName) {
        AmqpQueue queue = amqpDao.findByBrokerAndQueueName(
                _broker, queueName);
        queue.setUser("admin");
        queue.setPassword("admin");
        String brokerAddress =
                systemConfigService.getCachedStringConfigValue(queue.getBrokerId());

        setUpConnection(CommonSenderService.Broker.createUrl(queue, brokerAddress, 30));
        setUpQueue(queue.getQueueName());
        HashMap<String, String> queue_configuration = new HashMap<>();
        queue_configuration.put("url", CommonSenderService.Broker.createUrl(queue, brokerAddress, 30));
        queue_configuration.put("queue", queue.getQueueName());
        queue_configuration.put("destination_queue", queue.getDestination());
        return queue_configuration;

    }

    @Override
    public List<AmqpQueue> getMembersQueue() {
        return amqpDao.findMemberAmqpQueuesConfigForDirectionAndType("incoming", "request");
    }

    @Override
    public List<String> getListOfQueueForBroker(String brokerConnection) {
        Connection connection = ConnectionHelper.createConnection(brokerConnection);
        Console console = null;
        try {
            console = new Console();
        } catch (QmfException e) {
            e.printStackTrace();
        }
        try {
            assert console != null;
            console.addConnection(connection);
        } catch (QmfException e) {
            e.printStackTrace();
        }
        List<String> list_of_queues = new ArrayList<>();
        List<QmfConsoleData> queues = console.getObjects("org.apache.qpid.broker", "queue");
        for (QmfConsoleData q : queues) {
            String name = q.getStringValue("name");
            LOGGER.info("Loading Queue Info from the broker " + name);
            if (!name.contains("TempQueue"))
                list_of_queues.add(name);
        }
        return list_of_queues;
    }

    @Override
    public void setUpConnection(String connectionUrl) {

    }

    @Override
    public void setUpQueue(String queueName) {}

}
