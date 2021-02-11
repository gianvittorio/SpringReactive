package com.gianvittorio.springreactive.dao.repository;


import com.gianvittorio.springreactive.domain.document.Item;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface ItemReactiveRepository extends ReactiveMongoRepository<Item, String> {
    Flux<Item> findByDescription(String description);
}
