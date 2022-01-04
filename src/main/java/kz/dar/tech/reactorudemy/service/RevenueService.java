package kz.dar.tech.reactorudemy.service;

import kz.dar.tech.reactorudemy.domain.Revenue;

import static kz.dar.tech.reactorudemy.util.CommonUtil.delay;

public class RevenueService {
    public Revenue getRevenue(Long movieId) {
        delay(1000); // simulating a network call ( DB or Rest call)
        return Revenue.builder()
                .movieInfoId(movieId)
                .budget(1000000)
                .boxOffice(5000000)
                .build();
    }
}
