package kz.dar.tech.reactorudemy.service;

import kz.dar.tech.reactorudemy.exception.ReactorException;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import static kz.dar.tech.reactorudemy.util.CommonUtil.delay;

@Slf4j
public class FluxAndMonoGeneratorService {

    public Flux<String> namesFlux() {
        var namesList = List.of("alex", "ben", "chloe");
        //return Flux.just("alex", "ben", "chloe");
        return Flux.fromIterable(namesList); // coming from a db or remote service
    }

    public Mono<String> namesMono() {

        return Mono.just("alex");

    }

    public Flux<String> namesFluxMap(int stringLength) {
        var namesList = List.of("alex", "ben", "chloe");
        //return Flux.just("alex", "ben", "chloe");

        return Flux.fromIterable(namesList)
                .map(String::toUpperCase)
                .delayElements(Duration.ofMillis(500))
                .filter(s -> s.length() > stringLength)
                .map(s -> s.length() + "-" + s)
                .doOnNext(name -> System.out.println("name is : " + name.toLowerCase()))
                .doOnSubscribe(s -> System.out.println("Subscription  is : " + s))
                .doOnComplete(() -> System.out.println("Completed sending all the items."))
                .doFinally((signalType) -> System.out.println("value is : " + signalType))
                .defaultIfEmpty("default");
    }

    public Mono<String> namesMonoMapFilter(int stringLength) {
        return Mono.just("alex")
                .map(String::toUpperCase)
                .filter(i -> i.length() > stringLength);
    }

    public Flux<String> namesFluxImmutability() {
        var namesList = List.of("alex", "ben", "chloe");
        //return Flux.just("alex", "ben", "chloe");
        var namesFlux = Flux.fromIterable(namesList);
        namesFlux.map(String::toUpperCase);
        return namesFlux;
    }

    public Flux<String> namesFluxFlatMap(int stringLength) {
        var namesList = List.of("alex", "ben", "chloe");

        return Flux.fromIterable(namesList)
                //.map(s -> s.toUpperCase())
                .map(String::toUpperCase)
                .filter(s -> s.length() > stringLength)
                // ALEX,CHLOE -> A, L, E, X, C, H, L , O, E
                .flatMap(this::splitString);
    }

    private Flux<String> splitString(String name) {
        var charArray = name.split("");
        return Flux.fromArray(charArray);
    }

    public Flux<String> namesFluxFlatMapAsync(int stringLength) {
        var namesList = List.of("alex", "ben", "chloe");
        return Flux.fromIterable(namesList)
                //.map(s -> s.toUpperCase())
                .map(String::toUpperCase)
                .filter(s -> s.length() > stringLength)
                .flatMap(this::splitStringWithDelay);
    }

    private Flux<String> splitStringWithDelay(String name) {
        var delay = new Random().nextInt(1000);
        var charArray = name.split("");
        return Flux.fromArray(charArray)
                .delayElements(Duration.ofMillis(delay));
    }

    public Flux<String> namesFluxConcatMap(int stringLength) {
        var namesList = List.of("alex", "ben", "chloe");
        return Flux.fromIterable(namesList)
                .map(String::toUpperCase)
                .filter(s -> s.length() > stringLength)
                .concatMap(this::splitStringWithDelay);
    }

    public Mono<List<String>> namesMonoFlatMap(int stringLength) {
        return Mono.just("alex")
                .map(String::toUpperCase)
                .filter(s -> s.length() > stringLength)
                .flatMap(this::splitStringMono);
    }

    private Mono<List<String>> splitStringMono(String s) {
        var charArray = s.split("");
        return Mono.just(List.of(charArray))
                .delayElement(Duration.ofSeconds(1));
    }

    public Flux<String> namesMonoFlatMapMany(int stringLength) {
        return Mono.just("alex")
                .map(String::toUpperCase)
                .flatMapMany(this::splitStringWithDelay);
    }

    public Flux<String> namesFluxTransform(int stringLength) {
        Function<Flux<String>, Flux<String>> filterMap = name -> name.map(String::toUpperCase)
                .filter(s -> s.length() > stringLength);

        var namesList = List.of("alex", "ben", "chloe");
        return Flux.fromIterable(namesList)
                .transform(filterMap)
                .flatMap(this::splitString)
                .defaultIfEmpty("default");
    }

