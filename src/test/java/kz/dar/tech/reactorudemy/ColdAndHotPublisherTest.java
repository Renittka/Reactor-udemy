package kz.dar.tech.reactorudemy;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;

import java.time.Duration;

import static kz.dar.tech.reactorudemy.util.CommonUtil.delay;

@Slf4j
public class ColdAndHotPublisherTest {

    @Test
    void coldPublisherTest() {
        var flux = Flux.range(1, 10);

        flux.subscribe(i -> System.out.println("Subscriber 1: " + i));

        flux.subscribe(i -> System.out.println("Subscriber 2: " + i));
    }

    @Test
    void hotPublisherTest() {
        var flux = Flux.range(1, 10)
                .delayElements(Duration.ofSeconds(1));

        ConnectableFlux<Integer> connectableFlux = flux.publish();
        connectableFlux.connect();

        connectableFlux.subscribe(i -> System.out.println("Subscriber 1: " + i));
        delay(4000);
        connectableFlux.subscribe(i -> System.out.println("Subscriber 2: " + i));
        delay(10000);
    }

    @Test
    void hotPublisherTestAutoConnect() {
        var flux = Flux.range(1, 10)
                .delayElements(Duration.ofSeconds(1));

        var hotSource = flux.publish().autoConnect(2);


        hotSource.subscribe(i -> System.out.println("Subscriber 1: " + i));
        delay(2000);
        hotSource.subscribe(i -> System.out.println("Subscriber 2: " + i));
        System.out.println("Two subscribers are connected");
        delay(2000);
        hotSource.subscribe(i -> System.out.println("Subscriber 3: " + i));
        delay(10000);
    }

    @Test
    void hotPublisherTestRefCount() {
        var flux = Flux.range(1, 10)
                .delayElements(Duration.ofSeconds(1))
                .doOnCancel(() -> log.info("Received cancel signal"));

        var hotSource = flux.publish().refCount(2);


        var disposable = hotSource.subscribe(i -> System.out.println("Subscriber 1: " + i));
        delay(2000);
        var disposable1 = hotSource.subscribe(i -> System.out.println("Subscriber 2: " + i));
        System.out.println("Two subscribers are connected");
        delay(2000);
        disposable.dispose();
        disposable1.dispose();
        hotSource.subscribe(i -> System.out.println("Subscriber 3: " + i));
        delay(2000);
        hotSource.subscribe(i -> System.out.println("Subscriber 4: " + i));
        delay(10000);
    }
}
