package com.naum.system.moneyservice.repository.money;

import com.naum.system.moneyservice.domain.money.MoneyCosts;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MoneyCostsRepository extends CrudRepository<MoneyCosts, Long> {

    List<MoneyCosts> findAllByUserId(Long userId);
}
