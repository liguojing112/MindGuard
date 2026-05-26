package com.mindguard.module.user.controller;

import com.mindguard.common.Result;
import com.mindguard.module.user.dto.LoginDTO;
import com.mindguard.module.user.dto.RegisterDTO;
import com.mindguard.module.user.dto.UserVO;
import com.mindguard.module.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/login")
    public Result<UserVO> login(@Valid @RequestBody LoginDTO dto) {
        return Result.ok(userService.login(dto));
    }

    @PostMapping("/register")
    public Result<Void> register(@Valid @RequestBody RegisterDTO dto) {
        userService.register(dto);
        return Result.ok();
    }

    @GetMapping("/info")
    public Result<UserVO> getInfo(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.ok(userService.getInfo(userId));
    }

    @PutMapping("/profile")
    public Result<Void> updateProfile(HttpServletRequest request, @RequestBody UserVO vo) {
        Long userId = (Long) request.getAttribute("userId");
        userService.updateProfile(userId, vo);
        return Result.ok();
    }
}
