package com.green.greengram.feed.like;

import com.green.greengram.feed.like.model.FeedLikeReq;
import com.green.greengram.feed.like.model.FeedLikeVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface FeedLikeTestMapper {
    @Select("SELECT * FROM feed_like WHERE feed_id = #{feedId} AND user_id = #{userId}")
    FeedLikeVo selFeedLikeByFeedIdAndUserId(FeedLikeReq p); //이 때는 튜플 없으면 null이 넘어옴

    @Select("SELECT * FROM feed_like")
    List<FeedLikeVo> selFeedLikeAll(); //이 때는 튜플 없으면 size 0짜리 List 넘어옴

}
