package lk.ijse.springbootbackend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test") // 💡 ඉතාම සරල path එකක්
public class TestController {
    @GetMapping("/hello")
    public String hello() {
        System.out.println("🚀 HELLO API HIT!");
        return "Hello World";
    }
}