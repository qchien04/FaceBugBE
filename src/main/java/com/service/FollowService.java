package com.service;

import com.constant.FollowState;

public interface FollowService {
    void followPage(Integer pageId);
    void unFollowPage(Integer pageId);
    FollowState checkFollow(Integer pageId);
}
