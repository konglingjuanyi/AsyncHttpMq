package org.mqproducer.controller;

import java.io.IOException;
import java.util.concurrent.ThreadPoolExecutor;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mq.AppAsyncListener;
import org.mq.AsyncRequestProcessor;

@WebServlet(urlPatterns = "/message", asyncSupported = true)
public class JSONServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		final AsyncContext ac = request.startAsync();
		ac.addListener(new AppAsyncListener());
		ac.setTimeout(300);

		ThreadPoolExecutor executor = (ThreadPoolExecutor) request.getServletContext().getAttribute("executor");

		executor.execute(new AsyncRequestProcessor(ac));

	}
}