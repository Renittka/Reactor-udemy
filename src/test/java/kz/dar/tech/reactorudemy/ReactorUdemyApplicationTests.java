package kz.dar.tech.reactorudemy;

import kz.dar.tech.reactorudemy.service.FluxAndMonoGeneratorService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.test.StepVerifier;

@SpringBootTest
class ReactorUdemyApplicationTests {

    @Test
    void contextLoads() {
    }

    FluxAndMonoGeneratorService fluxAndMonoGeneratorService = new FluxAndMonoGeneratorService();

    @Test
    void namesFlux() {
        var namesFlux = fluxAndMonoGeneratorService.namesFlux();
        StepVerifier.create(namesFlux)
                .expectNext("alex")
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    void namesMono() {
        var namesMono = fluxAndMonoGeneratorService.namesMono();
        StepVerifier.create(namesMono)
                .expectNext("alex")
                .verifyComplete();
    }
}
