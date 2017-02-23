package com.derfy.web.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.{PathVariable, RequestMapping, ResponseBody}

@Controller
@RequestMapping(path = Array("scala"))
class HelloScalaController {

  @RequestMapping(path = Array("/hello"))
  @ResponseBody
  def hello(@PathVariable name: String): String = {
    "hello" + name
  }

}
