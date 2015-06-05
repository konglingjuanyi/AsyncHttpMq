package org.mq;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import javax.servlet.AsyncContext;
import javax.servlet.ServletResponse;

import org.mqservice.model.MessageDto;

import com.fasterxml.jackson.databind.ObjectMapper;

public class AsyncRequestProcessor implements Runnable {

	private AsyncContext asyncContext;

	public AsyncRequestProcessor() {
	}

	public AsyncRequestProcessor(AsyncContext asyncCtx) {
		this.asyncContext = asyncCtx;
	}

	@Override
	public void run() {
		System.out.println("Async Supported? " + asyncContext.getRequest().isAsyncSupported());
		try {
			processRequest();
		} catch (Exception e) {
			e.printStackTrace();
		}
		// complete the async request processing
		asyncContext.complete();
	}

	protected void processRequest() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		MessageDto requestDto = requestToDto(mapper);
		ServletResponse response = asyncContext.getResponse();
		response.setContentType("application/json");
		try {
			MessageDto responseDto = sendMessage(requestDto);
			mapper.writeValue(response.getOutputStream(), responseDto);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected MessageDto requestToDto(ObjectMapper mapper) throws Exception {
		BufferedReader br = new BufferedReader(new InputStreamReader(asyncContext.getRequest().getInputStream()));
		String json = "";
		if (br != null) {
			json = br.readLine();
		}
		MessageDto message = mapper.readValue(json, MessageDto.class);
		return message;
	}

	protected MessageDto sendMessage(MessageDto messageDto) throws Exception {
		MqClient client = new MqClient();
		return client.call(messageDto);
	}
}