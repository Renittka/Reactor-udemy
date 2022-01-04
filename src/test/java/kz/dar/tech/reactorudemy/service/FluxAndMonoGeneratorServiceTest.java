package kz.dar.tech.reactorudemy.service;

import kz.dar.tech.reactorudemy.exception.ReactorException;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Hooks;
import reactor.test.StepVerifier;
import reactor.test.scheduler.VirtualTimeScheduler;
import reactor.tools.agent.ReactorDebugAgent;

import java.time.Duration;
import java.util.List;

public class FluxAndMonoGeneratorServiceTest {
    FluxAndMonoGeneratorService fluxAndMonoGeneratorService = new FluxAndMonoGeneratorService();

    @Test
    void namesFlux() {
        var stringFlux = fluxAndMonoGeneratorService.namesFlux();

        StepVerifier.create(stringFlux)
                .expectNext("alex")
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    void namesMono() {
        var stringMono = fluxAndMonoGeneratorService.namesMono();
        StepVerifier.create(stringMono)
                .expectNext("alex")
                .verifyComplete();
    }

    @Test
    void namesFluxMap() {
        int stringLength = 3;

        var namesFlux = fluxAndMonoGeneratorService.namesFluxMap(stringLength).log();

        StepVerifier.create(namesFlux)
                //.expectNext("ALEX", "BEN", "CHLOE")
                .expectNext("4-ALEX", "5-CHLOE")
                .verifyComplete();
    }

    @Test
    void namesFluxImmutability() {
        var stringFlux = fluxAndMonoGeneratorService.namesFluxImmutability()
                .log();

        StepVerifier.create(stringFlux)
                //.expectNext("ALEX", "BEN", "CHLOE")
                .expectNextCount(3)
                .verifyComplete();
    }

    @Test
    void namesMonoMapFilter() {
        int stringLength = 3;
        var namesMono = new FluxAndMonoGeneratorService().namesMonoMapFilter(stringLength).log();
        StepVerifier.create(namesMono)
                .expectNext("ALEX")
                .verifyComplete();
    }

    @Test
    void testNamesFluxMap() {
        int stringLength = 3;
        var namesFlux = fluxAndMonoGeneratorService.namesFluxFlatMap(stringLength).log();

        StepVerifier.create(namesFlux)
                .expectNext("A", "L", "E", "X", "C", "H", "L", "O", "E")
                .verifyComplete();
    }

    @Test
    void namesFluxFlatMapAsync() {
        int stringLength = 3;
        var namesFlux = fluxAndMonoGeneratorService.namesFluxFlatMapAsync(stringLength).log();

        StepVerifier.create(namesFlux)
                .expectNextCount(9)
                .verifyComplete();
    }

    @Test
    void namesFluxConcatMap() {
        int stringLength = 3;

        var namesFlux = fluxAndMonoGeneratorService.namesFluxConcatMap(stringLength).log();

        StepVerifier.create(namesFlux)
                .expectNext("A", "L", "E", "X")
                //expectNext("0-A", "1-L", "2-E", "3-X")
                .expectNextCount(5)
                .verifyComplete();
    }

    @Test
    void namesFluxConcatMapVirtualTimer() {
        int stringLength = 3;
        VirtualTimeScheduler.getOrSet();

        var namesFlux = fluxAndMonoGeneratorService.namesFluxConcatMap(stringLength).log();

        StepVerifier.withVirtualTime(() -> namesFlux)
                .thenAwait(Duration.ofSeconds(10))
                .expectNext("A", "L", "E", "X")
                //expectNext("0-A", "1-L", "2-E", "3-X")
                .expectNextCount(5)
                .verifyComplete();
    }

    @Test
    void namesMonoFlatMap() {
        int stringLength = 3;

        var namesFlux = fluxAndMonoGeneratorService.namesMonoFlatMap(stringLength).log();

        StepVerifier.create(namesFlux)
                .expectNext(List.of("A", "L", "E", "X"))
                .verifyComplete();
    }

    @Test
    void namesMonoFlatMapMany() {
        int stringLength = 3;

        var namesFlux = fluxAndMonoGeneratorService.namesMonoFlatMapMany(stringLength).log();

        StepVerifier.create(namesFlux)
                .expectNext("A", "L", "E", "X")
                .verifyComplete();
    }

    @Test
    void namesFluxTransform() {
        int stringLength = 3;

        var namesFlux = fluxAndMonoGeneratorService.namesFluxTransform(stringLength).log();
        StepVerifier.create(namesFlux)
                .expectNext("A", "L", "E", "X")
                .expectNextCount(5)
                .verifyComplete();
    }

    @Test
    void namesFluxTransform1() {
        int stringLength = 6;

        var namesFlux = fluxAndMonoGeneratorService.namesFluxTransform(stringLength).log();

        StepVerifier.create(namesFlux)
                .expectNext("default")
                //.expectNextCount(5)
                .verifyComplete();
    }

    @Test
    void namesFluxTransformSwitchIfEmpty() {
        int stringLength = 6;

        var namesFlux = fluxAndMonoGeneratorService.namesFluxTransformSwitchIfEmpty(stringLength).log();
        StepVerifier.create(namesFlux)
                .expectNext("D", "E", "F", "A", "U", "L", "T")
                // .expectNextCount(5)
                .verifyComplete();
    }

    @Test
    void namesMonoMapFilterDefault() {
        int stringLength = 4;

        var namesFlux = fluxAndMonoGeneratorService.namesMonoMapFilterDefault(stringLength).log();
        StepVerifier.create(namesFlux)
                .expectNext("default")
                // .expectNextCount(5)
                .verifyComplete();
    }

    @Test
    void namesMonoFilterSwitchIfEmpty() {
        int stringLength = 4;

        var namesFlux = fluxAndMonoGeneratorService.namesMonoFilterSwitchIfEmpty(stringLength).log();
        StepVerifier.create(namesFlux)
                .expectNext("DEFAULT")
                // .expectNextCount(5)
                .verifyComplete();
    }

    @Test
    void exploreConcat() {
        var concatFlux = fluxAndMonoGeneratorService.exploreConcat().log();
        StepVerifier.create(concatFlux)
                .expectNext("A", "B", "C", "D", "E", "F")
                .verifyComplete();
    }

    @Test
    void exploreConcatWith() {
        var concatFlux = fluxAndMonoGeneratorService.exploreConcatWith().log();
        StepVerifier.create(concatFlux)
                .expectNext("A", "B", "C", "D", "E", "F")
                .verifyComplete();
    }

    @Test
    void exploreConcatWithMono() {
        var concatFlux = fluxAndMonoGeneratorService.exploreConcatWithMono().log();
        StepVerifier.create(concatFlux)
                .expectNext("A", "B")
                .verifyComplete();
    }

    @Test
    void exploreMerge() {
        var mergeFlux = fluxAndMonoGeneratorService.exploreMerge().log();
        StepVerifier.create(mergeFlux)
                .expectNext("A", "D", "B", "E", "C", "F")
                .verifyComplete();
    }

    @Test
    void exploreMergeWith() {
        var mergeFlux = fluxAndMonoGeneratorService.exploreMergeWith().log();
        StepVerifier.create(mergeFlux)
                .expectNext("A", "D", "B", "E", "C", "F")
                .verifyComplete();
    }

    @Test
    void exploreMergeWithMono() {
        var mergeFlux = fluxAndMonoGeneratorService.exploreMergeWithMono().log();
        StepVerifier.create(mergeFlux)
                .expectNext("A", "B")
                .verifyComplete();
    }

    @Test
    void exploreMergeSequential() {
        var mergeFlux = fluxAndMonoGeneratorService.exploreMergeSequential().log();
        StepVerifier.create(mergeFlux)
                .expectNext("A", "B", "C", "D", "E", "F")
                .verifyComplete();
    }

    @Test
    void exploreZip() {
        var zipFlux = fluxAndMonoGeneratorService.exploreZip().log();
        StepVerifier.create(zipFlux)
                .expectNext("AD", "BE", "CF")
                .verifyComplete();
    }

    @Test
    void exploreZip1() {
        var zipFlux = fluxAndMonoGeneratorService.exploreZip1().log();
        StepVerifier.create(zipFlux)
                .expectNext("AD14", "BE25", "CF36")
                .verifyComplete();
    }

    @Test
    void exploreZipWith() {
        var zipFlux = fluxAndMonoGeneratorService.exploreZipWith().log();
        StepVerifier.create(zipFlux)
                .expectNext("AD", "BE", "CF")
                .verifyComplete();
    }

    @Test
    void exploreZipWithMono() {
        var zipMono = fluxAndMonoGeneratorService.exploreZipWithMono().log();
        StepVerifier.create(zipMono)
                .expectNext("AB")
                .verifyComplete();
    }

    @Test
    void exceptionFlux() {
        var valueFlux = fluxAndMonoGeneratorService.exceptionFlux().log();
        StepVerifier.create(valueFlux)
                .expectNext("A", "B", "C")
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    void exceptionFlux1() {
        var valueFlux = fluxAndMonoGeneratorService.exceptionFlux().log();
        StepVerifier.create(valueFlux)
                .expectNext("A", "B", "C")
                .expectError()
                .verify();
    }

    @Test
    void exceptionFlux2() {
        var valueFlux = fluxAndMonoGeneratorService.exceptionFlux().log();
        StepVerifier.create(valueFlux)
                .expectNext("A", "B", "C")
                .expectErrorMessage("Exception occurred")
                .verify();
    }

    @Test
    void exploreOnErrorReturn() {
        var valueFlux = fluxAndMonoGeneratorService.exploreOnErrorReturn().log();
        StepVerifier.create(valueFlux)
                .expectNext("A", "B", "C", "D")
                .verifyComplete();
    }

    @Test
    void exploreOnErrorResume() {
        var error = new IllegalStateException("Not a valid state");
        var valueFlux = fluxAndMonoGeneratorService.exploreOnErrorResume(error).log();
        StepVerifier.create(valueFlux)
                .expectNext("A", "B", "C", "D", "E", "F")
                .verifyComplete();
    }

    @Test
    void exploreOnErrorResume1() {
        var error = new RuntimeException("Not a valid state");
        var valueFlux = fluxAndMonoGeneratorService.exploreOnErrorResume(error).log();
        StepVerifier.create(valueFlux)
                .expectNext("A", "B", "C")
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    void exploreOnErrorContinue() {
        var valueFlux = fluxAndMonoGeneratorService.exploreOnErrorContinue().log();
        StepVerifier.create(valueFlux)
                .expectNext("A", "C", "D")
                .verifyComplete();
    }

    @Test
    void exploreOnErrorMap() {
        var valueFlux = fluxAndMonoGeneratorService.exploreOnErrorMap().log();
        StepVerifier.create(valueFlux)
                .expectNext("A")
                .expectError(ReactorException.class)
                .verify();
    }

    @Test
    void exploreOnErrorMapOnOperatorDebug() {
//        Hooks.onOperatorDebug();
        var valueFlux = fluxAndMonoGeneratorService.exploreOnErrorMap().log();
        StepVerifier.create(valueFlux)
                .expectNext("A")
                .expectError(ReactorException.class)
                .verify();
    }

    @Test
    void exploreOnErrorMapReactorDebugAgent() {
        ReactorDebugAgent.init();
        ReactorDebugAgent.processExistingClasses();
        var valueFlux = fluxAndMonoGeneratorService.exploreOnErrorMap().log();
        StepVerifier.create(valueFlux)
                .expectNext("A")
                .expectError(ReactorException.class)
                .verify();
    }

    @Test
    void exploreDoOnError() {
        var valueFlux = fluxAndMonoGeneratorService.exploreDoOnError().log();
        StepVerifier.create(valueFlux)
                .expectNext("A", "B", "C")
                .expectError(IllegalStateException.class)
                .verify();
    }

    @Test
    void exploreOnErrorReturnMono() {
        var valueMono = fluxAndMonoGeneratorService.exploreOnErrorReturnMono().log();
        StepVerifier.create(valueMono)
                .expectNext("abc")
                .verifyComplete();
    }

    @Test
    void exploreOnErrorContinueMono() {
        var valueMono = fluxAndMonoGeneratorService.exploreOnErrorContinueMono("reactor").log();
        StepVerifier.create(valueMono)
                .expectNext("reactor")
                .verifyComplete();
    }

    @Test
    void exploreOnErrorContinueMono1() {
        var valueMono = fluxAndMonoGeneratorService.exploreOnErrorContinueMono("abc").log();
        StepVerifier.create(valueMono)
                .verifyComplete();
    }

    @Test
    void exploreGenerate() {
        var valueFlux = fluxAndMonoGeneratorService.exploreGenerate().log();
        StepVerifier.create(valueFlux)
                .expectNextCount(10)
                .verifyComplete();
    }

    @Test
    void exploreCreate() {
        var valueFlux = fluxAndMonoGeneratorService.exploreCreate().log();
        StepVerifier.create(valueFlux)
                .expectNextCount(9)
                .verifyComplete();
    }

    @Test
    void exploreCreateMono() {
        var valueMono = fluxAndMonoGeneratorService.exploreCreateMono().log();
        StepVerifier.create(valueMono)
                .expectNext("alex")
                .verifyComplete();
    }

    @Test
    void exploreHandle() {
        var valueFlux = fluxAndMonoGeneratorService.exploreHandle().log();
        StepVerifier.create(valueFlux)
                .expectNextCount(2)
                .verifyComplete();
    }
}
