package com.drmodi.learn.reactive.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@RestController
public class FluxAndMonoController {

    @GetMapping("/flux")
    public Flux<Integer> getFluxInteger(){
        return Flux.just(1,2,3,4)
                .delayElements(Duration.ofSeconds(1))
                .log();
    }

    @GetMapping(value = "/flux-stream", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Flux<Integer> getFluxIntegerStream(){
        return Flux.just(11,21,07,05,12)
                .delayElements(Duration.ofSeconds(1))
                .log();
    }

    @GetMapping(value = "/infinite-fluxstream", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Flux<Long> getInfiniteFluxStream(){
        return Flux.interval(Duration.ofSeconds(1))
                .log();
    }

    @GetMapping("/mono")
    public Mono<Integer> getMono(){
        return Mono.just(11).log();
    }
}
