package com.gianvittorio.springreactive.domain.document;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder(builderMethodName = "create")
@NoArgsConstructor
@AllArgsConstructor
@Document
public class Item {

    @Id
    private String id;

    private String description;

    private Double price;
}
