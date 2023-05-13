package com.moviebookingapp.repository;

import com.moviebookingapp.entity.TicketInfo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketInfoRepository extends MongoRepository<TicketInfo, String> {

}
