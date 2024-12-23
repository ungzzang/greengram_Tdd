package com.green.greengram.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

//500단위 에러 - 서버측 에러
//400단위 에러 - 클라이언트측 에러
// enum - 실수방지
@Getter
@RequiredArgsConstructor
public enum CommonErrorCode implements ErrorCode{

    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR
            , "서버 내부에서 에러가 발생하였습니다.")
    , INVALID_PARAMETER(HttpStatus.BAD_REQUEST
            , "잘못된 파라미터입니다.")

    //, AAA("이렇게 추가가능")
    ;


    private final HttpStatus httpStatus;
    private final String message;
}

