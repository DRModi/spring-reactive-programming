package com.drmodi.learn.reactive.handler;

import com.drmodi.learn.reactive.document.Item;
import com.drmodi.learn.reactive.repository.ItemReactiveRepository;
import com.drmodi.learn.reactive.util.ItemConstants;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureWebTestClient
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DirtiesContext
@ActiveProfiles("test")
public class ItemHandlerTest {

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    ItemReactiveRepository itemReactiveRepository;

    List<Item> items = List.of(new Item(null, "Apple iPhone", 1000.0),
            new Item(null, "Apple iMac", 2500.0),
            new Item(null, "Apple Watch", 850.0),
            new Item("111", "Apple Airpod", 150.0),
            new Item("211", "Apple Airport", 450.0));


    @BeforeAll
    public void setUp() {
        //clear all existing data
        itemReactiveRepository.deleteAll()
                .thenMany(Flux.fromIterable(items))
                .flatMap(itemReactiveRepository::save)
                .doOnNext((item -> {
                    System.out.println("Inserted Item is : "+item);
                })).blockLast();
    }


    @Test
    @Order(1)
    public void getAllItems(){
        webTestClient.get().uri(ItemConstants.ITEM_FUNCTIONAL_END_POINT_V1)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Item.class)
                .hasSize(4);
    }

    @Test
    @Order(2)
    public void getAllItems_approach2(){ //validate the imported values have id is not null
        webTestClient.get().uri(ItemConstants.ITEM_FUNCTIONAL_END_POINT_V1)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Item.class)
                .consumeWith((res) -> {
                    List<Item> itemList = res.getResponseBody();
                    itemList.forEach((item) -> {
                        Assertions.assertTrue(item.getId()!=null);
                    });
                });
    }

    @Test
    @Order(3)
    public void getAllItem_approach3(){ //using step verifier
        Flux<Item> itemFlux = webTestClient.get().uri(ItemConstants.ITEM_FUNCTIONAL_END_POINT_V1)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .returnResult(Item.class)
                .getResponseBody();

        StepVerifier.create(itemFlux.log("**** Happening Events over the network: "))
                .expectSubscription()
                .expectNextCount(4)
                .verifyComplete();
    }

    @Test
    @Order(4)
    public void getAnItemTest(){
        webTestClient.get().uri(ItemConstants.ITEM_FUNCTIONAL_END_POINT_V1.concat("/{id}"),"111")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.price").isEqualTo(150)
                .jsonPath("$.description", "Apple Airpod");
    }


    @Test
    @Order(5)
    public void getNotFoundTest(){
        webTestClient.get().uri(ItemConstants.ITEM_FUNCTIONAL_END_POINT_V1.concat("/{id}"),"badId")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @Order(6)
    public void addAnItemTest_200OK(){ //Status 200 OK test
        Item itemToAdd = new Item(null, "Apple iCar", 55000.00);
        webTestClient.post().uri(ItemConstants.ITEM_FUNCTIONAL_END_POINT_V1)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(itemToAdd), Item.class)
                .exchange()
                 //.expectStatus().isOk() //Status 200 OK test, in order to test, update the method in itemhandler to send ServerResponse.ok() status.
                .expectBody()
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.description").isEqualTo("Apple iCar")
                .jsonPath("$.price").isEqualTo(55000.0);
    }

    @Test
    @Order(6)
    public void addAnItemTest_201Created(){ //Status 200 OK test
        Item itemToAdd = new Item(null, "Apple iCar", 55000.00);
        webTestClient.post().uri(ItemConstants.ITEM_FUNCTIONAL_END_POINT_V1)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(itemToAdd), Item.class)
                .exchange()
                .expectStatus().isCreated() //Status 201 Test required different handler method
                .expectBody()
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.description").isEqualTo("Apple iCar")
                .jsonPath("$.price").isEqualTo(55000.0);
    }


    @Test
    @Order(7)
    public void updateAnItemTest(){
        double newAirPod2Price = 249.99;
        String newAirPod2 = "Apple Airpod2";

        Item updatedItem = new Item(null, newAirPod2, newAirPod2Price);

        webTestClient.put().uri(ItemConstants.ITEM_FUNCTIONAL_END_POINT_V1.concat("/{id}"),"111")
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(updatedItem),Item.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Item.class)
                .consumeWith(response -> {
                        Assertions.assertTrue(response.getResponseBody().getId().equals("111"));
                        Assertions.assertTrue(response.getResponseBody().getDescription().equals("Apple Airpod2"));
                        Assertions.assertTrue(response.getResponseBody().getPrice().equals(249.99));
                });


    }

    @Test
    @Order(8)
    public void updateAnItemTest_JSONPath(){
        double newAirPod2Price = 249.99;
        String newAirPod2 = "Apple Airpod2";

        Item updatedItem = new Item(null, newAirPod2, newAirPod2Price);

        webTestClient.put().uri(ItemConstants.ITEM_FUNCTIONAL_END_POINT_V1.concat("/{id}"),"111")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(updatedItem),Item.class)
                .exchange()
                .expectStatus().isOk()
                //.expectBody(Item.class)
                //.consumeWith((res) -> {
                //   System.out.println("*** Print the body : " +  res.getResponseBody());
                //});
                .expectBody()
                .jsonPath("$.price").isEqualTo(newAirPod2Price)
                .jsonPath("$.description").isEqualTo(newAirPod2);

    }

    @Test
    @Order(9)
    public void updateAnItem_NotFoundTest(){
        double newAirPod2Price = 249.99;
        String newAirPod2 = "Apple Airpod2";

        Item updatedItem = new Item(null, newAirPod2, newAirPod2Price);

        webTestClient.put().uri(ItemConstants.ITEM_FUNCTIONAL_END_POINT_V1.concat("/{id}"),"BadID")
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(updatedItem),Item.class)
                .exchange()
                .expectStatus().isNotFound();
    }


    @Test
    @Order(10)
    public void deleteAnItemTest_200OK(){
        webTestClient.delete().uri(ItemConstants.ITEM_FUNCTIONAL_END_POINT_V1.concat("/{id}"),"111")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                //.expectStatus().isOk() //Status 200 OK test, in order to test, update the method in itemhandler to send ServerResponse.ok() status.
                .expectBody(Void.class);
    }

    @Test
    @Order(11)
    public void deleteAnItemTest_204OK(){
        webTestClient.delete().uri(ItemConstants.ITEM_FUNCTIONAL_END_POINT_V1.concat("/{id}"),"211")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNoContent()
                .expectBody(Void.class);
    }

    @Test
    @Order(12)
    public void deleteAnItemTest_404NotFound(){
        webTestClient.delete().uri(ItemConstants.ITEM_FUNCTIONAL_END_POINT_V1.concat("/{id}"),"11")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(Void.class);
    }

    @Test
    @Order(13)
    public void runTimeException_Test(){
        webTestClient.get().uri("/test/exception")
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody()
                .jsonPath("$.error","");
    }

}
