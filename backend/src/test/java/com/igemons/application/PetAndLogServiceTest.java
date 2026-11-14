package com.igemons.application;

import com.igemons.application.petlog.PetLogCommand;
import com.igemons.application.petlog.PetLogRepository;
import com.igemons.application.petlog.PetLogService;
import com.igemons.application.pets.PetCommand;
import com.igemons.application.pets.PetRepository;
import com.igemons.application.pets.PetService;
import com.igemons.domain.DomainException;
import com.igemons.domain.petlog.*;
import com.igemons.domain.pets.*;
import com.igemons.domain.users.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

import static org.assertj.core.api.Assertions.*;

class PetAndLogServiceTest {

    static final LocalDateTime NOW = LocalDateTime.of(2026, 11, 14, 12, 0);
    static final UserId OWNER = UserId.of(1L);
    static final UserId STRANGER = UserId.of(2L);

    PetService petService;
    PetLogService logService;

    @BeforeEach
    void setUp() {
        Clock clock = Clock.fixed(NOW.atZone(ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault());
        petService = new PetService(new InMemoryPets(), clock);
        logService = new PetLogService(new InMemoryLogs(), petService, clock);
    }

    PetCommand gecko() {
        return new PetCommand("Mochi", "Leopard Gecko", "Tangerine", PetGender.FEMALE,
                LocalDate.of(2025, 5, 1), LocalDate.of(2025, 8, 1), null, "첫 개체");
    }

    @Test
    void 펫_등록_조회_수정_삭제() {
        Pet pet = petService.register(OWNER, gecko());
        assertThat(pet.getId()).isNotNull();
        assertThat(pet.getMemo()).isEqualTo("첫 개체");
        assertThat(petService.listMine(OWNER)).hasSize(1);
        assertThat(petService.listMine(STRANGER)).isEmpty();

        Pet updated = petService.update(OWNER, pet.getId(),
                new PetCommand("Mochi2", "Leopard Gecko", null, null, null, null, null, null));
        assertThat(updated.getName()).isEqualTo("Mochi2");
        assertThat(updated.getGender()).isEqualTo(PetGender.UNKNOWN);

        petService.delete(OWNER, pet.getId());
        assertThat(petService.listMine(OWNER)).isEmpty();
        assertThatThrownBy(() -> petService.get(OWNER, pet.getId())).isInstanceOf(NotFoundException.class);
    }

    @Test
    void 다른_사용자는_펫과_기록에_접근할_수_없다() {
        Pet pet = petService.register(OWNER, gecko());
        assertThatThrownBy(() -> petService.get(STRANGER, pet.getId()))
                .isInstanceOf(PetAccessDeniedException.class);
        assertThatThrownBy(() -> logService.list(STRANGER, pet.getId(), null))
                .isInstanceOf(PetAccessDeniedException.class);
    }

    @Test
    void 기록_생성_필터_수정_삭제() {
        PetId petId = petService.register(OWNER, gecko()).getId();
        PetLog feeding = logService.record(OWNER, petId,
                new PetLogCommand(new PetLogDetail.Feeding("귀뚜라미", 5), NOW.minusHours(2), null));
        logService.record(OWNER, petId,
                new PetLogCommand(new PetLogDetail.Weight(new BigDecimal("52.3")), null, null));

        assertThat(logService.list(OWNER, petId, null)).hasSize(2);
        assertThat(logService.list(OWNER, petId, PetLogType.FEEDING)).hasSize(1);

        PetLog amended = logService.amend(OWNER, petId, feeding.getId(),
                new PetLogCommand(new PetLogDetail.Feeding("밀웜", 3), NOW.minusHours(1), "잘 먹음"));
        assertThat(((PetLogDetail.Feeding) amended.getDetail()).item()).isEqualTo("밀웜");

        PetLog keptTime = logService.amend(OWNER, petId, feeding.getId(),
                new PetLogCommand(new PetLogDetail.Feeding("밀웜", 4), null, null));
        assertThat(keptTime.getLoggedAt()).isEqualTo(NOW.minusHours(1));

        assertThatThrownBy(() -> logService.amend(OWNER, petId, feeding.getId(),
                new PetLogCommand(new PetLogDetail.Shedding(), null, null)))
                .isInstanceOf(DomainException.class);

        logService.delete(OWNER, petId, feeding.getId());
        assertThat(logService.list(OWNER, petId, null)).hasSize(1);
    }

    @Test
    void 다른_펫의_기록_id로는_접근할_수_없다() {
        PetId a = petService.register(OWNER, gecko()).getId();
        PetId b = petService.register(OWNER, gecko()).getId();
        PetLog log = logService.record(OWNER, a,
                new PetLogCommand(new PetLogDetail.Shedding(), null, null));
        assertThatThrownBy(() -> logService.get(OWNER, b, log.getId()))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void 미래_시각_기록은_거부된다() {
        PetId petId = petService.register(OWNER, gecko()).getId();
        assertThatThrownBy(() -> logService.record(OWNER, petId,
                new PetLogCommand(new PetLogDetail.Shedding(), NOW.plusMinutes(1), null)))
                .isInstanceOf(DomainException.class);
    }

    // ---------- in-memory fakes ----------

    static class InMemoryPets implements PetRepository {
        final Map<Long, Pet> store = new LinkedHashMap<>();
        final AtomicLong seq = new AtomicLong();

        public Optional<Pet> findById(PetId id) { return Optional.ofNullable(store.get(id.value())); }

        public List<Pet> findActiveByOwner(UserId ownerId) {
            return store.values().stream()
                    .filter(p -> p.getOwnerId().equals(ownerId) && !p.isDeleted()).toList();
        }

        public Pet save(Pet p) {
            PetId id = p.isNew() ? PetId.of(seq.incrementAndGet()) : p.getId();
            Pet saved = Pet.restore(id, p.getOwnerId(), p.getName(), p.getSpecies(), p.getMorph(),
                    p.getGender(), p.getBirthDate(), p.getAdoptedDate(), p.getProfileImageUrl(),
                    p.getMemo(), p.getDeletedAt(), p.getCreatedAt());
            store.put(id.value(), saved);
            return saved;
        }
    }

    static class InMemoryLogs implements PetLogRepository {
        final Map<Long, PetLog> store = new LinkedHashMap<>();
        final AtomicLong seq = new AtomicLong();

        public Optional<PetLog> findById(PetLogId id) { return Optional.ofNullable(store.get(id.value())); }

        public List<PetLog> findByPet(PetId petId, PetLogType type) {
            return store.values().stream()
                    .filter(l -> l.getPetId().equals(petId) && (type == null || l.getType() == type))
                    .sorted(Comparator.comparing(PetLog::getLoggedAt).reversed()).toList();
        }

        public PetLog save(PetLog l) {
            PetLogId id = l.isNew() ? PetLogId.of(seq.incrementAndGet()) : l.getId();
            PetLog saved = PetLog.restore(id, l.getPetId(), l.getDetail(), l.getLoggedAt(),
                    l.getMemo(), l.getCreatedAt());
            store.put(id.value(), saved);
            return saved;
        }

        public void delete(PetLogId id) { store.remove(id.value()); }
    }
}
