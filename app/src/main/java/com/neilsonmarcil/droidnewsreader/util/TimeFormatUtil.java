package com.neilsonmarcil.droidnewsreader.util;

/**
 * utility class to format time stamp.
 */
public class TimeFormatUtil {

    public static final long UNIX_TO_JVM = 1000L;

    private static final long MILLISECOND_IN_SECOND = 1000;
    private static final long SECOND_IN_MINUTE = 60;
    private static final long MINUTES_IN_HOUR = 60;
    private static final long HOUR_IN_DAY = 24;
    private static final long DAY_IN_MONTH = 30;
    private static final long MONTH_IN_YEAR = 12;
    private static final long YEAR_IN_DECADE = 10;
    private static final long DECADE_IN_CENTURY = 10;

    private static final long SECOND_DIVIDER = MILLISECOND_IN_SECOND;
    private static final long MINUTE_DIVIDER = (SECOND_DIVIDER * SECOND_IN_MINUTE);
    private static final long HOUR_DIVIDER = (MINUTE_DIVIDER * MINUTES_IN_HOUR);
    private static final long DAY_DIVIDER = (HOUR_DIVIDER * HOUR_IN_DAY);
    private static final long MONTH_DIVIDER = (DAY_DIVIDER * DAY_IN_MONTH);
    private static final long YEAR_DIVIDER = (MONTH_DIVIDER * MONTH_IN_YEAR);
    private static final long DECADE_DIVIDER = (YEAR_DIVIDER * YEAR_IN_DECADE);
    private static final long CENTURY_DIVIDER = (DECADE_DIVIDER * DECADE_IN_CENTURY);

    private static final TimeUnitDescriptor DEFAULT_TIME_DESCRIPTOR = new TimeUnitDescriptor() {

        private final String[] SINGLE_UNIT = { "century", "decade", "year", "month", "day", "hour", "minute", "second", "millisecond" };
        private final String[] PLURAL_UNIT = { "centuries", "decades", "years", "months", "days", "hours", "minutes", "seconds", "milliseconds" };

        @Override
        public String getSingle(int i) {
            return SINGLE_UNIT[i];
        }

        @Override
        public String getPural(int i) {
            return PLURAL_UNIT[i];
        }

        @Override
        public String formatResult(String s) {
            return String.format("%sago", s);
        }
    };

    /**
     * Format the elapsed time in the of X time ago. It will display only the higher unit found.
     * For example: 5 hours ago will be returned even if the real time is 5 hours 5 minutes.
     * @param time the time in milliseconds
     * @param desc the TimeUnitDescriptor to append the time unit
     * @return a formatted timespan like X time ago.
     */
    public static String formatTimespanAgoFirstUnit(long time, TimeUnitDescriptor desc) {

        long[] data = getTimeCalculated(time);
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < data.length; i++) {
            if (data[i] > 0) {
                return desc.formatResult(formatTimeWithUnit(b, data[i], i, desc).toString());
            }
        }
        return "";
    }

    /**
     * Format the elapsed time in the of X time ago. It will display only the higher unit found.
     * For example: 5 hours ago will be returned even if the real time is 5 hours 5 minutes.
     * @param time the time in milliseconds
     * @return a formatted timespan like X time ago.
     */
    public static String formatTimespanAgoFirstUnit(long time) {
        return formatTimespanAgoFirstUnit(time, DEFAULT_TIME_DESCRIPTOR);
    }

    /**
     * Format the time as X time ago. It will displays all unit that are greater than 0. For example,
     * it will return 5 hours 5 minutes 4 seconds.
     * @param time the elapsed time to display
     * @param desc the TimeUnitDescriptor to append the time unit
     * @return the formatted time like X time ago
     */
    public static String formatTimespanAgo(long time, TimeUnitDescriptor desc) {

        long[] data = getTimeCalculated(time);
        StringBuilder b = new StringBuilder();

        for (int i = 0; i < data.length; i++) {
            formatTimeWithUnit(b, data[i], i, desc);
        }

        return desc.formatResult(b.toString());
    }

    /**
     * Format the time as X time ago. It will displays all unit that are greater than 0. For example,
     * it will return 5 hours 5 minutes 4 seconds.
     * @param time the elapsed time to display
     * @return the formatted time like X time ago
     */
    public static String formatTimespanAgo(long time) {
        return formatTimespanAgo(time, DEFAULT_TIME_DESCRIPTOR);
    }

    /**
     * calculate the time units according to the private static variables
     * @param time the time to calculated
     * @return a array of long containing the value for each unit (century to millisecond)
     */
    private static long[] getTimeCalculated(long time) {
        long[] data = new long[9];
        data[0] = time / CENTURY_DIVIDER;
        data[1] = (time % CENTURY_DIVIDER) / DECADE_DIVIDER;
        data[2] = (time % DECADE_DIVIDER) / YEAR_DIVIDER;
        data[3] = (time % YEAR_DIVIDER) / MONTH_DIVIDER;
        data[4] = (time % MONTH_DIVIDER) / DAY_DIVIDER;
        data[5] = (time % DAY_DIVIDER) / HOUR_DIVIDER;
        data[6] = (time % HOUR_DIVIDER) / MINUTE_DIVIDER;
        data[7] = (time % MINUTE_DIVIDER) / (SECOND_DIVIDER);
        data[8] = (time % SECOND_DIVIDER);
        return data;
    }

    /**
     * Encapsulates the logic to append the single or plural forms of a unit.
     * @param b a StringBuilder to append the values
     * @param t the value of the unit time
     * @param unit the unit
     * @return the StringBuilder passed as an argument, returns the same reference.
     */
    private static StringBuilder formatTimeWithUnit(StringBuilder b, long t, int unit, TimeUnitDescriptor desc) {
        if (t == 1) {
            b.append(t).append(" ").append(desc.getSingle(unit));
        } else if (t > 1) {
            b.append(t).append(" ").append(desc.getPural(unit));
        }
        if (t > 0) {
            b.append(" ");
        }
        return b;
    }

    /**
     * This interface defines how to retreive language specific time unit and how to format the final
     * result. The names must stored in an array in that particular order Century, decades, year,
     * month, day, hour, second, millisecond. If this is not respected, unpredictable behavior can
     * be expected.
     */
    public interface TimeUnitDescriptor {
        /**
         * return the single form of a time unit.
         * @param i the index used to retrieve the name from a zero index based String array.
         * @return a String representing the single time unit
         */
        String getSingle(int i);

        /**
         * return the plural form of a time unit.
         * @param i the index used to retrieve the name from a zero index based String array.
         * @return a String representing the plural time unit
         */
        String getPural(int i);

        /**
         * return the result with a user specific format. Due to the different way to write time
         * in different language, this function can arrange the wording as it see fits.
         * @param s the X time string (ex.: 5 hours 5 minutes)
         * @return the formatted string (ex: 5 hours 5 minutes <b>ago</b>)
         */
        String formatResult(String s);
    }


}