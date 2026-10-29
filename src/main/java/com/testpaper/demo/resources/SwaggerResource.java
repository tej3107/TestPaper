package com.testpaper.demo.resources;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class SwaggerResource implements ErrorController {

	@GetMapping("/")
	public String login() {
		return "redirect:/swagger-ui/index.html";
	}

	@RequestMapping("/error")
	public ResponseEntity<String> handleError(HttpServletRequest request) {
		Object attr = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
		HttpStatus status = attr instanceof Integer code && HttpStatus.resolve(code) != null
				? HttpStatus.valueOf(code) : HttpStatus.INTERNAL_SERVER_ERROR;
		String body = status == HttpStatus.NOT_FOUND ? "Page not found!!!" : status.getReasonPhrase();
		return ResponseEntity.status(status).body(body);
	}
}


