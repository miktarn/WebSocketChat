package com.websocket.chat.security.controller;

import com.websocket.chat.security.controller.dto.UserDetailsDto;
import com.websocket.chat.security.service.AuthService;
import com.websocket.chat.user.dto.UserDtoMapper;
import com.websocket.chat.user.dto.UserResponseDto;
import com.websocket.chat.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final UserService userService;
    private final UserDtoMapper mapper;

    @PostMapping("/login")
    public UserResponseDto login(@RequestBody UserDetailsDto dto) {
        return mapper.toDto(authService.login(dto.username(), dto.password()));
    }

    @PostMapping("/sighIn")
    public UserResponseDto sighIn(@RequestBody UserDetailsDto dto) {
        return mapper.toDto(userService.create(dto.username(), dto.password()));
    }
}
