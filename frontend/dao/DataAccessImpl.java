package com.eurexchange.clear.frontend.dao;

import com.eurexchange.clear.common.service.CommonSenderService;
import com.eurexchange.clear.common.value.ClearConstants;
import com.eurexchange.clear.dao.*;
import com.eurexchange.clear.domain.*;
import com.eurexchange.clear.domain.types.EnumListingStatusClearedInstrument;
import com.eurexchange.clear.systemconfig.service.SystemConfigService;
import com.eurexchange.clear.systemconfig.service.SystemConfigServiceImpl;
import org.hibernate.Query;
import org.hibernate.Session;
import org.joda.time.LocalDate;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

public class DataAccessImpl {

    private static final String FIND_AMQP_BROKER_QUERY =
            "select a from AmqpQueue a where a.brokerId = :brokerId";
    private TradeImportDao tradeImportDao;
    private ClearingProductDao clearingProductDao;
    private AmqpQueueDao amqpQueueDao;
    private EntityManager entityManager;
    private SystemConfigService systemConfigService;

    public DataAccessImpl(EntityManager em) {
        tradeImportDao = new TradeImportDaoImpl(em);
        clearingProductDao = new ClearingProductDaoImpl(em);
        amqpQueueDao = new AmqpQueueDaoImpl(em);
        ConfigurationDao configDao = new ConfigurationDaoImpl(em);
        systemConfigService = new SystemConfigServiceImpl(configDao);
        this.entityManager = em;

        for (AmqpQueue amqpQueue : getQueueListFor("TRADE")) {
            System.out.println("Getting Queue for TRADE");
            System.out.println(amqpQueue.getDestination());
        }
    }

    public String getBrokerConnectionUrlFor(CommonSenderService.Broker broker) {
        AmqpQueue queue = getBrokerConnection(broker);
        assert queue != null;
        String brokerAddress =
                systemConfigService.getCachedStringConfigValue(queue.getBrokerId());
        return String.format(ClearConstants.BROKER_CONNECTION_STRING, "admin",
                "admin",
                brokerAddress, "5");
    }

    private AmqpQueue getBrokerConnection(CommonSenderService.Broker broker) {
        Session session = ((Session) entityManager.getDelegate());
        Query query = session.createQuery(FIND_AMQP_BROKER_QUERY);
        query.setParameter("brokerId", broker.getBrokerId());
        query.setCacheable(true);
        return (query.list().isEmpty() ? null : (AmqpQueue) query.list().get(0));
    }

    public List<Account> getClearerFor(String symbol) {
        try {
            TypedQuery<Account> query =
                    entityManager.createQuery("Select distinct a FROM Account a "
                                    + "inner join a.owner rp where rp.symbol =:Symbol",
                            Account.class).setFirstResult(0).setParameter("Symbol", symbol);
            return query.getResultList();
        } catch (NoResultException no) {
            System.out.println("Error " + no.getLocalizedMessage());
            return null;
        }

    }

    public List<ClearedInstrument> getSingleClearedInstrument(String symbol) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<ClearedInstrument> query = cb.createQuery(ClearedInstrument.class);
        Root<ClearedInstrument> root = query.from(ClearedInstrument.class);
        query.where(cb.equal(root.get(ClearedInstrument_.status),
                EnumListingStatusClearedInstrument.ACTIVE),
                cb.and(cb.equal(root.get(ClearedInstrument_.symbol), symbol),
                        cb.and(cb.isNotNull(root.get(ClearedInstrument_.mmy)))));
        TypedQuery<ClearedInstrument> typedQuery = entityManager.createQuery(query);
        try {
            return typedQuery.getResultList();
        } catch (NoResultException noRecordsEx) {
            return null;
        }

    }

    public List<AmqpQueue> getQueueListFor(String brokerType) {
        ParamBuilder map = ParamBuilder.create("ID", String.format("%s%s%s", "%", brokerType, "%"));
        return amqpQueueDao.findByQuery("select a from AmqpQueue a where a.brokerId like :ID", map);
    }

    public long getMaximumCount() {
        List<TradeImport> tradeImport = tradeImportDao.findByQuery("select t from TradeImport t where "
                + "t.applicationSequenceNumber = (select max(x.applicationSequenceNumber) "
                + "from TradeImport x)");

        if (tradeImport.size() != 0) {
            return tradeImport.get(0).getApplicationSequenceNumber();
        }

        return 0;
    }

    public String getBusinessDateForProduct() {

        return String.valueOf(clearingProductDao.findMinimumBusinessDate());
    }

    public LocalDate getBusinessDateForSymbol(String symbol) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<LocalDate> query = cb.createQuery(LocalDate.class);
        Root<ClearingProduct> root = query.from(ClearingProduct.class);
        query.select(cb.least(root.get(ClearingProduct_.businessDate))).where(cb.equal(
                root.get(ClearingProduct_.symbol), symbol));
        return entityManager.createQuery(query).getSingleResult();
    }

}
