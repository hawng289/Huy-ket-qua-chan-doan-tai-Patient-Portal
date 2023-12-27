package vn.com.itechcorp.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {
    public static final DateFormat VR_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static final DateFormat HIS_DATE_FORMAT = new SimpleDateFormat("yyyyMMddHHmmss");

    public static final DateFormat YYYYMMDD_FORMAT = new SimpleDateFormat("yyyyMMdd");

    public static String formatDate(String date) {
        try {
            Date newDate = DateUtil.VR_DATE_FORMAT.parse(date);
            return DateUtil.HIS_DATE_FORMAT.format(newDate);
        } catch (ParseException ex) {
            return date;
        }
    }

    public static Date delayInSecond(Date date, int delay) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.SECOND, delay);
        return calendar.getTime();
    }

    public static Date delayInMinute(Date date, int delay) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MINUTE, delay);
        return calendar.getTime();
    }
}
