package com.example.ecommerce.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.ecommerce.model.Client;

public interface ClientRepository extends MongoRepository<Client, String> {
}