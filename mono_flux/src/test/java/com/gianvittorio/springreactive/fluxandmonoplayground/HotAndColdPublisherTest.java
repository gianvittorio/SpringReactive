package com.gianvittorio.springreactive.fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class HotAndColdPublisherTest {

    @Test
    public void coldPublisherTest() throws InterruptedException {
        Flux<String> stringFlux = Flux.just("A", "B", "C", "D", "E", "F")
                .delayElements(Duration.ofSeconds(1));

        stringFlux.subscribe(s -> System.out.println("Subscriber 1 : " + s));

        TimeUnit.SECONDS.sleep(2);

        stringFlux.subscribe(s -> System.out.println("Subscriber 2 : " + s));

        TimeUnit.SECONDS.sleep(4);
    }

    @Test
    public void hotPublisherTest() throws InterruptedException {
        Flux<String> stringFlux = Flux.just("A", "B", "C", "D", "E", "F")
                .delayElements(Duration.ofSeconds(1));

        ConnectableFlux<String> connectableFlux = stringFlux.publish();
        connectableFlux.connect();

        connectableFlux.subscribe(s -> System.out.println("Subscriber 1 : " + s));
        TimeUnit.SECONDS.sleep(3);

        connectableFlux.subscribe(s -> System.out.println("Subscriber 2 : " + s));
        TimeUnit.SECONDS.sleep(4);
    }
}
