package com.testpaper.demo.resources;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class SampleResource {
	// Remove this class

	@GetMapping("/sample")
	public String getSample() {
		return "Sample text";
	}
}


