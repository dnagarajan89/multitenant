package com.demo.multitenant.multitenantreactive.api;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import reactor.core.publisher.Mono;

public interface UserRepository extends ReactiveCrudRepository<User, String> {
	
	Mono<User> getById(String id);

}
