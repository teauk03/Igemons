package com.igemons.application.pets;

import lombok.RequiredArgsConstructor;
import com.igemons.application.NotFoundException;
import com.igemons.domain.pets.Pet;
import com.igemons.domain.pets.PetId;
import com.igemons.domain.users.UserId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PetService {

    private final PetRepository petRepository;
    private final Clock clock;

    /**
     * 새로운 반려 개체를 등록
     *
     * @param owner 개체 소유자 ID
     * @param cmd   개체 기본 정보(이름, 종, 모프 등) 및 프로필(이미지, 메모)이 담긴 커맨드
     * @return 저장된 {@link Pet} 도메인 엔티티
     */
    @Transactional
    public Pet register(UserId owner, PetCommand cmd) {
        Pet pet = Pet.register(owner, cmd.name(), cmd.species(), cmd.morph(), cmd.gender(),
                cmd.birthDate(), cmd.adoptedDate(), LocalDateTime.now(clock));
        // register 팩토리는 이미지/메모를 받지 않으므로 프로필 전체를 한 번 더 적용
        pet.updateProfile(owner, cmd.name(), cmd.species(), cmd.morph(), cmd.gender(),
                cmd.birthDate(), cmd.adoptedDate(), cmd.profileImageUrl(), cmd.memo());
        return petRepository.save(pet);
    }

    /**
     * 특정 사용자가 소유한 활성 상태(삭제되지 않은) 반려 개체 목록을 조회
     *
     * @param owner 소유자 ID
     * @return 활성 상태인 {@link Pet} 목록
     */
    @Transactional(readOnly = true)
    public List<Pet> listMine(UserId owner) {
        return petRepository.findActiveByOwner(owner);
    }

    /**
     * 특정 반려 개체의 상세 정보를 조회  (소유권 검증 포함)
     *
     * @param requester 요청자 ID
     * @param petId     조회할 반려 개체 ID
     * @return 조회된 {@link Pet} 도메인 엔티티
     */
    @Transactional(readOnly = true)
    public Pet get(UserId requester, PetId petId) {
        return loadAccessible(requester, petId);
    }

    /**
     * 기존 반려 개체의 프로필 정보를 수정
     *
     * @param requester 요청자 ID (도메인 내부에서 소유권 검증 수행)
     * @param petId     수정할 반려 개체 ID
     * @param cmd       수정할 정보가 담긴 커맨드
     * @return 수정 완료된 {@link Pet} 도메인 엔티티
     */
    @Transactional
    public Pet update(UserId requester, PetId petId, PetCommand cmd) {
        Pet pet = loadActive(petId);
        pet.updateProfile(requester, cmd.name(), cmd.species(), cmd.morph(), cmd.gender(),
                cmd.birthDate(), cmd.adoptedDate(), cmd.profileImageUrl(), cmd.memo());
        return petRepository.save(pet);
    }


    /**
     * 반려 개체를 논리 삭제(Soft Delete) 처리
     *
     * @param requester 요청자 ID (도메인 내부에서 소유권 검증 수행)
     * @param petId     삭제할 반려 개체 ID
     */
    @Transactional
    public void delete(UserId requester, PetId petId) {
        Pet pet = loadActive(petId);
        pet.delete(requester, LocalDateTime.now(clock));
        petRepository.save(pet);
    }

/**
     * 요청자가 접근 가능한(본인 소유이며 삭제되지 않은) 반려 개체를 조회
     * 다른 애플리케이션 서비스(예: {@code PetLogService} 사육일지 등)에서 소유권 확인용으로 재사용
     *
     * @param requester 요청자 ID
     * @param petId     대상 반려 개체 ID
     * @return 검증이 완료된 {@link Pet} 도메인 엔티티
     */
    @Transactional(readOnly = true)
    public Pet loadAccessible(UserId requester, PetId petId) {
        Pet pet = loadActive(petId);
        pet.verifyAccessibleBy(requester);
        return pet;
    }

    /**
     * 삭제되지 않은(활성 상태) 반려 개체 단건을 조회
     *
     * @throws NotFoundException 개체가 존재하지 않거나 이미 삭제된 상태인 경우
     */
    private Pet loadActive(PetId petId) {
        return petRepository.findById(petId)
            .filter(p -> !p.isDeleted())
            .orElseThrow(() -> new NotFoundException("반려동물을 찾을 수 없습니다."));
    }
}
