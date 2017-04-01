package cpen391.team6.bored.Utility;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by neema on 2017-03-31.
 */
public class DataUtil {


    public static int UPPER_CASE = 1;
    public static int LOWER_CASE = 0;

    /**
     * Static helper method that converts an int representation of a month
     * to a three character (ALL-CAPS) string representation of a month
     *
     * @param month
     * @return
     */
    public static String convertMonthToString(int month, int textCase) {
        switch (month) {

            case 0:
                return textCase == 1 ? "JAN" : "Jan";
            case 1:
                return textCase == 1 ? "FEB" : "Feb";
            case 2:
                return textCase == 1 ? "MAR" : "Mar";
            case 3:
                return textCase == 1 ? "APR" : "Apr";
            case 4:
                return textCase == 1 ? "MAY" : "May";
            case 5:
                return textCase == 1 ? "JUN" : "Jun";
            case 6:
                return textCase == 1 ? "JUL" : "Jul";
            case 7:
                return textCase == 1 ? "AUG" : "Aug";
            case 8:
                return textCase == 1 ? "SEP" : "Sep";
            case 9:
                return textCase == 1 ? "OCT" : "Oct";
            case 10:
                return textCase == 1 ? "NOV" : "Nov";
            case 11:
                return textCase == 1 ? "DEC" : "Dec";
        }
        return "";
    }

    /**
     * Static helper method for converting an int representation of a day to a
     * three character (ALL-CAPS) string representation
     */

    public static String convertDayToString(int day) {
        System.out.println(day);
        switch (day) {
            case 0:
                return "Sat";
            case 1:
                return "Sun";
            case 2:
                return "Mon";
            case 3:
                return "Tue";
            case 4:
                return "Wed";
            case 5:
                return "Thu";
            case 6:
                return "Fri";
        }
        return "";
    }

    /**
     * Function that converts the date fields to a
     * formatted string containing information relating to when
     * the note was created
     *
     *
     * @return String s
     */

    public static String getFormattedDate(Date date) {

        boolean time_of_day = false;

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        if (hour >= 12)
            time_of_day = true;

        String s = String.format(Locale.US, "%02d:%02d %s",
                (hour == 12 || hour == 0) ? 12 : hour % 12, minute,
                time_of_day ? "PM" : "AM");

        StringBuilder sb = new StringBuilder(s);

        if (s.charAt(0) == '0')
            sb.deleteCharAt(0);

        s = sb.toString();


        String formattedDate = DataUtil.convertMonthToString(month, DataUtil.LOWER_CASE) + " " + day;

        formattedDate = formattedDate + "," + " " + s;

        return formattedDate;
    }
}
