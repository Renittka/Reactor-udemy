package kz.dar.tech.reactorudemy.service;

import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;

class MovieInfoServiceTest {

    WebClient webClient = WebClient.builder()
            .baseUrl("http://localhost:8080/movies")
            .build();

    MovieInfoService movieInfoService = new MovieInfoService(webClient);

    MovieReactiveService movieReactiveService = new MovieReactiveService();

    @Test
    void retrieveAllMovieInfoRestClient() {
        var movieInfoFlux = movieInfoService.retrieveAllMovieInfoRestClient().log();
        StepVerifier.create(movieInfoFlux)
                .expectNextCount(7)
                .verifyComplete();
    }

    @Test
    void retrieveMovieInfoByIdRestClient() {
        var movieInfoId = 1;

        var movieMono = movieReactiveService.getMovieById(movieInfoId);

        StepVerifier.create(movieMono)
                .assertNext(movie -> {
                    assertEquals("Batman Begins", movie.getMovieInfo().getName());
                    assertEquals(movie.getReviewList().size(), 1);
                })
                .verifyComplete();
    }
}