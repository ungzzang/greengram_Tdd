package com.green.greengram.feed.model;

import com.green.greengram.feed.comment.model.FeedCommentDto;
import lombok.*;

import java.util.List;

//@NoArgsConstructor, @AllArgsConstructo: @builder랑 세트로 생각하자.(select 순서상관없이 가져올수있음)
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class FeedWithPicCommentDto {
    private long feedId;
    private String contents;
    private String location;
    private String createdAt;
    private long writerUserId;
    private String writerNm;
    private String writerPic;
    private int isLike;
    private List<String> pics;
    private List<FeedCommentDto> commentDtoList;
}
