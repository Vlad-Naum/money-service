package com.naum.system.producer.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MoneyCostsKafka implements Serializable {

    private int moneyCostsCategoryId;

    private long expenses;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm")
    private LocalDateTime localDateTime;

    private String userEmail;
}
