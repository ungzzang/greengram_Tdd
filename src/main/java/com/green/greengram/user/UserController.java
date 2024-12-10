package com.green.greengram.user;

import com.green.greengram.common.model.ResultResponse;
import com.green.greengram.user.follow.model.UserPicPatchReq;
import com.green.greengram.user.model.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.boot.info.BuildProperties;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("user")
@Tag(name = "1. 회원", description = "sign-in / sign-out")
public class UserController {
    private final UserService service;

    @PostMapping("sign-up")
    @Operation(summary = "회원가입")
    public ResultResponse<Integer> signUp(@RequestPart(required = false) MultipartFile pic
                                        , @RequestPart UserSignUpReq p){
        int result = service.signUp(pic, p);

        return ResultResponse.<Integer>builder()
                .resultMessage("회원가입 완료")
                .resultData(result)
                .build();
    }

    @PostMapping("sign-in")
    @Operation(summary = "로그인")
    public ResultResponse<UserSignInRes> signIn(@RequestBody UserSignInReq p){
        UserSignInRes res = service.signIn(p);

        return ResultResponse.<UserSignInRes>builder()
                .resultMessage(res.getMessage())
                .resultData(res)
                .build();
    }

    @GetMapping
    @Operation(summary = "유저 프로필정보")
    public ResultResponse<UserInfoGetRes> getUserInfo(@ParameterObject
                                                          @ModelAttribute UserInfoGetReq p){
        log.info("UserController > getUserInfo > p: {}", p);
        UserInfoGetRes res = service.getUserInfo(p);
        return ResultResponse.<UserInfoGetRes>builder()
                .resultMessage("유저 프로필 정보")
                .resultData(res)
                .build();
    }

    @PatchMapping("pic")
    public ResultResponse<String> patchProfilePic(@ModelAttribute UserPicPatchReq p){
        log.info("UserController > patchProfilePic > p: {}", p);
        String pic = service.patchUserPic(p);
        return ResultResponse.<String>builder()
                .resultMessage("프로필 사진 수정 완료")
                .resultData(pic)
                .build();
    }
}