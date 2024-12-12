package com.ecommerce.controller;

import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.service.BuyService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
public class BuyController {

    private final BuyService service;

    public BuyController(BuyService service) {
        this.service = service;
    }

    @PostMapping("buy")
    public ResponseEntity<String> buy(@RequestParam Long product, @RequestParam Long user, @RequestParam boolean ft) {
        return ResponseEntity.ok(service.buy(product, user, ft));
    }

}
