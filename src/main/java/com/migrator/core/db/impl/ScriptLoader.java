package com.migrator.core.db.impl;

import com.migrator.model.MigrationScript;
import com.migrator.model.MigrationStatus;
import com.migrator.util.ChecksumUtil;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

/**
 * Responsible for loading SQL migration scripts from the filesystem.
 *
 * This class:
 * - Reads all .sql files inside the migrations directory
 * - Parses version and description from the filename
 * - Reads file content and computes checksum
 * - Creates MigrationScript objects
 * - Returns them sorted by version (ascending)
 *
 * Expected filename format:
 *   YYYYMMDDHHMM__description.sql
 *
 * Example:
 *   202501121210__create_user_table.sql
 */
public class ScriptLoader {

    /** Path to the migrations directory */
    private final Path migrationDir;

    /**
     * @param directory Path to the folder containing .sql migration files
     */
    public ScriptLoader(String directory) {
        this.migrationDir = Paths.get(directory);
    }

    /**
     * Loads all migration scripts from the directory.
     *
     * @return List of MigrationScript objects sorted by version
     * @throws IOException when file access fails
     */
    public List<MigrationScript> loadScripts() throws IOException {

        // List all files in directory, filter only .sql files
        return Files.list(migrationDir)
                .filter(path -> path.toString().endsWith(".sql"))
                .map(this::parseFile)                   // Convert each file to a MigrationScript
                .sorted(Comparator.comparing(MigrationScript::getVersion)) // Order by version
                .toList();
    }

    /**
     * Reads a migration file and converts it into a MigrationScript object.
     * Filename format must be: version__description.sql
     *
     * @param path File path to parse
     * @return Parsed MigrationScript instance
     */
    private MigrationScript parseFile(Path path) {
        try {
            String fileName = path.getFileName().toString();

            // Split into version + description
            String[] parts = fileName.split("__");
            if (parts.length != 2 || !fileName.endsWith(".sql")) {
                throw new IllegalArgumentException(
                        "Invalid migration filename: " + fileName +
                                " (expected format: YYYYMMDDHHMM__description.sql)");
            }

            String version = parts[0];
            String description = parts[1].replace(".sql", "");

            // Read file content
            String content = Files.readString(path);

            // Compute checksum
            String checksum = ChecksumUtil.generateChecksum(content);

            return new MigrationScript(version, description, content, checksum, MigrationStatus.PENDING);

        } catch (Exception e) {
            throw new RuntimeException("Failed to parse migration file: " + path, e);
        }
    }
}
