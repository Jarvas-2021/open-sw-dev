package com.jarvas.mappyapp;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Scenario {

    public String start_time = "";
    public String arrive_time = "";
    public String time = "";
    public String start_place = "";
    public String arrive_place = "";

    Pattern time_check = Pattern.compile("[0-9]+시");
    Pattern date_time_check = Pattern.compile("<.*:TI>");
    Pattern place_check = Pattern.compile("<.*:LC>");

    public int check() {
        if (start_time.equals("")) {
            return 0;
        }
        if (arrive_time.equals("")) {
            return 1;
        }
        if (time.equals("")) {
            return 2;
        }
        if (start_place.equals("")) {
            return 3;
        }
        if (arrive_place.equals("")) {
            return 4;
        }
        return -1;
    }

    public String check_auto(String msg) {
        Matcher date_match_msg = date_time_check.matcher(msg);
        Matcher place_match_msg = place_check.matcher(msg);

        String return_msg = "";

        // input message에 시간이 있는지 확인
        if (date_match_msg.find()) {
            if (arrive_time.equals("")) {
                arrive_time = date_match_msg.group();
                return_msg = return_msg + "도착시간이 입력되었습니다.";
            }
            else if (start_time.equals("")) {
                start_time = date_match_msg.group();
                return_msg = return_msg + "출발시간이 입력되었습니다.";
            }
        }
        else {
            if (start_time.equals("") & arrive_time.equals("")) {
                return_msg = return_msg + "시간이 아직 입력되지 않았습니다.";
            }
            else if (start_time.equals("")) {
                return_msg = return_msg + "출발시간이 아직 입력되지 않았습니다.";
            }
            else if (arrive_time.equals("")) {
                return_msg = return_msg + "도착시간이 아직 입력되지 않았습니다.";
            }
            else {
                return_msg = return_msg + "시간은 모두 입력되었습니다.";
            }
        }

        // input message에 장소가 포함되어 있는지 확인
        if (place_match_msg.find()) {
            if (arrive_place.equals("")) {
                arrive_place = place_match_msg.group();
                return_msg = return_msg + "도착지가 입력되었습니다.";
            }
            else if (start_place.equals("")) {
                start_place = place_match_msg.group();
                return_msg = return_msg + "출발지가 입력되었습니다.";
            }
        }
        else {
            if (start_place.equals("") & arrive_place.equals("")) {
                return_msg = return_msg + "장소가 아직 입력되지 않았습니다.";
            }
            else if (start_place.equals("")) {
                return_msg = return_msg + "출발지가 아직 입력되지 않았습니다.";
            }
            else if (arrive_place.equals("")) {
                return_msg = return_msg + "목적지가 아직 입력되지 않았습니다.";
            }
            else {
                return_msg = return_msg + "장소는 모두 입력되었습니다.";
            }
        }

        return return_msg;
    }

    public void check_start_time(String msg) {
        Matcher match_msg = date_time_check.matcher(msg);
        if (match_msg.find()) {
            start_time = match_msg.group();
        }
    }

    public void check_arrive_time(String msg) {
        Matcher match_msg = date_time_check.matcher(msg);
        if (match_msg.find()) {
            arrive_time = match_msg.group();
        }
    }

    public void check_time(String msg) {
        Matcher match_msg = date_time_check.matcher(msg);
        if (match_msg.find()) {
            time = match_msg.group();
        }
    }

    public void check_start_place(String msg) {
        Matcher match_msg = place_check.matcher(msg);
        if (match_msg.find()) {
            start_place = match_msg.group();
        }
    }

    public void check_arrive_place(String msg) {
        Matcher match_msg = place_check.matcher(msg);
        if (match_msg.find()) {
            arrive_place = match_msg.group();
        }
    }


    public String simulation() {
        switch (check()) {
            case -1:
                return "모든 데이터가 입력되었습니다. 검색을 시작합니다.";
            case 0:
                return "출발 시간을 입력해주십시오.";
            case 1:
                return "원하시는 도착 시간을 입력해주십시오.";
            case 2:
                return "";
            case 3:
                return "출발지를 입력해주십시오.";
            case 4:
                return "목적지를 입력해주십시오.";
        }
        return "Error";
    }
}
