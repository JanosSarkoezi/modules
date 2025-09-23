package com.mycompany.module1;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

/**
 * A Maven plugin goal that lists all Java source files in the project.
 */
@Mojo(name = "list", defaultPhase = LifecyclePhase.INSTALL)
public class ListJavaFilesMojo extends AbstractMojo {

    /**
     * The Maven project itself, injected by Maven.
     */
    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    private MavenProject project;

    /**
     * Executes the Mojo.
     */
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("Starting to list Java files...");

        // Get the main source directory and test source directory
        String mainSrcDir = project.getBuild().getSourceDirectory();
        String testSrcDir = project.getBuild().getTestSourceDirectory();

        getLog().info("Scanning main source directory: " + mainSrcDir);
        listFilesInDirectory(new File(mainSrcDir));

        getLog().info("Scanning test source directory: " + testSrcDir);
        listFilesInDirectory(new File(testSrcDir));
    }

    /**
     * Recursively lists all Java files in a given directory.
     *
     * @param directory The directory to scan.
     */
    private void listFilesInDirectory(File directory) {
        if (!directory.exists()) {
            getLog().warn("Directory does not exist: " + directory.getAbsolutePath());
            return;
        }

        try (Stream<Path> paths = Files.walk(directory.toPath())) {
            paths.filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".java"))
                    .forEach(path -> getLog().info("Found Java file: " + path.toAbsolutePath().toString()));
        } catch (IOException e) {
            getLog().error("Failed to list files in directory: " + directory.getAbsolutePath(), e);
        }
    }
}