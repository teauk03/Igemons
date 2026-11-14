package com.igemons.domain.pets;

import com.igemons.domain.DomainException;

/**
 * 펫에 대한 접근 권한이 없는 경우 발생하는 예외
 */
public class PetAccessDeniedException extends DomainException {
    public PetAccessDeniedException() {
        super("펫에 대한 접근 권한이 없습니다.");
    }
}