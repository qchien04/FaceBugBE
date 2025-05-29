package com.service;

import com.entity.Post;
import com.entity.postRanking.ActionOnPost;

import java.util.List;

public interface PostRankingService {
    void pushAction(ActionOnPost actionOnPost);
    void deleteActionOlderThan2Days();
    void deleteTrendPostOlderThan6Days();
    void calculatorPoint();
    List<Integer> randomPublicPostSuggest();
    List<Post> randomPublicPostVideoSuggest();
}
