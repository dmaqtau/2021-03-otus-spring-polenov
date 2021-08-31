package ru.otus.spring.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {
    @GetMapping("/about")
    public String aboutPage(){
        return "about";
    }

    @GetMapping("/not_authorized")
    public String notAuthorized(){
        return "not_authorized";
    }

    @GetMapping("/admin")
    public String admin(){
        return "admin";
    }
}
