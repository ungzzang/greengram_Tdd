package com.green.greengram.feed;

import com.green.greengram.TestUtils;
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

        //feedPicListAfter에서 pic만 뽑아내서 이전처럼 List<String> 변형한 다음 체크한다.
        List<String> feedOnlyPicList = new ArrayList<>(feedPicListAfter.size());
        for(FeedPicVo item : feedPicListAfter){
            feedOnlyPicList.add(item.getPic());
        }

        //스트림 이용해서 한다.
        List<String> picList = Arrays.asList(pics);
        for(int i = 0; i < pics.length; i++) {
            String pic = picList.get(i);
            System.out.printf("%s - contains: %b\n", pic, feedOnlyPicList.contains(pic));
        } //보여주기용으로 만들어서 없어도 됨.


        //Predicate 리턴타입 o (boolean), 파라미터 o (FeedPicVo)
        feedPicListAfter.stream().allMatch(feedPicVo -> feedOnlyPicList.contains(feedPicVo.getPic()));


        assertAll(
                  () -> feedPicListAfter.forEach(feedPicVo -> TestUtils.assertCurrentTimestamp(feedPicVo.getCreatedAt()))
                , () -> { //윗줄이랑 이거랑 같음.
                      for(FeedPicVo feedPicVo : feedPicListAfter) {
                          TestUtils.assertCurrentTimestamp(feedPicVo.getCreatedAt());
                      }
                }

                , () -> assertEquals(givenParam.getPics().size(), actualAffectedRows)
                , () -> assertEquals(0, feedPicListBefore.size())
                , () -> assertEquals(givenParam.getPics().size(), feedPicListAfter.size())//람다식 어려우면
                , () -> assertTrue(feedOnlyPicList.containsAll(Arrays.asList(pics)))//양쪽다 포함되어있냐
                , () -> assertTrue(Arrays.asList(pics).containsAll(feedOnlyPicList))//람다식 어려우면
                , () -> assertTrue(feedPicListAfter.stream().allMatch(feedPicVo -> picList.contains(feedPicVo.getPic())))

                , () -> assertTrue(feedPicListAfter.stream() //스트림 생성
                                                           .map(FeedPicVo::getPic) //똑같은 크기의 새로운 반환 Stream<String> ["a.jpg", "b.jpg", "c.jpg"]
                                                           .filter(pic -> picList.contains(pic)) //필터는 연산의 결과가 true인 것만 뽑아내서 새로운 스트림 반환 Stream<String> ["a.jpg", "b.jpg", "c.jpg"]
                                                           .limit(picList.size()) //스트림 크기를 제한, 이전 스트림의 크기가 10개인데 limit(2)를 하면 2개짜리 스트림이 반환된다.
                                                           .count() == picList.size())

                // FeedPicVo::getPic - 메소드 참조
                , () -> assertTrue(feedPicListAfter.stream().map(FeedPicVo::getPic).toList().containsAll(Arrays.asList(pics)))

                    //Function return type o (String), parameter o (FeedPicVo)
                , () -> assertTrue(feedPicListAfter.stream().map(item-> item.getPic()) //["a.jpg", "b.jpg", "c.jpg"]
                                            .toList() //스트림 > List (리스트 형식으로 바뀜)
                                            .containsAll(Arrays.asList(pics)))
        );

        //created_at 단언
    }
}