package com.drmodi.learn.reactive.util;

import com.drmodi.learn.reactive.document.Item;
import com.drmodi.learn.reactive.document.ItemCapped;
import com.drmodi.learn.reactive.repository.ItemCappedReactiveRepository;
import com.drmodi.learn.reactive.repository.ItemReactiveRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.List;

@Component
@Profile("!test")
@Slf4j
public class ItemDataInitializer implements CommandLineRunner {

    @Autowired
    ItemReactiveRepository itemReactiveRepository;

    @Autowired
    ReactiveMongoOperations reactiveMongoOperations;

    @Autowired
    ItemCappedReactiveRepository itemCappedReactiveRepository;

    @Override
    public void run(String... args) throws Exception {
        initializeAllItems();
        initializeItemCappedCollection();
    }

    private void initializeItemCappedCollection() {
        reactiveMongoOperations.dropCollection(ItemCapped.class);
        reactiveMongoOperations.createCollection(ItemCapped.class, CollectionOptions.empty().maxDocuments(20).size(50000).capped());
        initializeCappedItemDataSetup();
    }

    private void initializeCappedItemDataSetup() {
        Flux<ItemCapped> itemCappedFlux = Flux.interval(Duration.ofSeconds(1))
                .map(i -> new ItemCapped(null, "Random Item - " + i, 100.00 + i));

        itemCappedReactiveRepository.insert(itemCappedFlux)
                .subscribe(itemCapped -> {
                   log.info("Inserted Capped Item is {}", itemCapped);
                });
    }

    private List<Item> itemList(){
        return List.of(new Item(null, "Apple iPhone", 1000.0),
                new Item(null, "Apple iMac", 2500.0),
                new Item(null, "Apple Watch", 850.0),
                new Item("111", "Apple Airpod", 250.0));
    }

    private void initializeAllItems() {
        itemReactiveRepository.deleteAll()
                .thenMany(Flux.fromIterable(itemList()))
                .flatMap(itemReactiveRepository::save)
                .thenMany(itemReactiveRepository.findAll())
                .subscribe((item -> {
                    System.out.println("Item inserted from CommandLineRunner : "+item);
                }));
    }
}
