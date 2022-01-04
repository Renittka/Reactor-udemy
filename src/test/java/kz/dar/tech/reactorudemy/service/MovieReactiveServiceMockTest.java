package kz.dar.tech.reactorudemy.service;

import kz.dar.tech.reactorudemy.exception.MovieException;
import kz.dar.tech.reactorudemy.exception.NetworkException;
import kz.dar.tech.reactorudemy.exception.ServiceException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MovieReactiveServiceMockTest {

    @Mock
    private MovieInfoService movieInfoService;

    @Mock
    private ReviewService reviewService;

    @InjectMocks
    MovieReactiveService movieReactiveService;

    @Test
    void getAllMovies() {
        Mockito.when(movieInfoService.retrieveMoviesFlux())
                .thenCallRealMethod();

        Mockito.when(reviewService.retrieveReviewsFlux(anyLong()))
                .thenCallRealMethod();

        var moviesFlux = movieReactiveService.getAllMovies().log();

        StepVerifier.create(moviesFlux)
                .expectNextCount(3)
                .verifyComplete();
    }

    @Test
    void getAllMovies1() {
        var errorMessage = "Exception occurred in ReviewService";
        Mockito.when(movieInfoService.retrieveMoviesFlux())
                .thenCallRealMethod();

        Mockito.when(reviewService.retrieveReviewsFlux(anyLong()))
                .thenThrow(new RuntimeException(errorMessage));

        var moviesFlux = movieReactiveService.getAllMovies().log();

        StepVerifier.create(moviesFlux)
//                .expectError(MovieException.class)
                .expectErrorMessage(errorMessage)
                .verify();
    }

    @Test
    void getAllMoviesRetry() {
        var errorMessage = "Exception occurred in ReviewService";
        Mockito.when(movieInfoService.retrieveMoviesFlux())
                .thenCallRealMethod();

        Mockito.when(reviewService.retrieveReviewsFlux(anyLong()))
                .thenThrow(new RuntimeException(errorMessage));

        var moviesFlux = movieReactiveService.getAllMoviesRetry().log();

        StepVerifier.create(moviesFlux)
                .expectErrorMessage(errorMessage)
                .verify();

        verify(reviewService, times(4))
                .retrieveReviewsFlux(isA(Long.class));
    }

    @Test
    void getAllMoviesRetryWhen() {
        var errorMessage = "Exception occurred in ReviewService";
        Mockito.when(movieInfoService.retrieveMoviesFlux())
                .thenCallRealMethod();

        Mockito.when(reviewService.retrieveReviewsFlux(anyLong()))
                .thenThrow(new NetworkException(errorMessage));

        var moviesFlux = movieReactiveService.getAllMoviesRetryWhen().log();

        StepVerifier.create(moviesFlux)
                .expectErrorMessage(errorMessage)
                .verify();

        verify(reviewService, times(4))
                .retrieveReviewsFlux(isA(Long.class));
    }

    @Test
    void getAllMoviesRetryWhen1() {
        var errorMessage = "Exception occurred in ReviewService";
        Mockito.when(movieInfoService.retrieveMoviesFlux())
                .thenCallRealMethod();

        Mockito.when(reviewService.retrieveReviewsFlux(anyLong()))
                .thenThrow(new ServiceException(errorMessage));

        var moviesFlux = movieReactiveService.getAllMoviesRetryWhen().log();

        StepVerifier.create(moviesFlux)
                .expectErrorMessage(errorMessage)
                .verify();

        verify(reviewService, times(1))
                .retrieveReviewsFlux(isA(Long.class));
    }

    @Test
    void getAllMoviesRepeat() {
        Mockito.when(movieInfoService.retrieveMoviesFlux())
                .thenCallRealMethod();

        Mockito.when(reviewService.retrieveReviewsFlux(anyLong()))
                .thenCallRealMethod();

        var moviesFlux = movieReactiveService.getAllMoviesRepeat().log();

        StepVerifier.create(moviesFlux)
                .expectNextCount(6)
                .thenCancel()
                .verify();

        verify(reviewService, times(6))
                .retrieveReviewsFlux(isA(Long.class));
    }

    @Test
    void getAllMoviesRepeatN() {
        Mockito.when(movieInfoService.retrieveMoviesFlux())
                .thenCallRealMethod();

        Mockito.when(reviewService.retrieveReviewsFlux(anyLong()))
                .thenCallRealMethod();

        var noOfTimes = 2L;

        var moviesFlux = movieReactiveService.getAllMoviesRepeatN(noOfTimes).log();


        StepVerifier.create(moviesFlux)
                .expectNextCount(9)
                .verifyComplete();

        verify(reviewService, times(9))
                .retrieveReviewsFlux(isA(Long.class));
    }
}