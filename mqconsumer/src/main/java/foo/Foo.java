package foo;

import org.mqservice.model.MessageDto;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Foo {

	public static void main(String[] args) throws Exception {
		ConfigurableApplicationContext context = new ClassPathXmlApplicationContext(
				"application-context.xml");
		System.out.println("Hit enter to terminate");
		System.in.read();
		context.close();
	}

	public MessageDto handlePerson(MessageDto messageDto)
			throws InterruptedException {
		System.out.println("messageDto Object received.");
		System.out.println(messageDto);
		messageDto.setCounter(messageDto.getCounter() + 1);
		double d = Math.random();
		if (d < 0.4) {
			//Inducing 50ms delay 40% of the time;
			Thread.sleep(50);
			return messageDto;
		} else if (d < 0.7) {
			//Inducing 200ms delay 30% of the time;
			Thread.sleep(200);
			return messageDto;
		} else {
			//Sending no message 30% of the time;
			Thread.sleep(400);
			return null;
		}
	}

}