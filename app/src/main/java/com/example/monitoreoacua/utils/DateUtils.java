package com.example.monitoreoacua.utils;

import android.text.format.DateFormat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

/**
 * Utility class for date-related operations and formatting
 */
public class DateUtils {
    // Common date formats
    private static final String ISO_8601_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    private static final String ISO_8601_FORMAT_WITHOUT_MS = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    private static final String DATE_FORMAT_DISPLAY = "dd MMM yyyy, HH:mm";
    private static final String DATE_FORMAT_SHORT = "dd/MM/yyyy";
    private static final String TIME_FORMAT = "HH:mm";
    
    // Time constants
    private static final long DAY_IN_MILLIS = TimeUnit.DAYS.toMillis(1);
    
    /**
     * Format a date string for display in the UI
     * @param dateString Date string in ISO 8601 format
     * @return Formatted date string for UI display
     */
    public static String formatDateForDisplay(String dateString) {
        if (dateString == null || dateString.isEmpty()) {
            return "";
        }
        
        try {
            Date date = parseISO8601Date(dateString);
            
            // If date is within 24 hours, show as relative time
            long now = System.currentTimeMillis();
            long dateTime = date.getTime();
            
            if (now - dateTime < DAY_IN_MILLIS) {
                return getRelativeTimeSpanString(dateTime);
            } else {
                SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_DISPLAY, Locale.getDefault());
                return sdf.format(date);
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return dateString; // Return original string if parsing fails
        }
    }
    
    /**
     * Parse ISO 8601 formatted date string to Date object
     * @param dateString Date string in ISO 8601 format
     * @return Date object
     * @throws ParseException if parsing fails
     */
    public static Date parseISO8601Date(String dateString) throws ParseException {
        SimpleDateFormat sdf;
        
        if (dateString.contains(".")) {
            sdf = new SimpleDateFormat(ISO_8601_FORMAT, Locale.getDefault());
        } else {
            sdf = new SimpleDateFormat(ISO_8601_FORMAT_WITHOUT_MS, Locale.getDefault());
        }
        
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf.parse(dateString);
    }
    
    /**
     * Convert Date to ISO 8601 formatted string
     * @param date Date to convert
     * @return ISO 8601 formatted date string
     */
    public static String formatToISO8601(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(ISO_8601_FORMAT, Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf.format(date);
    }
    
    /**
     * Format date to short date format (dd/MM/yyyy)
     * @param date Date to format
     * @return Formatted date string
     */
    public static String formatToShortDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_SHORT, Locale.getDefault());
        return sdf.format(date);
    }
    
    /**
     * Format date to time only (HH:mm)
     * @param date Date to format
     * @return Formatted time string
     */
    public static String formatToTimeOnly(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(TIME_FORMAT, Locale.getDefault());
        return sdf.format(date);
    }
    
    /**
     * Get a relative time span string (e.g., "2 hours ago")
     * This implementation replaces the Android DateUtils.getRelativeTimeSpanString method
     * @param timeMillis Time in milliseconds
     * @return Relative time span string
     */
    public static String getRelativeTimeSpanString(long timeMillis) {
        long now = System.currentTimeMillis();
        long diff = now - timeMillis;
        
        // Less than a minute
        if (diff < TimeUnit.MINUTES.toMillis(1)) {
            return "just now";
        }
        // Less than an hour
        else if (diff < TimeUnit.HOURS.toMillis(1)) {
            long minutes = TimeUnit.MILLISECONDS.toMinutes(diff);
            return minutes + (minutes == 1 ? " minute ago" : " minutes ago");
        }
        // Less than a day
        else if (diff < TimeUnit.DAYS.toMillis(1)) {
            long hours = TimeUnit.MILLISECONDS.toHours(diff);
            return hours + (hours == 1 ? " hour ago" : " hours ago");
        }
        // Less than a week
        else if (diff < TimeUnit.DAYS.toMillis(7)) {
            long days = TimeUnit.MILLISECONDS.toDays(diff);
            return days + (days == 1 ? " day ago" : " days ago");
        }
        // Default to formatted date
        else {
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_DISPLAY, Locale.getDefault());
            return sdf.format(new Date(timeMillis));
        }
    }
    
    /**
     * Check if two dates are on the same day
     * @param date1 First date
     * @param date2 Second date
     * @return true if dates are on the same day
     */
    public static boolean isSameDay(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
               cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }
    
    /**
     * Check if a date is today
     * @param date Date to check
     * @return true if date is today
     */
    public static boolean isToday(Date date) {
        return isSameDay(date, new Date());
    }
    
    /**
     * Format date according to device's locale settings
     * @param date Date to format
     * @return Formatted date string according to device settings
     */
    public static String formatAccordingToDeviceSettings(Date date) {
        java.text.DateFormat dateFormat = DateFormat.getDateFormat(
                ApplicationContextProvider.getContext());
        java.text.DateFormat timeFormat = DateFormat.getTimeFormat(
                ApplicationContextProvider.getContext());
        
        return dateFormat.format(date) + " " + timeFormat.format(date);
    }
}

