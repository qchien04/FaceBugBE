package com.config;

import lombok.Getter;
import lombok.Setter;

import java.security.Principal;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class CustomPrincipalChat implements Principal {
    private final String name;
    private final Integer userId;
    private final Set<Integer> subscribedChannels = new HashSet<>();

    public CustomPrincipalChat(Integer userId) {
        this.userId = userId;
        this.name = String.valueOf(userId);
    }

    @Override
    public String getName() {
        return name;
    }

    public void addChannel(Integer channel) {
        subscribedChannels.add(channel);
    }

    public void removeChannel(Integer channel) {
        subscribedChannels.remove(channel);
    }

    public void clearChannel() {
        subscribedChannels.clear();
    }
}
