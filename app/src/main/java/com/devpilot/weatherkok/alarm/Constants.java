package com.devpilot.weatherkok.alarm;

public class Constants {
    private static final String TAG = Constants.class.getSimpleName();

    // 알림 설정 Preference Key 값
    public static final String SHARED_PREF_NOTIFICATION_KEY = "Notification Value";

    //광고 제거 Preference Key 값
    public static final String SHARED_PREF_AD_KEY = "Notification Value";

    // 알림 채널 ID 값
    public static final String NOTIFICATION_CHANNEL_ID = "10001";

    // 한국 TimeZone
    public static final String KOREA_TIMEZONE = "Asia/Seoul";

    // 챌린지 랭킹 시작 시각
    public static final Integer A_MORNING_EVENT_TIME = 5;
    public static final Integer A_NIGHT_EVENT_TIME = 17;
    public static final Integer B_MORNING_EVENT_TIME = 6;
    public static final Integer B_NIGHT_EVENT_TIME = 15;

    // 푸시알림 허용 Interval 시간
    public static final Integer NOTIFICATION_INTERVAL_HOUR = 1;

    // 백그라운드 work Unique 이름
    public static final String WORK_A_NAME = "Challenge Notification";
    public static final String WORK_B_NAME = "Ranking Notification";

}
