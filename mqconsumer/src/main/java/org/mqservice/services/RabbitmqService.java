package org.mqservice.services;

import java.io.IOException;

import org.mqservice.model.JsonMessage;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.ShutdownSignalException;

@Service
public class RabbitmqService {

	private final static String QUEUE_NAME = "hello";
	

	public String handleMessage(JsonMessage jsonMessage) {
		System.out.println(jsonMessage);
		return "test1";
	}

	public void sendMessage() throws IOException {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();

		channel.queueDeclare(QUEUE_NAME, false, false, false, null);
		String message = "Hello World!";
		channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
		System.out.println(" [x] Sent '" + message + "'");

		channel.close();
		connection.close();
	}

	public void receiveMessage() throws IOException, ShutdownSignalException,
			ConsumerCancelledException, InterruptedException {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();

		channel.queueDeclare(QUEUE_NAME, false, false, false, null);
		System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

		QueueingConsumer consumer = new QueueingConsumer(channel);
		channel.basicConsume(QUEUE_NAME, true, consumer);

		while (true) {
			QueueingConsumer.Delivery delivery = consumer.nextDelivery();
			String message = new String(delivery.getBody());
			System.out.println(" [x] Received '" + message + "'");
		}
	}

	public static void main(String[] argv) throws IOException,
			ShutdownSignalException, ConsumerCancelledException,
			InterruptedException {
		AbstractApplicationContext ctx = new ClassPathXmlApplicationContext("context.xml");
		boolean inf = true;
		while(inf) {
			
		}
		ctx.destroy();
	}

	public static String getQueueName() {
		return QUEUE_NAME;
	}

}
