package com.naum.system.moneyservice.controller.user;

import com.naum.system.moneyservice.domain.user.User;
import com.naum.system.moneyservice.domain.user.UserCreateDto;
import com.naum.system.moneyservice.domain.user.UserDto;
import com.naum.system.moneyservice.service.user.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private ModelMapper modelMapper;

    @RequestMapping(path = "/", method = RequestMethod.POST)
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

    @RequestMapping(path = "/{id}", method = RequestMethod.GET)
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

    @RequestMapping(path = "/", method = RequestMethod.GET)
    public ResponseEntity<List<UserDto>> getAll() {
        ArrayList<User> allUser =  userService.findAllUser();
        List<UserDto> allUserDto = allUser.stream()
                .map(user -> modelMapper.map(user, UserDto.class))
                .collect(Collectors.toList());
        return new ResponseEntity<>(
                allUserDto,
                HttpStatus.OK);
    }

    @RequestMapping(path = "/", method = RequestMethod.DELETE)
    public ResponseEntity<Boolean> deleteUserById(@RequestBody Long userId) {
        boolean result = userService.deleteUserById(userId);
        return new ResponseEntity<>(
                result,
                HttpStatus.OK);
    }
}
