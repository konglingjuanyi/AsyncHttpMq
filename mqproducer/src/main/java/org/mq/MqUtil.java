package org.mq;

import java.io.IOException;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.ShutdownSignalException;

public class MqUtil {

	public static final String QUEUE_NAME = "si.test.queue", EXCHANGE = "si.test.exchange", R_KEY = "si.test.binding";

	public static Channel channel = null;
	public static boolean created = false;
	public static String replyQueueName;
	private static QueueingConsumer consumer;

	public static void createConnection() throws IOException {
		if (!created) {
			ConnectionFactory factory = new ConnectionFactory();
			factory.setHost("localhost");
			Connection connection = factory.newConnection();
			channel = connection.createChannel();

			replyQueueName = channel.queueDeclare().getQueue();
			consumer = new QueueingConsumer(channel);
			channel.basicConsume(replyQueueName, true, consumer);

			channel.exchangeDeclare(EXCHANGE, "direct", true);
			channel.queueDeclare(QUEUE_NAME, true, false, false, null);
			channel.queueBind(QUEUE_NAME, EXCHANGE, R_KEY);
			created = true;
		}
	}

	public static Object publish(Object object) throws IOException, ShutdownSignalException, ConsumerCancelledException, InterruptedException,
			ClassNotFoundException {
		createConnection();
		System.out.println("publish");
		String corrId = java.util.UUID.randomUUID().toString();

		BasicProperties props = new BasicProperties.Builder().correlationId(corrId).replyTo(replyQueueName).build();
		channel.basicPublish(EXCHANGE, R_KEY, props, Serializer.serialize(object));

		while (true) {
			QueueingConsumer.Delivery delivery = consumer.nextDelivery();
			if (delivery.getProperties().getCorrelationId().equals(corrId)) {
				Object response = Serializer.deserialize(delivery.getBody());
				System.out.println("response" + response);
				break;
			}
		}
		return null;
	}

}
