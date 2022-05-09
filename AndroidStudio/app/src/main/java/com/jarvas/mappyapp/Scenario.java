package com.jarvas.mappyapp;

import android.content.Intent;

import com.jarvas.mappyapp.activities.SettingActivity;
import com.jarvas.mappyapp.activities.StarActivity;
import com.jarvas.mappyapp.activities.TimePopupActivity;
import com.jarvas.mappyapp.utils.ContextStorage;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Scenario {

    static public String start_time_scene = "";
    static public String arrive_time_scene = "";
    static public String time_scene = "";
    static public String start_place_scene = "";
    static public String arrive_place_scene = "";
    static public String what_time = "";

    /*
     * error code numbers
     * -1: 문제 없음
     * 1: TI 정보가 하나만 있을 때. 출발 시간인지 도착시간인지 모름
     * 2: 도착지를 입력 안 했을 때
     * 3: 출발 시간, 도착 시간 둘 다 없을 때
     * */

    public int error_code_scene = -1;

    Pattern time_check = Pattern.compile("[0-9]+시");
    Pattern date_time_check = Pattern.compile("<.*:TI>");
    Pattern place_check = Pattern.compile("<.*:LC>");

    public int check_scene() {
        if (arrive_place_scene.equals("")){
            return 2;
        }
        if (arrive_time_scene.equals("") && start_time_scene.equals("")){
            return 3;
        }

        if (error_code_scene != -1) {
            return error_code_scene;
        }
        /*if (start_time_scene.equals("")) {
            return 0;
        }
        if (arrive_time_scene.equals("")) {
            return 1;
        }
        if (time_scene.equals("")) {
            return 2;
        }
        if (start_place_scene.equals("")) {
            return 3;
        }
        if (arrive_place_scene.equals("")) {
            return 4;
        }*/
        return -1;
    }

    public int check_main(String msg) {
        if (msg.equals("매피")) return 1;
        if (msg.equals("맵피")) return 1;
        if (msg.equals("해피")) return 1;
        if (msg.equals("웨피")) return 1;
        if (msg.equals("웹피")) return 1;

        if (msg.equals("매피야")) return 1;
        if (msg.equals("맵피야")) return 1;
        if (msg.equals("해피야")) return 1;
        if (msg.equals("웨피야")) return 1;
        if (msg.equals("웹피야")) return 1;

        if (msg.equals("웨피아")) return 1;
        if (msg.equals("웨피아")) return 1;
        if (msg.equals("웨피아")) return 1;
        if (msg.equals("웨피아")) return 1;
        if (msg.equals("웹피아")) return 1;

        if (msg.equals("매피 야")) return 1;
        if (msg.equals("맵피 야")) return 1;
        if (msg.equals("해피 야")) return 1;
        if (msg.equals("웨피 야")) return 1;
        if (msg.equals("웹피 야")) return 1;

        if (msg.equals("웨피 아")) return 1;
        if (msg.equals("웨피 아")) return 1;
        if (msg.equals("웨피 아")) return 1;
        if (msg.equals("웨피 아")) return 1;
        if (msg.equals("웨피 아")) return 1;

        if (msg.equals("매퍄")) return 1;
        if (msg.equals("피야")) return 1;

        return 0;
    }

    public int check_input_start(String msg) {
        if (msg.contains("일") || msg.contains("첫번째") || msg.contains("1") || msg.contains("위")) {
            return 0;
        }
        if (msg.contains("이") || msg.contains("두번째") || msg.contains("2")) {
            return 1;
        }
        if (msg.contains("삼") || msg.contains("세번째") || msg.contains("3")) {
            return 2;
        }
        if (msg.contains("사") || msg.contains("네번째") || msg.contains("4")) {
            return 3;
        }
        return 0;
    }

    public int check_search(String msg) {
        if (msg.contains("검색") || msg.contains("서치") || msg.contains("찾아")) {
            return 1;
        }
        else {
            return 0;
        }
    }



    public String check_auto(String msg) {
        // 설정창 액티비티로 이동
        if (msg.contains("설정")) {
            Intent intent = new Intent(ContextStorage.getCtx(), SettingActivity.class);
            ContextStorage.getCtx().startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
        // 즐겨찾기 액티비티로 이동
        if (msg.contains("즐겨") || msg.contains("즐겨찾기")) {
            Intent intent = new Intent(ContextStorage.getCtx(), StarActivity.class);
            ContextStorage.getCtx().startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
        // 현재위치 찾기
        if (msg.contains("현재") || msg.contains("위치")) {

        }

        // 장소 검색
        if (msg.contains("어디") || msg.contains("어디야")) {

        }
        // 출발도착
        if (what_time != "") {
            if (msg.contains("출발") || msg.contains("출발 시간") || msg.contains("출발시간")) {
                start_time_scene = what_time;
                what_time = "";
            }
            if (msg.contains("도착") || msg.contains("도착 시간") || msg.contains("도착시간")) {
                arrive_time_scene = what_time;
                what_time = "";
            }
        }

        error_code_scene = -1;
        Matcher date_match_msg = date_time_check.matcher(msg);
        Matcher place_match_msg = place_check.matcher(msg);

        String return_msg = "";

        int placeCount = 0;
        int startPlaceCount = 0;
        int arrivePlaceCount = 0;

        int timeCount = 0;
        int startTimeCount = 0;
        int arriveTimeCount = 0;
        int whatTimeCount = 0;





        // input message에 시간이 있는지 확인
        while (date_match_msg.find()) {
            timeCount++;

            if (arrive_time_scene.equals("")) {
                try {
                    if (msg.substring(date_match_msg.end(),date_match_msg.end() + 2).equals("까지") ||
                            msg.contains("도착시간") || msg.contains("도착 시간")){
                        arrive_time_scene = date_match_msg.group();
                        return_msg = return_msg + "도착시간이 입력되었습니다.";
                        arriveTimeCount++;

                        System.out.println("arriveTime: " + arrive_time_scene);

                    }

                }catch (StringIndexOutOfBoundsException e){
                    continue;
                }

            }
            if (start_time_scene.equals("")) {
                try {
                    if (msg.substring(date_match_msg.end(), date_match_msg.end() + 2).equals("부터") ||
                            msg.contains("출발시간") || msg.contains("출발 시간")) {
                        start_time_scene = date_match_msg.group();
                        return_msg = return_msg + "출발시간이 입력되었습니다.";
                        startTimeCount++;

                        System.out.println("startTime: " + start_time_scene);
                    }

                } catch (StringIndexOutOfBoundsException e) {
                    continue;
                }
            }

            if (startTimeCount == 0 && arriveTimeCount == 0 && timeCount == 1) {
                what_time = date_match_msg.group();
                whatTimeCount++;
            }

            if (whatTimeCount == 1 && timeCount == 2) {
                return_msg = return_msg + "시간은 한 가지만 입력하실 수 있습니다.";

            }

            System.out.println("Time count = " + timeCount);
        }

        if (whatTimeCount == 1 && timeCount == 1) {
            return_msg += "출발시간인지 도착시간인지 알려주세요.";
            error_code_scene = 1;
        }
        if (start_time_scene.equals("") & arrive_time_scene.equals("")) {
            return_msg = return_msg + "시간이 아직 입력되지 않았습니다.";
        }
        else if (start_time_scene.equals("")) {
            return_msg = return_msg + "출발시간이 아직 입력되지 않았습니다.";
        }
        else if (arrive_time_scene.equals("")) {
            return_msg = return_msg + "도착시간이 아직 입력되지 않았습니다.";
        }
        else {
            return_msg = return_msg + "시간은 모두 입력되었습니다.";
        }

        // input message에 장소가 포함되어 있는지 확인
        while (place_match_msg.find()){
            placeCount++;

            if (arrive_place_scene.equals("")) {
                try {
                    if (msg.substring(place_match_msg.end(),place_match_msg.end() + 2).equals("까지")
                            || msg.substring(place_match_msg.end(),place_match_msg.end() + 2).equals("으로")
                            || msg.substring(place_match_msg.end(),place_match_msg.end() + 1).equals("로")
                            || msg.substring(place_match_msg.end(),place_match_msg.end() + 1).equals("을")
                            || msg.substring(place_match_msg.end(),place_match_msg.end() + 1).equals("를")){
                        arrive_place_scene = place_match_msg.group();
                        return_msg = return_msg + "도착지가 입력되었습니다.";
                        arrivePlaceCount++;

                        System.out.println("arrive: " + arrive_place_scene);

                    }

                }catch (StringIndexOutOfBoundsException e){
                    continue;
                }

            }

            if (start_place_scene.equals("")) {
                try {
                    if (msg.substring(place_match_msg.end(), place_match_msg.end() + 2).equals("에서")
                            || msg.substring(place_match_msg.end(), place_match_msg.end() + 2).equals("부터")) {
                        start_place_scene = place_match_msg.group();
                        return_msg = return_msg + "출발지가 입력되었습니다.";
                        startPlaceCount++;

                        System.out.println("start: " + start_place_scene);
                    }

                } catch (StringIndexOutOfBoundsException e) {
                    continue;
                }

            }

            if (startPlaceCount == 0 && arrivePlaceCount == 0 && placeCount == 1) {
                arrive_place_scene = place_match_msg.group();
                return_msg = return_msg + "도착지가 입력되었습니다.";
                arrivePlaceCount++;

                System.out.println("arrive: " + arrive_place_scene);
            }

            if (startPlaceCount == 0 && arrivePlaceCount == 1 && placeCount == 2) {
                start_place_scene = arrive_place_scene;
                arrive_place_scene = "";
                arrive_place_scene = place_match_msg.group();
                return_msg = return_msg + "출발지가 입력되었습니다.";
                startPlaceCount++;


                System.out.println("start: " + start_place_scene);
                System.out.println("arrive: " + arrive_place_scene);
            }

            System.out.println("count = " + placeCount);
        }

        if (start_place_scene.equals("") & arrive_place_scene.equals("")) {
            return_msg = return_msg + "장소가 아직 입력되지 않았습니다.";
        }
        else if (start_place_scene.equals("")) {
            return_msg = return_msg + "출발지가 아직 입력되지 않았습니다.";
        }
        else if (arrive_place_scene.equals("")) {
            return_msg = return_msg + "목적지가 아직 입력되지 않았습니다.";
        }
        else {
            return_msg = return_msg + "장소는 모두 입력되었습니다.";
        }

        convertWord();
        System.out.println();
        System.out.println("startTime: " + start_time_scene);
        System.out.println("arriveTime: " + arrive_time_scene);
        System.out.println("start: " + start_place_scene);
        System.out.println("arrive: " + arrive_place_scene);
        error_code_scene = check_scene();
        return return_msg;
    }



    public static void convertWord(){
        start_place_scene = start_place_scene.replaceAll("^[<]|:LC[>]", "");

        arrive_place_scene = arrive_place_scene.replaceAll("^[<]|:LC[>]", "");

        start_time_scene = start_time_scene.replaceAll("^[<]|:TI[>]", "");
        start_time_scene = start_time_scene.replaceAll("[반]", "30분");

        arrive_time_scene = arrive_time_scene.replaceAll("^[<]|:TI[>]", "");
        arrive_time_scene = arrive_time_scene.replaceAll("[반]", "30분");
    }


    public void check_start_time(String msg) {
        Matcher match_msg = date_time_check.matcher(msg);
        if (match_msg.find()) {
            start_time_scene = match_msg.group();
        }
    }

    public void check_arrive_time(String msg) {
        Matcher match_msg = date_time_check.matcher(msg);
        if (match_msg.find()) {
            arrive_time_scene = match_msg.group();
        }
    }

    public void check_time(String msg) {
        Matcher match_msg = date_time_check.matcher(msg);
        if (match_msg.find()) {
            time_scene = match_msg.group();
        }
    }

    public void check_start_place(String msg) {
        Matcher match_msg = place_check.matcher(msg);
        if (match_msg.find()) {
            start_place_scene = match_msg.group();
        }
    }

    public void check_arrive_place(String msg) {
        Matcher match_msg = place_check.matcher(msg);
        if (match_msg.find()) {
            arrive_place_scene = match_msg.group();
        }
    }




    /*public String simulation() {
        switch (check()) {
            case -1:
                return "모든 데이터가 입력되었습니다. 검색을 시작합니다.";
            *//*case 0:
                return "출발 시간을 입력해주십시오.";
            case 1:
                return "원하시는 도착 시간을 입력해주십시오.";
            case 2:
                return "";
            case 3:
                return "출발지를 입력해주십시오.";
            case 4:
                return "목적지를 입력해주십시오.";*//*

        }
        return "Error";
    }*/

}
