package com.drmodi.learn.reactive.fluxmonotesting;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import reactor.test.scheduler.VirtualTimeScheduler;

import java.time.Duration;

public class FluxTestingWithVirtualTime {

    @Test
    public void fluxTestWithoutVirtualTime(){
        Flux<Long> longFlux = Flux.interval(Duration.ofSeconds(1)).take(3);

        StepVerifier.create(longFlux.log())
                .expectSubscription()
                .expectNext(0l, 1l, 2l)
                .verifyComplete();
    }

    @Test
    public void fluxTestWithVirtualTime(){
       VirtualTimeScheduler.getOrSet();

       Flux<Long> longFlux = Flux.interval(Duration.ofSeconds(1)).take(3);

       StepVerifier.withVirtualTime(()->longFlux.log())
               .expectSubscription()
               .thenAwait(Duration.ofSeconds(3)) //Provide the total or more delay
               .expectNext(0l, 1l, 2l)
               .verifyComplete();
    }

}
