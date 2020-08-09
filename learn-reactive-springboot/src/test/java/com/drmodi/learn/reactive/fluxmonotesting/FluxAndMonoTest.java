package com.drmodi.learn.reactive.fluxmonotesting;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;



public class FluxAndMonoTest {

    @Test
    public void fluxTest(){

        Flux<String> strFlux = Flux.just("Spring", "Spring Boot", "Spring Reactive")
//                .concatWith(Flux.error(new RuntimeException("Runtime Exception Occurred!!")))
                .concatWith(Flux.just("After Error"))
                .log();

        strFlux
            .subscribe(System.out::println,
                        (e)->System.out.println("*** Exception is: "+e),
                        () -> System.out.println("*** Completed!"));
    }

    @Test
    public void fluxTestElements_withOutError(){
        Flux<String> strFlux = Flux.just("Spring", "Spring Boot", "Spring Reactive")
                .log();

        StepVerifier.create(strFlux)
                .expectNext("Spring")
                .expectNext("Spring Boot")
                .expectNext("Spring Reactive")
                .verifyComplete();
    }

    @Test
    public void fluxTestElements_withError(){
        Flux<String> strFlux = Flux.just("Spring", "Spring Boot", "Spring Reactive")
                .concatWith(Flux.error(new RuntimeException("Runtime Exception Occurred!")))
                .log();

        StepVerifier.create(strFlux)
                .expectNext("Spring", "Spring Boot", "Spring Reactive")
                //.expectError(RuntimeException.class)
                .expectErrorMessage("Runtime Exception Occurred!")
                //.verifyComplete();
                .verify();
    }

    @Test
    public void fluxTestElementsCount_withOutError(){
        Flux<String> strFlux = Flux.just("Spring", "Spring Boot", "Spring Reactive")
                .log();

        StepVerifier.create(strFlux)
                .expectNextCount(3)
                .verifyComplete();
    }

    @Test
    public void fluxTestElementsCount_withError(){
        Flux<String> strFlux = Flux.just("Spring", "Spring Boot", "Spring Reactive")
                .concatWith(Flux.error(RuntimeException::new))
                .log();

        StepVerifier.create(strFlux)
                .expectNextCount(3)
                .expectError(RuntimeException.class)
                .verify();
    }


    @Test
    public void monoTest(){
         Mono<String> strMono = Mono.just("Reactive Spring");

         StepVerifier.create(strMono.log())
                 .expectNext("Reactive Spring")
                 .verifyComplete();
    }

    @Test
    public void monoTest_withError(){

        StepVerifier.create(Mono.error(new RuntimeException("Exception Occurred!")).log())
                //.expectError(RuntimeException.class)
                .expectErrorMessage("Exception Occurred!")
                .verify();
    }

}
