package kz.dar.tech.reactorudemy.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;

class MovieReactiveServiceRestClientTest {
    WebClient webClient = WebClient.builder()
            .baseUrl("http://localhost:8080/movies")
            .build();

    private MovieInfoService movieInfoService = new MovieInfoService(webClient);

    private ReviewService reviewService = new ReviewService(webClient);

    private MovieReactiveService movieReactiveService = new MovieReactiveService(movieInfoService, reviewService);


    @Test
    void getAllMoviesRestClient() {
        var moviesFlux = movieReactiveService.getAllMoviesRestClient();
        StepVerifier.create(moviesFlux)
                .expectNextCount(7)
                .verifyComplete();
    }

    @Test
    void getMovieByIdRestClient() {
        var movieMono = movieReactiveService.getMovieByIdRestClient(1L);
        StepVerifier.create(movieMono)
                .expectNextCount(1)
                .verifyComplete();
    }
}