package com.mindguard.module.user.controller;

import com.mindguard.common.Result;
import com.mindguard.module.user.dto.LoginDTO;
import com.mindguard.module.user.dto.RegisterDTO;
import com.mindguard.module.user.dto.UserVO;
import com.mindguard.module.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @Value("${mindguard.upload.path:./uploads}")
    private String uploadPath;

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

    @PostMapping("/avatar")
    public Result<Map<String, String>> uploadAvatar(HttpServletRequest request,
                                                     @RequestParam("file") MultipartFile file) throws IOException {
        Long userId = (Long) request.getAttribute("userId");
        Path basePath = Paths.get(uploadPath);
        if (!basePath.isAbsolute()) {
            basePath = Paths.get(System.getProperty("user.dir")).resolve(uploadPath);
        }
        Path uploadDir = basePath.resolve("avatars");
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }
        String ext = getFileExtension(file.getOriginalFilename());
        String filename = "avatar_" + userId + "_" + UUID.randomUUID().toString().substring(0, 8) + "." + ext;
        Path filePath = uploadDir.resolve(filename);
        file.transferTo(filePath.toFile());
        String url = "/uploads/avatars/" + filename;
        userService.updateAvatar(userId, url);
        return Result.ok(Map.of("url", url));
    }

    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) return "png";
        return filename.substring(filename.lastIndexOf(".") + 1);
    }
}
