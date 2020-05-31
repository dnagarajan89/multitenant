package com.example.mongo.tenant.mongomultitenant;

import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, String> {
	
	User getById(String id);

}
