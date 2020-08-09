package com.drmodi.learn.reactive.fluxmonotesting;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import java.lang.Thread;
import java.util.List;
import java.util.function.Function;

import static reactor.core.scheduler.Schedulers.parallel;

public class FluxAndMonoTransformTest {

    List<String> strList = List.of("Spring", "Spring Boot", "Reactive Spring");


    @Test
    public void transformUsingFlatMap() {

        Flux<String> strFlux = Flux.fromIterable(List.of("A", "B", "C", "D", "E", "F"))
                .flatMap(str -> {
                    return Flux.fromIterable(convertStrToList(str));
                }); //use for external call that returns flux (fluxElement -> Flux<String>)

        StepVerifier.create(strFlux.log())
                .expectNextCount(12)
                .verifyComplete();
    }


    private List<String> convertStrToList(String str) {
        try {
            Thread.sleep(1000); //delay 1 sec
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return List.of(str, str + " * updated *");
    }


    @Test
    public void transformUsingFlatMap_Parallel() {
        Flux<String> strFlux = Flux.fromIterable(List.of("A", "B", "C", "D", "E", "F"))
                .window(2) //Instead of passing one by one, it will wait till 2 elements arrive
                // then it will send list of two element like (A,B), (C,D), (E,F)
                // it become Flux<Flux<String>>, same like Stream<Stream<String>>
                .flatMap((str) -> str.map(this::convertStrToList).subscribeOn(parallel()))
                .flatMap(s -> Flux.fromIterable(s))
                .log();


        StepVerifier.create(strFlux)
                .expectNextCount(12)
                .verifyComplete();

        //Note: Sequence is not the important then use the flatmap to create Flux<List<String>> for mapping.
    }

    @Test
    public void transformUsingFlatMap_parallel_maintainOrder() {
        Flux<String> strFlux = Flux.fromIterable(List.of("A", "B", "C", "D", "E", "F"))
                .window(2)
                //concateMap will work for maintaining order with parallel but no improvement in execution
                /*.concatMap((str) -> str.map(this::convertStrToList).subscribeOn(parallel()))*/
                .flatMapSequential((str) -> str.map(this::convertStrToList).subscribeOn(parallel()))
                .flatMap(str -> Flux.fromIterable(str));

        StepVerifier.create(strFlux.log())
                .expectNextCount(12)
                .verifyComplete();

    }


    @Test
    public void fluxTransformListToUpperCase() {
        Function<String, String> upperCaseMapper = String::toUpperCase;
        Flux<String> stringFlux = Flux.fromStream(strList.stream()
                .map(upperCaseMapper));
//          strList.stream()
//          .map(upperCaseMapper)
//          .forEach(System.out::println);
//        "SPRING","SPRING BOOT","REACTIVE SPRING"

        StepVerifier.create(stringFlux.log())
                .expectNext("SPRING", "SPRING BOOT", "REACTIVE SPRING")
                .verifyComplete();

    }

    @Test
    public void fluxTransformToLength() {

        Flux<Integer> intFluxWithLength = Flux.fromIterable(strList)
                .map(s -> s.length())
                .repeat(1);

        StepVerifier.create(intFluxWithLength.log())
                .expectNext(6, 11, 15, 6, 11, 15)
                .verifyComplete();
    }

    @Test
    public void fluxUsingPredicateAndMapper() {

        Flux<String> stringFlux = Flux.fromStream(strList.stream())
                .filter(str -> str.length() > 6)
                .map(str -> str.toLowerCase());

        StepVerifier.create(stringFlux.log())
                .expectNext("spring boot", "reactive spring")
                .verifyComplete();
    }


}
