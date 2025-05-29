package com.scheduled;

import com.service.PostRankingService;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@AllArgsConstructor
public class UpdatePageRanking {
    private final PostRankingService postRankingService;


    @Transactional
    @Scheduled(fixedRate = 24*60*60*1000)
    public void updateTrendPost() {
        postRankingService.calculatorPoint();
    }

    @Transactional
    @Scheduled(fixedRate = 2*24*60*60*1000)
    public void deleteActionOlderThan2Days() {
        postRankingService.deleteActionOlderThan2Days();
    }

    @Transactional
    @Scheduled(fixedRate = 6*24*60*60*1000)
    public void deleteTrendPost() {
        postRankingService.deleteTrendPostOlderThan6Days();
    }
}
