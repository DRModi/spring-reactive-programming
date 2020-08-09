package com.drmodi.learn.reactive.fluxmonotesting;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.test.StepVerifierOptions;

import java.util.List;
import java.util.function.Supplier;

public class FluxAndMonoFectoryMethodsTest {

    List<String> strList = List.of("Spring", "Spring Boot", "Reactive Spring");


    @Test
    public void flux_usingIterable(){

        Flux<String> stringFlux = Flux.fromIterable(strList);

        StepVerifier.create(stringFlux.log())
                .expectNext("Spring", "Spring Boot", "Reactive Spring")
                .verifyComplete();
    }

    @Test
    public void flux_usingArrays(){

        String[] strArray = new String[]{"Spring", "Spring Boot", "Reactive Spring"};

        Flux<String> stringArrayFlux = Flux.fromArray(strArray);

        StepVerifier.create(stringArrayFlux.log())
                .expectNext("Spring", "Spring Boot", "Reactive Spring")
                .verifyComplete();
    }

    @Test
    public void flux_usingStream(){
        Flux<String> strFluxStream = Flux.fromStream(strList.stream()).log();

        StepVerifier.create(strFluxStream)
                .expectNext("Spring", "Spring Boot", "Reactive Spring")
                .verifyComplete();
    }


    @Test
    public void mono_nullTesting_UsingJustOrEmpty(){
        Mono<String> strNullMono = Mono.justOrEmpty(null);

        StepVerifier.create(strNullMono.log())
                .verifyComplete(); //Since there are no element to expect
    }

    @Test
    public void mono_UsingSupplier(){
        Supplier<String> strSupplier = () -> "Reactive Spring";

        System.out.println("Supplier Value: "+strSupplier.get());

        Mono<String> strMono = Mono.fromSupplier(strSupplier).log();

        StepVerifier.create(strMono)
                .expectNext("Reactive Spring")
                .verifyComplete();
    }


    @Test
    public void flux_UsingRange(){
        Flux<Integer> integerFlux = Flux.range(1, 7);

        StepVerifier.create(integerFlux.log())
                .expectNext(1,2,3,4,5,6,7)
                .verifyComplete();
    }


}
