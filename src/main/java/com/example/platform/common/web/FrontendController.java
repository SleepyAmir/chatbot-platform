package com.example.platform.common.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FrontendController {

    @GetMapping({
            "/",
            "/courses",
            "/careers",
            "/careers/{id:[0-9]+}",
            "/ocr",
            "/chat"
    })
    public String forwardToFrontend() {
        return "forward:/index.html";
    }
}
