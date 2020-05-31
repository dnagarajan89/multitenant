package com.example.apigateway;

import static org.springframework.security.config.Customizer.withDefaults;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;


@EnableWebFluxSecurity
public class SecurityConfiguration {

	@Bean
	SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
		http
			.authorizeExchange(exchanges ->
				exchanges
						/* .pathMatchers(HttpMethod.GET, "/").hasAnyRole(roles) */
					.anyExchange().authenticated()
			)
			.oauth2ResourceServer(oauth2ResourceServer ->
				oauth2ResourceServer
					.jwt(withDefaults())
			);
		return http.build();
	}
}
