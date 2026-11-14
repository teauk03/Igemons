package com.igemons.domain;

/**
 * 도메인 규칙 검증 유틸리티
 */
public final class Guard {
    private Guard() {}
    
    /** 
     * 필수 텍스트를 검증하고, 공백을 제거한 값을 반환
     * @param value 검증할 값
     * @param field 필드 이름 (예: "name")
     * @param maxLength 최대 길이
     * @return 공백 제거된 값
     * @throws DomainException 필수 값이 없거나 최대 길이를 초과하면 발생
     */
    public static String requireText(String value, String field, int maxLength) {
        if (value == null || value.isBlank()) {
            throw new DomainException(field + "은(는) 필수입니다.");
        }
        String trimmed = value.strip();
        if (trimmed.length() > maxLength) {
            throw new DomainException(field + "은(는) " + maxLength + "자를 넘을 수 없습니다.");
        }
        return trimmed;
    }
    
    /** 
     * 선택적 텍스트를 검증하고, 공백을 제거한 값을 반환
     * @param value 검증할 값
     * @param field 필드 이름 (예: "memo")
     * @param maxLength 최대 길이
     * @return 공백 제거된 값 또는 null
     * @throws DomainException 최대 길이를 초과하면 발생
     */
    public static String optionalText(String value, String field, int maxLength) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return requireText(value, field, maxLength);
    }
    
    /** 
     * 조건을 검증하고, 위반 시 DomainException 발생
     * @param condition 검증할 조건
     * @param message 예외 메시지
     * @throws DomainException 조건이 false이면 발생
     */
    public static void require(boolean condition, String message) {
        if (!condition) {
            throw new DomainException(message);
        }
    }
}
