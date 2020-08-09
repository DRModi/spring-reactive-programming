package com.drmodi.learnreactive.controller;

import com.drmodi.learnreactive.domain.Item;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@Slf4j
public class ItemClientController {

    WebClient webClient = WebClient.create("http://localhost:8080");

    @GetMapping("/client/retrieve")
    public Flux<Item> getAllItemsUsingRetrieve(){
        return webClient.get().uri("/v1/items")
                    .retrieve()
                    .bodyToFlux(Item.class)
                    .log("Items in client project - Using Retrieve: ");
    }

    @GetMapping("/client/exchange")
    public Flux<Item> getAllItemsUsingExchange(){
        return webClient.get().uri("/v1/items")
                .exchange()
                .flatMapMany(clientResponse -> clientResponse.bodyToFlux(Item.class))
                .log("Items in client project - Using Exchange: ");
    }

    @GetMapping("/client/retrieve/{id}")
    public Mono<Item> getAnItemById_UsingRetrieve(@PathVariable String id){

        return webClient.get().uri("/v1/items/".concat(id))
                .retrieve()
                .bodyToMono(Item.class)
                .log("Retrieved the Item by ID using retrieve: ");
    }

    @GetMapping("/client/exchange/{id}")
    public Mono<Item> getAnItemById_UsingExchange(@PathVariable String id){

        return webClient.get().uri("/v1/items/".concat(id))
                .exchange()
                .flatMap(clientResponse -> clientResponse.bodyToMono(Item.class))
                .log("Retrieved the Item by ID using exchange: ");
    }

    @PostMapping("/client/addItem")
    public Mono<Item> addAnItem(@RequestBody Item item){

        return webClient.post().uri("/v1/items/")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(item), Item.class)
                .retrieve()
                .bodyToMono(Item.class)
                .log("Added Item is: ");
    }

    @PutMapping("/client/updateItem/{id}")
    public Mono<Item> updateAnItem(@PathVariable String id, @RequestBody Item item){

        return webClient.put().uri("/v1/items/".concat(id))
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(item), Item.class)
                .retrieve()
                .bodyToMono(Item.class)
                .log("Updated Item: ");
    }

    @DeleteMapping("/client/deleteItem/{id}")
    public Mono<Void> deleteAnItem(@PathVariable String id){
        return webClient.delete().uri("/v1/items/{id}", id)
                .retrieve()
                .bodyToMono(Void.class)
                .log("Deleted Item is : ");
    }

    @GetMapping("/client/retrieve/error")
    public Flux<Item> errorRetrieve(){

        return webClient.get()
                .uri("/v1/items/runtimeException")
                .retrieve()
                .onStatus(HttpStatus::is5xxServerError, clientResponse -> {
                    Mono<String> errorMono = clientResponse.bodyToMono(String.class);
                    return errorMono.flatMap((errorMessage) -> {
                       log.error("The Error Message is : "+errorMessage);
                       throw new RuntimeException(errorMessage);
                    });
                })
               .bodyToFlux(Item.class);
    }

    @GetMapping("/client/exchange/error")
    public Flux<Item> errorExchange(){

        return webClient.get()
                .uri("/v1/items/runtimeException")
                .exchange()
                .flatMapMany(clientResponse -> {
                    if(clientResponse.statusCode().is5xxServerError()){
                        return clientResponse.bodyToMono(String.class)
                                .flatMap(errorMessage -> {
                                    log.error("The error message is from exchange: "+errorMessage);
                                    throw new RuntimeException(errorMessage);
                                });
                    }else {
                        return clientResponse.bodyToFlux(Item.class);
                    }
                });
    }
}
