package com.gianvittorio.springreactive.fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static reactor.core.scheduler.Schedulers.parallel;

public class FluxAndMonoTransformTest {
    List<String> names = List.of("adam", "anna", "jack", "jenny");

    @Test
    public void transformUsingMap() {
        Flux<String> namesFlux = Flux.fromIterable(names)
                .map(String::toUpperCase)
                .log();

        StepVerifier.create(namesFlux)
                .expectNext("ADAM", "ANNA", "JACK", "JENNY")
                .verifyComplete();
    }

    @Test
    public void transformUsingLength() {
        Flux<Integer> namesFlux = Flux.fromIterable(names)
                .map(String::length)
                .log();

        StepVerifier.create(namesFlux)
                .expectNext(4, 4, 4, 5)
                .verifyComplete();
    }

    @Test
    public void transformUsingLengthRepeat() {
        Flux<Integer> namesFlux = Flux.fromIterable(names)
                .map(String::length)
                .repeat(1)
                .log();

        StepVerifier.create(namesFlux)
                .expectNext(4, 4, 4, 5, 4, 4, 4, 5)
                .verifyComplete();
    }

    @Test
    public void transformUsingLengthRepeatFilter() {
        Flux<String> namesFlux = Flux.fromIterable(names)
                .filter(s -> s.length() > 4)
                .map(String::toUpperCase)
                .log();

        StepVerifier.create(namesFlux)
                .expectNext("JENNY")
                .verifyComplete();
    }

    @Test
    public void transformUsingFlatMap() {
        Flux<String> stringFlux = Flux.fromIterable(Arrays.asList("A", "B", "C", "D", "E", "F"))
                .flatMap(
                        s -> Flux.fromIterable(convertToList(s))
                )
                .log();

        StepVerifier.create(stringFlux)
                .expectNextCount(12)
                .verifyComplete();
    }

    @Test
    public void transformUsingFlatMapUsingParallel() {
        Flux<String> stringFlux = Flux.fromIterable(Arrays.asList("A", "B", "C", "D", "E", "F"))
                .window(2)
                .flatMap(s -> s.map(FluxAndMonoTransformTest::convertToList).subscribeOn(parallel()))
                .flatMap(Flux::fromIterable)
                .log();

        StepVerifier.create(stringFlux)
                .expectNextCount(12)
                .verifyComplete();
    }

    @Test
    public void transformUsingFlatMapParallel() {
        Flux<String> stringFlux = Flux.fromIterable(Arrays.asList("A", "B", "C", "D", "E", "F"))
                .window(2)
                .concatMap(s -> s.map(FluxAndMonoTransformTest::convertToList).subscribeOn(parallel()))
                .flatMap(Flux::fromIterable)
                .log();

        StepVerifier.create(stringFlux)
                .expectNextCount(12)
                .verifyComplete();
    }

    @Test
    public void transformUsingFlatMapParallelMaintainOrder() {
        Flux<String> stringFlux = Flux.fromIterable(Arrays.asList("A", "B", "C", "D", "E", "F"))
                .window(2)
//                .concatMap(s -> s.map(FluxAndMonoTransformTest::convertToList).subscribeOn(parallel()))
                .flatMapSequential(s -> s.map(FluxAndMonoTransformTest::convertToList).subscribeOn(parallel()))
                .flatMap(Flux::fromIterable)
                .log();

        StepVerifier.create(stringFlux)
                .expectNextCount(12)
                .verifyComplete();
    }

    private static List<String> convertToList(String s) {
        try {
            TimeUnit.MILLISECONDS.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return Arrays.asList(s, "newValue");
    }

}
