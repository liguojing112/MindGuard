package com.mindguard.module.user.service;

import com.mindguard.module.user.dto.LoginDTO;
import com.mindguard.module.user.dto.RegisterDTO;
import com.mindguard.module.user.dto.UserVO;

public interface UserService {
    UserVO login(LoginDTO dto);
    void register(RegisterDTO dto);
    UserVO getInfo(Long userId);
    void updateProfile(Long userId, UserVO vo);
    void updateAvatar(Long userId, String avatarUrl);
    java.util.List<UserVO> listLoginUsers();
}
