package com.igemons.adapter.in.web.petlog;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.igemons.domain.petlog.PetLog;
import com.igemons.domain.petlog.PetLogDetail;
import com.igemons.domain.petlog.PetLogType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 사육일지 응답 (요청과 같은 평면 구조, 해당 type에 없는 필드는 생략)
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record PetLogResponse(
    Long id,
    Long petId,
    PetLogType type,
    LocalDateTime loggedAt,
    String memo,
    String feedItem,
    Integer quantity,
    BigDecimal weightGrams,
    BigDecimal temperatureC,
    BigDecimal humidityPct,
    String content,
    LocalDateTime createdAt
) {
    public static PetLogResponse from(PetLog log) {
        String feedItem = null, content = null;
        Integer quantity = null;
        BigDecimal weight = null, temp = null, humidity = null;
        switch (log.getDetail()) {
            case PetLogDetail.Feeding f -> { feedItem = f.item(); quantity = f.quantity(); }
            case PetLogDetail.Shedding s -> { }
            case PetLogDetail.Weight w -> weight = w.grams();
            case PetLogDetail.Environment e -> { temp = e.temperatureC(); humidity = e.humidityPct(); }
            case PetLogDetail.Note n -> content = n.content();
        }
        return new PetLogResponse(log.getId().value(), log.getPetId().value(), log.getType(),
                log.getLoggedAt(), log.getMemo(), feedItem, quantity, weight, temp, humidity,
                content, log.getCreatedAt());
    }
}
