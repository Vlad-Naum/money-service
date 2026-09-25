package com.naum.system.moneyservice.controller.user;

import com.naum.system.moneyservice.controller.user.dto.UserMapper;
import com.naum.system.moneyservice.domain.user.User;
import com.naum.system.moneyservice.controller.user.dto.UserCreateDto;
import com.naum.system.moneyservice.controller.user.dto.UserDto;
import com.naum.system.moneyservice.service.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private final UserMapper userMapper;

    @GetMapping
    public ResponseEntity<List<UserDto>> getAll() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userMapper.toDto(userService.findAllUser()));
    }

    @PostMapping
    public ResponseEntity<Long> createNewUser(@Valid @RequestBody UserCreateDto userCreateDto) {
        User userSave = userService.create(userCreateDto.name(), userCreateDto.email());
        return ResponseEntity.status(HttpStatus.CREATED).body(userSave.getId());
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable(name = "id") Long userId) {
        User user = userService.getUserById(userId);
        UserDto userDto = userMapper.toDto(user);
        return ResponseEntity.status(HttpStatus.OK).body(userDto);
    }

    @DeleteMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUserById(@PathVariable(name = "id") Long userId) {
        userService.deleteUserById(userId);
    }
}
