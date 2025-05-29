package com.scheduled;

import com.repository.SuggestPostRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class SuggestPostCleaner {

    @Autowired
    private SuggestPostRepo suggestPostRepo;

    @Transactional
    @Scheduled(fixedRate = 60000)
    public void cleanExpiredSuggestions() {
        suggestPostRepo.deleteAllExpired();
    }
}
