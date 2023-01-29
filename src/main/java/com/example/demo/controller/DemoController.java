package com.example.demo.controller;

import com.example.demo.model.Cart;
import com.example.demo.model.Product;
import com.example.demo.model.User;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Arrays;

@RestController
@RequestMapping(path = "api")
public class DemoController {

    @Getter
    @Setter
    @ToString
    public static class ProductAndCart {
        private Product[] products;
        private Cart[] carts;
    }

    @Getter
    @Setter
    @ToString
    public static class UserAndProduct {
        private User user;
        private Product product;
    }

    private final WebClient webClient;

    public DemoController(WebClient webClient) {
        this.webClient = webClient;
    }

    @GetMapping("/productsAndCarts")
    public Mono<ProductAndCart> getAllProductsAndCarts() {
        Mono<Product[]> allProducts = this.getAllProducts();
        Mono<Cart[]> allCarts = this.getAllCarts();
        return Mono.zip(allProducts, allCarts).map(tuple -> {
            Arrays.stream(tuple.getT1()).forEach(System.out::println);
            Arrays.stream(tuple.getT2()).forEach(System.out::println);
            ProductAndCart productAndCart = new ProductAndCart();
            productAndCart.setProducts(tuple.getT1());
            productAndCart.setCarts(tuple.getT2());
            return productAndCart;
        });
    }

    @GetMapping("/products")
    public Mono<Product[]> getAllProducts() {
        return this.webClient
                .get()
                .uri("/products")
                .retrieve()
                .bodyToMono(Product[].class)
                .map(products -> {
                    Arrays.stream(products).forEach(product -> {
                        System.out.println("FROM SINGLE PRODUCT :: " + product);
                    });
                    return products;
                });
    }

    @GetMapping("/carts")
    public Mono<Cart[]> getAllCarts() {
        return this.webClient
                .get()
                .uri("/carts")
                .retrieve()
                .bodyToMono(Cart[].class)
                .map(carts -> {
                    Arrays.stream(carts).forEach(cart -> {
                        System.out.println("FROM SINGLE CART :: " + cart);
                    });
                    return carts;
                });
    }

    @GetMapping("/users")
    public Mono<User[]> getAllUsers() {
        return this.webClient
                .get()
                .uri("/users")
                .retrieve()
                .bodyToMono(User[].class);
    }

    @GetMapping("/users/{id}")
    public Mono<User> getUserById(@PathVariable("id") String id) {
        return this.webClient
                .get()
                .uri("/users/{id}", id)
                .retrieve()
                .onStatus(HttpStatus::isError, clientResponse -> Mono.error(new RuntimeException("User Error")))
                .bodyToMono(User.class)
                .map(user -> {
                    System.out.println("Checking User... : " + user);
                    return user;
                });
    }

    @GetMapping("/products/{id}")
    public Mono<Product> getProductById(@PathVariable("id") String id) {
        return this.webClient
                .get()
                .uri("/products/{productId}", id)
                .retrieve()
                .onStatus(HttpStatus::isError, clientResponse -> Mono.error(new RuntimeException("Product Error")))
                .bodyToMono(Product.class)
                .map(product -> {
                    System.out.println("Checking product... : " + product);
                    return product;
                });
    }

    @GetMapping("/userAndProduct/{id}")
    public Mono<UserAndProduct> getUserAndProductById(@PathVariable("id") String userId) {
        UserAndProduct userAndProduct = new UserAndProduct();
        Mono<User> userMono = this.getUserById(userId);
        return userMono
                .map(user -> {
                    userAndProduct.setUser(user);
                    return user;
                })
                .flatMap(user -> this.getProductById(user.getId().toString()))
                .map(product -> {
                    userAndProduct.setProduct(product);
                    return userAndProduct;
                });
    }

    @GetMapping("/userAndProduct/block/{id}")
    public UserAndProduct getUserAndProductBlocked(@PathVariable("id") String userId) {
        return this.getUserAndProductById(userId).block();
    }

}
