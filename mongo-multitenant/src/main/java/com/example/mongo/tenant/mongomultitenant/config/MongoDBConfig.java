package com.example.mongo.tenant.mongomultitenant.config;

import java.net.UnknownHostException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
public class MongoDBConfig {
	
	@Bean
    @Primary
    public MongoDbFactory mongoDbFactory() throws UnknownHostException {
        return new MultitenantMongoDBFactory("mongodb://localhost:27017/demo");
    }

    @Bean
    @Primary
    public MongoTemplate mongoTemplate() throws UnknownHostException {
        return new MongoTemplate(mongoDbFactory());
    }

}
