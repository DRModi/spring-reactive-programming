package com.drmodi.learn.reactive.fluxmonotesting;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

public class FluxAndMonoErrorHandlingException {

    @Test
    public void fluxErrorHandling_usingOnErrorResume(){

        Flux<String> strFlux = Flux.just("A", "B", "C")
                .concatWith(Flux.error(new RuntimeException("Exception Occurred!")))
                .concatWith(Flux.just("D"))
                .onErrorResume((exc) -> {
                    System.out.println("** OnErrorResume: Exception is : "+exc);
                    return Flux.just("DefaultVal", "DefaultVal1");
                });


        StepVerifier.create(strFlux.log())
                .expectNext("A", "B", "C")
                //.expectError(RuntimeException.class)
                //.verify();
                .expectNext("DefaultVal", "DefaultVal1")
                .verifyComplete();
    }


    @Test
    public void fluxErrorHandling_usingOnErrorReturn(){

        Flux<String> strFlux = Flux.fromIterable(Arrays.asList("A", "B", "C"))
                .concatWith(Flux.error(new RuntimeException("Exception occurred")))
                .concatWith(Flux.just("D"))
                .onErrorReturn("DefaultFallbackValue");

        StepVerifier.create(strFlux.log())
                .expectNext("A", "B", "C")
                .expectNext("DefaultFallbackValue")
                .verifyComplete();

    }

    @Test
    public void fluxErrorHandling_OnErrorMap(){

        Flux<String> stringFlux = Flux.fromIterable(List.of("A", "B", "C"))
                .concatWith(Flux.error(new RuntimeException("Exception Occurred!")))
                .concatWith(Flux.just("D"))
                .onErrorMap((e) -> new CustomException(e));

        StepVerifier.create(stringFlux.log())
                .expectNext("A","B","C")
                .expectError(CustomException.class)
                .verify();
    }

    @Test
    public void fluxErrorHandling_OnErrorMap_witRetry(){

        Flux<String> stringFlux = Flux.fromIterable(List.of("A", "B", "C"))
                .concatWith(Flux.error(new RuntimeException("Exception Occurred!")))
                .concatWith(Flux.just("D"))
                .onErrorMap((e) -> new CustomException(e))
                .retry(2);

        StepVerifier.create(stringFlux.log())
                .expectNext("A","B","C")
                .expectNext("A","B","C") //Due to retry sequence omitted 1 time
                .expectNext("A","B","C") //Due to retry sequence omitted 2 time
                .expectError(CustomException.class)
                .verify();
    }

    @Test
    public void fluxErrorHandling_OnErrorMap_witRetry_BackOff(){

        Flux<String> stringFlux = Flux.fromIterable(List.of("A", "B", "C"))
                .concatWith(Flux.error(new RuntimeException("Exception Occurred!")))
                .concatWith(Flux.just("D"))
                .onErrorMap((e) -> new CustomException(e))
                .retryBackoff(2, Duration.ofSeconds(3));

        StepVerifier.create(stringFlux.log())
                .expectNext("A","B","C")
                .expectNext("A","B","C")
                .expectNext("A","B","C")
                //.expectError(CustomException.class) //it throws exception called retry exhausted
                .expectError(IllegalStateException.class)
                .verify();
    }



}
