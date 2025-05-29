package com.service;

import com.constant.AccountType;
import com.entity.Post;
import com.entity.SuggestPost;

import java.util.List;

public interface SuggestPostService {
    public void decreaseRenderTime(Integer profileSeen, List<Integer> postIds);
    public void pushSuggestPost(AccountType accountType,Integer authorId, Post post, Boolean isGroup);

    public List<SuggestPost> getSuggestPosts(Integer profileSeen);
}
