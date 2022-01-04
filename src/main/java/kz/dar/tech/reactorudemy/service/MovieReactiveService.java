package kz.dar.tech.reactorudemy.service;

import kz.dar.tech.reactorudemy.domain.Movie;
import kz.dar.tech.reactorudemy.domain.Review;
import kz.dar.tech.reactorudemy.exception.MovieException;
import kz.dar.tech.reactorudemy.exception.NetworkException;
import kz.dar.tech.reactorudemy.exception.ServiceException;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.Exceptions;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.retry.Retry;
import reactor.util.retry.RetryBackoffSpec;

import javax.management.ConstructorParameters;
import java.time.Duration;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class MovieReactiveService {
    @Autowired
    private MovieInfoService movieInfoService;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private RevenueService revenueService;

    public MovieReactiveService(MovieInfoService movieInfoService, ReviewService reviewService) {
        this.movieInfoService = movieInfoService;
        this.reviewService = reviewService;
    }

    public Flux<Movie> getAllMovies() {
        var moviesInfoFlux = movieInfoService.retrieveMoviesFlux();
        return moviesInfoFlux
                .flatMap(movieInfo -> {
                    Mono<List<Review>> reviewsMono = reviewService.retrieveReviewsFlux(movieInfo.getMovieInfoId())
                            .collectList();
                    return reviewsMono
                            .map(reviewsList -> new Movie(movieInfo, reviewsList));
                })
                .onErrorMap(ex -> {
                    log.error("Exception is: " + ex);
                    throw new MovieException(ex.getMessage());
                });
    }

    public Flux<Movie> getAllMoviesRestClient() {
        var moviesInfoFlux = movieInfoService.retrieveAllMovieInfoRestClient();
        return moviesInfoFlux
                .flatMap(movieInfo -> {
                    Mono<List<Review>> reviewsMono = reviewService.retrieveReviewsFluxRestClient(movieInfo.getMovieInfoId())
                            .collectList();
                    return reviewsMono
                            .map(reviewsList -> new Movie(movieInfo, reviewsList));
                })
                .onErrorMap(ex -> {
                    log.error("Exception is: " + ex);
                    throw new MovieException(ex.getMessage());
                });
    }

    public Mono<Movie> getMovieById(long movieId) {
        var movieInfoMono = movieInfoService.retrieveMovieInfoMonoUsingId(movieId);
        var reviewsFlux = reviewService.retrieveReviewsFlux(movieId)
                .collectList();
        return movieInfoMono
                .zipWith(reviewsFlux, (movieInfo, review) -> new Movie(movieInfo, review));
    }

    public Mono<Movie> getMovieByIdRestClient(long movieId) {
        var movieInfoMono = movieInfoService.retrieveMovieInfoByIdRestClient(movieId);
        var reviewsFlux = reviewService.retrieveReviewsFluxRestClient(movieId)
                .collectList();
        return movieInfoMono
                .zipWith(reviewsFlux, (movieInfo, review) -> new Movie(movieInfo, review));
    }

    public Mono<Movie> getMovieByIdFlatMap(long movieId) {
        var movieInfoMono = movieInfoService.retrieveMovieInfoMonoUsingId(movieId);
        var reviewsFlux = reviewService.retrieveReviewsFlux(movieId)
                .collectList();
        return movieInfoMono
                .flatMap(movieInfo -> reviewsFlux
                        .map(reviewsList -> new Movie(movieInfo, reviewsList)));
    }

    public Flux<Movie> getAllMoviesRetry() {
        var moviesInfoFlux = movieInfoService.retrieveMoviesFlux();
        return moviesInfoFlux
                .flatMap(movieInfo -> {
                    Mono<List<Review>> reviewsMono = reviewService.retrieveReviewsFlux(movieInfo.getMovieInfoId())
                            .collectList();
                    return reviewsMono
                            .map(reviewsList -> new Movie(movieInfo, reviewsList));
                })
                .onErrorMap(ex -> {
                    log.error("Exception is: " + ex);
                    throw new MovieException(ex.getMessage());
                })
                .retry(3);
    }

    public Flux<Movie> getAllMoviesRetryWhen() {
        var moviesInfoFlux = movieInfoService.retrieveMoviesFlux();
        return moviesInfoFlux
                .flatMap(movieInfo -> {
                    Mono<List<Review>> reviewsMono = reviewService.retrieveReviewsFlux(movieInfo.getMovieInfoId())
                            .collectList();
                    return reviewsMono
                            .map(reviewsList -> new Movie(movieInfo, reviewsList));
                })
                .onErrorMap(ex -> {
                    log.error("Exception is: " + ex);
                    if (ex instanceof NetworkException)
                        throw new MovieException(ex.getMessage());
                    else
                        throw new ServiceException(ex.getMessage());
                })
                .retryWhen(getRetryBackoffSpec());
    }

    private RetryBackoffSpec getRetryBackoffSpec() {
        return Retry.fixedDelay(3, Duration.ofMillis(500))
                .filter(ex -> ex instanceof MovieException)
                .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) ->
                        Exceptions.propagate(retrySignal.failure()));
    }

    public Flux<Movie> getAllMoviesRepeat() {
        var moviesInfoFlux = movieInfoService.retrieveMoviesFlux();
        return moviesInfoFlux
                .flatMap(movieInfo -> {
                    Mono<List<Review>> reviewsMono = reviewService.retrieveReviewsFlux(movieInfo.getMovieInfoId())
                            .collectList();
                    return reviewsMono
                            .map(reviewsList -> new Movie(movieInfo, reviewsList));
                })
                .onErrorMap(ex -> {
                    log.error("Exception is: " + ex);
                    if (ex instanceof NetworkException)
                        throw new MovieException(ex.getMessage());
                    else
                        throw new ServiceException(ex.getMessage());
                })
                .retryWhen(getRetryBackoffSpec())
                .repeat();
    }

    public Flux<Movie> getAllMoviesRepeatN(long n) {
        var moviesInfoFlux = movieInfoService.retrieveMoviesFlux();
        return moviesInfoFlux
                .flatMap(movieInfo -> {
                    Mono<List<Review>> reviewsMono = reviewService.retrieveReviewsFlux(movieInfo.getMovieInfoId())
                            .collectList();
                    return reviewsMono
                            .map(reviewsList -> new Movie(movieInfo, reviewsList));
                })
                .onErrorMap(ex -> {
                    log.error("Exception is: " + ex);
                    if (ex instanceof NetworkException)
                        throw new MovieException(ex.getMessage());
                    else
                        throw new ServiceException(ex.getMessage());
                })
                .retryWhen(getRetryBackoffSpec())
                .repeat(n);
    }

    public Mono<Movie> getMovieByIdWithRevenue(long movieId) {
        var movieInfoMono = movieInfoService.retrieveMovieInfoMonoUsingId(movieId);
        var reviewsFlux = reviewService.retrieveReviewsFlux(movieId)
                .collectList();

        var revenueMono = Mono.fromCallable(() -> revenueService.getRevenue(movieId))
                .subscribeOn(Schedulers.boundedElastic());

        return movieInfoMono
                .zipWith(reviewsFlux, (movieInfo, reviews) -> new Movie(movieInfo, reviews))
                .zipWith(revenueMono, ((movie, revenue) -> {
                    movie.setRevenue(revenue);
                    return movie;
                }));
    }
}
