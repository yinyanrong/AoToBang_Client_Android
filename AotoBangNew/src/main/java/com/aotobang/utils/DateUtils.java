package com.aotobang.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {
	 private static final DateFormat datetimeDf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
	  private static final DateFormat timedfm = new SimpleDateFormat("HH:mm:ss");  
	  private static final DateFormat dfm = new SimpleDateFormat("yyyy-MM-dd");  
	public static boolean  isCloseEnough(long time,long preTime){
		return time-preTime<60*60*1000?true:false;
		
		
		
	}
	
	public static String getTimestampString(Date msgDate){
		Date currentDate=new Date(System.currentTimeMillis());
		if(isSameDay(msgDate,currentDate))
			return timedfm.format(msgDate);
		try {
			if(inSameWeek(msgDate, currentDate))
				return getWeekOfDate(msgDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return dfm.format(msgDate);
	}
	public static boolean isSameDay(Date day1, Date day2) {
	    String ds1 = dfm.format(day1);
	    String ds2 = dfm.format(day2);
	    if (ds1.equals(ds2)) {
	        return true;
	    } else {
	        return false;
	    }
	}
	 /** 
     * 判断两个时间是否在同一周 
     *  
     * @param firstDate 
     * @param secondDate 
     * @return 
     * @throws ParseException 
     */  
    public static boolean inSameWeek(Date firstDate, Date secondDate) throws ParseException {  
        /** 以下先根据第一个时间找出所在周的星期一、星期日, 再对第二个时间进行比较 */  
          
        Calendar calendarMonday = Calendar.getInstance();  
        calendarMonday.setTime(firstDate);  
          
        // 获取firstDate在当前周的第几天. （星期一~星期日：1~7）  
        int monday = calendarMonday.get(Calendar.DAY_OF_WEEK);  
        if (monday == 0) monday = 7;  
          
        // 星期一开始时间  
        calendarMonday.add(Calendar.DAY_OF_MONTH, - monday + 1);  
        calendarMonday.set(Calendar.HOUR, 0);  
        calendarMonday.set(Calendar.MINUTE, 0);  
        calendarMonday.set(Calendar.SECOND, 0);  
          
        // 星期日结束时间  
        Calendar calendarSunday = Calendar.getInstance();  
        calendarSunday.setTime(calendarMonday.getTime());  
        calendarSunday.add(Calendar.DAY_OF_MONTH, 6);  
        calendarSunday.set(Calendar.HOUR, 23);  
        calendarSunday.set(Calendar.MINUTE, 59);  
        calendarSunday.set(Calendar.SECOND, 59);  
          
        System.out.println("星期一开始时间：" + datetimeDf.format(calendarMonday.getTime()));  
        System.out.println("星期日结束时间：" + datetimeDf.format(calendarSunday.getTime()));  
          
        // 比较第二个时间是否与第一个时间在同一周  
        if (secondDate.getTime() >= calendarMonday.getTimeInMillis() &&   
                secondDate.getTime() <= calendarSunday.getTimeInMillis()) {  
            return true;  
        }  
        return false;  
    }  
    /**
     * 获取当前日期是星期几<br>
     * 
     * @param dt
     * @return 当前日期是星期几
     */
    public static String getWeekOfDate(Date dt) {
        String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;
        return weekDays[w];
    }
}
