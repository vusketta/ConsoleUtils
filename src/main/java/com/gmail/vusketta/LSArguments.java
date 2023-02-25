package com.gmail.vusketta;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

public class LSArguments {
    @Option(name = "-l", usage = """
            Switches the output to a long format, in which, in addition to the file name,
            execution/read/write rights are specified in the form of a bit mask XXX,
            the time of the last modification and the size in bytes.""")
    private boolean isLong;

    @Option(name = "-h", depends = "-l", usage = """
            Switches the output to a human-readable format
            (size in kilo-, mega- or gigabytes, execution rights in the form of rwx).""")
    private boolean isHumanReadable;

    @Option(name = "-r", usage = """
            Changes order of the output to reversed.
            """)
    private boolean isReverse;

    @Option(name = "-o", metaVar = "fileName", usage = """
            Specifies the name of the output file;
            if this flag is omitted, the result is write to the console.
            """)
    private String outputName;

    @Argument(metaVar = "directory_or_file", required = true)
    private String directory_or_file;

    public LSArguments(String[] args) {
        parseArguments(args);
    }

    public boolean isLong() {
        return isLong;
    }

    public boolean isHumanReadable() {
        return isHumanReadable;
    }

    public boolean isReverse() {
        return isReverse;
    }

    public String getOutputName() {
        return outputName;
    }

    public String getDirectoryOrFile() {
        return directory_or_file;
    }
   private void parseArguments(final String[] args) {
        final CmdLineParser parser = new CmdLineParser(this);
        try {
            parser.parseArgument(args);
        } catch (CmdLineException e) {
            e.printStackTrace();
        }
    }
}
