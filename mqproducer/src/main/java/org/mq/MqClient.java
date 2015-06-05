package org.mq;

import org.mqservice.model.MessageDto;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;

public class MqClient {
	private Connection connection;
	private Channel channel;
	private String requestQueueName = "rpc_queue";
	private String replyQueueName;
	private QueueingConsumer consumer;

	public MqClient() throws Exception {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");
		connection = factory.newConnection();
		channel = connection.createChannel();

		replyQueueName = channel.queueDeclare().getQueue();
		consumer = new QueueingConsumer(channel);
		channel.basicConsume(replyQueueName, true, consumer);
	}

	public MessageDto call(MessageDto messageDto) throws Exception {
		MessageDto response = null;
		String corrId = java.util.UUID.randomUUID().toString();

		BasicProperties props = new BasicProperties.Builder()
				.correlationId(corrId).replyTo(replyQueueName).build();

		channel.basicPublish("", requestQueueName, props,
				Serializer.serialize(messageDto));
		System.out.println("Message Sent:" + messageDto);

		while (true) {
			QueueingConsumer.Delivery delivery = consumer.nextDelivery();
			if (delivery.getProperties().getCorrelationId().equals(corrId)) {
				response = (MessageDto) Serializer.deserialize(delivery
						.getBody());
				System.out.println("Received Response: " + response);
				break;
			}
		}

		return response;
	}
	
	public static void main(String[] args) throws Exception {
		MqClient client = new MqClient();
		MessageDto messageDto = new MessageDto();
		messageDto.setName("Message Name");
		messageDto.setCounter(1);
		messageDto.setId(1L);
		messageDto.setMessage("My Message");
		client.call(messageDto);
	}

	public void close() throws Exception {
		connection.close();
	}
}
