package com.ecommerce.controller;

import com.ecommerce.service.BuyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BuyController {

    private final BuyService service;

    public BuyController(BuyService service) {
        this.service = service;
    }

    @PostMapping("buy")
    public ResponseEntity<String> buy(@RequestParam Long product, @RequestParam Integer user, @RequestParam boolean ft) {
        String transactionId;

        if(ft) {
            transactionId = service.buyFtEnabled(product, user);
        } else {
            transactionId = service.buyFtDisabled(product, user);
        }
        return ResponseEntity.ok(transactionId);
    }
}
