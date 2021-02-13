package com.gianvittorio.springreactive.web.controller.v1;

import com.gianvittorio.springreactive.constants.ItemConstants;
import com.gianvittorio.springreactive.dao.repository.ItemReactiveRepository;
import com.gianvittorio.springreactive.domain.document.Item;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static com.gianvittorio.springreactive.constants.ItemConstants.ITEM_ENDPOINT_V1;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext
@AutoConfigureWebTestClient
@ActiveProfiles("test")
public class ItemControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ItemReactiveRepository itemReactiveRepository;

    @BeforeEach
    public void setUp() {
        itemReactiveRepository.deleteAll()
                .thenMany(Flux.fromIterable(getItemList()))
                .flatMap(itemReactiveRepository::save)
                .doOnNext(item -> System.out.println("Inserted item is: " + item))
                .blockLast();
    }

    @Test
    public void getAllItems() {
        webTestClient.get()
                .uri(ITEM_ENDPOINT_V1)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Item.class)
                .hasSize(4);
    }

    @Test
    public void getAllItemsApproach2() {
        webTestClient.get()
                .uri(ITEM_ENDPOINT_V1)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Item.class)
                .hasSize(4)
                .consumeWith(response -> {

                    List<Item> items = response.getResponseBody();

                    items.forEach(
                            item -> org.assertj.core.api.Assertions.assertThat(item.getId()).isNotNull()
                    );
                });
    }

    @Test
    public void getAllItemsApproach3() {
        Flux<Item> itemsFlux = webTestClient.get()
                .uri(ITEM_ENDPOINT_V1)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .returnResult(Item.class)
                .getResponseBody();

        StepVerifier.create(itemsFlux.log())
                .expectSubscription()
                .expectNextCount(4)
                .expectComplete();
    }

    @Test
    public void getOneItem() {
        webTestClient.get()
                .uri(ITEM_ENDPOINT_V1.concat("/{id}"), "ABC")
                .exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath("$.price", 149.99);
    }

    @Test
    public void getItemNotFound() {
        webTestClient.get()
                .uri(ITEM_ENDPOINT_V1.concat("/{id}"), "DEF")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    public void createItemTest() {
        Item item = Item.create()
                .description("Amazon Fire Stick")
                .price(39.99)
                .build();

        webTestClient.post()
                .uri(ITEM_ENDPOINT_V1)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(item), Item.class)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.description").isEqualTo(item.getDescription())
                .jsonPath("$.price").isEqualTo(item.getPrice());
    }

    @Test
    public void deleteItemTest() {
        webTestClient.delete()
                .uri(ITEM_ENDPOINT_V1.concat("/{id}"), "ABC")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNoContent()
                .expectBody(Void.class);
    }

    @Test
    public void updateItemTest() {
        double newPrice = 129.99;
        Item item = Item.create()
                .price(newPrice)
                .build();

        webTestClient.put()
                .uri(ITEM_ENDPOINT_V1.concat("/{id}"), "ABC")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(item), Item.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.price").isEqualTo(newPrice);
    }

    @Test
    public void updateItemTestNotFound() {
        double newPrice = 129.99;
        Item item = Item.create()
                .price(newPrice)
                .build();

        webTestClient.put()
                .uri(ITEM_ENDPOINT_V1.concat("/{id}"), "DEF")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(item), Item.class)
                .exchange()
                .expectStatus().isNotFound();
    }

    public static List<? extends Item> getItemList() {
        return List.of(
                Item.create().description("Samsumg TV").price(399.99).build(),
                Item.create().description("LG TV").price(329.99).build(),
                Item.create().description("Apple Watch").price(349.99).build(),
                Item.create().id("ABC").description("Beats Headphones").price(149.99).build()
        );
    }
}
