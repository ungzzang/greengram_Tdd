package com.green.greengram.feed.like;

import com.green.greengram.TestUtils;
import com.green.greengram.feed.like.model.FeedLikeReq;
import com.green.greengram.feed.like.model.FeedLikeVo;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*; //이안에있는 모든 static import할수 있게 해서 따로 뭐할필요x

@ActiveProfiles("test") //yaml 적용되는 파일 선택(applicaton-test-yml), test 프로파일을 활성화하겠다.
@MybatisTest //Mybatis Mapper Test이기 때문에 작성 >> Mapper 들이 전부 객체화 >> DI를 할 수 있다.
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
//테스트는 기본적으로 메모리 데이터베이스 (H2)를 사용하는데 메모리 데이터베이스로 교체하지 않겠다.
//즉, 우리가 원래 쓰는 데이터베이스로 테스트를 진행하겠다.
//@TestInstance(TestInstance.Lifecycle.PER_METHOD) //메소드마다 객체 하나생성, 없어도 디폴트라서 PER_CLASS할때만 넣어준다.
class FeedLikeMapperTest {

    @Autowired //DI 시켜줌 //TODO: 스프링 컨테이너가 DI해주는게 맞는지 확인
    FeedLikeMapper feedLikeMapper; // 필드 주입 방식의 DI가 된다.

    @Autowired
    FeedLikeTestMapper feedLikeTestMapper;

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


    // @BeforeAll - 모든 테스트 메소드 실행되기 최초 딱 한번 실행이 되는 메소드
    // 테스트 메소드마다 테스트 객체가 만들어지면 BeforeAll 메소드는 무조건 static 메소드여야 한다.
    // 한 테스트 객체가 만들어지면 non-static 메소드일 수 있다.
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
        //중복은 데이터 입력시 DuplicateKeyException 발생 체크
    void insFeedLikeDuplicateDataThrowDuplicateKeyException(){
        assertThrows(DuplicateKeyException.class, () -> {
            feedLikeMapper.insFeedLike(existedDate);
        }, "데이터 중복시 에러 발생되지 않음 > primary key(feed_id, user_id) 확인 바람");
    }

    @Test
    void insFeedLike () {
        // when (실행)
        List<FeedLikeVo> actualFeedLikeListBefore = feedLikeTestMapper.selFeedLikeAll(); //insert전 기존 튜플 수
        FeedLikeVo actualFeedLikeVoBefore = feedLikeTestMapper.selFeedLikeByFeedIdAndUserId(notExistedData); //insert전 WHERE절에 PK로 데이터를 가져옴
        int actualAffectedRows = feedLikeMapper.insFeedLike(notExistedData); //이게 1이넘어오면 무조건 insert된거라서 나머지는 그냥 더블체크용
        FeedLikeVo actualFeedLikeVoAfter = feedLikeTestMapper.selFeedLikeByFeedIdAndUserId(notExistedData); //insert후 WHERE절에 PK로 데이터를 가져옴
        List<FeedLikeVo> actualFeedLikeListAfter = feedLikeTestMapper.selFeedLikeAll(); //insert후 튜플 수

        // then (단언 , 체크)
        assertAll(
                  () -> TestUtils.assertCurrentTimestamp(actualFeedLikeVoAfter.getCreatedAt())
                , () -> assertEquals(actualFeedLikeListBefore.size() + 1 ,actualFeedLikeListAfter.size())
                , () -> assertNull(actualFeedLikeVoBefore) //내가 insert하려고 하는 데이터가 없었는지 단언
                , () -> assertNotNull(actualFeedLikeVoAfter) //실제 insert가 내가 원하는 데이터로 되었는지 단언, 얘가 null이면 안된다.

                , () -> assertEquals(1, actualAffectedRows) //예상이 1인데 그렇게 넘어왔는지 체크

                , () -> assertEquals(notExistedData.getFeedId(),actualFeedLikeVoAfter.getFeedId()) //내가 원하는 데이터로 insert 되었는지 더블 체크
                , () -> assertEquals(notExistedData.getUserId(),actualFeedLikeVoAfter.getUserId()) //내가 원하는 데이터로 insert 되었는지 더블 체크
        );
    }

    @Test
    void delFeedLikeNoData() { //기존 데이터베이스에 없는 걸 삭제처리
        //when (실행)
        int actualAffectedRows = feedLikeMapper.delFeedLike(notExistedData);

        //then (단언, 체크)
        assertEquals(0, actualAffectedRows);
    }

    @Test
    void delFeedLike() { //기존 데이터베이스에 존재하는 걸 삭제처리
        // when (실행)
        FeedLikeVo actualFeedLikeVoBefore = feedLikeTestMapper.selFeedLikeByFeedIdAndUserId(existedDate); //delete전 WHERE절에 PK로 데이터를 가져옴
        int actualAffectedRows = feedLikeMapper.delFeedLike(existedDate);
        FeedLikeVo actualFeedLikeVoAfter = feedLikeTestMapper.selFeedLikeByFeedIdAndUserId(existedDate); //delete후 WHERE절에 PK로 데이터를 가져옴


        // then (단언 , 체크)
        assertAll(//size 굳이 체크할 필요없어서 뺐음.
                  () -> assertEquals(1, actualAffectedRows) //예상이 1인데 그렇게 넘어왔는지 체크
                , () -> assertNotNull(actualFeedLikeVoBefore) //내가 delete하려고 하는 데이터가 없었는지 단언
                , () -> assertNull(actualFeedLikeVoAfter) //실제 delete가 내가 원하는 데이터로 되었는지 단언


        );

    }
}


//github 보기