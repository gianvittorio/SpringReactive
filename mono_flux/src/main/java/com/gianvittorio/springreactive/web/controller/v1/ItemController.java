package com.gianvittorio.springreactive.web.controller.v1;

import com.gianvittorio.springreactive.domain.document.Item;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.gianvittorio.springreactive.constants.ItemConstants.ITEM_ENDPOINT_V1;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(ITEM_ENDPOINT_V1)
public class ItemController {

    private final ReactiveMongoRepository repository;

    @GetMapping
    public Flux<Item> getAllItems() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Item>> getOneItem(@PathVariable String id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Item> createItem(@RequestBody Item item) {
        return repository.save(item);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteItem(@PathVariable String id) {
        return repository.deleteById(id);
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Item>> updateItem(@PathVariable String id, @RequestBody Item item) {
        return repository.findById(id)
                .flatMap(obj -> {
                    if (obj instanceof Item) {
                        Item foundItem = (Item)obj;

                        foundItem.setDescription(item.getDescription());
                        foundItem.setPrice(item.getPrice());

                        return repository.save(foundItem);
                    }

                    return Mono.just(obj);
                })
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}
