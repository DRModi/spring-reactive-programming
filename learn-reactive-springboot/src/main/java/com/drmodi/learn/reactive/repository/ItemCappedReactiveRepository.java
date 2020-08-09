package com.drmodi.learn.reactive.repository;


import com.drmodi.learn.reactive.document.ItemCapped;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.mongodb.repository.Tailable;
import reactor.core.publisher.Flux;


public interface ItemCappedReactiveRepository extends ReactiveMongoRepository<ItemCapped, String> {

    @Tailable
    Flux<ItemCapped> findItemCappedBy();
}
