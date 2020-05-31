package com.example.apigateway;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/test")
public class TestController {
	
	@Autowired
	ApplicationContext applicationContext;
	
	@GetMapping("/hello") 
	public Mono<String> test() {
		return ReactiveSecurityContextHolder.getContext().map(SecurityContext::getAuthentication).map(auth  -> {
			Jwt jwtToken = (Jwt)auth.getPrincipal();			
			return jwtToken;
		}).map(jwt -> jwt.getClaimAsString("crato_tenant_id"));
	}

}
