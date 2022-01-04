package kz.dar.tech.reactorudemy.service;

import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class MovieReactiveServiceTest {
    private MovieInfoService movieInfoService = new MovieInfoService();
    private ReviewService reviewService = new ReviewService();
    private RevenueService revenueService = new RevenueService();
    private MovieReactiveService movieReactiveService =
            new MovieReactiveService(movieInfoService, reviewService, revenueService);

    @Test
    void getAllMovies() {
        var moviesInfo = movieReactiveService.getAllMovies().log();

        StepVerifier.create(moviesInfo)
                .assertNext(movieInfo -> {
                    assertEquals("Batman Begins", movieInfo.getMovieInfo().getName());
                    assertEquals(movieInfo.getReviewList().size(), 2);

                })
                .assertNext(movieInfo -> {
                    assertEquals("The Dark Knight", movieInfo.getMovieInfo().getName());
                    assertEquals(movieInfo.getReviewList().size(), 2);
                })
                .assertNext(movieInfo -> {
                    assertEquals("Dark Knight Rises", movieInfo.getMovieInfo().getName());
                    assertEquals(movieInfo.getReviewList().size(), 2);
                })
                .verifyComplete();
    }

    @Test
    void getMovieById() {
        var movieMono = movieReactiveService.getMovieById(100L).log();
        StepVerifier.create(movieMono)
                .assertNext(movieInfo -> {
                    assertEquals("Batman Begins", movieInfo.getMovieInfo().getName());
                    assertEquals(movieInfo.getReviewList().size(), 2);

                })
                .verifyComplete();
    }

    @Test
    void getMovieByIdFlatMap() {
        var movieMono = movieReactiveService.getMovieById(100L).log();
        StepVerifier.create(movieMono)
                .assertNext(movieInfo -> {
                    assertEquals("Batman Begins", movieInfo.getMovieInfo().getName());
                    assertEquals(movieInfo.getReviewList().size(), 2);

                })
                .verifyComplete();
    }

    @Test
    void getMovieByIdWithRevenue() {
        var movieMono = movieReactiveService.getMovieByIdWithRevenue(100L).log();
        StepVerifier.create(movieMono)
                .assertNext(movieInfo -> {
                    assertEquals("Batman Begins", movieInfo.getMovieInfo().getName());
                    assertEquals(movieInfo.getReviewList().size(), 2);
                    assertNotNull(movieInfo.getRevenue());
                })
                .verifyComplete();
    }
}