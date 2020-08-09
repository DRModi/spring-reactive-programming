package com.drmodi.learn.reactive.fluxmonotesting;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;

public class FluxInfiniteStream {

    @Test
    public void infiniteSequence() throws InterruptedException {

        Flux<Long> infiniteFlux = Flux.interval(Duration.ofMillis(100))
                .log();

        infiniteFlux
                .subscribe((element) -> System.out.println("Element is :" + element));

        Thread.sleep(3000);
    }


    @Test
    public void infiniteSequenceTest() throws InterruptedException {

        Flux<Long> finiteFlux = Flux.interval(Duration.ofMillis(100))
                .take(3)
                .log();


        StepVerifier.create(finiteFlux)
                .expectSubscription()
                .expectNext(0L, 1L, 2L)
                .verifyComplete();
    }

    @Test
    public void infiniteSequenceTest_withDelay(){

        Flux<Integer> finiteFlux = Flux.interval(Duration.ofMillis(100))
                .delayElements(Duration.ofSeconds(1))
                .map(l -> new Integer(l.intValue()))
                .take(3)
                .log();


        StepVerifier.create(finiteFlux)
                .expectSubscription()
                .expectNext(0,1,2)
                .verifyComplete();
    }




}
