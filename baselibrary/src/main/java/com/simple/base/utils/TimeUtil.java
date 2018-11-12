package com.simple.base.utils;

import android.annotation.SuppressLint;
import android.text.TextUtils;


import com.simple.base.utils.DateUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@SuppressLint("SimpleDateFormat")
public class TimeUtil {


    public static String getTime(String pattern) {
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(new Date());
    }

    public static String getTime(long time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(new Date(time));
    }

    public static String getIMGTime() {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd_HHmmssSSS");
        return format.format(new Date());
    }

//    public static String getDayTime(long time) {
//        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
//        return format.format(new Date(time));
//
//    }


    public static String getWeekTime(long time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日 HH:mm ");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(time));
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        String week = getWeekString(dayOfWeek);
        return format.format(new Date(time)) + week;
    }


    public static String getMailTime(long time) {
        Calendar current = Calendar.getInstance();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(time));
        if (calendar.get(Calendar.YEAR) == current.get(Calendar.YEAR)) {//同一年
            if (calendar.get(Calendar.DAY_OF_YEAR) == current.get(Calendar.DAY_OF_YEAR)) {//今天
                String string = new SimpleDateFormat("HH:mm").format(time);
                String prefixion = getTimeSectionInDay(Integer.parseInt(string.substring(0, 2).trim()));
                return prefixion + " " + string;
            }
            return new SimpleDateFormat("MM-dd HH:mm").format(time);
        } else {
            calendar.setTime(new Date(time + 24 * 60 * 60 * 1000));
            return new SimpleDateFormat("yyyy-MM-dd HH:mm").format(time);
        }
    }




    /**
     * 获取聊天时间显示
     *
     * @param timeMillis 距离1970-01-01 00:00 的时间偏移量（单位：毫秒）
     * @return
     */
    public static String getChatTimeByTimeMillis(String timeMillis) {
        if (TextUtils.isEmpty(timeMillis)) {
            return "";
        }
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(Long.parseLong(timeMillis));
            return getChatTime(calendar);
        } catch (NumberFormatException e) {
            return "";
        }
    }

    /**
     * 获取聊天时间
     *
     * @param calendar
     * @return
     */
    public static String getChatTime(Calendar calendar) {
        String timeString = "";
        Calendar calendarNow = Calendar.getInstance();
        if (DateUtils.compareCalendarField(calendarNow, calendar, Calendar.YEAR) == 0) {//今年
            if (DateUtils.compareCalendarField(calendarNow, calendar, Calendar.WEEK_OF_YEAR) == 0) {//本周
                timeString = DateUtils.formatDate(calendar, "HH:mm");
                switch (DateUtils.compareCalendarField(calendarNow, calendar, Calendar.DAY_OF_YEAR)) {
                    case 0://今天
                        break;
                    case 1://昨天
                        timeString = "昨天 " + timeString;
                        break;
                    default:
                        timeString = getWeekString(calendar.get(Calendar.DAY_OF_WEEK)) + " " + timeString;
                        break;
                }
            } else {//非本周
                String timeSection = getTimeSectionInDay(calendar.get(Calendar.HOUR_OF_DAY));
                timeString = DateUtils.formatDate(calendar, "M月d日 " + timeSection + "HH:mm");
            }
        } else {//非今年
            String timeSection = getTimeSectionInDay(calendar.get(Calendar.HOUR_OF_DAY));
            timeString = DateUtils.formatDate(calendar, "yyyy年M月d日 " + timeSection + "HH:mm");
        }
        return timeString;
    }


    /**
     * 获取会话时间显示
     *
     * @param timeMillis 距离1970-01-01 00:00 的时间偏移量（单位：毫秒）
     * @return
     */
    public static String getConversationTimeByTimeMillis(String timeMillis) {
        if (TextUtils.isEmpty(timeMillis)) {
            return "";
        }
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(Long.parseLong(timeMillis));
            return getConversationTime(calendar);
        } catch (NumberFormatException e) {
            return "";
        }
    }

    /**
     * 获取会话时间显示
     *
     * @param calendar
     * @return
     */
    public static String getConversationTime(Calendar calendar) {
        String timeString = "";
        Calendar calendarNow = Calendar.getInstance();
        if (DateUtils.compareCalendarField(calendarNow, calendar, Calendar.YEAR) == 0) {//今年
            int dateOffset = DateUtils.compareCalendarField(calendarNow, calendar, Calendar.DAY_OF_YEAR);
            switch (dateOffset) {
                case 0://今天
                    timeString = DateUtils.formatDate(calendar, "HH:mm");
                    break;
                case 1://昨天
                    timeString = "昨天";
                    break;
                default:
                    if (dateOffset < 7) {//一周内
                        timeString = getWeekString(calendar.get(Calendar.DAY_OF_WEEK));
                    } else {//超过一周
                        timeString = DateUtils.formatDate(calendar, "MM月dd日");
                    }
                    break;
            }
        } else {//非今年
            timeString = DateUtils.formatDate(calendar, "yyyy年MM月dd日");
        }
        return timeString;
    }

    /**
     * 获取时间段文字
     *
     * @param hour
     * @return
     */
    public static String getTimeSectionInDay(int hour) {
        String timeSection = "";
        if (hour <= 6) {
            timeSection = "凌晨";
        } else if (hour <= 12) {
            timeSection = "上午";
        } else if (hour <= 18) {
            timeSection = "下午";
        } else if (hour <= 24) {
            timeSection = "晚上";
        }
        return timeSection;
    }

    /**
     * 获取周字符串
     *
     * @param dayOfWeek
     * @return
     */
    public static String getWeekString(int dayOfWeek) {
        String week = "";
        switch (dayOfWeek) {
            case 1:
                week = "周日";
                break;
            case 2:
                week = "周一";
                break;
            case 3:
                week = "周二";
                break;
            case 4:
                week = "周三";
                break;
            case 5:
                week = "周四";
                break;
            case 6:
                week = "周五";
                break;
            case 7:
                week = "周六";
                break;
        }
        return week;
    }


}
