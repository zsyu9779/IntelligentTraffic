package com.manager.traffic.dao.onetoone_dispache;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;

public class Recv {
	public static final String queue_name = "my_queue";
	public static final boolean autoAck = false;
	public static final boolean durable = true;

	public static void main(String[] args) throws java.io.IOException,
			InterruptedException {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");
//		factory.setVirtualHost("my_mq");
//		factory.setUsername("aaa");
//		factory.setPassword("bbb");
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();
		channel.queueDeclare(queue_name, durable, false, false, null);
		System.out.println("Wait for message");
		// 消息分发处理
		channel.basicQos(1); 
		QueueingConsumer consumer = new QueueingConsumer(channel);
		channel.basicConsume(queue_name, autoAck, consumer);
		while (true) {
			Thread.sleep(500);
			QueueingConsumer.Delivery deliver = consumer.nextDelivery();
			String message = new String(deliver.getBody());
			System.out.println("Message received:" + message);
			channel.basicAck(deliver.getEnvelope().getDeliveryTag(), false);
		}
	}
}
