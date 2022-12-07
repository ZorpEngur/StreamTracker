package org.example;


public class Main {
    public static void main(String[] args) {
        TwitchLiveBot twitchLiveBot = new TwitchLiveBot();
        twitchLiveBot.registerFeatures();

        TwitchExpandBot twitchExpandBot = new TwitchExpandBot(twitchLiveBot);
        twitchExpandBot.registerFeatures();

        System.out.println("---------- Bot Running ----------");

    }
}