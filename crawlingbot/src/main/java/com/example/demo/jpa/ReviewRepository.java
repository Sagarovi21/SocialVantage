package com.example.demo.jpa;

import org.springframework.data.repository.CrudRepository;

import com.example.demo.entity.Reviews;

public interface ReviewRepository extends CrudRepository<Reviews,Integer>{

}
