package com.naum.system.moneyservice.controller.money;

import com.naum.system.moneyservice.domain.money.MoneyCostsDto;
import com.naum.system.moneyservice.service.money.MoneyCostsService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/users/{user_id}/money_costs")
public class MoneyCostsController {

    @Autowired
    private MoneyCostsService moneyCostsService;

    @Autowired
    private ModelMapper modelMapper;

    @RequestMapping(path = "/", method = RequestMethod.GET)
    public ResponseEntity<List<MoneyCostsDto>> getAllByUserId(@PathVariable(name = "user_id") Long userId) {
        List<MoneyCostsDto> allMoneyCostsDto = moneyCostsService.findAllByUserId(userId)
                .stream()
                .map(moneyCosts -> modelMapper.map(moneyCosts, MoneyCostsDto.class))
                .collect(Collectors.toList());
        return new ResponseEntity<>(
                allMoneyCostsDto,
                HttpStatus.OK);
    }
}
