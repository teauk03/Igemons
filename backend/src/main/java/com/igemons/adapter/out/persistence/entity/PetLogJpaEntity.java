package com.igemons.adapter.out.persistence.entity;

import com.igemons.domain.petlog.PetLog;
import com.igemons.domain.petlog.PetLogDetail;
import com.igemons.domain.petlog.PetLogId;
import com.igemons.domain.petlog.PetLogType;
import com.igemons.domain.pets.PetId;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 사육일지 단일 테이블 매핑
 * 종류(type)별 상세 값은 nullable 컬럼에 나눠 저장
 */
@Entity
@Table(name = "pet_logs", indexes = @Index(name = "idx_pet_logs_pet_logged", columnList = "pet_id, logged_at"))
public class PetLogJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "pet_id", nullable = false)
    private Long petId;

    @Enumerated(EnumType.STRING)
    @Column(name = "log_type", nullable = false, length = 20)
    private PetLogType type;

    @Column(name = "feeding_item", length = 50)
    private String feedItem;

    @Column(name = "feeding_quantity")
    private Integer feedQuantity;

    @Column(name = "weight_grams", precision = 10, scale = 2)
    private BigDecimal weightGrams;

    @Column(name = "temperature_c", precision = 5, scale = 2)
    private BigDecimal temperatureC;

    @Column(name = "humidity_pct", precision = 5, scale = 2)
    private BigDecimal humidityPct;

    @Column(name = "note_content", length = 500)
    private String noteContent;

    @Column(name = "logged_at", nullable = false)
    private LocalDateTime loggedAt;

    @Column(length = 1000)
    private String memo;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    protected PetLogJpaEntity() {}

    public static PetLogJpaEntity from(PetLog log) {
        PetLogJpaEntity e = new PetLogJpaEntity();
        if (log.getId() != null) e.id = log.getId().value();
        e.petId = log.getPetId().value();
        e.type = log.getType();
        switch (log.getDetail()) {
            case PetLogDetail.Feeding f -> { e.feedItem = f.item(); e.feedQuantity = f.quantity(); }
            case PetLogDetail.Shedding s -> { }
            case PetLogDetail.Weight w -> e.weightGrams = w.grams();
            case PetLogDetail.Environment env -> { e.temperatureC = env.temperatureC(); e.humidityPct = env.humidityPct(); }
            case PetLogDetail.Note n -> e.noteContent = n.content();
        }
        e.loggedAt = log.getLoggedAt();
        e.memo = log.getMemo();
        e.createdAt = log.getCreatedAt();
        return e;
    }

    public PetLog toDomain() {
        PetLogDetail detail = switch (type) {
            case FEEDING -> new PetLogDetail.Feeding(feedItem, feedQuantity == null ? 0 : feedQuantity);
            case SHEDDING -> new PetLogDetail.Shedding();
            case WEIGHT -> new PetLogDetail.Weight(weightGrams);
            case ENVIRONMENT -> new PetLogDetail.Environment(temperatureC, humidityPct);
            case NOTE -> new PetLogDetail.Note(noteContent);
        };
        return PetLog.restore(PetLogId.of(id), PetId.of(petId), detail, loggedAt, memo, createdAt);
    }
}
