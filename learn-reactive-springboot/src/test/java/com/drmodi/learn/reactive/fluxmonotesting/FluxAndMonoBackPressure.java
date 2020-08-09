package com.drmodi.learn.reactive.fluxmonotesting;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

public class FluxAndMonoBackPressure {

    @Test
    public void backpressure(){

        Flux<Integer> intFlux = Flux.range(1,10);

        StepVerifier.create(intFlux.log())
                .expectSubscription()
                .thenRequest(1)
                .expectNext(1)
                .thenRequest(1)
                .expectNext(2)
                .thenRequest(1)
                .expectNext(3)
                .thenCancel()
                .verify();
    }


    @Test
    public void backPressure_subRequest(){
        Flux<Integer> intFlux = Flux.range(1,10);

        intFlux.subscribe((element) -> System.out.println("Element is : "+element)
        , (error) -> System.err.println("Exception occurred : "+error)
        , () -> System.out.println("Done") //after on complete, but in this case we are cancelling
        , (subscription -> subscription.request(3)));

    }



    @Test
    public void backpressure_subCancel(){
        Flux<Integer> intFlux = Flux.range(1, 10);

        intFlux.subscribe((e) -> System.out.println("Element value is : "+e)
        , (er) -> System.err.println("In case of exception, print exception: "+er)
        , () -> System.out.println("This will be done in case of onComplete")
        , (subscription -> subscription.cancel()));
    }


    @Test
    public void backpressure_customized(){

        Flux<Integer> intFlux = Flux.range(1,10).log();

        intFlux.subscribe(new BaseSubscriber<Integer>() {
            @Override
            protected void hookOnNext(Integer value) {
                request(1);
                System.out.println("Received Value is : "+value);
                if(value == 5){
                    cancel();
                }
            }
        });

    }
}
