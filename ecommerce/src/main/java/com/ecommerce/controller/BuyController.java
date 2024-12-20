package com.ecommerce.controller;

import org.springframework.http.HttpStatus;
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
    public ResponseEntity<Void> buy(@RequestParam Long product, @RequestParam Integer user, @RequestParam boolean ft) {
        if(ft) {
            service.buyFtEnabled(product, user);
        } else {
            service.buyFtDisabled(product, user);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
