package com.tea.common.common.rabbitmq;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import com.tea.common.common.RedisLockImp;
import com.tea.common.spring.DefaultTransactionSynchronization;
import com.tea.common.util.Device;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import com.rabbitmq.client.ConnectionFactory;
import com.tea.common.base.constant.Constants;
import com.tea.common.common.IRedisService;
import com.tea.common.common.IZooKeeperConfig;
import com.tea.common.common.rabbitmq.exception.MQNotStartException;
import com.tea.common.common.rabbitmq.exception.RepeatedDefinitionListenerException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * @category mq核心服务
 * @author MegaX
 *
 */
public abstract class RabbitMqServiceImp implements IRabbitMqService {
	private static Log log = LogFactory.getLog(RabbitMqServiceImp.class);
	@Primary
	@Service
	private static class DefaultRabbitMqServiceImp extends  RabbitMqServiceImp
	{
		@Override
		public String getName() {
			return null;
		}
	}

	// --------------------------------
	@Autowired
	private IZooKeeperConfig zkConfig;
	@Autowired
	private ThreadPoolTaskExecutor taskExecutor;
	@Autowired
	private IRedisService redisService;

	// ---------------------------------
	private List<ConnectionFactory> factorys = new ArrayList<>();
	private RabbitMqSend rabbitMqSend;
	private RabbitMqRecv rabbitMqRecv;
	private boolean isInit = false;
	// -----------------------------------

	public abstract String getName();

	private final String getDeviceId()
	{
		String name = getName();
		if(StringUtils.isEmpty(name))
		{
			return Device.getDeviceId();
		}else
		{
			return Device.getDeviceId() + "-" + name;
		}
	}

	@PostConstruct
	protected synchronized void init() {
		if(isInit) return;
		
		log.debug("start RabbitMqServiceImp.............");
		String rabbitMQUrl = zkConfig.get(Constants.YunWei+"/RabbitMQ/RabbitMQUrl");
		List<HostAndPort> hosts = new ArrayList<>();
		if(StringUtils.isEmpty(rabbitMQUrl))
		{
			String rabbitMQHost = zkConfig.get(Constants.YunWei+"/RabbitMQ/RabbitMQHost");
			Integer rabbitMQHostPort = Integer.parseInt(zkConfig.get(Constants.YunWei+"/RabbitMQ/RabbitMQPort"));
			hosts.add(new HostAndPort( rabbitMQHost, rabbitMQHostPort));
		}else
		{
			hosts.addAll(HostAndPort.getAddresses(rabbitMQUrl));
		}		
		initMQService(hosts);
		isInit = true;
		log.debug("start RabbitMqServiceImp end.............");
	}

	private void initMQService(List<HostAndPort> hosts) {
		for(HostAndPort h : hosts)
		{
			log.debug("MQ Server ->" + h);
			ConnectionFactory factory = new ConnectionFactory();
			factory.setHost(h.host);
			factory.setPort(h.port);
			factory.setThreadFactory(taskExecutor);
			factory.setSharedExecutor(taskExecutor.getThreadPoolExecutor());
			factory.setConnectionTimeout(60000);
			factory.setRequestedHeartbeat(5);
			factorys.add(factory);
		}
		
		log.debug("factory init.....");
		startSend();
	}

	private void startSend() {
		log.debug("start Mq Send......");
		if (rabbitMqSend == null)
			rabbitMqSend = new RabbitMqSend(factorys,redisService,getDeviceId());
		rabbitMqSend.start();
	}

	@PreDestroy
	protected void destory() {
		log.debug("destory RabbitMqServiceImp.............");
		if (rabbitMqSend != null)
			rabbitMqSend.stop();
		if(rabbitMqRecv != null)
			rabbitMqRecv.stop();
		log.debug("destory RabbitMqServiceImp end.............");
	}

	// -----------------------------------------

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void sendMsg(String name, String Routingkey, String msg) throws MQNotStartException {
        sendMsgOnTransactional(name,Routingkey,msg);

	}
	private void sendMsgOnTransactional(String name, String Routingkey, String msg) throws MQNotStartException
    {
        if (!rabbitMqSend.isStart()) {
            throw new MQNotStartException();
        }
        TransactionSynchronizationManager.registerSynchronization(new DefaultTransactionSynchronization()
        {
            @Override
            public void afterCompletion(int status) {
                if(status == TransactionSynchronization.STATUS_COMMITTED)
                {
                    try {
                        rabbitMqSend.sendMsg(name,  Routingkey, msg);
                    } catch (MQNotStartException e) {
                        log.error("name -> " + name + ", Routingkey -> " + Routingkey + ", msg -> " + msg,e);
                    }
                }
            }
        });
    }

	@Override
    @Transactional(propagation = Propagation.REQUIRED)
	public void sendMsg(String name, String msg) throws MQNotStartException {
        sendMsgOnTransactional(name,null,msg);
	}


    @Override
    public void sendMsgNoTransactional(String name,String Routingkey, String msg) throws MQNotStartException
    {
        if (!rabbitMqSend.isStart()) {
            throw new MQNotStartException();
        }
        rabbitMqSend.sendMsg(name,  Routingkey, msg);
    }

    @Override
    public  void sendMsgNoTransactional(String name,String msg) throws MQNotStartException
    {
        if (!rabbitMqSend.isStart()) {
            throw new MQNotStartException();
        }
        rabbitMqSend.sendMsg(name,  null, msg);
    }



	@Override
	public synchronized void setMQListerner(String name, RabbitMqType type, String Routingkey, IRabbitMqMsgListener lis)
			throws RepeatedDefinitionListenerException {
		if(rabbitMqRecv == null)
		{
			rabbitMqRecv = new RabbitMqRecv(factorys,taskExecutor,redisService,getDeviceId());
		}
		rabbitMqRecv.setMQListerner(name, type, Routingkey, lis);
		
	}	
}
