package com.naum.system.moneyservice.service.money;

import com.naum.system.moneyservice.domain.money.MoneyCosts;
import com.naum.system.moneyservice.domain.money.MoneyCostsCategory;
import com.naum.system.moneyservice.domain.user.User;
import com.naum.system.moneyservice.repository.money.MoneyCostsRepository;
import com.naum.system.moneyservice.service.kafka.message.MoneyCostsKafka;
import com.naum.system.moneyservice.service.user.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MoneyCostsService {

    private final MoneyCostsRepository moneyCostsRepository;

    private final UserService userService;

    public MoneyCosts create(User user, LocalDateTime dateTime, Long expenses, MoneyCostsCategory costsCategory) {
        MoneyCosts moneyCosts = new MoneyCosts();
        moneyCosts.setUser(user);
        moneyCosts.setDateTime(dateTime);
        moneyCosts.setExpenses(expenses);
        moneyCosts.setMoneyCostsCategory(costsCategory);
        return moneyCostsRepository.save(moneyCosts);
    }

    @Transactional
    public MoneyCosts registerExpense(MoneyCostsKafka message) {
        User user = userService.getOrCreate(message.getUserEmail());
        MoneyCostsCategory category = MoneyCostsCategory.getById(message.getMoneyCostsCategoryId());
        category = (category == null) ? MoneyCostsCategory.getDefault() : category;
        return create(user, message.getLocalDateTime(), message.getExpenses(), category);
    }

    public List<MoneyCosts> findAllByUserId(Long userId) {
        return moneyCostsRepository.findAllByUserId(userId);
    }

    public Page<MoneyCosts> findAllByDateAndUserId(LocalDate date, Long userId, Pageable pageable) {
        return moneyCostsRepository.findByDateAndUserId(date, userId, pageable);
    }

    public Page<MoneyCosts> findAllByDateAndUserIdAndCategory(LocalDate date, Long userId, Pageable pageable,
                                                              MoneyCostsCategory category) {
        return moneyCostsRepository.findByDateAndUserIdAndCategory(date, userId, category, pageable);
    }
}
