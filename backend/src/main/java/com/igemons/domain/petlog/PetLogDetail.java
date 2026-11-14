package com.igemons.domain.petlog;

import java.math.BigDecimal;

import com.igemons.domain.Guard;

public sealed interface PetLogDetail {

    /**
     * 먹이 급여 기록
     * @param item 급여한 먹이의 이름
     * @param quantity 급여 수량 (1 이상)
     */
    record Feeding(String item, int quantity) implements PetLogDetail {
        public Feeding {
            item = Guard.requireText(item, "feedItem", 50);
            Guard.require(quantity > 0, "급여 수량은 1 이상이어야 합니다.");
        }
    }

    /**
     * 탈피 기록
     */
    record Shedding() implements PetLogDetail {}

    /**
     * 펫의 체중 측정 기록
     * @param grams 체중 (그램 단위, 0 초과)
     */
    record Weight(BigDecimal grams) implements PetLogDetail {
        public Weight {
            Guard.require(grams != null && grams.signum() > 0, "체중은 0보다 커야 합니다.");
        }
    }

    /**
     * 사육 환경 측정 기록
     * 온도와 습도 중 하나 이상을 기록
     *
     * @param temperatureC 온도 (섭씨, -20~70)
     * @param humidityPct 습도 (0~100%)
     */
    record Environment(BigDecimal temperatureC, BigDecimal humidityPct) implements PetLogDetail {
        public Environment {
            Guard.require(temperatureC != null || humidityPct != null,
                    "온도 또는 습도 중 하나는 입력해야 합니다.");
            if (temperatureC != null) {
                Guard.require(temperatureC.compareTo(BigDecimal.valueOf(-20)) >= 0
                        && temperatureC.compareTo(BigDecimal.valueOf(70)) <= 0,
                        "온도 범위가 올바르지 않습니다.");
            }
            if (humidityPct != null) {
                Guard.require(humidityPct.signum() >= 0
                        && humidityPct.compareTo(BigDecimal.valueOf(100)) <= 0,
                        "습도는 0~100 사이여야 합니다.");
            }
        }
    }
    
    /**
     * 펫의 특이사항 및 기타 메모 기록
     * @param content 메모 내용
     */
    record Note(String content) implements PetLogDetail {
        public Note {
            content = Guard.requireText(content, "content", 500);
        }
    }
    
    /**
     * 펫 로그 종류 반환
     * @return PetLogType
     */
    default PetLogType type() {
        return switch (this) {
            case Feeding f -> PetLogType.FEEDING;
            case Shedding s -> PetLogType.SHEDDING;
            case Weight w -> PetLogType.WEIGHT;
            case Environment e -> PetLogType.ENVIRONMENT;
            case Note n -> PetLogType.NOTE;
        };
    }
}
