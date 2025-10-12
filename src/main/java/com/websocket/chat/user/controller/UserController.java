package com.websocket.chat.user.controller;

import com.websocket.chat.user.dto.UserDtoMapper;
import com.websocket.chat.user.dto.UserResponseDto;
import com.websocket.chat.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserDtoMapper mapper;
    private final UserService userService;

    @PostMapping
    public UserResponseDto create(@RequestParam String name) {
        return mapper.toDto(userService.create(name));
    }
}
