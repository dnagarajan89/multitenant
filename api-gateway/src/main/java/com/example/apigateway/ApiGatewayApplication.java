package com.example.apigateway;

import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

@SpringBootApplication
public class ApiGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiGatewayApplication.class, args);
	}
	
	@Bean
	public RouteLocator routes(RouteLocatorBuilder builder) {
		return builder.routes().route("test", r -> r.path("/rest/**")
				.uri("http://localhost:8061")).build();
	}

	@Component
	public class LoggingGlobalPreFilter implements GlobalFilter {
		final org.slf4j.Logger logger = LoggerFactory.getLogger(LoggingGlobalPreFilter.class);		
		@Override
		public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
			logger.info("Global Pre Filter executed");								
			return ReactiveSecurityContextHolder.getContext().map(SecurityContext::getAuthentication).map(auth  -> {
				Jwt jwtToken = (Jwt)auth.getPrincipal();			
				return jwtToken;
			}).map(jwt -> jwt.getClaimAsString("crato_tenant_id")).map(tenantId -> {
				 exchange.getRequest().mutate().header("crato_tenant_id", tenantId).build();
				 return exchange;
			})
	        .flatMap(chain::filter);
		}
	}


}
