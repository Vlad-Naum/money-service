package com.naum.system.moneyservice.controller.user;

import com.naum.system.moneyservice.domain.user.User;
import com.naum.system.moneyservice.domain.user.UserCreate;
import com.naum.system.moneyservice.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping(path = "/users")
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping(path = "/", method = RequestMethod.POST)
    public ResponseEntity<?> createNewUser(@RequestBody UserCreate userCreate) {
        try {
            User userSave = userService.create(userCreate.name(), userCreate.email());
            return new ResponseEntity<>(
                    userSave.getId(),
                    HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    @RequestMapping(path = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<User> getUserById(@PathVariable(name = "id") Long userId) {
        User user = userService.findUserById(userId);
        return new ResponseEntity<>(
                user,
                HttpStatus.OK);
    }

    @RequestMapping(path = "/", method = RequestMethod.GET)
    public ResponseEntity<ArrayList<User>> getAll() {
        ArrayList<User> allUser =  userService.findAllUser();
        return new ResponseEntity<>(
                allUser,
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
