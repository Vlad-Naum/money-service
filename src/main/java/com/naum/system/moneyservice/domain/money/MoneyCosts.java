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
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "money_costs_seq")
    @SequenceGenerator(name = "money_costs_seq", sequenceName = "money_costs_seq")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "category")
    private MoneyCostsCategory moneyCostsCategory;

    @Column(name = "expenses")
    private Long expenses;

    @Column(name = "date_time")
    private LocalDateTime dateTime;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="user_id", nullable=false, updatable=false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MoneyCosts other)) return false;
        return id != null && id.equals(other.getId());
    }

    @Override
    public int hashCode() {
        return MoneyCosts.class.hashCode();
    }

    @Override
    public String toString() {
        return "MoneyCosts{" +
                "id=" + id +
                ", moneyCostsCategory=" + moneyCostsCategory +
                ", expenses=" + expenses +
                ", dateTime=" + dateTime +
                ", userId=" + user.getId() +
                '}';
    }
}
