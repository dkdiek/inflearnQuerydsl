package study.querydsl.controller;

import org.springframework.web.bind.annotation.GetMapping;

/**
 * study.querydsl.controller HelloController
 *
 * @author : K
 */
public class HelloController {
  @GetMapping("/hello")
  public String hello(){
    return "hello";
  };
}

