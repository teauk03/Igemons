package com.igemons.adapter.in.web.petlog;

import com.igemons.application.petlog.PetLogCommand;
import com.igemons.domain.DomainException;
import com.igemons.domain.petlog.PetLogDetail;
import com.igemons.domain.petlog.PetLogType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 사육일지 요청 (type에 해당하는 필드만 사용)
 * - FEEDING:     feedItem, quantity
 * - SHEDDING:    (없음)
 * - WEIGHT:      weightGrams
 * - ENVIRONMENT: temperatureC, humidityPct (하나 이상)
 * - NOTE:        content
 */
public record PetLogRequest(
    PetLogType type,
    LocalDateTime loggedAt,
    String memo,
    String feedItem,
    Integer quantity,
    BigDecimal weightGrams,
    BigDecimal temperatureC,
    BigDecimal humidityPct,
    String content
) {
    public PetLogCommand toCommand() {
        if (type == null) {
            throw new DomainException("type은(는) 필수입니다.");
        }
        PetLogDetail detail = switch (type) {
            case FEEDING -> new PetLogDetail.Feeding(feedItem, quantity == null ? 0 : quantity);
            case SHEDDING -> new PetLogDetail.Shedding();
            case WEIGHT -> new PetLogDetail.Weight(weightGrams);
            case ENVIRONMENT -> new PetLogDetail.Environment(temperatureC, humidityPct);
            case NOTE -> new PetLogDetail.Note(content);
        };
        return new PetLogCommand(detail, loggedAt, memo);
    }
}
