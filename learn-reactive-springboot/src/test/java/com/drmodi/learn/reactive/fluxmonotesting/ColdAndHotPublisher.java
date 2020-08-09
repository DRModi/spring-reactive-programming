package com.drmodi.learn.reactive.fluxmonotesting;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;

import java.time.Duration;

public class ColdAndHotPublisher {

    @Test
    public void coldPublisherTest() throws InterruptedException {

        Flux<String> strFlux = Flux.just("A", "B", "C", "D", "E", "F")
                .delayElements(Duration.ofSeconds(1));

        strFlux.subscribe((s) -> System.out.println("Subscriber 1, received value: "+s)); //emits the values from beginning.
        Thread.sleep(3000);

        strFlux.subscribe((str) -> System.out.println("Subscriber 2, received value: "+ str));//emits the values from beginning.
        Thread.sleep(4000);
    }


    @Test
    public void hotPublisherTest() throws InterruptedException{

        Flux<String> strFlux = Flux.just("A", "B", "C", "D", "E", "F")
                .delayElements(Duration.ofSeconds(1));

        ConnectableFlux<String> connectableFlux = strFlux.publish();
        connectableFlux.connect();
        connectableFlux.subscribe((s) -> System.out.println("Subscriber 1, received value: "+s));
        Thread.sleep(3000);

        connectableFlux.subscribe((s) -> System.out.println("Subscriber 2, received value: "+s)); //doesn't emits the values from beginning.
        Thread.sleep(4000);

    }

}
