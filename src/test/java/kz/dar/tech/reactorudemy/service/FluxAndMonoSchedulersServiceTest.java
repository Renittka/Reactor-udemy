package kz.dar.tech.reactorudemy.service;

import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;

class FluxAndMonoSchedulersServiceTest {

    FluxAndMonoSchedulersService fluxAndMonoSchedulersService = new FluxAndMonoSchedulersService();

    @Test
    void explorePublishOn() {
        var flux = fluxAndMonoSchedulersService.explorePublishOn();

        StepVerifier.create(flux)
                .expectNextCount(6)
                .verifyComplete();
    }

    @Test
    void exploreSubscribeOn() {
        var flux = fluxAndMonoSchedulersService.exploreSubscribeOn();

        StepVerifier.create(flux)
                .expectNextCount(6)
                .verifyComplete();
    }

    @Test
    void exploreParallel() {
        var flux = fluxAndMonoSchedulersService.exploreParallel();

        StepVerifier.create(flux)
                .expectNextCount(3)
                .verifyComplete();
    }

    @Test
    void exploreParallelFlatMap() {
        var flux = fluxAndMonoSchedulersService.exploreParallelFlatMap();

        StepVerifier.create(flux)
                .expectNextCount(3)
                .verifyComplete();
    }

    @Test
    void exploreParallelFlatMap1() {
        var flux = fluxAndMonoSchedulersService.exploreParallelFlatMap1();

        StepVerifier.create(flux)
                .expectNextCount(6)
                .verifyComplete();
    }

    @Test
    void exploreParallelFlatMapSequential() {
        var flux = fluxAndMonoSchedulersService.exploreParallelFlatMapSequential();

        StepVerifier.create(flux)
                .expectNext("ALEX", "BEN", "CHLOE")
                .verifyComplete();
    }
}