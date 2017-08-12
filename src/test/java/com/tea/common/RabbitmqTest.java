package com.tea.common;


import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;

public class RabbitmqTest {
   private static final String QUEUE_NAME = "hello";
   
   public static void main(String [] args) throws Exception{
	   ConnectionFactory factory = new ConnectionFactory();
	   factory.setHost("192.168.1.103");
	   factory.setPort(5672);
	   factory.setUsername("admin");
	   factory.setPassword("admin");
	   
	   Connection conn = factory.newConnection();
	   Channel channel = conn.createChannel();
	   channel.queueDeclare(QUEUE_NAME,false,false,false,null);
	   
	   String msg = "hell,ganbo!";
	   channel.basicPublish("", QUEUE_NAME, null,msg.getBytes());
	   System.out.println("sent msg:" + msg);
	   
	   channel.close();
	   conn.close();
	   
	   ConnectionFactory factory = new ConnectionFactory();  
	   
       factory.setUsername("admin");  
       factory.setPassword("admin");  
       factory.setHost("192.168.1.103");  
       factory.setVirtualHost("/");   
       factory.setPort(5672);  
       Connection connection = factory.newConnection();  
       Channel channel = connection.createChannel();  
       channel.queueDeclare(QUEUE_NAME, false, false, false, null);  
       System.out.println(" [*] Waiting for messages. To exit press CTRL+C");  
       QueueingConsumer consumer = new QueueingConsumer(channel);  
       channel.basicConsume(QUEUE_NAME, true, consumer);  
       while(true){  
           QueueingConsumer.Delivery delivery = consumer.nextDelivery();  
           String message = new String(delivery.getBody());  
           System.out.println(" [x] Received '" + message + "'");  
       }  
	   
   }
}
