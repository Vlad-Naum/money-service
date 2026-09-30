package com.naum.system.moneyservice.repository.money;

import com.naum.system.moneyservice.domain.money.MoneyCosts;
import com.naum.system.moneyservice.domain.money.MoneyCostsCategory;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class MoneyCostsSpecifications {

    private MoneyCostsSpecifications() {
    }

    public static Specification<MoneyCosts> ofUser(Long userId) {
        return (root, query, cb) -> cb.equal(root.get("user").get("id"), userId);
    }

    public static Specification<MoneyCosts> between(LocalDateTime start, LocalDateTime end) {
        return (root, query, cb) -> cb.and(
                cb.greaterThanOrEqualTo(root.get("dateTime"), start),
                cb.lessThan(root.get("dateTime"), end));
    }

    public static Specification<MoneyCosts> hasCategory(MoneyCostsCategory category) {
        return category == null ? null
                : (root, query, cb) -> cb.equal(root.get("moneyCostsCategory"), category);
    }

}
