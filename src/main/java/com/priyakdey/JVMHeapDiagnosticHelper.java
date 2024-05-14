package com.priyakdey;

import com.sun.management.HotSpotDiagnosticMXBean;
import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * A helper class for taking heap dumps and histogram dumps for diagnostic purposes.
 * It uses the <a href="https://docs.oracle.com/en/java/javase/21/docs/specs/man/jcmd.html">jcmd</a>
 * tool to take histogram dumps and {@link HotSpotDiagnosticMXBean} for heap dumps.
 * The results are stored in the specified directory structure.
 *
 * @author Priyak Dey
 */
public class JVMHeapDiagnosticHelper {

    private static final String STATS_DIR = "stats";

    private static final String CAMEL_CASE_REGEX = "([a-z])([A-Z]+)";
    private static final String REPLACEMENT = "$1_$2";

    private final String[] cmd;

    private final String className;

    private final ProcessBuilder preProcessBuilder;
    private final ProcessBuilder postProcessBuilder;

    private final String preAllocHprofFilename;
    private final String postAllocHprofFilename;
    private final HotSpotDiagnosticMXBean mxBean;

    /**
     * Constructs a DiagnosticHelper for the specified class.
     * Sets up the command for taking histogram dumps, creates the necessary directory structure,
     * and initializes the {@link ProcessBuilder} and {@link HotSpotDiagnosticMXBean} instances.
     *
     * @param className The name of the class for which diagnostics are being taken.
     */
    public JVMHeapDiagnosticHelper(String className) {
        this.className = camelToSnakeCase(className);

        // setup cmd
        String pid = String.valueOf(ProcessHandle.current().pid());
        this.cmd = new String[] {"jcmd", pid, "GC.class_histogram"};

        // setup directory
        createDir(Path.of(STATS_DIR));
        createDir(Path.of(STATS_DIR, this.className));


        // setup process builder for jcmd
        preProcessBuilder = processBuilder("pre.hist");
        postProcessBuilder = processBuilder("post.hist");

        // setup hotspot bean for head dump
        Path path = Path.of(STATS_DIR, this.className, "pre.hprof");
        deleteFileIfExists(path);
        preAllocHprofFilename = path.toString();
        deleteFileIfExists(path);


        path = Path.of(STATS_DIR, this.className, "post.hprof");
        deleteFileIfExists(path);
        postAllocHprofFilename = path.toString();
        deleteFileIfExists(path);

        mxBean = ManagementFactory.getPlatformMXBean(HotSpotDiagnosticMXBean.class);
    }

    /**
     * Takes a histogram dump before allocation.
     */
    public void takePreAllocHistDump() {
        executeProcess(preProcessBuilder, "pre.hist");
    }

    /**
     * Takes a histogram dump after allocation.
     */
    public void takePostAllocHistDump() {
        executeProcess(postProcessBuilder, "post.hist");
    }

    /**
     * Takes a heap dump before allocation.
     */
    public void takePreAllocHeapDump() {
        try {
            mxBean.dumpHeap(preAllocHprofFilename, true);
        } catch (IOException e) {
            die("ERROR: Could not take pre-allocation heap dump: %s%n", e.getMessage());
        }
    }

    /**
     * Takes a heap dump after allocation.
     */
    public void takePostAllocHeapDump() {
        try {
            mxBean.dumpHeap(postAllocHprofFilename, true);
        } catch (IOException e) {
            die("ERROR: Could not take pre-allocation heap dump: %s%n", e.getMessage());
        }
    }



    /*-------------  private methods -------------*/

    /**
     * Creates a directory if it does not already exist.
     * If the directory cannot be created, the program will print an error message and exit.
     *
     * @param path The {@link Path} object representing the directory to be created.
     */
    private void createDir(Path path) {
        File file = path.toFile();
        if (!file.exists()) {
            try {
                Files.createDirectory(path);
            } catch (IOException e) {
                die("ERROR: Could not create directory %s: %s%n", file.getName(), e.getMessage());
            }
        }
    }

    /**
     * Deletes a file if exists.
     * If the file cannot be deleted, the program will print an error message and exit.
     *
     * @param path The {@link Path} object representing the file to be deleted.
     */
    private void deleteFileIfExists(Path path) {
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            die("ERROR: Could not delete file %s: %s%n", path.toFile().getName(), e.getMessage());
        }
    }

    /**
     * Executes a {@link ProcessBuilder} and handles errors.
     *
     * @param processBuilder The {@link ProcessBuilder} to execute.
     * @param phase          The phase of the allocation (pre or post).
     */
    private void executeProcess(ProcessBuilder processBuilder, String phase) {
        try {
            Process process = processBuilder.start();
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                die("ERROR: %s jcmd command failed with exit code: %d%n", phase, exitCode);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            die("ERROR: %s jcmd process wasinterrupted: %s%n", phase, e.getMessage());
        } catch (IOException e) {
            die("ERROR: Could not start %s jcmd process: %s%n", phase, e.getMessage());
        }
    }

    /**
     * Creates a {@link ProcessBuilder} for the given filename to take a histogram dump.
     *
     * @param filename The name of the file to store the histogram dump.
     * @return The {@link ProcessBuilder} configured to take the histogram dump.
     */
    private ProcessBuilder processBuilder(String filename) {
        Path path = Path.of(STATS_DIR, className, filename);
        File file = path.toFile();
        deleteFileIfExists(path);

        ProcessBuilder processBuilder = new ProcessBuilder(cmd);
        processBuilder.redirectErrorStream(true);
        processBuilder.redirectOutput(ProcessBuilder.Redirect.to(file));
        return processBuilder;
    }

    /**
     * Prints an error message and exits the program.
     *
     * @param msg  The error message format string.
     * @param args The arguments referenced by the format specifiers in the format string.
     */
    private void die(String msg, Object... args) {
        System.err.printf(msg, args);
        System.exit(1);
    }

    /**
     * Converts a camelCase string to snake_case.
     *
     * @param s The camelCase string.
     * @return The snake_case string.
     */
    private String camelToSnakeCase(String s) {
        return s.replaceAll(CAMEL_CASE_REGEX, REPLACEMENT).toLowerCase();
    }
}
