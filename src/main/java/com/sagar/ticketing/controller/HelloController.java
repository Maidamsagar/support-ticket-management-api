package com.sagar.ticketing.controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController//this class contains restAPIs
public class HelloController {

    @GetMapping("/hello")
    public String hello() {
        return "Ticketing API is running";
    }
}
