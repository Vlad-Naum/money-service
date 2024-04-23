package com.naum.system.moneyservice.domain.money;

import com.naum.system.moneyservice.domain.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "money_costs")
public class MoneyCosts {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    Long id;

    @Column(name = "category")
    private MoneyCostsCategory moneyCostsCategory;

    @Column(name = "expenses")
    private Long expenses;

    @Column(name = "date_time")
    private LocalDateTime dateTime;

    @ManyToOne(optional = false)
    @JoinColumn(name="user_id", nullable=false, updatable=false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @Override
    public String toString() {
        return "MoneyCosts{" +
                "id=" + id +
                ", moneyCostsCategory=" + moneyCostsCategory +
                ", expenses=" + expenses +
                ", dateTime=" + dateTime +
                ", user=" + user +
                '}';
    }
}
