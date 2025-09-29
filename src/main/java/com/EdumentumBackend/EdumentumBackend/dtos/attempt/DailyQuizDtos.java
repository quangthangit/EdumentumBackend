package com.EdumentumBackend.EdumentumBackend.dtos.attempt;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface DailyQuizDtos {
    LocalDate getDay();
    Long getAttempts();
    BigDecimal getAvgScore();
}
