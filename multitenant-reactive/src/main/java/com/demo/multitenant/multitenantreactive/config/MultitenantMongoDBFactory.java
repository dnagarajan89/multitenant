package com.demo.multitenant.multitenantreactive.config;

import com.mongodb.ConnectionString;
import com.mongodb.reactivestreams.client.MongoDatabase;
import org.springframework.dao.DataAccessException;
import org.springframework.data.mongodb.core.SimpleReactiveMongoDatabaseFactory;
import reactor.core.publisher.Mono;

public class MultitenantMongoDBFactory extends SimpleReactiveMongoDatabaseFactory {
	

	public MultitenantMongoDBFactory(ConnectionString connectionString) {
		super(connectionString);
	}

	public MongoDatabase getMongoDatabase() {

		return super.getMongoDatabase();
	}

	public Mono<MongoDatabase> getMongoDatabaseMono() throws DataAccessException {
		return Mono
				.subscriberContext()
				.map(context -> context.get("tenantId"))
				.map(obj -> String.valueOf(obj))
				.map(dbName -> super.getMongoDatabase(dbName));
	}
}
