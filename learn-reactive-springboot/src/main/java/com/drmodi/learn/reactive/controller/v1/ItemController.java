package com.drmodi.learn.reactive.controller.v1;

import com.drmodi.learn.reactive.document.Item;
import com.drmodi.learn.reactive.repository.ItemReactiveRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.drmodi.learn.reactive.util.ItemConstants.ITEM_END_POINT_V1;

@RestController
@Slf4j
public class ItemController {

   /* @ExceptionHandler(RuntimeException.class) //Added to global controller exception handler
    public ResponseEntity<String> handleRuntimeException(RuntimeException exception){
        log.error("Exception caught in handleRuntimeException : {} ", exception);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exception.getMessage());
    }*/

    @Autowired
    ItemReactiveRepository itemReactiveRepository;

    @GetMapping(ITEM_END_POINT_V1)
    public Flux<Item> getAllItems(){
        return itemReactiveRepository.findAll();
    }

    @GetMapping(ITEM_END_POINT_V1+"/{id}")
    public Mono<ResponseEntity<Item>> getAnItem(@PathVariable String id){
        return itemReactiveRepository.findById(id)
                .map((item) -> new ResponseEntity<>(item, HttpStatus.OK))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping(ITEM_END_POINT_V1)
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Item> addAnItem(@RequestBody Item item){
        return itemReactiveRepository.save(item);
    }

    @DeleteMapping(ITEM_END_POINT_V1+"/{id}")
    public Mono<Void> deleteAnItem(@PathVariable String id){ //it will return the void back in case of successful
        return itemReactiveRepository.deleteById(id);
    }

    //Update Existing Item
    //1. Create the endpoint - with ID path variable which needs to be updated, Provide an updated item as request body
    //2. Find the Item from DB based on id
    //3. Update the item
    //4. Save back to the db
    //5. return the saved item
    @PutMapping(ITEM_END_POINT_V1+"/{id}")
    public Mono<ResponseEntity<Item>> updateAnItem(@PathVariable String id, @RequestBody Item updatedItem){ //Step 1
        return  itemReactiveRepository.findById(id) //Step 2
                .flatMap((retrieveItemById) -> {
                    retrieveItemById.setDescription(updatedItem.getDescription()); //step 3
                    retrieveItemById.setPrice(updatedItem.getPrice());// step 3
                    return itemReactiveRepository.save(retrieveItemById);}) //step 4
                .map(returnItem -> new ResponseEntity<>(returnItem, HttpStatus.OK)) //step 5
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }


    @GetMapping(ITEM_END_POINT_V1+"/runtimeException")
    public Flux<Item> getRuntimeException(){

        return itemReactiveRepository.findAll()
                .concatWith(Mono.error(new RuntimeException("Error: getRuntimeException: Runtime Exception Occurred!")));
    }
}
