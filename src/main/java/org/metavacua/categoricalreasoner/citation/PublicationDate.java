package org.metavacua.categoricalreasoner.citation;

import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.util.Objects;
import java.util.Optional;

/**
 * Publication date supporting various granularities.
 * Handles EDTF/ISO8601-2 date formats.
 * <p>
 * Supports:
 * <ul>
 *   <li>Year only: "1987"</li>
 *   <li>Year-month: "1987-03"</li>
 *   <li>Full date: "1987-03-15"</li>
 * </ul>
 *
 * @param year       The year (required)
 * @param month      Optional month (1-12)
 * @param day        Optional day (1-31)
 * @param edtfString Optional full EDTF string representation
 */
public record PublicationDate(
    int year,
    Optional<Integer> month,
    Optional<Integer> day,
    Optional<String> edtfString
) {

    /**
     * Compact constructor validating date components.
     *
     * @throws IllegalArgumentException if year is invalid
     * @throws IllegalArgumentException if month is invalid
     * @throws IllegalArgumentException if day is invalid for given month/year
     */
    public PublicationDate {
        if (year < 0 || year > 9999) {
            throw new IllegalArgumentException(
                "Year must be between 0 and 9999: " + year
            );
        }

        // Validate month if present
        month = month.map(m -> {
            if (m < 1 || m > 12) {
                throw new IllegalArgumentException("Month must be between 1 and 12: " + m);
            }
            return m;
        });

        // Validate day if present
        day = day.map(d -> {
            if (month.isPresent()) {
                int m = month.get();
                // Validate day against month
                YearMonth yearMonth = YearMonth.of(year, m);
                int maxDays = yearMonth.lengthOfMonth();
                if (d < 1 || d > maxDays) {
                    throw new IllegalArgumentException(
                        "Day must be between 1 and " + maxDays + " for " + year + "-" + m + ": " + d
                    );
                }
            } else if (d < 1 || d > 31) {
                throw new IllegalArgumentException("Day must be between 1 and 31: " + d);
            }
            return d;
        });

        // Normalize EDTF string
        edtfString = Optional.ofNullable(edtfString)
            .map(String::trim)
            .filter(s -> !s.isBlank());
    }

    /**
     * Creates a publication date from year only.
     *
     * @param year The year
     * @return A new PublicationDate instance
     */
    public static PublicationDate of(int year) {
        return new PublicationDate(year, Optional.empty(), Optional.empty(), Optional.empty());
    }

    /**
     * Creates a publication date from year and month.
     *
     * @param year  The year
     * @param month The month (1-12)
     * @return A new PublicationDate instance
     */
    public static PublicationDate of(int year, int month) {
        return new PublicationDate(year, Optional.of(month), Optional.empty(), Optional.empty());
    }

    /**
     * Creates a publication date from year, month, and day.
     *
     * @param year  The year
     * @param month The month (1-12)
     * @param day   The day (1-31)
     * @return A new PublicationDate instance
     */
    public static PublicationDate of(int year, int month, int day) {
        return new PublicationDate(year, Optional.of(month), Optional.of(day), Optional.empty());
    }

    /**
     * Creates a publication date from an ISO8601 string.
     *
     * @param isoString The ISO8601 date string
     * @return A new PublicationDate instance
     */
    public static PublicationDate parse(String isoString) {
        if (isoString == null || isoString.isBlank()) {
            throw new IllegalArgumentException("Date string cannot be null or blank");
        }

        String trimmed = isoString.trim();

        // Try full date first
        try {
            LocalDate date = LocalDate.parse(trimmed);
            return new PublicationDate(
                date.getYear(),
                Optional.of(date.getMonthValue()),
                Optional.of(date.getDayOfMonth()),
                Optional.of(trimmed)
            );
        } catch (Exception e) {
            // Fall through to partial date
        }

        // Try year-month
        if (trimmed.matches("^\\d{4}-\\d{2}$")) {
            String[] parts = trimmed.split("-");
            return new PublicationDate(
                Integer.parseInt(parts[0]),
                Optional.of(Integer.parseInt(parts[1])),
                Optional.empty(),
                Optional.of(trimmed)
            );
        }

        // Try year only
        if (trimmed.matches("^\\d{4}$")) {
            return new PublicationDate(
                Integer.parseInt(trimmed),
                Optional.empty(),
                Optional.empty(),
                Optional.of(trimmed)
            );
        }

        throw new IllegalArgumentException(
            "Invalid ISO8601 date format: " + isoString
        );
    }

    /**
     * Returns the ISO8601 string representation.
     *
     * @return ISO8601 string
     */
    public String toIsoString() {
        if (month.isPresent() && day.isPresent()) {
            return String.format("%04d-%02d-%02d", year, month.get(), day.get());
        } else if (month.isPresent()) {
            return String.format("%04d-%02d", year, month.get());
        } else {
            return String.format("%04d", year);
        }
    }

    /**
     * Returns EDTF string if available, otherwise ISO8601.
     *
     * @return Date string
     */
    public String toEdtfString() {
        return edtfString.orElse(toIsoString());
    }

    /**
     * Checks if this date is before another date.
     *
     * @param other The other date
     * @return true if this date is before the other
     */
    public boolean isBefore(PublicationDate other) {
        if (this.year != other.year) {
            return this.year < other.year;
        }
        if (this.month.isPresent() && other.month.isPresent()) {
            if (!this.month.get().equals(other.month.get())) {
                return this.month.get() < other.month.get();
            }
            if (this.day.isPresent() && other.day.isPresent()) {
                return this.day.get() < other.day.get();
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return toIsoString();
    }
}
