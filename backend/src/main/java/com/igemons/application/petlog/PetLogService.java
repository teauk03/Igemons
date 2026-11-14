package com.igemons.application.petlog;

import lombok.RequiredArgsConstructor;
import com.igemons.application.NotFoundException;
import com.igemons.application.pets.PetService;
import com.igemons.domain.petlog.PetLog;
import com.igemons.domain.petlog.PetLogId;
import com.igemons.domain.petlog.PetLogType;
import com.igemons.domain.pets.PetId;
import com.igemons.domain.users.UserId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PetLogService {

    private final PetLogRepository petLogRepository;
    private final PetService petService;
    private final Clock clock;

    /**
     * 특정 반려 개체에 새로운 케어 기록을 등록
     *
     * @param requester 요청자 ID (개체 접근 권한 검증용)
     * @param petId     대상 반려 개체 ID
     * @param cmd       기록 상세 내용, 발생 시각(미입력 시 현재 시각), 메모가 담긴 커맨드
     * @return 저장된 {@link PetLog} 도메인 엔티티
     */
    @Transactional
    public PetLog record(UserId requester, PetId petId, PetLogCommand cmd) {
        petService.loadAccessible(requester, petId);
        LocalDateTime now = LocalDateTime.now(clock);
        PetLog log = PetLog.record(petId, cmd.detail(), orNow(cmd.loggedAt(), now), cmd.memo(), now);
        return petLogRepository.save(log);
    }

    /**
     * 특정 반려 개체의 케어 기록 목록을 유형별로 조회
     *
     * @param requester 요청자 ID
     * @param petId     대상 반려 개체 ID
     * @param type      조회할 기록 유형 (null인 경우 전체 유형 조회)
     * @return 조건에 일치하는 {@link PetLog} 목록
     */
    @Transactional(readOnly = true)
    public List<PetLog> list(UserId requester, PetId petId, PetLogType type) {
        petService.loadAccessible(requester, petId);
        return petLogRepository.findByPet(petId, type);
    }

    /**
     * 특정 반려 개체에 속한 단건 케어 기록을 상세 조회
     *
     * @param requester 요청자 ID
     * @param petId     대상 반려 개체 ID
     * @param logId     조회할 기록 ID
     * @return 조회된 {@link PetLog} 도메인 엔티티
     */
    @Transactional(readOnly = true)
    public PetLog get(UserId requester, PetId petId, PetLogId logId) {
        petService.loadAccessible(requester, petId);
        return loadOfPet(petId, logId);
    }

    /**
     * 기존 케어 기록의 내용, 발생 시각, 메모를 수정
     *
     * @param requester 요청자 ID
     * @param petId     대상 반려 개체 ID
     * @param logId     수정할 기록 ID
     * @param cmd       수정할 내용이 담긴 커맨드
     * @return 수정 완료된 {@link PetLog} 도메인 엔티티
     */
    @Transactional
    public PetLog amend(UserId requester, PetId petId, PetLogId logId, PetLogCommand cmd) {
        petService.loadAccessible(requester, petId);
        PetLog log = loadOfPet(petId, logId);
        LocalDateTime now = LocalDateTime.now(clock);
        // loggedAt을 보내지 않으면 기존 발생 시각 유지
        LocalDateTime loggedAt = Objects.requireNonNullElse(cmd.loggedAt(), log.getLoggedAt());
        log.amend(cmd.detail(), loggedAt, cmd.memo(), now);
        return petLogRepository.save(log);
    }

    /**
     * 특정 반려 개체의 케어 기록을 삭제
     *
     * @param requester 요청자 ID
     * @param petId     대상 반려 개체 ID
     * @param logId     삭제할 기록 ID
     */
    @Transactional
    public void delete(UserId requester, PetId petId, PetLogId logId) {
        petService.loadAccessible(requester, petId);
        loadOfPet(petId, logId);
        petLogRepository.delete(logId);
    }

    /**
     * 기록 ID로 엔티티를 조회하고, 요청된 반려 개체({@code petId})의 기록이 맞는지 소유 관계를 검증
     *
     * @throws NotFoundException 기록이 존재하지 않거나 다른 반려 개체의 기록인 경우
     */
    private PetLog loadOfPet(PetId petId, PetLogId logId) {
        return petLogRepository.findById(logId)
            .filter(l -> l.getPetId().equals(petId))
            .orElseThrow(() -> new NotFoundException("기록을 찾을 수 없습니다."));
    }

    /**
     * 전달된 시각 값이 null이면 기본 시각({@code now})을 반환
     */
    private static LocalDateTime orNow(LocalDateTime value, LocalDateTime now) {
        return Objects.requireNonNullElse(value, now);
    }
}
