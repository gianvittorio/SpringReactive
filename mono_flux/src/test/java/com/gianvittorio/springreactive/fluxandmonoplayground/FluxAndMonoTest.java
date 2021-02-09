package com.gianvittorio.springreactive.fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class FluxAndMonoTest {
    @Test
    public void fluxTest() {
        Flux<String> stringFlux = Flux.just("Spring", "Spring Boot", "Reactive Spring")
                //.concatWith(Flux.error(new RuntimeException("Exception Occurred")))
                .concatWith(Flux.just("After Error"))
                .log();

        stringFlux.subscribe(
                System.out::println,
                ex -> System.out.println("Exception is: " + ex),
                () -> System.out.println("Completed")
        );
    }

    @Test
    public void fluxTestElementsWithError() {
        Flux<String> stringFlux = Flux.just("Spring", "Spring Boot", "Spring Reactive")
                .concatWith(Flux.error(new RuntimeException("Exception occurred")))
                .log();

        StepVerifier.create(stringFlux)
                .expectNext("Spring")
                .expectNext("Spring Boot")
                .expectNext("Spring Reactive")
                .expectErrorMessage("Exception occurred")
                .verify();
    }

    @Test
    public void fluxTestElementsCountWithError() {
        Flux<String> stringFlux = Flux.just("Spring", "Spring Boot", "Spring Reactive")
                .concatWith(Flux.error(new RuntimeException("Exception occurred")))
                .log();

        StepVerifier.create(stringFlux)
                .expectNextCount(3)
                .expectErrorMessage("Exception occurred")
                .verify();
    }

    @Test
    public void fluxTestElementsWithError1() {
        Flux<String> stringFlux = Flux.just("Spring", "Spring Boot", "Spring Reactive")
                .concatWith(Flux.error(new RuntimeException("Exception occurred")))
                .log();

        StepVerifier.create(stringFlux)
                .expectNext("Spring", "Spring Boot", "Spring Reactive")
                .expectErrorMessage("Exception occurred")
                .verify();
    }

    @Test
    public void monoTest() {
        Mono<String> stringMono = Mono.just("Spring");

        StepVerifier.create(stringMono.log())
                .expectNext("Spring")
                .verifyComplete();
    }

    @Test
    public void monoTestError() {
        StepVerifier.create(
                Mono.error(new RuntimeException("Exception occurred"))
                        .log()
        )
        .expectError(RuntimeException.class)
        .verify();
    }
}
