package com.learnreactivespring.itemclient.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(builderMethodName = "create")
@NoArgsConstructor
@AllArgsConstructor
public class Item {

    private String id;

    private String description;

    private Double price;
}
