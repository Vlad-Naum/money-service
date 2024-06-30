package com.naum.system.moneyservice.repository.money;

import com.naum.system.moneyservice.domain.money.MoneyCosts;
import com.naum.system.moneyservice.domain.money.MoneyCostsCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MoneyCostsRepository extends CrudRepository<MoneyCosts, Long> {

    List<MoneyCosts> findAllByUserId(Long userId);

    @Query(value = "SELECT e.* FROM money_costs e WHERE DATE(date_time) =:date AND user_id =:userId", nativeQuery = true)
    Page<MoneyCosts> findByDateAndUserId(LocalDate date, Long userId, Pageable pageable);

    @Query(value = "SELECT e.* FROM money_costs e WHERE DATE(date_time) =:date AND user_id =:userId AND category IN (:category)", nativeQuery = true)
    Page<MoneyCosts> findByDateAndUserIdAndCategory(LocalDate date, Long userId, MoneyCostsCategory category, Pageable pageable);
}
