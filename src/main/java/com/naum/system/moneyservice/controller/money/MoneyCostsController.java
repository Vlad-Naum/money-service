package com.naum.system.moneyservice.controller.money;

import com.naum.system.moneyservice.domain.money.MoneyCostsCategory;
import com.naum.system.moneyservice.domain.money.MoneyCostsDto;
import com.naum.system.moneyservice.service.money.MoneyCostsService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping(path = "/users/{user_id}/money_costs")
public class MoneyCostsController {

    @Autowired
    private MoneyCostsService moneyCostsService;

    @Autowired
    private ModelMapper modelMapper;

    @RequestMapping(path = "/", method = RequestMethod.GET)
    public ResponseEntity<Page<MoneyCostsDto>> getAllByDateAndUserIdWithCategory(
            @PathVariable(name = "user_id") Long userId,
            @RequestParam("localDate") LocalDate date,
            @SortDefault(sort = "id", direction = Sort.Direction.ASC) Pageable pageable,
            @RequestParam("moneyCostsCategory") @Nullable MoneyCostsCategory category) {
        Page<MoneyCostsDto> moneyCostsPage;
        if (category == null) {
            moneyCostsPage = moneyCostsService.findAllByDateAndUserId(date, userId, pageable)
                    .map(moneyCosts -> modelMapper.map(moneyCosts, MoneyCostsDto.class));
        } else {
            moneyCostsPage = moneyCostsService.findAllByDateAndUserIdAndCategory(date, userId, pageable, category)
                    .map(moneyCosts -> modelMapper.map(moneyCosts, MoneyCostsDto.class));
        }
        return new ResponseEntity<>(moneyCostsPage, HttpStatus.OK);
    }
}
