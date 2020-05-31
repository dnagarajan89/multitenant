package com.demo.multitenant.multitenantreactive.config;

import java.net.UnknownHostException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.ReactiveMongoDatabaseFactory;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;

import com.mongodb.ConnectionString;

//@Configuration
public class MongoDBConfig {
	
	@Bean
    @Primary
    public ReactiveMongoDatabaseFactory reactiveMongoDatabaseFactory() throws UnknownHostException {
        return new MultitenantMongoDBFactory(new ConnectionString("mongodb://localhost:27017/demo"));
    }

    @Bean
    @Primary
    public ReactiveMongoTemplate reactiveMongoTemplate() throws UnknownHostException {
        return new ReactiveMongoTemplate(reactiveMongoDatabaseFactory());
    }

}
