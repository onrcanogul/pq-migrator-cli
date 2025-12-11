package com.migrator.util;

import java.security.MessageDigest;

/**
 * Utility class responsible for generating a cryptographic checksum
 * for SQL migration files.
 *
 * --------------------------
 * Checksums are used to:
 * - Detect whether a migration file was modified after being applied.
 * - Prevent accidental or malicious tampering.
 * - Ensure database consistency across environments (dev/stage/prod).
 *
 * SHA-256 is used because:
 * - It is collision-resistant.
 * - Widely accepted and secure.
 * - Easy to generate without extra dependencies.
 */
public class ChecksumUtil {

    /**
     * Generates a SHA-256 checksum for the given input string.
     *
     * @param input SQL content of the migration file
     * @return Hexadecimal SHA-256 hash
     */
    public static String generateChecksum(String input) {
        try {
            // Create SHA-256 hash generator
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            // Compute hash
            byte[] hashBytes = md.digest(input.getBytes());

            // Convert hash bytes to hex string
            StringBuilder hex = new StringBuilder();
            for (byte b : hashBytes) {
                hex.append(String.format("%02x", b));
            }

            return hex.toString();

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate checksum", e);
        }
    }
}