    public Flux<String> namesFluxTransformSwitchIfEmpty(int stringLength) {
        Function<Flux<String>, Flux<String>> filterMap = name -> name.map(String::toUpperCase)
                .filter(s -> s.length() > stringLength)
                .flatMap(this::splitString);
        var defaultFlux = Flux.just("default")
                .transform(filterMap);
        var namesList = List.of("alex", "ben", "chloe");
        return Flux.fromIterable(namesList)
                .transform(filterMap)
                .switchIfEmpty(defaultFlux);
    }

    public Mono<String> namesMonoMapFilterDefault(int stringLength) {
        Function<Mono<String>, Mono<String>> filterMap = name -> name.map(String::toUpperCase)
                .filter(s -> s.length() > stringLength);

        return Mono.just("alex")
                .transform(filterMap)
                .defaultIfEmpty("default");
    }

    public Mono<String> namesMonoFilterSwitchIfEmpty(int stringLength) {
        Function<Mono<String>, Mono<String>> filterMap = name -> name.map(String::toUpperCase)
                .filter(s -> s.length() > stringLength);
        var defaultFlux = Mono.just("default")
                .transform(filterMap);

        return Mono.just("alex")
                .transform(filterMap)
                .switchIfEmpty(defaultFlux);
    }

    public Flux<String> exploreConcat() {
        var abcFlux = Flux.just("A", "B", "C");
        var defFlux = Flux.just("D", "E", "F");

        return Flux.concat(abcFlux, defFlux);
    }

    public Flux<String> exploreConcatWith() {
        var abcFlux = Flux.just("A", "B", "C");
        var defFlux = Flux.just("D", "E", "F");

        return abcFlux.concatWith(defFlux);
    }

    public Flux<String> exploreConcatWithMono() {
        var aMono = Flux.just("A");
        var bMono = Flux.just("B");

        return aMono.concatWith(bMono);
    }

    public Flux<String> exploreMerge() {
        var abcFlux = Flux.just("A", "B", "C")
                .delayElements(Duration.ofMillis(100));
        var defFlux = Flux.just("D", "E", "F")
                .delayElements(Duration.ofMillis(125));

        return Flux.merge(abcFlux, defFlux);
    }

    public Flux<String> exploreMergeWith() {
        var abcFlux = Flux.just("A", "B", "C")
                .delayElements(Duration.ofMillis(100));
        var defFlux = Flux.just("D", "E", "F")
                .delayElements(Duration.ofMillis(125));

        return abcFlux.mergeWith(defFlux);
    }

    public Flux<String> exploreMergeWithMono() {
        var aMono = Mono.just("A");
        var bMono = Mono.just("B");

        return aMono.mergeWith(bMono);
    }

    public Flux<String> exploreMergeSequential() {
        var abcFlux = Flux.just("A", "B", "C")
                .delayElements(Duration.ofMillis(100));
        var defFlux = Flux.just("D", "E", "F")
                .delayElements(Duration.ofMillis(125));

        return Flux.mergeSequential(abcFlux, defFlux);
    }


    public Flux<String> exploreZip() {
        var abcFlux = Flux.just("A", "B", "C");
        var defFlux = Flux.just("D", "E", "F");

        return Flux.zip(abcFlux, defFlux, (first, second) -> first + second);
    }

    public Flux<String> exploreZip1() {
        var abcFlux = Flux.just("A", "B", "C");
        var defFlux = Flux.just("D", "E", "F");
        var _123Flux = Flux.just("1", "2", "3");
        var _456Flux = Flux.just("4", "5", "6");

        return Flux.zip(abcFlux, defFlux, _123Flux, _456Flux)
                .map(t4 -> t4.getT1() + t4.getT2() + t4.getT3() + t4.getT4());
    }

    public Flux<String> exploreZipWith() {
        var abcFlux = Flux.just("A", "B", "C");
        var defFlux = Flux.just("D", "E", "F");

        return abcFlux.zipWith(defFlux, (first, second) -> first + second);
    }

    public Mono<String> exploreZipWithMono() {
        var aMono = Mono.just("A");
        var bMono = Mono.just("B");

        return aMono.zipWith(bMono)
                .map(t2 -> t2.getT1() + t2.getT2());
    }

    public Flux<String> exceptionFlux() {
        return Flux.just("A", "B", "C")
                .concatWith(Flux.error(new RuntimeException("Exception occurred")))
                .concatWith(Flux.just("D"));
    }

