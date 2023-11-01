package com.zorpengur.notification

import spock.lang.Specification

class BotTemplateSpec extends Specification {


    protected File file

    protected String fileName = "target/test.txt"

    void createFile() {
        file = new File(fileName)
        file.createNewFile()
    }

    void deleteFile() {
        new File(fileName).delete()
    }
}
