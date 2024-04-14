package com.naum.system.moneyservice.controller;

import com.naum.system.moneyservice.domain.User;
import com.naum.system.moneyservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/")
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping(path = "/", method = RequestMethod.POST)
    public ResponseEntity<Long> createNewUser(@RequestBody User user) {
        User userSave = userService.create(user);
        return new ResponseEntity<>(
                userSave.getId(),
                HttpStatus.CREATED);
    }

    @RequestMapping(path = "/users/{id}", method = RequestMethod.GET)
    public ResponseEntity<User> getUserById(@PathVariable(name = "id") Long userId) {
        User user = userService.findUserById(userId);
        return new ResponseEntity<>(
                user,
                HttpStatus.OK);
    }
}
