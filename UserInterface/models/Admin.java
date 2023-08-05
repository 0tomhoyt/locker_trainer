package models;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Admin {
    private int id;
    private String username;
    private String password;
    private int workStationID;
    private int machineID;
    private String authToken;
    private String enrollDate;
    private String headerURL;

    public Admin(String username, String password, int workStationID, int machineID) {
        this.username = username;
        this.password = password;
        this.workStationID = workStationID;
        this.machineID = machineID;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setWorkStationID(int workStationID) {
        this.workStationID = workStationID;
    }

    public void setMachineID(int machineID) {
        this.machineID = machineID;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public void setEnrollDate(String enrollDate) {
        this.enrollDate = enrollDate;
    }

    public void setHeaderURL(String headerURL) {
        this.headerURL = headerURL;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public int getWorkStationID() {
        return workStationID;
    }

    public int getMachineID() {
        return machineID;
    }

    public String getAuthToken() {
        return authToken;
    }

    public String getEnrollDate() {
        return enrollDate;
    }

    public String getHeaderURL() {
        return headerURL;
    }

    public int getWorkLength() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date enrollDay = sdf.parse(enrollDate);

        Calendar cal = Calendar.getInstance();

        if (cal.before(enrollDay)) { //入职日期晚于当前时间，无法计算
            throw new IllegalArgumentException(
                    "The birthDay is before Now.It's unbelievable!");
        }

        int yearNow = cal.get(Calendar.YEAR); //当前年份
        int monthNow = cal.get(Calendar.MONTH); //当前月份
        int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH); //当前日期

        cal.setTime(enrollDay);

        int yearBirth = cal.get(Calendar.YEAR);
        int monthBirth = cal.get(Calendar.MONTH);
        int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);
        int age = yearNow - yearBirth; //计算工龄

        if (monthNow <= monthBirth) {
            if (monthNow == monthBirth) {
                if (dayOfMonthNow < dayOfMonthBirth) age--;//当前日期在入职日期之前，年龄减一
            }else{
                age--;//当前月份在入职日期之前，年龄减一
            }
        }

        return age;
    }
}
