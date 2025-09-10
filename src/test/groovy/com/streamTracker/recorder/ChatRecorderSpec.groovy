package com.streamTracker.recorder

import com.streamTracker.ApplicationProperties
import spock.lang.Specification

import java.nio.file.Files
import java.nio.file.Path
import java.time.Instant

class ChatRecorderSpec extends Specification {

    private static final String PATH = "target/tmp/"

    private static ApplicationProperties PROPERTIES;

    def setupSpec() {
        new File(PATH).mkdir()
        PROPERTIES = Mock() {
            getChatMessageQueue() >> 10
        }
    }

    void "One message"() {
        given:
        def channel = "oneMessageTest"
        def fileController = Spy(new FileController(Mock(ApplicationProperties))) {
            it.chatFilePath(channel) >> PATH + channel + ".txt"
        }
        def chatRecorder = new ChatRecorder(fileController, PROPERTIES)

        when:
        chatRecorder.recordChat(channel)
        chatRecorder.message(channel, Instant.parse("2025-09-18T20:00:00.00Z"), "User", "Hello")
        chatRecorder.finishChatRecording(channel)

        then:
        def liens = Files.lines(Path.of(PATH + channel + ".txt")).toList()
        liens.size() == 1
        liens.iterator().next() == "20:00 User: Hello"
    }

    void "Multiple messages"() {
        given:
        def channel = "multipleMessagesTest"
        def fileController = Spy(new FileController(Mock(ApplicationProperties))) {
            it.chatFilePath(channel) >> PATH + channel + ".txt"
        }
        def chatRecorder = new ChatRecorder(fileController, PROPERTIES)

        when:
        chatRecorder.recordChat(channel)
        chatRecorder.message(channel, Instant.parse("2025-09-18T20:00:00.00Z"), "Streamer", "Second")
        chatRecorder.message(channel, Instant.parse("2025-09-18T21:00:00.00Z"), "Bot", "Third")
        chatRecorder.message(channel, Instant.parse("2025-09-18T19:00:00.00Z"), "User", "First")
        chatRecorder.finishChatRecording(channel)

        then:
        def liens = Files.lines(Path.of(PATH + channel + ".txt")).toList()
        liens.size() == 3
        liens[0] == "19:00 User: First"
        liens[1] == "20:00 Streamer: Second"
        liens[2] == "21:00 Bot: Third"
    }

    void "Lots of messages"() {
        given:
        def channel = "lotsOfMessagesTest"
        def fileController = Spy(new FileController(Mock(ApplicationProperties))) {
            it.chatFilePath(channel) >> PATH + channel + ".txt"
        }
        def chatRecorder = new ChatRecorder(fileController, PROPERTIES)

        when:
        chatRecorder.recordChat(channel)
        for (int i = 0; i < 10; i++) {
            chatRecorder.message(channel, Instant.parse("2025-09-18T20:00:00.00Z"), "Spammer1", "Spam")
        }
        for (int i = 0; i < 10; i++) {
            chatRecorder.message(channel, Instant.parse("2025-09-18T21:00:00.00Z"), "Spammer2", "Spam more")
        }
        chatRecorder.message(channel, Instant.parse("2025-09-18T22:00:00.00Z"), "Last", "Last message")
        chatRecorder.finishChatRecording(channel)

        then:
        def liens = Files.lines(Path.of(PATH + channel + ".txt")).toList()
        liens.size() == 21
        liens[0] == "20:00 Spammer1: Spam"
        liens[9] == "20:00 Spammer1: Spam"
        liens[10] == "21:00 Spammer2: Spam more"
        liens[19] == "21:00 Spammer2: Spam more"
        liens[20] == "22:00 Last: Last message"
    }

    void "Multiple streams"() {
        given:
        def channel1 = "stream1"
        def channel2 = "stream2"
        def fileController = Spy(new FileController(Mock(ApplicationProperties))) {
            it.chatFilePath(channel1) >> PATH + channel1 + ".txt"
            it.chatFilePath(channel2) >> PATH + channel2 + ".txt"
        }
        def chatRecorder = new ChatRecorder(fileController, PROPERTIES)

        when:
        chatRecorder.recordChat(channel1)
        chatRecorder.message(channel1, Instant.parse("2025-09-18T19:00:00.00Z"), "User", "First")
        chatRecorder.message(channel2, Instant.parse("2025-09-18T19:00:00.00Z"), "User", "First")
        chatRecorder.recordChat(channel2)
        chatRecorder.message(channel1, Instant.parse("2025-09-18T20:00:00.00Z"), "Streamer", "Second")
        chatRecorder.message(channel2, Instant.parse("2025-09-18T20:00:00.00Z"), "Streamer", "Second")
        chatRecorder.finishChatRecording(channel2)
        chatRecorder.message(channel1, Instant.parse("2025-09-18T21:00:00.00Z"), "Bot", "Third")
        chatRecorder.message(channel2, Instant.parse("2025-09-18T21:00:00.00Z"), "Bot", "Third")
        chatRecorder.finishChatRecording(channel1)

        then:
        def liens1 = Files.lines(Path.of(PATH + channel1 + ".txt")).toList()
        liens1.size() == 3
        liens1[0] == "19:00 User: First"
        liens1[1] == "20:00 Streamer: Second"
        liens1[2] == "21:00 Bot: Third"
        def liens2 = Files.lines(Path.of(PATH + channel2 + ".txt")).toList()
        liens2.size() == 1
        liens2[0] == "20:00 Streamer: Second"
    }

    void cleanupSpec() {
        new File(PATH).deleteDir()
    }
}
