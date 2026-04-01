package com.example.list.Utils;

import android.app.DatePickerDialog;
import android.content.Context;

import java.util.Calendar;

public class DatePickerUtil {
    public interface DateResultCallback {
        void onDateSelected(String displayDate, String formattedDate);
    }

    public static void showDatePicker(Context context, final DateResultCallback callback) {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(context, (view, selectedYear, selectedMonth, selectedDay) -> {
            String display = getMonthFormat(selectedMonth) + " " + selectedDay + " " + selectedYear;
            String formatted = formatDateForDatabase(selectedYear, selectedMonth + 1, selectedDay);
            callback.onDateSelected(display, formatted);
        }, year, month, day);

        datePickerDialog.show();
    }

    public static String formatDateForDatabase(int year, int month, int day) {
        String m = (month < 10) ? "0" + month : String.valueOf(month);
        String d = (day < 10) ? "0" + day : String.valueOf(day);
        return year + "-" + m + "-" + d;
    }

    public static String getMonthFormat(int month){
        return new String[]{"JAN", "FEB", "MAR", "APR", "MAY", "JUN",
                "JUL", "AUG", "SEP", "OCT", "NOV", "DEC"}[month];
    }

    public static int getTodayYear(){
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        return year;
    }
    private static int getTodayMonth(){
        Calendar cal = Calendar.getInstance();
        int month = cal.get(Calendar.MONTH);
        return month;
    }
    private static int getTodayDay() {
        Calendar cal = Calendar.getInstance();
        int day = cal.get(Calendar.DAY_OF_MONTH);
        return day;
    }

    public static String getTodayDateString() {
        int year = getTodayYear();
        int month = getTodayMonth();
        int day = getTodayDay();
        return getMonthFormat(month) + " " + day + " " + year;
    }
    public static String getTodayFormattedDate(){
        int year = getTodayYear();
        int month = getTodayMonth();
        int day = getTodayDay();
        return formatDateForDatabase(year, month + 1, day);
    }
}


