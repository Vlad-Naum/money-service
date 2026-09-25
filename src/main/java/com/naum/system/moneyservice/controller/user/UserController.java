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
        return new ResponseEntity<>(
                userMapper.toDto(userService.findAllUser()),
                HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> createNewUser(@Valid @RequestBody UserCreateDto userCreateDto) {
        try {
            User userSave = userService.create(userCreateDto.name(), userCreateDto.email());
            return new ResponseEntity<>(
                    userSave.getId(),
                    HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable(name = "id") Long userId) {
        User user = userService.getUserById(userId);
        UserDto userDto = userMapper.toDto(user);
        return new ResponseEntity<>(
                userDto,
                HttpStatus.OK);
    }

    @DeleteMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUserById(@PathVariable(name = "id") Long userId) {
        userService.deleteUserById(userId);
    }
}
