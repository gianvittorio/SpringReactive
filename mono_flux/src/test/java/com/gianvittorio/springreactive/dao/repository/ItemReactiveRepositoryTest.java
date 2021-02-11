package com.gianvittorio.springreactive.dao.repository;

import com.gianvittorio.springreactive.domain.document.Item;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

@DataMongoTest
@ExtendWith(SpringExtension.class)
@DirtiesContext
public class ItemReactiveRepositoryTest {

    @Autowired
    private ItemReactiveRepository itemReactiveRepository;

    private List<Item> itemList = Arrays.asList(
            Item.create().description("Samsumg TV").price(400.0).build(),
            Item.create().description("LG TV").price(299.0).build(),
            Item.create().description("Apple Watch").price(400.0).build(),
            Item.create().description("Beats Headphones").price(149.0).build(),
            Item.create().id("ABC").description("Bose Headphones").price(149.0).build()
    );

    @BeforeEach
    public void setUp() {
        itemReactiveRepository.deleteAll()
                .thenMany(Flux.fromIterable(itemList))
                .flatMap(itemReactiveRepository::save)
                .doOnNext(item -> System.out.println("Inserted item is: " + item))
                .blockLast();
    }

    @Test
    public void getAllItems() {
        StepVerifier.create(itemReactiveRepository.findAll())
                .expectSubscription()
                .expectNextCount(5)
                .verifyComplete();

    }

    @Test
    public void getItemById() {
        StepVerifier.create(itemReactiveRepository.findById("ABC"))
                .expectSubscription()
                .expectNextMatches(
                        item -> item.getDescription().equals("Bose Headphones") &&
                                item.getPrice().equals(149.0)
                )
                .verifyComplete();
    }

    @Test
    public void findItemByDescription() {
        StepVerifier.create(itemReactiveRepository.findByDescription("Bose Headphones")
                .log("findItemByDescription"))
                .expectSubscription()
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    public void saveItem() {
        Item item = Item.create()
                .description("Google Home Mini")
                .price(30.0)
                .build();

        Mono<Item> savedItem = itemReactiveRepository.save(item);

        StepVerifier.create(savedItem.log("savedItem: "))
                .expectSubscription()
                .expectNextMatches(item1 -> item1.getId() != null && item1.getDescription().equals("Google Home Mini"))
                .verifyComplete();
    }

    @Test
    public void updateItem() {
        double newPrice = 250.0;

        Flux<Item> updatedItemFlux = itemReactiveRepository.findByDescription("LG TV")
                .map(item -> {
                    item.setPrice(newPrice);
                    return item;
                })
                .flatMap(itemReactiveRepository::save);

        StepVerifier.create(updatedItemFlux)
                .expectSubscription()
                .expectNextMatches(item -> item.getPrice().equals(newPrice))
                .verifyComplete();
    }

    @Test
    public void deleteItemById() {
        String expectedId = "ABC";

        Mono<Void> itemMono = itemReactiveRepository.findById(expectedId)
                .map(Item::getId)
                .flatMap(itemReactiveRepository::deleteById);

        StepVerifier.create(itemMono.log())
                .expectSubscription()
                .expectNextCount(0)
                .verifyComplete();

        StepVerifier.create(itemReactiveRepository.findAll().log())
                .expectSubscription()
                .expectNextCount(4)
                .expectComplete();
    }

    @Test
    public void deleteItemByDescription() {
        String expectedDescription = "LG TV";

        Flux<Void> itemMono = itemReactiveRepository.findByDescription(expectedDescription)
                .flatMap(itemReactiveRepository::delete);

        StepVerifier.create(itemMono.log())
                .expectSubscription()
                .expectNextCount(0)
                .expectComplete();

        StepVerifier.create(itemReactiveRepository.findAll().log())
                .expectSubscription()
                .expectNextCount(4)
                .expectComplete();
    }
}
