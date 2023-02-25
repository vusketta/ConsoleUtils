package com.gmail.vusketta;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

public class LS {
    private static Writer out;
    public static void main(String[] args) {
        LSArguments arguments = new LSArguments(args);

        final Path directory = Path.of(arguments.getDirectoryOrFile());
        try (Stream<Path> pathStream = Files.walk(directory, 1)) {
            OutputStream outputStream =
                    arguments.getOutputName() == null ? System.out : new FileOutputStream(arguments.getOutputName());
            out = new BufferedWriter(new OutputStreamWriter(outputStream));
            if (Files.isDirectory(directory)) {
                final List<String> files = new ArrayList<>(pathStream.map(Path::toString)
                        .sorted(arguments.isReverse() ? Comparator.reverseOrder() : Comparator.naturalOrder()).toList());
                files.remove(directory.toString());
                for (String file : files) {
                    writeFile(arguments, Path.of(file));
                    out.write('\n');
                }
            } else {
                writeFile(arguments, directory);
            }
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void writeFile(final LSArguments arguments, final Path file) throws IOException {
        out.write(file.getFileName().toString());
        if (arguments.isLong()) {
            out.write(" " + LSUtils.getFileRights(file, arguments.isHumanReadable()));
            out.write(" " + LSUtils.getFileLastModifiedTime(file));
            out.write(" " + LSUtils.getFileMemory(file, arguments.isHumanReadable()));
        }
    }
}