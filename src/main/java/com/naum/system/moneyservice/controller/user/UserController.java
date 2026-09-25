package com.naum.system.moneyservice.controller.user;

import com.naum.system.moneyservice.domain.user.User;
import com.naum.system.moneyservice.domain.user.UserCreateDto;
import com.naum.system.moneyservice.domain.user.UserDto;
import com.naum.system.moneyservice.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private final  ModelMapper modelMapper;

    @GetMapping
    public ResponseEntity<List<UserDto>> getAll() {
        ArrayList<User> allUser =  userService.findAllUser();
        List<UserDto> allUserDto = allUser.stream()
                .map(user -> modelMapper.map(user, UserDto.class))
                .collect(Collectors.toList());
        return new ResponseEntity<>(
                allUserDto,
                HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> createNewUser(@RequestBody UserCreateDto userCreateDto) {
        try {
            User userSave = userService.create(userCreateDto);
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
        User user = userService.findUserById(userId);
        if (user != null) {
            UserDto userDto = modelMapper.map(user, UserDto.class);
            return new ResponseEntity<>(
                    userDto,
                    HttpStatus.OK);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @DeleteMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUserById(@PathVariable(name = "id") Long userId) {
        userService.deleteUserById(userId);
    }
}
