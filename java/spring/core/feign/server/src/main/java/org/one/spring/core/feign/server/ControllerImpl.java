package org.one.spring.core.feign.server;

import org.one.spring.core.feign.common.ResponseObject;
import org.one.spring.core.feign.common.api.Controller;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ControllerImpl implements Controller {

	@Override
	public ResponseObject<String> greetings(String name) {
		return new ResponseObject<String>(String.format("Hello %s", name == null ? "World" : name));
	}

	@Override
	public ResponseObject<String> greetings() {
		return null;
	}

}
