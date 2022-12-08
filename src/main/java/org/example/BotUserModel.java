package org.example;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BotUserModel {
    private final String name;
    private final String discordID;
}
