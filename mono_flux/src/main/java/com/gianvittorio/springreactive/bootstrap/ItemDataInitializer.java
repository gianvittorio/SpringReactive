package com.gianvittorio.springreactive.bootstrap;

import com.gianvittorio.springreactive.domain.document.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.List;

@Component
@RequiredArgsConstructor
@Profile("!test")
public class ItemDataInitializer implements CommandLineRunner {

    private final ReactiveMongoRepository repository;

    @Override
    public void run(String... args) throws Exception {
        initaldatasetup();
    }

    private void initaldatasetup() {
        repository.deleteAll()
                .thenMany(Flux.fromIterable(itemList()))
                .flatMap(repository::save)
                .thenMany(repository.findAll())
                .subscribe(item -> System.out.println("Item inserted from CommandLineRunner: " + item));
    }

    private static List<? extends Item> itemList() {
        return List.of(
                Item.create().description("Samsumg TV").price(399.99).build(),
                Item.create().description("LG TV").price(329.99).build(),
                Item.create().description("Apple Watch").price(349.99).build(),
                Item.create().id("ABC").description("Beats Headphones").price(19.99).build()
        );
    }
}
