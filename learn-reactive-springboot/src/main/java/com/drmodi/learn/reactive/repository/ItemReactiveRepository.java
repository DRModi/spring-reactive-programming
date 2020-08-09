package com.drmodi.learn.reactive.repository;

import com.drmodi.learn.reactive.document.Item;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ItemReactiveRepository extends ReactiveMongoRepository<Item, String> {

    //Custom method created
    Mono<Item> findByDescription(String description);
}
