package bind.auth.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/api/v1/auth")
public class AuthController {

    @GetMapping()
    public String healthCheck() {
        System.out.println("Auth Service is running");
        return "Auth Service is running";
    }
}
