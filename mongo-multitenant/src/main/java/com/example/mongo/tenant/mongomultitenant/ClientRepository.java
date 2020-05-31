package com.example.mongo.tenant.mongomultitenant;

import org.springframework.data.repository.CrudRepository;

public interface ClientRepository extends CrudRepository<Client, String> {
	
	Client getByClientId(String clientId);

}
