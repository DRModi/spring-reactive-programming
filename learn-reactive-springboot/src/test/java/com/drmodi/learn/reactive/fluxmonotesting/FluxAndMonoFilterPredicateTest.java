package com.drmodi.learn.reactive.fluxmonotesting;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

public class FluxAndMonoFilterPredicateTest {

    List<String> strList = Arrays.asList("Spring","Spring Boot","Reactive Spring");



    @Test
    public void fluxPredicatePatternMatch(){

        Flux<String> stringFlux = Flux.fromIterable(strList)
                .filter(str -> str.startsWith("Spring"))
                .log();

        StepVerifier.create(stringFlux)
                .expectNext("Spring", "Spring Boot")
                .verifyComplete();
    }

    @Test
    public void fluxUsingFilterWithLength(){

        StepVerifier.create(Flux.fromStream(strList.stream())
                .filter(s -> s.length()>7).log())
                .expectNext("Spring Boot","Reactive Spring")
                .verifyComplete();


    }
}
