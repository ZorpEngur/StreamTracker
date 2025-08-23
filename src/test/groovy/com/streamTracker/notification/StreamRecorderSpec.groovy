package com.streamTracker.notification

import com.streamTracker.ApplicationProperties
import com.streamTracker.recorder.FileController
import com.streamTracker.recorder.StreamRecorder
import spock.lang.Specification

class StreamRecorderSpec extends Specification {
    
    void "Test deleting old files"() {
        given:
        ApplicationProperties properties = Mock() {
            getSpaceThreshold() >> 1
        }
        File oldFile = Mock(File) {
            getName() >> "OLD_FILE-20240318-120000.mkv"
        }
        File newFile = Mock(File) {
            getName() >> "NEW_FILE-20250318-120000.mkv"
        }
        File dir = Mock() {
            listFiles() >> [newFile, oldFile]
        }

        def recorder = new StreamRecorder(Spy(new FileController(properties)) { it.getVodDirectory() >> dir}, properties)

        when:
        recorder.makeSpace()

        then:
        2 * dir.getUsableSpace() >> 0L >> 1100000000
        0 * newFile.delete()
        1 * oldFile.delete()
    }
}
