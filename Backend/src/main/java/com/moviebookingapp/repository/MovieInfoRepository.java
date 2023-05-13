package com.moviebookingapp.repository;

import com.moviebookingapp.dto.CompositeId;
import com.moviebookingapp.entity.MovieInfo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MovieInfoRepository extends MongoRepository<MovieInfo, CompositeId> {
    @Query("{'_id.movieName':?0}")
    Optional<List<MovieInfo>> findByMovie(String movieName);
}
