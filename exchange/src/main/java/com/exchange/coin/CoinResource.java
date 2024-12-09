package com.exchange.coin;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Márcio
 * @since 08/12/2024
 */

@RestController
@RequestMapping("/exchange")
public class CoinResource {

    @GetMapping
    public ResponseEntity<BigDecimal> exchange() {
        // Fail (Crash, 0.1, _ )
        ThreadLocalRandom random = ThreadLocalRandom.current();
        double percent = random.nextDouble();
        if(percent <= 0.1) {
            System.exit(1);
        }

        return ResponseEntity.ok(
                BigDecimal.valueOf(random.nextDouble(5.0, 7.0)).setScale(2, RoundingMode.FLOOR)
        );
    }
}
