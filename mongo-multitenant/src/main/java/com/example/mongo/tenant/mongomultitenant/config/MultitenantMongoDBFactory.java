package com.example.mongo.tenant.mongomultitenant.config;

import org.springframework.data.mongodb.core.SimpleMongoClientDbFactory;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import com.mongodb.client.MongoDatabase;

public class MultitenantMongoDBFactory extends SimpleMongoClientDbFactory {
	

	public MultitenantMongoDBFactory(String connectionString) {
		super(connectionString);
	}
	
	@Override
	protected MongoDatabase doGetMongoDatabase(String dbName) {
		Object tenant = RequestContextHolder.getRequestAttributes().getAttribute("tenantId", RequestAttributes.SCOPE_REQUEST);
		return getMongoClient().getDatabase(String.valueOf(tenant));
	}


}
