package com.gianvittorio.springreactive.fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

public class FluxAndMonoFactoryTest {

    List<String> names = List.of("Adam", "Anna", "Jack", "Jenny");

    @Test
    public void fluxUsingIterable() {
        Flux<String> namesFlux = Flux.fromIterable(names).log();

        StepVerifier.create(namesFlux)
                .expectNext("Adam", "Anna", "Jack", "Jenny")
                .verifyComplete();
    }

    @Test
    public void fluxUsingArray() {
        String[] names = {"Adam", "Anna", "Jack", "Jenny"};

        StepVerifier.create(Flux.fromArray(names).log())
                .expectNext("Adam", "Anna", "Jack", "Jenny")
                .verifyComplete();
    }

    @Test
    public void fluxUsingStream() {
        Flux<String> namesFlux = Flux.fromStream(names.stream());

        StepVerifier.create(namesFlux.log())
                .expectNext("Adam", "Anna", "Jack", "Jenny")
                .verifyComplete();
    }

    @Test
    public void monoUsingJustOrEmpty() {
        Mono<String> mono = Mono.justOrEmpty(null);

        StepVerifier.create(mono.log())
                .verifyComplete();
    }

    @Test
    public void monoUsingSupplier() {
        Supplier<? extends String> supplier = () -> "adam";

        Mono<String> stringMono = Mono.fromSupplier(supplier);

        StepVerifier.create(stringMono.log())
                .expectNext("adam")
                .verifyComplete();
    }

    @Test
    public void fluxUsingRange() {
        Flux<Integer> integerFlux = Flux.range(1, 5);

        StepVerifier.create(integerFlux.log())
                .expectNext(1,2,3,4,5)
                .verifyComplete();
    }
}
