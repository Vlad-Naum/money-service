package com.naum.system.moneyservice.service.money;

import com.naum.system.moneyservice.domain.money.MoneyCosts;
import com.naum.system.moneyservice.domain.money.MoneyCostsCategory;
import com.naum.system.moneyservice.domain.user.User;
import com.naum.system.moneyservice.repository.money.MoneyCostsRepository;
import com.naum.system.moneyservice.service.kafka.message.MoneyCostsKafka;
import com.naum.system.moneyservice.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static com.naum.system.moneyservice.repository.money.MoneyCostsSpecifications.*;

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
        MoneyCostsCategory category = MoneyCostsCategory.getOrDefault(message.getMoneyCostsCategory());
        return create(user, message.getLocalDateTime(), message.getExpenses(), category);
    }

    @Transactional(readOnly = true)
    public List<MoneyCosts> findAllByUserId(Long userId) {
        return moneyCostsRepository.findAllByUserId(userId);
    }

    @Transactional(readOnly = true)
    public Page<MoneyCosts> find(Long userId, LocalDate date, MoneyCostsCategory category, Pageable pageable) {
        Specification<MoneyCosts> spec = Specification.where(ofUser(userId))
                .and(between(date.atStartOfDay(), date.plusDays(1).atStartOfDay()))
                .and(hasCategory(category));
        return moneyCostsRepository.findAll(spec, pageable);
    }

}