    public Flux<String> exploreOnErrorReturn() {
        return Flux.just("A", "B", "C")
                .concatWith(Flux.error(new IllegalStateException("Exception occurred")))
                .onErrorReturn("D");
    }

    public Flux<String> exploreOnErrorResume(Exception e) {
        var recoveryFlux = Flux.just("D", "E", "F");
        return Flux.just("A", "B", "C")
                .concatWith(Flux.error(e))
                .onErrorResume(ex -> {
                    log.error("Exception is ", ex);
                    if (ex instanceof IllegalStateException)
                        return recoveryFlux;
                    else
                        return Flux.error(ex);
                });
    }

    public Flux<String> exploreOnErrorContinue() {
        return Flux.just("A", "B", "C")
                .map(name -> {
                    if (name.equals("B"))
                        throw new IllegalStateException("Exception occurred");
                    return name;
                })
                .concatWith(Flux.just("D"))
                .onErrorContinue((ex, name) -> {
                    log.error("Exception is ", ex);
                    log.info("name is {}", name);
                });
    }

    public Flux<String> exploreOnErrorMap() {
        return Flux.just("A", "B", "C")
                //.checkpoint("errorSpot")
                .map(name -> {
                    if (name.equals("B"))
                        throw new IllegalStateException("Exception occurred");
                    return name;
                })
                .concatWith(Flux.just("D"))
                .onErrorMap(ex -> {
                    log.error("Exception is ", ex);
                    return new ReactorException(ex, ex.getMessage());
                });
    }

    public Flux<String> exploreDoOnError() {
        return Flux.just("A", "B", "C")
                .concatWith(Flux.error(new IllegalStateException("Exception occurred")))
                .doOnError(ex -> {
                    log.error("Exception is ", ex);
                });
    }

    public Mono<Object> exploreOnErrorReturnMono() {
        return Mono.just("A")
                .map(value -> {
                    throw new RuntimeException("Exception occurred");
                })
                .onErrorReturn("abc");
    }

    public Mono<String> exploreOnErrorContinueMono(String monoValue) {
        return Mono.just(monoValue)
                .map(value -> {
                    if (value.equals("abc"))
                        throw new RuntimeException("Exception occurred");
                    return value;
                })
                .onErrorContinue((ex, name) -> {
                    log.error("Exception is ", ex);
                    log.info("name is {}", name);
                });
    }

    public Flux<Integer> exploreGenerate() {
        return Flux.generate(
                () -> 1, (state, sink) -> {
                    sink.next(state * 2);
                    if (state == 10) {
                        sink.complete();
                    }
                    return state + 1;
                }
        );
    }

    public static List<String> names() {
        delay(1000);
        return List.of("alex", "ben", "chloe");
    }

    public Flux<String> exploreCreate() {
        return Flux.create(sink -> {
            /* names()
                    .forEach(sink::next);*/
            CompletableFuture
                    .supplyAsync(() -> names())
                    .thenAccept(names ->
                            names.forEach(name -> {
                                sink.next(name);
                                sink.next(name);
                            })
                    )
                    .thenRun(() -> sendEvents(sink));
            // sink.complete();
        });
    }

    public void sendEvents(FluxSink<String> sink) {
        CompletableFuture
                .supplyAsync(() -> names())
                .thenAccept(names -> names.forEach(sink::next))
                .thenRun(sink::complete);
    }

    public Mono<String> exploreCreateMono() {
        return Mono.create(sink -> sink.success("alex"));
    }

    public Flux<String> exploreHandle() {
        return Flux.fromIterable(List.of("alex", "ben", "chloe"))
                .handle((name, sink) -> {
                    if (name.length() > 3) sink.next(name.toUpperCase());
                });
    }

    public static void main(String[] args) {

        FluxAndMonoGeneratorService fluxAndMonoGeneratorService = new FluxAndMonoGeneratorService();

        Flux<String> namesFlux = fluxAndMonoGeneratorService.namesFlux().log();

        namesFlux.subscribe((name) -> {
            System.out.println("Name is : " + name);
        });

        Mono<String> namesMono = fluxAndMonoGeneratorService.namesMono().log();

        namesMono.subscribe((name) -> {
            System.out.println("Name is : " + name);
        });
    }
}
