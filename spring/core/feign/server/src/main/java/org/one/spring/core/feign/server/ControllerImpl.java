package org.one.spring.core.feign.server;

import java.util.Objects;

import org.one.spring.core.feign.common.ResponseObject;
import org.one.spring.core.feign.common.api.Controller;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ControllerImpl implements Controller {

	@Override
	public ResponseObject<String> greetings(final String name) {
		return new ResponseObject<String>(String.format("Hello %s", Objects.requireNonNullElse(name, "World")));
	}

	@Override
	public ResponseObject<String> greetings() {
		return null;
	}

}
