package org.one.spring.core.security.jwt.controllers;

import java.util.Collections;
import java.util.Map;
import javax.servlet.Filter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("greetings")
public class GreetingsController {

	private static final Logger LOGGER = LoggerFactory.getLogger(GreetingsController.class);

	@Autowired
	private SecurityFilterChain filterChain;

	@GetMapping
	public Map<String, String> get() {
		LOGGER.info("Security context : {}", SecurityContextHolder.getContext().getAuthentication());
		for (final Filter filter : filterChain.getFilters()) {
			LOGGER.info("Filter in chain : {}", filter.getClass());
		}
		return Collections.singletonMap("message", "Hello World!!!");
	}

}
