package org.example

import spock.lang.Specification

class BotTemplateSpec extends Specification {


    protected File file

    protected String fileName = "target/test.txt"

    def createFile() {
        file = new File(fileName)
        file.createNewFile()
    }

    def deleteFile() {
        new File(fileName).delete()
    }
}
