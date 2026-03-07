// SPDX-FileCopyrightText: 2025-2026 Ian Douglas Lawrence Norman McLean
// SPDX-License-Identifier: AGPL-3.0-or-later

/**
 * Minimal "Hello World" program serving as a test payload for infrastructure validation.
 *
 * <p>This is NOT a formal methods project. It exists solely to demonstrate that:
 *
 * <ul>
 *   <li>REUSE.software licensing works correctly</li>
 *   <li>Pre-commit hooks execute successfully</li>
 *   <li>GitHub Actions CI builds and runs</li>
 *   <li>Maven compiles with Java 25</li>
 * </ul>
 */
public final class HelloWorld {
    private HelloWorld() {
        // Prevent instantiation
    }

    /**
     * Main entry point that prints "Hello World" to standard output.
     *
     * @param args command-line arguments (ignored)
     */
    public static void main(final String[] args) {
        System.out.println("Hello World");
    }
}
