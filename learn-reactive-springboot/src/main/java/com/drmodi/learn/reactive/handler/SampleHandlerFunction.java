package com.drmodi.learn.reactive.handler;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class SampleHandlerFunction {

    public Mono<ServerResponse> fluxOfInteger(ServerRequest serverRequest){
        return ServerResponse.ok()
                .body(
                        Flux.just(05,07,11).log(),Integer.class
                );
    }

    public Mono<ServerResponse> monoOfInteger(ServerRequest serverRequest){
        return ServerResponse.ok()
                .body(
                        Mono.just(11).log(),Integer.class
                );
    }
}
