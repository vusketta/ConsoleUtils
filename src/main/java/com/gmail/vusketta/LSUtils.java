package com.gmail.vusketta;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class LSUtils {
    public static String getFileRights(final Path file, final boolean isHumanReadable) {
        final List<Boolean> rights = List.of(Files.isReadable(file), Files.isWritable(file), Files.isExecutable(file));
        return getRights(rights, isHumanReadable);
    }

    public static String getRights(final List<Boolean> rights, final boolean isHumanReadable) {
        return (rights.get(0) ? isHumanReadable ? "r" : "1" : isHumanReadable ? "-" : "0") +
                (rights.get(1) ? isHumanReadable ? "w" : "1" : isHumanReadable ? "-" : "0") +
                (rights.get(2) ? isHumanReadable ? "x" : "1" : isHumanReadable ? "-" : "0");
    }

    public static String getFileLastModifiedTime(final Path file) throws IOException {
        return getTime(Files.getLastModifiedTime(file));
    }

    public static String getTime(final FileTime time) {
        return time.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime().format(DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss"));
    }

    public static String getFileMemory(final Path file, final boolean isHumanReadable) throws IOException {
        final long bytes = Files.size(file);
        return getMemory(bytes, isHumanReadable);
    }

    public static String getMemory(final long bytes, final boolean isHumanReadable) {
        if (isHumanReadable) {
            if (bytes >= 1073741824) {
                return bytes / 1073741824 + " gigabyte(s)";
            } else if (bytes >= 1048576) {
                return bytes / 1048576 + " megabyte(s)";
            } else if (bytes >= 1024) {
                return bytes / 1024 + " kilobyte(s)";
            }
        }
        return bytes + " byte(s)";
    }

    record LSFile(String name, List<Boolean> rights, FileTime lastModifiedTime, int bytes) implements Comparable<LSFile> {
        @Override
        public int compareTo(final LSFile o) {
            return name.compareTo(o.name);
        }
    }
}
