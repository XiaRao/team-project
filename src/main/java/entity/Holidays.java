package entity;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Stores holiday dates in memory, grouped by country.
 *
 * <p>Callers populate this collection with holiday dates obtained elsewhere.
 * This class does not fetch API data or load or save persistent data.
 * Each stored LocalDate includes its year.
 *
 * <p>This class does not track whether holiday data for a country or year
 * has been fully loaded. An unsuccessful lookup therefore does not establish
 * that the queried date is not a public holiday.
 *
 * <p>The current implementation accepts null country codes and null dates.
 */
public class Holidays {
    /** Maps each country key to its distinct stored holiday dates. */
    private final Map<Country, Set<LocalDate>> countryCodeToHolidays;

    /**
     * Creates an empty holiday collection.
     */
    public Holidays() {
        countryCodeToHolidays = new HashMap<>();
    }

    /**
     * Adds a holiday date for the specified country.
     *
     * <p>Creates the country's date set when necessary. Adding the same date
     * for the same country more than once does not create duplicate entries.
     *
     * @param countryCode the country key; may be null
     * @param holidayDate the holiday date, including its year; may be null
     */
    public void addHoliday(Country countryCode, LocalDate holidayDate) {
        if (!countryCodeToHolidays.containsKey(countryCode)) {
            countryCodeToHolidays.put(countryCode, new HashSet<LocalDate>());
        }

        countryCodeToHolidays.get(countryCode).add(holidayDate);
    }

    /**
     * Checks whether the specified date is stored for the specified country.
     *
     * <p>This method checks only the currently stored dates. A false result
     * may mean that the relevant holiday data has not been added.
     *
     * @param countryCode the country key to query; may be null
     * @param date the exact date to look up, including its year; may be null
     * @return true if the date is stored for the country; otherwise false
     */
    public boolean isHoliday(Country countryCode, LocalDate date) {
        if (!countryCodeToHolidays.containsKey(countryCode)) {
            return false;
        }

        return countryCodeToHolidays.get(countryCode).contains(date);
    }

    /**
     * Returns a mutable copy of the dates stored for the specified country.
     *
     * <p>Changing the returned set does not change this collection.
     * The returned set has no guaranteed iteration order.
     *
     * <p>An empty result does not distinguish between a country with no
     * holiday dates added and a country whose data has not been loaded.
     *
     * @param countryCode the country key to query; may be null
     * @return a new set containing the country's stored dates, or an empty
     *         set if the country has no entry
     */
    public Set<LocalDate> getHolidays(Country countryCode) {
        if (!countryCodeToHolidays.containsKey(countryCode)) {
            return new HashSet<LocalDate>();
        }

        return new HashSet<>(countryCodeToHolidays.get(countryCode));
    }
}