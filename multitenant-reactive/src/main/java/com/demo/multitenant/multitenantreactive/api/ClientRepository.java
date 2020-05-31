package com.demo.multitenant.multitenantreactive.api;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import reactor.core.publisher.Mono;

public interface ClientRepository extends ReactiveCrudRepository<Client, String> {
	
	Mono<Client> getById(String id);

}
