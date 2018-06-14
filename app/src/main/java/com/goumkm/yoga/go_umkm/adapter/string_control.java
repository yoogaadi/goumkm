package com.goumkm.yoga.go_umkm.adapter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class string_control {
    String[] strBulan = {"Jan","Feb","Mar","Apr","Mei","Jun","Jul","Aug","Sep","Okt","Nov","Des"};
    public String getTimestamp(String tanggal){
        String result = "";
        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        try {
            if (!tanggal.equalsIgnoreCase("now")) {
                Date parsedTimeStamp = dateFormat.parse(tanggal);
                java.sql.Timestamp timestamp = new java.sql.Timestamp(parsedTimeStamp.getTime());
                date = new java.util.Date(timestamp.getTime());

            }
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            int month = cal.get(Calendar.MONTH) + 1;
            int day = cal.get(Calendar.DAY_OF_MONTH);
            int year = cal.get(Calendar.YEAR);
            String minute, hour, second;
            minute = "" + (cal.get(Calendar.MINUTE));
            hour = "" + (cal.get(Calendar.HOUR_OF_DAY));
            second = "" + (cal.get(Calendar.SECOND));

            if (cal.get(Calendar.MINUTE) < 10) {
                minute = "0" + minute;
            }
            if (cal.get(Calendar.HOUR_OF_DAY) < 10) {
                hour = "0" + hour;
            }
            if (cal.get(Calendar.SECOND) < 10) {
                second = "0" + second;
            }

            String str = year + "/" + (month) + "/" + day;
            SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");
            Date date2 = df.parse(str);
            long epoch = date2.getTime() / 1000L;

            result = epoch + "";


        }catch (Exception e) {

        }
        return result;
    }
    public String getWaktuByTimestamp(int timestamp) {
        java.util.Date time = new java.util.Date((long)timestamp*1000);
        Calendar cal = Calendar.getInstance();
        cal.setTime(time);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int year = cal.get(Calendar.YEAR);

        return day+" "+ strBulan[month] + " " + year;
    }
}
