package com.example.mongo.tenant.mongomultitenant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Document
@Data
public class Client {
	
	@Id
	private String clientId;
	private String clientName;

}
