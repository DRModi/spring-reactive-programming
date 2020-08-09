package com.drmodi.learn.reactive.fluxmonotesting;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.List;

public class FluxAndMonoCombinePublisher {

    @Test
    public void combineUsingMerge() {

        Flux<String> strFlux1 = Flux.just("A", "B", "C");
        Flux<String> strFlux2 = Flux.just("D", "E", "F");

        Flux<String> fluxMerged = Flux.merge(strFlux1, strFlux2);

        StepVerifier.create(fluxMerged.log())
                .expectNext("A", "B", "C", "D", "E", "F")
                .verifyComplete();
    }

    @Test
    public void combineUsingMerge_withDelay() {

        Flux<String> strFlux1 = Flux.just("A", "B", "C").delayElements(Duration.ofSeconds(1));
        Flux<String> strFlux2 = Flux.just("D", "E", "F").delayElements(Duration.ofSeconds(1));

        Flux<String> fluxMerged = Flux.merge(strFlux1, strFlux2);

        StepVerifier.create(fluxMerged.log())
                //.expectNext("A", "B", "C", "D", "E", "F")
                .expectNextCount(6)
                .verifyComplete();
    }

    @Test
    public void combineUsingConcat() {

        Flux<String> strFlux1 = Flux.just("A", "B", "C");
        Flux<String> strFlux2 = Flux.just("D", "E", "F");

        Flux<String> fluxMerged = Flux.concat(strFlux1, strFlux2);

        StepVerifier.create(fluxMerged.log())
                .expectNext("A", "B", "C", "D", "E", "F")
                .verifyComplete();
    }

    @Test
    public void combineUsingConcat_withDelay() {

        Flux<String> strFlux1 = Flux.just("A", "B", "C").delayElements(Duration.ofSeconds(1));
        Flux<String> strFlux2 = Flux.just("D", "E", "F").delayElements(Duration.ofSeconds(1));

        Flux<String> fluxMerged = Flux.concat(strFlux1, strFlux2);

        StepVerifier.create(fluxMerged.log())
                //.expectNext("A", "B", "C", "D", "E", "F")
                .expectNextCount(6)
                .verifyComplete();
    }


    @Test
    public void combineUsingZip(){
        Flux<String> strFlux1 = Flux.fromIterable(List.of("A","B","C"));
        Flux<String> strFlux2 = Flux.just("D", "E", "F");

        Flux<String> fluxMerged = Flux.zip(strFlux1, strFlux2, (str1, str2) -> {
           return str1.concat(str2);
        });

        StepVerifier.create(fluxMerged.log())
                .expectNext("AD", "BE", "CF")
                //.expectNextCount(3)
                .verifyComplete();

    }

}
