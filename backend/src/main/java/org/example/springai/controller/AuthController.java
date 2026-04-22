package org.example.springai.controller;

import org.example.springai.service.UserManager;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UserManager userManager;

    public AuthController(UserManager userManager) {
        this.userManager = userManager;
    }

    // 注册
    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");

        boolean ok = userManager.createUser(username, password);

        return Map.of(
                "success", ok,
                "message", ok ? "registered" : "username exists or invalid input"
        );
    }

    // 登录
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");

        boolean ok = userManager.validateUser(username, password);

        return Map.of(
                "success", ok,
                "message", ok ? "login ok" : "invalid username or password"
        );
    }

    // （可选）删除账号：你 UserManager 里有 deleteUser()，所以顺手开放一个 API
    @DeleteMapping("/users/{username}")
    public Map<String, Object> delete(@PathVariable String username) {
        boolean ok = userManager.deleteUser(username);
        return Map.of(
                "success", ok,
                "message", ok ? "deleted" : "user not found"
        );
    }
}
