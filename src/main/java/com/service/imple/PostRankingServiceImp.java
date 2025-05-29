package com.service.imple;

import com.constant.AccountType;
import com.constant.MediaType;
import com.entity.Post;
import com.entity.postRanking.ActionOnPost;
import com.entity.postRanking.PostRanking;
import com.repository.ActionOnPostRepo;
import com.repository.PostRankingRepo;
import com.service.PostRankingService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;


@Service
@AllArgsConstructor
public class PostRankingServiceImp implements PostRankingService {
    private final PostRankingRepo postRankingRepo;
    private final ActionOnPostRepo actionOnPostRepo;


    @Override
    public void pushAction(ActionOnPost actionOnPost) {
        actionOnPostRepo.save(actionOnPost);
    }

    @Override
    public void deleteActionOlderThan2Days() {
        actionOnPostRepo.deleteOlderThan2Days();
    }

    @Override
    public void deleteTrendPostOlderThan6Days() {
        postRankingRepo.deleteOlderThan6Days();
    }

    @Override
    public void calculatorPoint() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime since = now.minusHours(24);

        List<Object[]> results = actionOnPostRepo.calculateTotalScoreForPosts(since);

        List<PostRanking> rankings = results.stream()
                .filter(result -> {
                    Long totalScore = (Long) result[1];
                    return totalScore != null && totalScore > 10;
                })
                .map(result -> {
                    Integer postId = (Integer) result[0];
                    Long totalScore = (Long) result[1];

                    return PostRanking.builder()
                            .core(totalScore)
                            .post(Post.builder().id(postId).build())
                            .timeCalculator(LocalDateTime.now())
                            .build();
                })
                .collect(Collectors.toList());

        postRankingRepo.saveAll(rankings);
    }

    @Override
    public List<Integer> randomPublicPostSuggest() {
        System.out.println("aclll");
        int total = postRankingRepo.countEligiblePosts();
        System.out.println(total+" total");
        if (total <= 0) return Collections.emptyList();
        int offset = new Random().nextInt(Math.max(1, total - 10));
        System.out.println(offset+" ne");
        List<Integer> result = postRankingRepo.findRecommendedPosts(offset);
        System.out.println(result.size()+ " size");
        return result;
    }

    @Override
    public List<Post> randomPublicPostVideoSuggest() {
        LocalDateTime threshold = LocalDateTime.now().minusDays(6);
        List<Post> posts=postRankingRepo.findRecommendedPostsVideo(AccountType.PAGE, MediaType.VIDEO,threshold);
        System.out.println(posts.size()+" size suggest");
        return posts;
    }
}
