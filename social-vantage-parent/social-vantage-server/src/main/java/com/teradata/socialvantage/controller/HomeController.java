/*
 * Copyright (C) 2019  by Teradata Corporation.
 * All Rights Reserved.
 * TERADATA CONFIDENTIAL AND TRADE SECRET
 * 
 * @author Meeraj Kanaparthi
 * 
 */

package com.teradata.socialvantage.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/home")
public class HomeController {
	@GetMapping
    public String home(	) {
           return "forward:/index.html";
    }

}
