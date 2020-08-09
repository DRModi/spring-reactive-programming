package com.drmodi.learn.reactive.handler;

import com.drmodi.learn.reactive.document.Item;
import com.drmodi.learn.reactive.document.ItemCapped;
import com.drmodi.learn.reactive.repository.ItemCappedReactiveRepository;
import com.drmodi.learn.reactive.repository.ItemReactiveRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import java.net.URI;

import static com.drmodi.learn.reactive.util.ItemConstants.ITEM_FUNCTIONAL_END_POINT_V1;


@Component
@Slf4j
public class ItemHandler {

    @Autowired
    ItemReactiveRepository itemReactiveRepository;

    @Autowired
    ItemCappedReactiveRepository itemCappedReactiveRepository;

    static Mono<ServerResponse> notFoundResponse = ServerResponse.notFound().build();

    public Mono<ServerResponse> getAllItems(ServerRequest request){

        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(itemReactiveRepository.findAll(), Item.class);
    }

    public Mono<ServerResponse> getItemById(ServerRequest request){
        //1. retrieve the id
        //2. Fetch the data using id from repository
        //3. Build the response using flatmap
        //4. Build response for not found

        String itemId = request.pathVariable("id"); //1. retrieve the id from the ServerRequest

/*      Approach 1:
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(itemReactiveRepository.findById(request.pathVariable("/id")), Item.class)
                .switchIfEmpty(ServerResponse.notFound().build());*/


/*      Approach 2:
        String itemId = request.pathVariable("id"); //1. retrieve the id from the ServerRequest
        Mono<Item> itemMonoById = itemReactiveRepository.findById(itemId);
        return itemMonoById.flatMap(item ->
                    ServerResponse.ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(Mono.just(item),Item.class)
                            .switchIfEmpty(ServerResponse.notFound().build()));
*/

        //Approach 3:
        return itemReactiveRepository.findById(itemId).flatMap(item ->
                        ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(item)
                        .switchIfEmpty(notFoundResponse));


    }

    public Mono<ServerResponse> saveAnItem(ServerRequest request){

        Mono<Item> itemToBeSaved = request.bodyToMono(Item.class);

        /*// create 200 OK Response. Approach 1.
        return itemToBeSaved.flatMap(item ->
                ServerResponse.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(itemReactiveRepository.save(item),Item.class));

       // */

        return itemToBeSaved.flatMap(itemReactiveRepository::save).flatMap(createdItem ->
                ServerResponse.created(URI.create(ITEM_FUNCTIONAL_END_POINT_V1.concat(createdItem.getId())))
                        .contentType(MediaType.APPLICATION_JSON).bodyValue(createdItem));

    }


    public Mono<ServerResponse> deleteItemById(ServerRequest request) {
        String id = request.pathVariable("id");
        Mono<Void> deleteAnItemById = itemReactiveRepository.deleteById(id);

        /* Approach -1
        return ServerResponse.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(deleteAnItemById,Void.class);
        */
        return itemReactiveRepository.findById(id).flatMap(item ->
                itemReactiveRepository.delete(item).then(ServerResponse.noContent().build()))
                .switchIfEmpty(ServerResponse.notFound().build());


    }

    public Mono<ServerResponse> updateAnItem(ServerRequest request) {

        String id = request.pathVariable("id");
        Mono<Item> updatedItem = request.bodyToMono(Item.class);



        //Approach -1
        /*
        Mono<Item> updatedItem = request.bodyToMono(Item.class).flatMap((item) ->
                {
                    Mono<Item> itemMono = itemReactiveRepository.findById(id)
                            .flatMap(currentItem -> {
                                currentItem.setDescription(item.getDescription());
                                currentItem.setPrice(item.getPrice());
                                return itemReactiveRepository.save(currentItem);
                            });
                    return itemMono;
                });

        return updatedItem.flatMap(item ->
                ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(item)
                .switchIfEmpty(notFoundResponse));

        */

       return updatedItem.flatMap(itemInRequest -> itemReactiveRepository.findById(id).flatMap(itemForDB -> {
            itemForDB.setDescription(itemInRequest.getDescription());
            itemForDB.setPrice(itemInRequest.getPrice());
            return Mono.just(itemForDB);
            }).flatMap(itemToUpdate -> itemReactiveRepository.save(itemToUpdate)))
                .flatMap(itemUpdated -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(itemUpdated))
                .switchIfEmpty(ServerResponse.notFound().build());
    }




   /* @ExceptionHandler(RuntimeException.class) //Added to global controller exception handler
    public ResponseEntity<String> handleRuntimeException(RuntimeException exception){
        log.error("Exception caught in handleRuntimeException : {} ", exception);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exception.getMessage());
    }*/


    /*public Mono<ServerResponse> getRuntimeException(ServerRequest request) {
        Mono<Object> got_the_exception_mono = Mono.error(new RuntimeException());

        return got_the_exception_mono.onErrorReturn("getRuntimeException: Runtime Exception occurred")
                .flatMap(s -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(s)).log("**** Error Logged : -> ");
    }*/


    public Mono<ServerResponse> getRuntimeException(ServerRequest request) {
        throw new RuntimeException("getRuntimeException: Runtime Exception occurred!");

       /* return Mono.empty()
                .flatMap(s -> ErrorAttributeOptions.defaults().isIncluded
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(s)).log("**** Error Logged : -> ");*/
        //throw new RuntimeException("Error");
        /*return Mono.empty()
                .switchIfEmpty(ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.APPLICATION_JSON).;*/
        //throw new RuntimeException("getRuntimeException: Runtime Exception occurred");

        /*return got_the_exception_mono.flatMap(data -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).build())
                .switchIfEmpty(ServerResponse.notFound().build())
                .onErrorResume(error -> { return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).build();});*/
    }


    /*public Mono<ServerResponse> itemsEx(ServerRequest serverRequest){
        //throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "getRuntimeException: Runtime Exception Occurred!", new RuntimeException());
        //return Mono.error(new RuntimeException("getRuntimeException: Runtime Exception Occurred!"));

        //return Mono.error(new CustomExceptionHandler(new RuntimeException("getRuntimeException: Runtime Exception Occurred!")));
        //Item item = new Item(null, "1", 2.0);
        //return Mono.just((ServerResponse) Mono.error(new RuntimeException("Error: getRuntimeException: Runtime Exception Occurred!")));
       // CustomExceptionHandler cts =
        *//*return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .bodyValue(Mono.error(new RuntimeException("Exception Occurred!")));*//*


       return Mono.error(new RuntimeException("Exception Occured"))
               .onErrorReturn(new CustomException(new RuntimeException("ExceptionOccured at this endpoint")))
               .flatMap(s -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
               .contentType(MediaType.APPLICATION_JSON)
               .syncBody(s));



    }*/

    public Mono<ServerResponse> itemsEx(ServerRequest serverRequest){
        Mono<Object> got_the_exception_mono = Mono.error(new RuntimeException());

        return got_the_exception_mono.onErrorReturn("getRuntimeException: Runtime Exception occurred")
                .flatMap(s -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(s)).log("**** Error Logged : -> ");
    }

    public Mono<ServerResponse> itemCappedStream(ServerRequest request) {

        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_STREAM_JSON)
                .body(itemCappedReactiveRepository.findItemCappedBy(), ItemCapped.class);
    }
}
