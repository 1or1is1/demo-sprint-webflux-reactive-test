package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class Product {

    @JsonAlias("productId")
    private Long id;
    @JsonProperty("title")
    private String title;
    @JsonProperty("price")
    private Double price;
    @JsonProperty("description")
    private String description;
    @JsonProperty("category")
    private String category;
    @JsonProperty("image")
    private String image;
    @JsonProperty("rating")
    private Rating rating;

    @JsonAlias("quantity")
    private Long quantity;

}
