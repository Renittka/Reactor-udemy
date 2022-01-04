package kz.dar.tech.reactorudemy.service;

import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;

class ReviewServiceTest {
    WebClient webClient = WebClient.builder()
            .baseUrl("http://localhost:8080/movies")
            .build();
    ReviewService reviewService = new ReviewService(webClient);


    @Test
    void retrieveReviewsFluxRestClient() {
        long reviewId = 1L;

        var reviewsFlux = reviewService.retrieveReviewsFluxRestClient(reviewId);

        StepVerifier.create(reviewsFlux)
                .assertNext(review -> {
                    assertEquals("Nolan is the real superhero", review.getComment());
                })
                .verifyComplete();
    }
}