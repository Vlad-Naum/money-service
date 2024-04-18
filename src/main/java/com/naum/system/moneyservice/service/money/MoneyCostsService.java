package com.naum.system.moneyservice.service.money;

import com.naum.system.moneyservice.domain.money.MoneyCosts;
import com.naum.system.moneyservice.domain.money.MoneyCostsCategory;
import com.naum.system.moneyservice.domain.user.User;
import com.naum.system.moneyservice.repository.money.MoneyCostsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MoneyCostsService {

    @Autowired
    MoneyCostsRepository moneyCostsRepository;

    public MoneyCosts create(User user, LocalDateTime dateTime, Long expenses, MoneyCostsCategory costsCategory) {
        MoneyCosts moneyCosts = new MoneyCosts();
        moneyCosts.setUser(user);
        moneyCosts.setLocalDateTime(dateTime);
        moneyCosts.setExpenses(expenses);
        moneyCosts.setMoneyCostsCategory(costsCategory);
        return moneyCostsRepository.save(moneyCosts);
    }

    public List<MoneyCosts> findAllByUserId(Long userId) {
        return moneyCostsRepository.findAllByUserId(userId);
    }
}
