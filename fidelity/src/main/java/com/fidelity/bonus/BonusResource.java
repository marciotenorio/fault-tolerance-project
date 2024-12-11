package com.fidelity.bonus;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.Optional;

/**
 * @author Márcio Tenório
 * @since 10/12/2024
 */

@RestController
@RequestMapping("bonus")
public class BonusResource {

    private final BonusRepository bonusRepository;
    private final ErrorSetterRepository errorSetterRepository;

    public BonusResource(BonusRepository bonusRepository, ErrorSetterRepository errorSetterRepository) {
        this.bonusRepository = bonusRepository;
        this.errorSetterRepository = errorSetterRepository;
    }

    @PostMapping
    public ResponseEntity<?> bonus(@Valid @RequestBody Bonus bonusDto) throws InterruptedException {
        ErrorSetter errorSetter = errorSetterRepository.findById(1).get();
        if(errorSetter.isDelayed()) {
            Thread.sleep(Duration.ofSeconds(2));
        }

        Optional<Bonus> bonus = bonusRepository.findById(bonusDto.getUser());
        bonus.ifPresentOrElse(b -> {
            b.setBonus(b.getBonus() + bonusDto.getBonus());
            bonusRepository.save(b);
        }, () -> {
            bonusRepository.save(bonusDto);
        });

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
