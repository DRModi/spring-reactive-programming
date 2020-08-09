package com.drmodi.learn.reactive.repository;

import com.drmodi.learn.reactive.document.Item;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

@DataMongoTest
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DirtiesContext
public class ItemReactiveRepositoryTest {

    @Autowired
    ItemReactiveRepository itemReactiveRepository;

    List<Item> items = List.of(new Item(null, "Apple iPhone", 1000.0),
            new Item(null, "Apple iMac", 2500.0),
            new Item(null, "Apple Watch", 850.0),
            new Item("111", "Apple Airpod", 250.0),
            new Item("1111", "Apple iCar", 55000.0));


    @BeforeAll
    public void setUp() {
        //clear all existing data
        itemReactiveRepository.deleteAll()
                .thenMany(Flux.fromIterable(items))
                .flatMap(itemReactiveRepository::save)
                .doOnNext((item -> {
                    System.out.println("Saved Item is: " + item);
                }))
                .blockLast();//Important, only for testing purpose, because we need the stream remains blocked
        //to be available for test methods


    }


    @Test
    @Order(1)
    public void getAllItems() {
        Flux<Item> allItems = itemReactiveRepository.findAll();

        System.out.println("**************** STARTED *************");

        StepVerifier.create(allItems.log())
                .expectSubscription()
                .expectNextCount(5)
                .verifyComplete();

        System.out.println("**************** ENDED *************");
    }

    @Test
    @Order(2)
    public void getItemById() {

        System.out.println("**************** STARTED *************");

        StepVerifier.create(itemReactiveRepository.findById("111").log())
                .expectSubscription()
                .expectNextMatches((item -> item.getDescription().equalsIgnoreCase("Apple Airpod")))
                .verifyComplete();

        System.out.println("**************** ENDED *************");
    }


    @Test
    @Order(3)
    public void findItemByDescription() {
        System.out.println("**************** STARTED *************");

        StepVerifier.create(itemReactiveRepository.findByDescription("Apple iMac"))
                .expectSubscription()
                .expectNextMatches(item -> item.getPrice().doubleValue() == 2500.00)
                .verifyComplete();

        System.out.println("**************** ENDED *************");

    }


    @Test
    @Order(4)
    public void saveItem() {
        System.out.println("**************** STARTED *************");

        Item newItem = new Item("112", "Google Nest Wifi Mesh Network", 400.00);

        Mono<Item> savedItem = itemReactiveRepository.save(newItem);

        StepVerifier.create(savedItem.log("Saved Item: "))
                .expectSubscription()
                .expectNextMatches(item -> item.getId().equalsIgnoreCase("112") && item.getDescription().equalsIgnoreCase("Google Nest Wifi Mesh Network") && item.getPrice().doubleValue() == 400.00)
                .verifyComplete();


        System.out.println("**************** ENDED *************");
    }

    @Test
    @Order(5)
    public void updateItem() {
        System.out.println("**************** STARTED *************");
        double newPrice = 1099.99;
        Mono<Item> updatedItem = itemReactiveRepository.findByDescription("Apple iPhone")
                .map(item -> {
                    item.setPrice(newPrice); //setting the new price
                    return item;
                })
                .flatMap(item -> {
                    return itemReactiveRepository.save(item); //saving the updated price
                });


        StepVerifier.create(updatedItem.log())
                .expectSubscription()
                .expectNextMatches((item -> item.getPrice().doubleValue() == 1099.99))
                .verifyComplete();

        System.out.println("**************** ENDED *************");

    }
    
    @Test
    @Order(6)
    public void deleteItem(){
        System.out.println("**************** STARTED *************");

        Mono<Void> afterDeletedItemList = itemReactiveRepository.findByDescription("Apple iCar") //retrieve the item by description
                .flatMap(item -> {
                    return itemReactiveRepository.delete(item);
                });

        StepVerifier.create(afterDeletedItemList.log())
                .expectSubscription()
                .verifyComplete();

        StepVerifier.create(itemReactiveRepository.findAll().log("Items after deleted are: "))
                .expectSubscription()
                .expectNextCount(5)
                .verifyComplete();

        System.out.println("**************** ENDED *************");
    }
}
