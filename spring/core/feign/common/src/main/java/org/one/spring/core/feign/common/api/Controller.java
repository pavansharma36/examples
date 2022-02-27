package org.one.spring.core.feign.common.api;

import org.one.spring.core.feign.common.ResponseObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("controller")
public interface Controller {

	@GetMapping("greetings")
	ResponseObject<String> greetings(@RequestParam(value = "name", required = false) String name);

	@PostMapping("greetings")
	ResponseObject<String> greetings();

}
