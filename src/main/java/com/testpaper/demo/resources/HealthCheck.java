package com.testpaper.demo.resources;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/healthcheck")
public class HealthCheck {
	// Remove this class

	@GetMapping
	public String getSample() {
		return "Healthcheck OK";
	}
}


