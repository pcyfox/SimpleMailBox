package com.simple.base.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * @Purpose
 * @Author Naruto Yang
 * @CreateDate ${Date}
 * @Note
 */
public class DateUtils {

    /**
     * 格式化日期
     *
     * @param calendar
     * @param formatString
     * @return
     */
    public static String formatDate(Calendar calendar, String formatString) {
        if (calendar == null) {
            return "";
        }
        return formatDate(calendar.getTime(), formatString);
    }

    /**
     * 格式化日期
     *
     * @param date
     * @param formatString
     * @return
     */
    public static String formatDate(Date date, String formatString) {
        if (date == null) {
            return "";
        }
        String s = "";
        SimpleDateFormat df = new SimpleDateFormat(formatString);// 设置日期格式
        s = df.format(date);
        return s;
    }

    /**
     * 格式化日期
     *
     * @param dateString
     * @param formateFrom
     * @param formateTo
     * @return
     */
    public static String formatDate(String dateString, String formateFrom, String formateTo) {
        if ((dateString == null || dateString.equals(""))
                || (formateFrom == null || formateFrom.equals(""))
                || (formateTo == null || formateTo.equals(""))) {
            return "";
        }
        String s = "";
        SimpleDateFormat sdfF = new SimpleDateFormat(formateFrom, Locale.ENGLISH);
        SimpleDateFormat sdfT = new SimpleDateFormat(formateTo, Locale.ENGLISH);
        Date date;
        try {
            date = sdfF.parse(dateString);
            s = sdfT.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return s;
    }

    /**
     * 字符串转Date
     *
     * @param dateString
     * @param format
     * @return
     */
    public static Date stringToDate(String dateString, String format) {
        if ((dateString == null || dateString.equals(""))
                || (format == null || format.equals(""))) {
            return null;
        }
        SimpleDateFormat sdfF = new SimpleDateFormat(format, Locale.ENGLISH);
        Date date = null;
        try {
            date = sdfF.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * 比较两个日期
     *
     * @param calendar1
     * @param calendar2
     * @param calendarField
     * @return
     */
    public static int compareCalendarField(Calendar calendar1, Calendar calendar2, int calendarField) {
        return calendar1.get(calendarField) - calendar2.get(calendarField);
    }
}
