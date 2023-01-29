package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class Cart {

    @JsonProperty("id")
    private Long id;
    @JsonProperty("userId")
    private Long userId;
    @JsonProperty("date")
    private String date;
    @JsonProperty("products")
    private List<Product> products = null;
    @JsonProperty("__v")
    private Long v;

}
