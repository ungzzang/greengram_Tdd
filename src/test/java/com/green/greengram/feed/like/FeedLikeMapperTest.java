package com.green.greengram.feed.like;

import com.green.greengram.feed.like.model.FeedLikeReq;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*; //이안에있는 모든 static import할수 있게 해서 따로 뭐할필요x

@ActiveProfiles("test") //yaml 적용되는 파일 선택(applicaton-test-yml), test 프로파일을 활성화하겠다.
@MybatisTest //Mybatis Mapper Test이기 때문에 작성 >> Mapper 들이 전부 객체화 >> DI를 할 수 있다.
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
//테스트는 기본적으로 메모리 데이터베이스 (H2)를 사용하는데 메모리 데이터베이스로 교체하지 않겠다.
//즉, 우리가 원래 쓰는 데이터베이스로 테스트를 진행하겠다.
@TestInstance(TestInstance.Lifecycle.PER_METHOD) //메소드마다 객체 하나생성
class FeedLikeMapperTest {

    @Autowired //DI 시켜줌 //TODO: 스프링 컨테이너가 DI해주는게 맞는지 확인
    FeedLikeMapper feedLikeMapper; // 필드 주입 방식의 DI가 된다.

    static final long FEED_ID_1 = 1L;
    static final long FEED_ID_5 = 5L;
    static final long USER_ID_2 = 2L;

    static final FeedLikeReq existedDate = new FeedLikeReq();
    static final FeedLikeReq notExistedData = new FeedLikeReq();
    /*
        @BeforeAll - 모든 테스트 실행 전에 최초 한 번 실행
        ---
        @BeforeEach - 각 테스트 실행 전에 실행
        @Test
        @AfterEach - 각 테스트 실행 후에 실행
        ---
        @AfterAll - 모든 테스트 실행 후에 최초 한 번 실행
     */


    // @BeforeAll - 테스트 메소드 실행되기 최초 딱 한번 실행이 되는 메소드
    // 테스트 메소드마다 테스트 객체가 만들어지면 BeforeAll 메소드는 static 메소드여야 한다.
    // 한 테스트 객체가 만들어지면 non-static 메소드여야 한다.
    @BeforeAll
    static void initData() {
        existedDate.setFeedId(FEED_ID_1);
        existedDate.setUserId(USER_ID_2);

        notExistedData.setFeedId(FEED_ID_5);
        notExistedData.setUserId(USER_ID_2);
    }

    // @BeforeEach - 테스트 메소드마다 테스트 메소드 실행 전에 실행되는 before메소드
    // before메소드


    @Test
    @DisplayName("중복된 데이터 입력시 DuplicateKeyException 발생 체크")
    void insFeedLikeDuplicateDataThrowDuplicateKeyException(){
        assertThrows(DuplicateKeyException.class, () -> {
            feedLikeMapper.insFeedLike(existedDate);
        }, "데이터 중복시 에러 발생되지 않음 > primary key(feed_id");
    }

    @Test
    void insFeedLikeNormal() {
        //when (실행)
        int actualAffectedRows = feedLikeMapper.insFeedLike(notExistedData);

        //then (단언, 체크)
        assertEquals(1, actualAffectedRows );
    }

    @Test
    void delFeedLikeNoData() {
        //when (실행)
        int actualAffectedRows = feedLikeMapper.delFeedLike(notExistedData);

        //then (단언, 체크)
        assertEquals(0, actualAffectedRows);
    }

    @Test
    void delFeedLikeNormal() {
        int actualAffectedRows = feedLikeMapper.delFeedLike(existedDate);
        assertEquals(1, actualAffectedRows);
    }
}


//github 보기