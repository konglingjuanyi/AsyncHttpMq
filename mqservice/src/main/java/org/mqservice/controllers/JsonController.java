package org.mqservice.controllers;

import java.util.UUID;

import org.mqservice.model.MessageDto;
import org.mqservice.services.RabbitmqService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JsonController {

	@Autowired
	RabbitmqService rabbitmqService;

	@RequestMapping(value = "message", produces = { "application/json" }, consumes = "application/json")
	public MessageDto greeting(
			@RequestBody(required = true) MessageDto messageDto) {
		messageDto.setUuid(UUID.randomUUID().toString());
		System.out.println("In controller");
		messageDto = rabbitmqService.sendToQueue(messageDto);
		return messageDto;
	}

	public RabbitmqService getRabbitmqService() {
		return rabbitmqService;
	}

	public void setRabbitmqService(RabbitmqService rabbitmqService) {
		this.rabbitmqService = rabbitmqService;
	}

}
