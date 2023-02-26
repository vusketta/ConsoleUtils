package com.gmail.vusketta;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.*;
import java.nio.file.attribute.FileTime;
import java.util.*;

import com.gmail.vusketta.LSUtils.LSFile;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LSTest {
    private static final String directory = "testDirectory";
    private static final String output = "test.out";

    @BeforeAll
    static void createDirectory() {
        try {
            Files.createDirectory(Path.of(directory));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @AfterAll
    static void deleteDirectory() {
        try {
            Files.delete(Path.of(directory));
            Files.delete(Path.of(output));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void simpleDirectoryTest() {
        final List<LSFile> files = createFiles().stream().sorted().toList();
        final String[] commands = ("-o " + output + " " + directory).split(" ");
        LS.main(commands);
        final List<String> fileNames = files.stream().map(LSFile::name).toList();
        deleteFiles(fileNames);
        final String expectedContent = String.join("\n", fileNames) + '\n';
        assertFileContent(output, expectedContent);
    }

    @Test
    void longFormatTest() {
        final List<LSFile> files = createFiles().stream().sorted().toList();
        final String[] commands = ("-l -o " + output + " " + directory).split(" ");
        LS.main(commands);
        final List<String> fileNames = files.stream().map(LSFile::name).toList();
        deleteFiles(fileNames);
        final List<String> expectedLines = new ArrayList<>();
        for (final LSFile file: files) {
            expectedLines.add(file.name() + " " +
                    LSUtils.getRights(file.rights(), false) + " " +
                    LSUtils.getTime(file.lastModifiedTime()) + " " +
                    LSUtils.getMemory(file.bytes() + 2, false)
            );
        }
        final String expectedContent = String.join("\n", expectedLines) + '\n';
        assertFileContent(output, expectedContent);
    }

    @Test
    void humanReadableTest() {
        final List<LSFile> files = createFiles().stream().sorted().toList();
        final String[] commands = ("-l -h -o " + output + " " + directory).split(" ");
        LS.main(commands);
        final List<String> fileNames = files.stream().map(LSFile::name).toList();
        deleteFiles(fileNames);
        final List<String> expectedLines = new ArrayList<>();
        for (final LSFile file: files) {
            expectedLines.add(file.name() + " " +
                    LSUtils.getRights(file.rights(), true) + " " +
                    LSUtils.getTime(file.lastModifiedTime()) + " " +
                    LSUtils.getMemory(file.bytes() + 2, true)
            );
        }
        final String expectedContent = String.join("\n", expectedLines) + '\n';
        assertFileContent(output, expectedContent);
    }

    @Test
    void reverseTest() {
        final List<LSFile> files = createFiles().stream().sorted(Comparator.reverseOrder()).toList();
        final String[] commands = ("-l -h -r -o " + output + " " + directory).split(" ");
        LS.main(commands);
        final List<String> fileNames = files.stream().map(LSFile::name).toList();
        deleteFiles(fileNames);
        final List<String> expectedLines = new ArrayList<>();
        for (final LSFile file: files) {
            expectedLines.add(file.name() + " " +
                    LSUtils.getRights(file.rights(), true) + " " +
                    LSUtils.getTime(file.lastModifiedTime()) + " " +
                    LSUtils.getMemory(file.bytes() + 2, true)
            );
        }
        final String expectedContent = String.join("\n", expectedLines) + '\n';
        assertFileContent(output, expectedContent);
    }

    @Test
    void consoleTest() {
        try (PrintStream console = new PrintStream("console")) {
            final List<LSFile> files = createFiles().stream().sorted().toList();
            final String[] commands = ("-l -h " + directory).split(" ");
            System.setOut(console);
            LS.main(commands);
            deleteFiles(files.stream().map(LSFile::name).toList());
            System.out.flush();
            System.setOut(System.out);
            final List<String> expectedLines = new ArrayList<>();
            for (final LSFile file: files) {
                expectedLines.add(file.name() + " " +
                        LSUtils.getRights(file.rights(), true) + " " +
                        LSUtils.getTime(file.lastModifiedTime()) + " " +
                        LSUtils.getMemory(file.bytes() + 2, true)
                );
            }
            final String expectedContent = String.join("\n", expectedLines) + '\n';
            assertFileContent("console", expectedContent);
            Files.deleteIfExists(Path.of("console"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void simpleFileTest() {
        final String file = "file";
        final String[] commands = ("-o " + output + " " + file).split(" ");
        try {
            final Path path = Path.of("file");
            Files.createFile(path);
            LS.main(commands);
            Files.deleteIfExists(path);
            assertFileContent(output, file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void assertFileContent(final String fileName, final String expectedContent) {
        try {
            final String content = new String(Files.readAllBytes(Paths.get(fileName)));
            assertEquals(expectedContent, content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<LSFile> createFiles() {
        final List<LSFile> files = new ArrayList<>();
        try {
            final int fileNumbers = new Random().nextInt(5, 25);
            for (int i = 0; i < fileNumbers; i++) {
                final String fileName = RandomStringUtils.randomAlphabetic(5, 11);
                files.add(generateFile(fileName, FileTime.fromMillis(10000)));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return files;
    }

    private LSFile generateFile(final String name, final FileTime time) throws IOException {
        final Path file = Path.of(directory, name);
        final int bytes = new Random().nextInt(0, 30000);
        Files.write(file, List.of(RandomStringUtils.randomAlphabetic(bytes)));
        Files.setLastModifiedTime(file, time);
        return new LSFile(name, List.of(true, true, true), time, bytes);
    }

    private void deleteFiles(final List<String> files) {
        try {
            for (final String file : files) {
                Files.deleteIfExists(Path.of(directory, file));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}