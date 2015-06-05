package org.mq;

import java.io.IOException;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.ShutdownSignalException;

public class MqUtil {

	public static final String QUEUE_NAME = "si.test.queue", EXCHANGE = "si.test.exchange", R_KEY = "si.test.binding";

	public static Channel channel = null;
	public static boolean created = false;

	public static void createConnection() throws IOException {
		if (!created) {
			ConnectionFactory factory = new ConnectionFactory();
			factory.setHost("localhost");
			Connection connection = factory.newConnection();
			channel = connection.createChannel();
			channel.exchangeDeclare(EXCHANGE, "direct", true);
			channel.queueDeclare(QUEUE_NAME, true, false, false, null);
			channel.basicQos(1);
			channel.queueBind(QUEUE_NAME, EXCHANGE, R_KEY);
			created = true;
		}
	}

	public static Object publish(Object object) throws IOException {
		createConnection();
		channel.basicPublish(EXCHANGE, R_KEY, null, Serializer.serialize(object));
		return null;
	}

	public static void main(String[] args) throws Exception {
		receiveMessage();
		System.out.println("Hit enter to terminate");
		System.in.read();
	}

	public static void receiveMessage() throws IOException, ShutdownSignalException, ConsumerCancelledException, InterruptedException {
		createConnection();
		boolean autoAck = false;
		channel.basicConsume(QUEUE_NAME, autoAck, "myConsumerTag", new DefaultConsumer(channel) {
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] bytes) throws IOException {
				String routingKey = envelope.getRoutingKey();
				String contentType = properties.getContentType();
				System.out.println(routingKey);
				System.out.println(contentType);
				long deliveryTag = envelope.getDeliveryTag();
				try {
					Object object = Serializer.deserialize(bytes);
					System.out.println(object);
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
				channel.basicAck(deliveryTag, false);
			}
		});
	}

}
