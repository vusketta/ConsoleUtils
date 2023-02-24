package com.gmail.vusketta;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

public class LS {
    public static void main(String[] args) {
        LSArguments arguments = new LSArguments(args);

        final Path path = Path.of(arguments.getDirectoryOrFile());
        try (Stream<Path> pathStream = Files.walk(Paths.get(arguments.getDirectoryOrFile()), 1)
                .sorted(arguments.isReverse() ? Comparator.reverseOrder() : Comparator.naturalOrder())) {
            OutputStream outputStream =
                    arguments.getOutputName() == null ? System.out : new FileOutputStream(arguments.getOutputName());
            Writer out = new BufferedWriter(new OutputStreamWriter(outputStream));
            if (Files.isDirectory(path)) {
                final List<Path> files = new ArrayList<>(pathStream.toList());
                files.remove(path);
                for (Path file : files) {
                    writeFile(arguments, out, file);
                    out.write('\n');
                }
            } else {
                writeFile(arguments, out, path);
            }
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void writeFile(LSArguments arguments, Writer out, Path file) throws IOException {
        out.write(file.getFileName().toString());
        if (arguments.isLong()) {
            out.write(" " + LSFile.getRights(file, arguments.isHumanReadable()));
            out.write(" " + LSFile.getLastModifiedTime(file));
            out.write(" " + LSFile.getMemory(file, arguments.isHumanReadable()));
        }
    }
}