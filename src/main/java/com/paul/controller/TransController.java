package com.paul.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TransController {
	
	@GetMapping("/goRatesQuery")
    public String hello(Model model) {
        return "ratesQuery"; // forward to rateQuery.html
    }
}
