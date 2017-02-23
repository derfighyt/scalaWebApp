package com.derfy.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(path = "java")
public class HelloJavaController {

    @RequestMapping(path = "/hello")
    @ResponseBody
    public String hello() {
        return "hello java";
    }
}
