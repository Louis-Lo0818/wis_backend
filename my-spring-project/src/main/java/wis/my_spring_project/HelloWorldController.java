package wis.my_spring_project;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
public class HelloWorldController {
    
    @GetMapping("/")
    public String sayHello() {
        return "Hello, World!";
    }
}
