package com.naum.system.moneyservice.controller.money;

import com.naum.system.moneyservice.domain.money.MoneyCosts;
import com.naum.system.moneyservice.service.money.MoneyCostsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "/users/{user_id}/money_costs")
public class MoneyCostsController {

    @Autowired
    MoneyCostsService moneyCostsService;

    @RequestMapping(path = "/", method = RequestMethod.GET)
    public ResponseEntity<List<MoneyCosts>> getAllByUserId(@PathVariable(name = "user_id") Long userId) {
        List<MoneyCosts> allMoneyCosts = moneyCostsService.findAllByUserId(userId);
        return new ResponseEntity<>(
                allMoneyCosts,
                HttpStatus.OK);
    }
}
