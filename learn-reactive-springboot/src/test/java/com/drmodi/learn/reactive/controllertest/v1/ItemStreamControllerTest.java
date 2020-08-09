package com.drmodi.learn.reactive.controllertest.v1;

import com.drmodi.learn.reactive.document.ItemCapped;
import com.drmodi.learn.reactive.repository.ItemCappedReactiveRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureWebTestClient
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DirtiesContext
@ActiveProfiles("test")
public class ItemStreamControllerTest {

    @Autowired
    ItemCappedReactiveRepository itemCappedReactiveRepository;

    @Autowired
    ReactiveMongoOperations reactiveMongoOperations;

    @Autowired
    WebTestClient webTestClient;

    @BeforeAll
    public void Setup(){
        reactiveMongoOperations.dropCollection(ItemCapped.class);
        reactiveMongoOperations.createCollection(ItemCapped.class, CollectionOptions.empty().maxDocuments(20).size(50000).capped());

        Flux<ItemCapped> itemCappedFlux = Flux.interval(Duration.ofSeconds(1))
                .map(i -> new ItemCapped(null, "Randon Item: "+i, 100.00+i))
                .take(5);

        itemCappedReactiveRepository.insert(itemCappedFlux)
                .doOnNext(itemCapped -> {
                    System.out.println("Inserted Item is : "+itemCapped);
                })
        .blockLast();
    }

    @Test
    public void testStreamAllItems(){
        Flux<ItemCapped> take5ItemCapped = webTestClient.get().uri("/v1/stream/items")
                .exchange()
                .expectStatus().isOk()
                .returnResult(ItemCapped.class)
                .getResponseBody()
                .take(5);

        StepVerifier.create(take5ItemCapped)
                .expectNextCount(5)
                .thenCancel()
                .verify();
    }
}
