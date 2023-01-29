package com.example.demo.controller;

import com.example.demo.model.Cart;
import com.example.demo.model.Product;
import com.example.demo.model.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class DemoControllerTest {

    public static MockWebServer mockWebServer;

    private DemoController demoController;

    private static final ObjectMapper mapper = new ObjectMapper();

    static Product[] products = new Product[3];
    static Cart[] carts = new Cart[3];
    static User user;

    @BeforeAll
    static void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        products[0] = mapper.readValue("{\"id\":1,\"title\":\"Fjallraven-FoldsackNo.1Backpack,Fits15Laptops\",\"price\":109.95,\"description\":\"Yourperfectpackforeverydayuseandwalksintheforest.Stashyourlaptop(upto15inches)inthepaddedsleeve,youreveryday\",\"category\":\"men'sclothing\",\"image\":\"https://fakestoreapi.com/img/81fPKd-2AYL._AC_SL1500_.jpg\",\"rating\":{\"rate\":3.9,\"count\":120}}", Product.class);
        products[1] = mapper.readValue("{\"id\":2,\"title\":\"MensCasualPremiumSlimFitT-Shirts\",\"price\":22.3,\"description\":\"Slim-fittingstyle,contrastraglanlongsleeve,three-buttonhenleyplacket,lightweight&softfabricforbreathableandcomfortablewearing.AndSolidstitchedshirtswithroundneckmadefordurabilityandagreatfitforcasualfashionwearanddiehardbaseballfans.TheHenleystyleroundnecklineincludesathree-buttonplacket.\",\"category\":\"men'sclothing\",\"image\":\"https://fakestoreapi.com/img/71-3HjGNDUL._AC_SY879._SX._UX._SY._UY_.jpg\",\"rating\":{\"rate\":4.1,\"count\":259}}", Product.class);
        products[2] = mapper.readValue("{\"id\":3,\"title\":\"MensCottonJacket\",\"price\":55.99,\"description\":\"greatouterwearjacketsforSpring/Autumn/Winter,suitableformanyoccasions,suchasworking,hiking,camping,mountain/rockclimbing,cycling,travelingorotheroutdoors.Goodgiftchoiceforyouoryourfamilymember.AwarmheartedlovetoFather,husbandorsoninthisthanksgivingorChristmasDay.\",\"category\":\"men'sclothing\",\"image\":\"https://fakestoreapi.com/img/71li-ujtlUL._AC_UX679_.jpg\",\"rating\":{\"rate\":4.7,\"count\":500}}", Product.class);

        carts[0] = mapper.readValue("{\"id\":1,\"userId\":1,\"date\":\"2020-03-02T00:00:02.000Z\",\"products\":[{\"productId\":1,\"quantity\":4},{\"productId\":2,\"quantity\":1},{\"productId\":3,\"quantity\":6}],\"__v\":0}", Cart.class);
        carts[1] = mapper.readValue("{\"id\":3,\"userId\":2,\"date\":\"2020-03-01T00:00:02.000Z\",\"products\":[{\"productId\":1,\"quantity\":2},{\"productId\":9,\"quantity\":1}],\"__v\":0}", Cart.class);
        carts[2] = mapper.readValue("{\"id\":4,\"userId\":3,\"date\":\"2020-01-01T00:00:02.000Z\",\"products\":[{\"productId\":1,\"quantity\":4}],\"__v\":0}", Cart.class);

        user = mapper.readValue("{\"address\":{\"geolocation\":{\"lat\":\"-37.3159\",\"long\":\"81.1496\"},\"city\":\"kilcoole\",\"street\":\"newroad\",\"number\":7682,\"zipcode\":\"12926-3874\"},\"id\":1,\"email\":\"john@gmail.com\",\"username\":\"johnd\",\"password\":\"m38rmF$\",\"name\":{\"firstname\":\"john\",\"lastname\":\"doe\"},\"phone\":\"1-570-236-7033\",\"__v\":0}", User.class);
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @BeforeEach
    void initialize() {
        String baseUrl = String.format("http://localhost:%s",
                mockWebServer.getPort());
        WebClient webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .build();
        this.demoController = new DemoController(webClient);
    }

    @Test
    void getAllProductsSuccessTest() throws JsonProcessingException {
        mockWebServer.enqueue(new MockResponse()
                .setBody(mapper.writeValueAsString(products))
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

        Mono<Product[]> productsMono = this.demoController.getAllProducts();

        StepVerifier.create(productsMono)
                .expectNextMatches(productsArray -> productsArray[2].getId() == 3)
                .verifyComplete();
    }

    @Test
    void getAllProductsAndCartSuccessTest() throws JsonProcessingException {
        mockWebServer.enqueue(new MockResponse()
                .setBody(mapper.writeValueAsString(products))
                .setResponseCode(200)
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

        mockWebServer.enqueue(new MockResponse()
                .setBody(mapper.writeValueAsString(carts))
                .setResponseCode(200)
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

        Mono<DemoController.ProductAndCart> productAndCartMono = this.demoController.getAllProductsAndCarts();

        StepVerifier.create(productAndCartMono)
                .expectNextMatches(productAndCart -> productAndCart.getProducts()[0].getId() == 1)
                .verifyComplete();
    }

    @Test
    void getUserAndProductByIdSuccessTest() throws JsonProcessingException {
        mockWebServer.enqueue(new MockResponse()
                .setBody(mapper.writeValueAsString(user))
                .setResponseCode(200)
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

        mockWebServer.enqueue(new MockResponse()
                .setBody(mapper.writeValueAsString(products[2]))
                .setResponseCode(200)
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

        Mono<DemoController.UserAndProduct> userAndProductMono = this.demoController.getUserAndProductById("100");

        StepVerifier.create(userAndProductMono)
                .expectNextMatches(userAndProduct ->
                        userAndProduct.getUser().getId() == 1 &&
                                userAndProduct.getProduct().getTitle().equals("MensCottonJacket"))
                .verifyComplete();
    }

    @Test
    void getUserAndProductByIdProductFailureTest() throws JsonProcessingException {
        mockWebServer.enqueue(new MockResponse()
                .setBody(mapper.writeValueAsString(user))
                .setResponseCode(HttpStatus.OK.value())
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

        mockWebServer.enqueue(new MockResponse()
                .setBody("{}")
                .setResponseCode(HttpStatus.NOT_FOUND.value())
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

        Mono<DemoController.UserAndProduct> userAndProductMono = this.demoController.getUserAndProductById("100");

        StepVerifier.create(userAndProductMono)
                .verifyErrorMessage("Product Error");
    }

    @Test
    void getUserAndProductSuccessBlock() throws JsonProcessingException {
        mockWebServer.enqueue(new MockResponse()
                .setBody(mapper.writeValueAsString(user))
                .setResponseCode(HttpStatus.NOT_FOUND.value())
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

        mockWebServer.enqueue(new MockResponse()
                .setBody(mapper.writeValueAsString(products[2]))
                .setResponseCode(HttpStatus.OK.value())
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

        assertEquals("User Error",
                assertThrows(RuntimeException.class, () -> this.demoController.getUserAndProductBlocked("100"))
                        .getMessage());

    }

    @Test
    void getUserAndProductFailureDispatcher() {
        final Dispatcher dispatcher = new Dispatcher() {
            @NotNull
            @Override
            public MockResponse dispatch(@NotNull RecordedRequest recordedRequest) {
                switch (Objects.requireNonNull(recordedRequest.getPath())) {
                    case "/users/100":
                        try {
                            return new MockResponse()
                                    .setBody(mapper.writeValueAsString(user))
                                    .setResponseCode(HttpStatus.OK.value())
                                    .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
                        } catch (JsonProcessingException e) {
                            throw new RuntimeException(e);
                        }
                    case "/products/1":
                        try {
                            return new MockResponse()
                                    .setBody(mapper.writeValueAsString(products[2]))
                                    .setResponseCode(HttpStatus.NOT_FOUND.value())
                                    .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
                        } catch (JsonProcessingException e) {
                            throw new RuntimeException(e);
                        }
                }
                return new MockResponse().setBody("{}").setResponseCode(404);
            }
        };
        mockWebServer.setDispatcher(dispatcher);

        assertThrows(RuntimeException.class, () -> this.demoController.getUserAndProductBlocked("100"));
    }


}

















