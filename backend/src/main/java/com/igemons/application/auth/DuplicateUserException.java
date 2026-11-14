package com.igemons.application.auth;

/**
 * 이미 가입된 이메일로 회원가입을 시도할 때 발생하는 예외
 * 서비스의 사전 중복 검사와 DB UNIQUE 제약 위반(동시 가입) 모두 이 예외로 통일
 * HTTP 409 매핑은 adapter에서 처리
 */
public class DuplicateUserException extends RuntimeException {
    public DuplicateUserException() {
        super("이미 가입된 회원 정보가 있습니다.");
    }
}
