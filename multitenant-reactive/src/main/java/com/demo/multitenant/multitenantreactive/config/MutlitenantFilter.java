package com.demo.multitenant.multitenantreactive.config;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public class MutlitenantFilter implements WebFilter {

    static String TENANT_KEY = "tenantId";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
    	String tenantId = exchange.getRequest().getHeaders().getFirst(TENANT_KEY);
    	if(StringUtils.hasText(tenantId)) {
    		return chain.filter(exchange).subscriberContext(context -> context.put(TENANT_KEY, tenantId));
    	} else {
    		return chain.filter(exchange);
    	}
        
    }

}
