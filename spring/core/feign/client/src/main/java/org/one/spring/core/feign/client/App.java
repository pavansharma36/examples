package org.one.spring.core.feign.client;

import org.one.spring.core.feign.common.ResponseObject;
import org.one.spring.core.feign.common.api.Controller;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class App {

	public static void main(final String[] args) {
		final Controller controller = ClientBuilder.buildClient(Controller.class, "http://localhost:8080");

		final ResponseObject<String> response = controller.greetings("test feign");
		log.info("Response from server {}", response);
	}
}
