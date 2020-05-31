package com.demo.multitenant.multitenantreactive.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/sample")
public class SampleController {
	
	private UserRepository userRepository;
	private ClientRepository clientRespository;
	
	@Autowired
	public SampleController(UserRepository userRepository, ClientRepository clientRespository) {
		this.userRepository = userRepository;
		this.clientRespository = clientRespository;
	}
	
	@PostMapping("/createUser")
	@ResponseBody
	public Mono<User> createUser(@RequestBody User user) {
		return this.userRepository.save(user);
	}
	
	@GetMapping("/user/{id}")
	public Mono<User> getUserById(@PathVariable("id") String id) {
		return this.userRepository.getById(id);
	}
	
	@GetMapping("/client/{id}")
	public Mono<Client> getClientById(@PathVariable("id") String id) {
		return this.clientRespository.getById(id);
	}
	
	@PostMapping("/createClient")
	@ResponseBody
	public Mono<Client> createClient(@RequestBody Client client) {
		return this.clientRespository.save(client);
	}

}
