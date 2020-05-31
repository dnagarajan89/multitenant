package com.example.mongo.tenant.mongomultitenant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/multitenant")
public class TestMultitenantApi {
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	ClientRepository clientRespository;
	
	@GetMapping
	public String hello() {
		return "Hello World!!!";
	}
	
	@PostMapping(path = "/createUser")
	@ResponseBody
	public User createUser(@RequestBody User user) {
		return this.userRepository.save(user);
	}
	
	@PostMapping(path = "/createClient")
	@ResponseBody
	public Client createClient(@RequestBody Client client) {
		return this.clientRespository.save(client);
	}
	
	@GetMapping(path = "/user/{id}")
	public User getUser(@PathVariable("id") String userId) {
		return this.userRepository.getById(userId);
	}
	
	@GetMapping(path = "/client/{id}")
	public Client getClient(@PathVariable("id") String clientId) {
		return this.clientRespository.getByClientId(clientId);
	}
	

}
