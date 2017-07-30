package com.tea.common.common.rabbitmq;

public interface IRabbitMqMsgListener {

	void handleMsg(String name, String routingkey,String msg) throws Exception;
	
}
