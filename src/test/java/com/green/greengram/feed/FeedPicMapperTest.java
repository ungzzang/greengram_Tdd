package com.green.greengram.feed;

import com.green.greengram.feed.like.model.FeedPicVo;
import com.green.greengram.feed.model.FeedPicDto;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test") // yaml 저굥ㅇ되는 파일 선택 (application-test.yml)
@MybatisTest // Mybatis Mapper Test 이기 때문에 작성 >> Mapper 들이 전부 객체화
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class FeedPicMapperTest {
    @Autowired
    FeedPicMapper feedPicMapper;

    @Autowired
    FeedPicTestMapper feedPicTestMapper;

    @Test
    void insFeedPicNoFeedIdThrowException(){
        FeedPicDto givenParam = new FeedPicDto();
        givenParam.setFeedId(10L);
        givenParam.setPics(new ArrayList<>());
        givenParam.getPics().add("a.jpg");

        assertThrows(DataIntegrityViolationException.class, () -> {
            feedPicMapper.insFeedPic(givenParam);
        });
    }

    @Test
    void insFeedPicNullPicsThrowException () {
        FeedPicDto givenParam = new FeedPicDto();
        givenParam.setFeedId(1L);

        assertThrows(Exception.class, () -> {
            feedPicMapper.insFeedPic(givenParam);
        });
    }
    @Test
    void insFeedPicNoPicThrowException () {
        FeedPicDto givenParam = new FeedPicDto();
        givenParam.setFeedId(1L);
        givenParam.setPics(new ArrayList<>());
        assertThrows(BadSqlGrammarException.class, () -> {
            feedPicMapper.insFeedPic(givenParam);
        });
    }
    @Test
    void insFeedPic_PicStringLengthMoreThan50_ThrowException (){
        FeedPicDto givenParam = new FeedPicDto();
        givenParam.setFeedId(1L);
        givenParam.setPics(new ArrayList<>(1));
        givenParam.getPics().add("56848964165489643165468_568489413216846135165.jpg");

        assertThrows(BadSqlGrammarException.class , () -> {
            feedPicMapper.insFeedPic(givenParam);
        });
    }

    @Test
    void insFeedPic(){
        String[] pics = {"a.jpg" , "b.jpg" , "c.jpg"};
        FeedPicDto givenParam = new FeedPicDto();
        givenParam.setFeedId(5L);
        givenParam.setPics(new ArrayList<>(pics.length));
        for (String pic : pics){
            givenParam.getPics().add(pic);
        }
        List<FeedPicVo> feedPicListBefore = feedPicTestMapper.selFeedPicListByFeedId(givenParam.getFeedId());
        int actualAffectedRows = feedPicMapper.insFeedPic(givenParam);
        List<FeedPicVo> feedPicListAfter = feedPicTestMapper.selFeedPicListByFeedId(givenParam.getFeedId());

        List<String> picList = Arrays.asList(pics);
        for(int i = 0; i < pics.length; i++) {
            String pic = picList.get(i);
            System.out.printf("%s - contains: %b\n", pic, feedPicListAfter.contains(pic));
        } //보여주기용으로 만들어서 없어도 됨.
        assertAll(
                  () -> {

                  }
                , () -> assertEquals(givenParam.getPics().size(), actualAffectedRows)
                , () -> assertEquals(0, feedPicListBefore.size())
                , () -> assertEquals(givenParam.getPics().size(), feedPicListAfter.size())
                , () -> assertTrue(feedPicListAfter.containsAll(Arrays.asList(pics)))
        );

        //created_at 단언
    }
}