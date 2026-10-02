package com.naum.system.moneyservice.service.money;

import com.naum.system.moneyservice.domain.money.MoneyCosts;
import com.naum.system.moneyservice.domain.money.MoneyCostsCategory;
import com.naum.system.moneyservice.domain.user.User;
import com.naum.system.moneyservice.repository.money.MoneyCostsRepository;
import com.naum.system.moneyservice.service.kafka.message.RegisterExpenseCommand;
import com.naum.system.moneyservice.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import static com.naum.system.moneyservice.repository.money.MoneyCostsSpecifications.*;

@Service
@RequiredArgsConstructor
public class MoneyCostsService {

    private final MoneyCostsRepository moneyCostsRepository;

    private final UserService userService;

    public MoneyCosts create(User user, Instant occurredAt, Long expenses, MoneyCostsCategory costsCategory) {
        MoneyCosts moneyCosts = new MoneyCosts();
        moneyCosts.setUser(user);
        moneyCosts.setDateTime(LocalDateTime.ofInstant(occurredAt, ZoneId.systemDefault()));
        moneyCosts.setExpenses(expenses);
        moneyCosts.setMoneyCostsCategory(costsCategory);
        return moneyCostsRepository.save(moneyCosts);
    }

    @Transactional
    public MoneyCosts registerExpense(RegisterExpenseCommand expenseCommand) {
        User user = userService.getOrCreate(expenseCommand.userEmail());
        return create(user, expenseCommand.occurredAt(), expenseCommand.expenses(), expenseCommand.category());
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
