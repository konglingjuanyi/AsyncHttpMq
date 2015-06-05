package org.mq;

import org.mqservice.model.MessageDto;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;

public class MqServer {
	private static final String RPC_QUEUE_NAME = "rpc_queue";

	public MqServer() {

	}

	public static void main(String[] args) throws Exception {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");

		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();

		channel.queueDeclare(RPC_QUEUE_NAME, false, false, false, null);

		channel.basicQos(1);

		QueueingConsumer consumer = new QueueingConsumer(channel);
		channel.basicConsume(RPC_QUEUE_NAME, false, consumer);

		System.out.println(" [x] Awaiting RPC requests");

		while (true) {
			QueueingConsumer.Delivery delivery = consumer.nextDelivery();

			BasicProperties props = delivery.getProperties();
			BasicProperties replyProps = new BasicProperties.Builder().correlationId(props.getCorrelationId()).build();

			MessageDto messageDto = (MessageDto) Serializer.deserialize(delivery.getBody());

			System.out.println("Received Object in consumer:" + messageDto);

			channel.basicPublish("", props.getReplyTo(), replyProps, updateDto(messageDto));

			channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
		}
	}

	public static byte[] updateDto(MessageDto messageDto) throws Exception {
		messageDto.setName(messageDto.getName() + " Consumed");
		messageDto.setCounter(messageDto.getCounter() + 1);
		byte[] response = Serializer.serialize(messageDto);
		double d = Math.random();
		if (d < 0.4) {
			// Inducing 50ms delay 40% of the time;
			Thread.sleep(50);
			return response;
		} else if (d < 0.7) {
			// Inducing 200ms delay 30% of the time;
			Thread.sleep(200);
			return response;
		} else {
			// Sending no message 30% of the time;
			Thread.sleep(400);
			return null;
		}
	}
}
