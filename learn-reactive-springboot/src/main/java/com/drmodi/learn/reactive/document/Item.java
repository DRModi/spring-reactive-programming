package com.drmodi.learn.reactive.document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Item {

    @Id
    private String id;
    private String description;
    private Double price;

/*
    public Item(String id, String description, Double price){
        this.id = id;
        this.description = description;
        this.price = price;
    }
*/


}
