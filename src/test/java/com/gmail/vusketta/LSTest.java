package com.gmail.vusketta;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LSTest {
    @Test
    void simpleDirectoryTest() {
        String[] command = "-o file.out files".split(" ");
        LS.main(command);
        assertFileContent("""
                directory
                file1.txt
                file2
                file3.out
                file4.in
                """);
    }

    @Test
    void longFormatTest() {
        String[] command = "-l -o file.out files".split(" ");
        LS.main(command);
        assertFileContent("""
                directory 111 02/23/2023 15:48:33 0 byte(s)
                file1.txt 111 02/24/2023 01:10:14 1367 byte(s)
                file2 111 02/24/2023 01:11:24 138876 byte(s)
                file3.out 111 02/23/2023 15:45:07 0 byte(s)
                file4.in 111 02/24/2023 01:12:10 352 byte(s)
                """);
    }

    @Test
    void humanReadableTest() {
        String[] command = "-l -h -o file.out files".split(" ");
        LS.main(command);
        assertFileContent("""
                directory rwx 02/23/2023 15:48:33 0 byte(s)
                file1.txt rwx 02/24/2023 01:10:14 2.0 kilobyte(s)
                file2 rwx 02/24/2023 01:11:24 136.0 kilobyte(s)
                file3.out rwx 02/23/2023 15:45:07 0 byte(s)
                file4.in rwx 02/24/2023 01:12:10 352 byte(s)
                """);
    }

    @Test
    void reverseTest() {
        String[] command = "-l -h -r -o file.out files".split(" ");
        LS.main(command);
        assertFileContent("""
                file4.in rwx 02/24/2023 01:12:10 352 byte(s)
                file3.out rwx 02/23/2023 15:45:07 0 byte(s)
                file2 rwx 02/24/2023 01:11:24 136.0 kilobyte(s)
                file1.txt rwx 02/24/2023 01:10:14 2.0 kilobyte(s)
                directory rwx 02/23/2023 15:48:33 0 byte(s)
                """);
    }

    @Test
    void consoleTest() {
        try (PrintStream console = new PrintStream("console.txt")) {
            String[] command = "pom.xml".split(" ");
            System.setOut(console);
            LS.main(command);
            System.out.flush();
            System.setOut(System.out);
            assertFileContent("pom.xml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void simpleFileTest() {
        String[] command = "-o file.out pom.xml".split(" ");
        LS.main(command);
        assertFileContent("pom.xml");
    }

    private void assertFileContent(String expectedContent) {
        try {
            String content = new String(Files.readAllBytes(Paths.get("file.out")));
            assertEquals(expectedContent, content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}