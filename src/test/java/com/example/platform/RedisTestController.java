package com.example.platform;


import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
/// /
@RestController
@RequiredArgsConstructor
public class RedisTestController {

    private final RedisTestService service;

    @GetMapping("/test/redis")
    public String run() {

        service.runTest();
         return "Redis Test Finished!";
    }
}
