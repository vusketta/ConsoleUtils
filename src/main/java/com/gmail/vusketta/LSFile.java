package com.gmail.vusketta;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;

public class LSFile {
    public static String getRights(Path file, boolean isHumanReadable) {
        return (Files.isReadable(file) ? isHumanReadable ? "r" : "1" : isHumanReadable ? "-" : "0") +
                (Files.isWritable(file) ? isHumanReadable ? "w" : "1" : isHumanReadable ? "-" : "0") +
                (Files.isReadable(file) ? isHumanReadable ? "x" : "1" : isHumanReadable ? "-" : "0");
    }

    public static String getLastModifiedTime(Path file) throws IOException {
        return new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(Files.getLastModifiedTime(file).toMillis());
    }

    public static String getMemory(Path file, boolean isHumanReadable) throws IOException {
        final long bytes = Files.size(file);
        final String memory;
        if (bytes >= 1073741824) {
            memory = Math.ceil((double) bytes / 1073741824.0) + " gigabyte(s)";
        } else if (bytes >= 1048576) {
            memory = Math.ceil((double) bytes / 1048576.0) + " megabyte(s)";
        } else if (bytes >= 1024) {
            memory = Math.ceil((double) bytes / 1024.0) + " kilobyte(s)";
        } else memory = bytes + " byte(s)";
        return isHumanReadable ? memory : bytes + " byte(s)";
    }
}
