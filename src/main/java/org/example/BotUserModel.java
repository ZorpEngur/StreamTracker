package org.example;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class BotUserModel {
    private final String name;
    private final String discordID;
    @Setter
    private LocalDateTime lastPing = LocalDateTime.MIN;
}
