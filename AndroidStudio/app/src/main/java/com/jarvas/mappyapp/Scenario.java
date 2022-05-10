package com.jarvas.mappyapp;

import android.content.Intent;

import com.jarvas.mappyapp.activities.InputActivity;
import com.jarvas.mappyapp.activities.SettingActivity;
import com.jarvas.mappyapp.activities.StarActivity;
import com.jarvas.mappyapp.utils.ContextStorage;
import com.jarvas.mappyapp.utils.Util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Scenario {

    static public String start_time_scene = "";
    static public String arrive_time_scene = "";
    static public String time_scene = "";
    static public String start_place_scene = "";
    static public String arrive_place_scene = "";
    static public String what_time = "";
    static public String place_search = "";

    /*
     * error code numbers
     * -1: 문제 없음
     * 1: TI 정보가 하나만 있을 때. 출발 시간인지 도착시간인지 모름
     * 2: 도착지를 입력 안 했을 때
     * 3: 출발 시간, 도착 시간 둘 다 없을 때
     * 4: 오전, 오후인지 모를 때
     * */


    public int error_code_scene = -1;
    public boolean searchStart = false;
    public boolean searchNo = false;
    public boolean whenTime = false;

    public int whatTimeCount = 0;
    public int timeCount = 0;
    public int startTimeCount = 0;
    public int arriveTimeCount = 0;

    public String currentLocation;

    Pattern time_check = Pattern.compile("[0-9]+시");
    Pattern date_time_check = Pattern.compile("<[\\s[^\\s]]*:(TI|DT)>");
    Pattern place_check = Pattern.compile("<[[가-힣][a-zA-Z][0-9][\\s]]*:(LC|OG|PS)>");

    public Scenario(String current_location) {
        currentLocation= current_location;
    }

    public int check_scene() {
        if (arrive_place_scene.equals("") && place_search.equals("")){
            return 2;
        }
        if (arrive_time_scene.equals("") && start_time_scene.equals("")){
            return 3;
        }
        if (whenTime == true) {
            return 4;
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
        error_code_scene = -1;
        Matcher date_match_msg = date_time_check.matcher(msg);
        Matcher place_match_msg = place_check.matcher(msg);

        String return_msg = "";

        int placeCount = 0;
        int startPlaceCount = 0;
        int arrivePlaceCount = 0;

        int placeSearchCount = 0;

        // 설정창 액티비티로 이동
        if (msg.contains("설정")) {
            ((ContextStorage) ContextStorage.getCtx().getApplicationContext()).setEnd_point_show_data(true);
            Intent intent = new Intent(ContextStorage.getCtx(), SettingActivity.class);
            ContextStorage.getCtx().startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
        // 즐겨찾기 액티비티로 이동
        if (msg.contains("즐겨") || msg.contains("즐겨찾기")) {
            ((ContextStorage) ContextStorage.getCtx().getApplicationContext()).setEnd_point_show_data(true);
            Intent intent = new Intent(ContextStorage.getCtx(), StarActivity.class);
            ContextStorage.getCtx().startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
        // 현재위치 찾기
        if (msg.contains("현재") || msg.contains("위치")) {

        }

        if (msg != "" && !msg.isEmpty()) {
            msg += "  ";
        }

        // 출발도착
        if (what_time != "") {
            if (msg.contains("출발") || msg.contains("출발 시간") || msg.contains("출발시간")) {
                start_time_scene = what_time;
                return_msg = return_msg + "출발시간이 입력되었습니다.";
                startTimeCount++;
                what_time = "";
                whatTimeCount--;
                whenTime = true;
            }
            if (msg.contains("도착") || msg.contains("도착 시간") || msg.contains("도착시간")) {
                arrive_time_scene = what_time;
                return_msg = return_msg + "도착시간이 입력되었습니다.";
                arriveTimeCount++;
                what_time = "";
                whatTimeCount--;
                whenTime = true;
            }
        }

        //검색을 할까요??에 대답
        if (searchStart) {
            //긍정의 대답일 경우
            if (msg.contains("네") || msg.contains("그래") || msg.contains("예") || msg.contains("응")
                    || msg.contains("엉") || msg.contains("웅") || msg.contains("웅웅") || msg.contains("음") || msg.contains("어")) {
                ((ContextStorage) ContextStorage.getCtx().getApplicationContext()).setEnd_point_show_data(true);
                return_msg += "검색을 시작하겠습니다.";
                Intent intent = new Intent(ContextStorage.getCtx(), InputActivity.class);
                intent.putExtra("start_time_scene",start_time_scene);
                intent.putExtra("arrive_time_scene",arrive_time_scene);
                if (start_place_scene.equals("")) {
                    start_place_scene = currentLocation;
                }
                intent.putExtra("start_place_scene",start_place_scene);
                intent.putExtra("arrive_place_scene",arrive_place_scene);
                ContextStorage.getCtx().startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }
            //부정의 대답일 경우
            else if (msg.contains("아니") || msg.contains("아직") || msg.contains("잠시만") || msg.contains("잠시")) {
                searchNo = true;
                searchStart = false;
            }
        }

        //오전오후에 대한 답변
        if (whenTime) {
            if (msg.contains("오후")) {
                convertTime();
                if (!arrive_time_scene.equals("")) {
                    return_msg = return_msg + "도착시간이 입력되었습니다.";
                    whenTime = false;
                }
                if (!start_time_scene.equals("")) {
                    return_msg = return_msg + "출발시간이 입력되었습니다.";
                    whenTime = false;
                }
            } else if (msg.contains("오전")) {
                if (!arrive_time_scene.equals("")) {
                    return_msg = return_msg + "도착시간이 입력되었습니다.";
                    whenTime = false;
                }
                if (!start_time_scene.equals("")) {
                    return_msg = return_msg + "출발시간이 입력되었습니다.";
                    whenTime = false;
                }

            }
        }

        // input message에 시간이 있는지 확인
        while (date_match_msg.find()) {
            timeCount++;

            if (arrive_time_scene.equals("")) {
                try {
                    if (msg.substring(date_match_msg.end(),date_match_msg.end() + 2).equals("까지") ||
                            msg.contains("도착시간") || msg.contains("도착 시간") || msg.contains("도착")){

                        arrive_time_scene = date_match_msg.group();

                        if (msg.contains("오후")) {
                            convertTime();
                            return_msg = return_msg + "도착시간이 입력되었습니다.";
                            arriveTimeCount++;
                        } else if (msg.contains("오전")) {
                            return_msg = return_msg + "도착시간이 입력되었습니다.";
                            arriveTimeCount++;
                        } else {
                            whenTime = true;
                        }

                        System.out.println("arriveTime: " + arrive_time_scene);

                    }

                }catch (StringIndexOutOfBoundsException e){
                    continue;
                }

            }
            if (start_time_scene.equals("")) {
                try {
                    if (msg.substring(date_match_msg.end(), date_match_msg.end() + 2).equals("부터") ||
                            msg.contains("출발시간") || msg.contains("출발 시간") || msg.contains("출발")) {

                        start_time_scene = date_match_msg.group();

                        if (msg.contains("오후")) {
                            convertTime();
                            return_msg = return_msg + "출발시간이 입력되었습니다.";
                            startTimeCount++;
                        } else if (msg.contains("오전")) {
                            return_msg = return_msg + "출발시간이 입력되었습니다.";
                            startTimeCount++;
                        } else {
                            whenTime = true;
                        }

                        System.out.println("startTime: " + start_time_scene);
                    }

                } catch (StringIndexOutOfBoundsException e) {
                    continue;
                }
            }

            if (startTimeCount == 0 && arriveTimeCount == 0 && timeCount == 1 && whenTime == false) {
                what_time = date_match_msg.group();
                whatTimeCount++;
            }

            if (whatTimeCount == 1 && timeCount == 2) {
                return_msg = return_msg + "시간은 한 가지만 입력하실 수 있습니다.\n";

            }

            System.out.println("Time count = " + timeCount);
        }

        // input message에 장소가 포함되어 있는지 확인
        while (place_match_msg.find()){
            placeCount++;

            // 장소 검색
            if (place_search.equals("")) {
                if (msg.contains("어디") || msg.contains("어디야")) {
                    place_search = place_match_msg.group();
                    convertWord();
                    placeSearchCount++;

                    System.out.println("place_search = " + place_search);
                }
            }

            if (arrive_place_scene.equals("")) {
                try {
                    if (msg.substring(place_match_msg.end(),place_match_msg.end() + 2).equals("까지")
                            || msg.substring(place_match_msg.end(),place_match_msg.end() + 2).equals("으로")
                            || msg.substring(place_match_msg.end(),place_match_msg.end() + 1).equals("로")
                            || msg.substring(place_match_msg.end(),place_match_msg.end() + 1).equals("을")
                            || msg.substring(place_match_msg.end(),place_match_msg.end() + 1).equals("를")){
                        arrive_place_scene = place_match_msg.group();
                        return_msg = return_msg + "도착지가 입력되었습니다.\n";
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
                        return_msg = return_msg + "출발지가 입력되었습니다.\n";
                        startPlaceCount++;

                        System.out.println("start: " + start_place_scene);
                    }

                } catch (StringIndexOutOfBoundsException e) {
                    continue;
                }

            }

            if (startPlaceCount == 0 && arrivePlaceCount == 0 && placeCount == 1 && placeSearchCount == 0) {
                arrive_place_scene = place_match_msg.group();
                return_msg = return_msg + "도착지가 입력되었습니다.\n";
                arrivePlaceCount++;

                System.out.println("arrive: " + arrive_place_scene);
            }

            if (startPlaceCount == 0 && arrivePlaceCount == 1 && placeCount == 2) {
                start_place_scene = arrive_place_scene;
                arrive_place_scene = "";
                arrive_place_scene = place_match_msg.group();
                return_msg = return_msg + "출발지가 입력되었습니다.\n";
                startPlaceCount++;


                System.out.println("start: " + start_place_scene);
                System.out.println("arrive: " + arrive_place_scene);
            }

            System.out.println("count = " + placeCount);
        }

        if (whenTime == true) {
            return_msg += "오전인지 오후인지 알려주세요.";
        }

        if (whatTimeCount == 1 && timeCount == 1 && whenTime == false) {
            return_msg += "출발시간인지 도착시간인지 알려주세요.\n";
            error_code_scene = 1;
        }

        if (start_place_scene.equals("") & arrive_place_scene.equals("") && placeSearchCount == 0) {
            return_msg = return_msg + "장소가 아직 입력되지 않았습니다.\n";
        }
        else if (arrive_place_scene.equals("") && placeSearchCount == 0) {
            return_msg = return_msg + "목적지가 아직 입력되지 않았습니다.\n";
        }
        else if (!arrive_place_scene.equals("") && !start_place_scene.equals("")){
            return_msg = return_msg + "장소는 모두 입력되었습니다.\n";
        }

        if (!arrive_place_scene.equals("") && whatTimeCount == 0 && placeSearchCount == 0 && searchNo == false && whenTime == false && searchStart == false && msg != "" && !Util.isStringEmpty(msg)) {
            return_msg += "검색을 시작할까요?";
            searchStart = true;
        }
        if (searchNo == true) {
            return_msg += "무엇을 입력하시겠습니까?";
            searchNo = false;
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
        start_place_scene = start_place_scene.replaceAll("^[<]|:(LC|OG|PS)[>]", "");

        arrive_place_scene = arrive_place_scene.replaceAll("^[<]|:(LC|OG|PS)[>]", "");

        start_time_scene = start_time_scene.replaceAll("^[<]|:(TI|DT)[>]", "");
        start_time_scene = start_time_scene.replaceAll("[반]", "30분");

        arrive_time_scene = arrive_time_scene.replaceAll("^[<]|:(TI|DT)[>]", "");
        arrive_time_scene = arrive_time_scene.replaceAll("[반]", "30분");

        place_search = place_search.replaceAll("^[<]|:(LC|OG|PS)[>]", "");
    }

    public static void convertTime() {
        start_time_scene = start_time_scene.replaceAll("^[<]|:(TI|DT)[>]", "");
        start_time_scene = start_time_scene.replaceAll("[반]", "30분");

        arrive_time_scene = arrive_time_scene.replaceAll("^[<]|:(TI|DT)[>]", "");
        arrive_time_scene = arrive_time_scene.replaceAll("[반]", "30분");

        int hour = 12;
        if (!start_time_scene.equals("") && start_time_scene.contains("시")) {
            hour += Integer.parseInt(start_time_scene.split("시")[0]);
            if (hour >= 24) {
                hour -= 24;
            }
            start_time_scene = start_time_scene.replaceAll("[0-9]+시", Integer.toString(hour) + "시");
        }
        if (!arrive_time_scene.equals("") && arrive_time_scene.contains("시")) {
            hour += Integer.parseInt(arrive_time_scene.split("시")[0]);
            if (hour >= 24) {
                hour -= 24;
            }
            arrive_time_scene = arrive_time_scene.replaceAll("[0-9]+시", hour + "시");

        }

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
