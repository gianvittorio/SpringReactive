package com.gianvittorio.springreactive.web.handler;

import com.gianvittorio.springreactive.dao.repository.ItemReactiveRepository;
import com.gianvittorio.springreactive.domain.document.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.BodyInserters.fromObject;

@Component
@RequiredArgsConstructor
public class ItemsHandler {

    private final ItemReactiveRepository itemReactiveRepository;

    private static Mono<ServerResponse> notFound = ServerResponse.notFound().build();

    public Mono<ServerResponse> getAllItems(ServerRequest serverRequest) {

        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(itemReactiveRepository.findAll(), Item.class);
    }

    public Mono<ServerResponse> getOneItem(ServerRequest serverRequest) {

        String id = serverRequest.pathVariable("id");
        Mono<Item> itemMono = itemReactiveRepository.findById(id);

        return itemMono.flatMap(item ->
                ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(fromObject(item)))
                .switchIfEmpty(notFound);
    }

    public Mono<ServerResponse> createItem(ServerRequest serverRequest) {

        return serverRequest.bodyToMono(Item.class)
                .flatMap(item ->
                        ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(itemReactiveRepository.save(item), Item.class)
                );
    }

    public Mono<ServerResponse> deleteItem(ServerRequest serverRequest) {
        String id = serverRequest.pathVariable("id");

        Mono<Void> deleteItem = itemReactiveRepository.deleteById(id);

        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(deleteItem, Void.class);
    }

    public Mono<ServerResponse> updateItem(ServerRequest serverRequest) {
        String id = serverRequest.pathVariable("id");

        Mono<Item> updatedItem = serverRequest.bodyToMono(Item.class)
                .flatMap(item -> {
                    Mono<Item> itemMono = itemReactiveRepository.findById(id)
                            .flatMap(savedItem -> {
                                savedItem.setPrice(item.getPrice());
                                savedItem.setDescription(item.getDescription());

                                return itemReactiveRepository.save(savedItem);
                            });

                    return itemMono;
                });

        return updatedItem.flatMap(item ->
                ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(updatedItem, Item.class))
                .switchIfEmpty(notFound);
    }
}
