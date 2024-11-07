package com.example.bluecon.defines;

public class Defines {
    // 웹뷰에 연결할 웹 URL
    //public static final String BLUECON_SAMPYO_WEB_URL = "http://221.156.216.210:8085/";
    public static final String BLUECON_SAMPYO_WEB_URL = "http://58.228.245.178:8085/";
    //public static final String BLUECON_SAMPYO_WEB_URL = "http://localhost:8085/";
    //public static final String BLUECON_SAMPYO_WEB_URL = "http://192.168.0.184:8085/";
    //public static final String BLUECON_SAMPYO_WEB_HOME_URL = "http://221.156.216.210:8085/common/main";
    //public static final String BLUECON_SAMPYO_WEB_HOME_URL = "http://221.156.216.210:8085/common/main";
    public static final String BLUECON_SAMPYO_WEB_HOME_URL = "http://58.228.245.178:8085/common/main";

    // Intro 화면 재생 시간
    public static final long BLUECON_SAMPYO_INTRO_DELAY = 1500L;

    // 모바일 자동 업데이트
    public static final String BLUECON_SAMPYO_ISOAP_ACTION_WS_OSS_MB_UPDATE_NEWS  = "http://221.156.216.210:8085/WS_OSS_MB_UPDATE_NEWS";
    public static final String BLUECON_SAMPYO_IMETHOD_NAME_WS_OSS_MB_UPDATE_NEWS  = "WS_OSS_MB_UPDATE_NEWS";
    public static final String BLUECON_SAMPYO_INAMESPACE = "http://www.unierp.com/";
    public static final Long BLUECON_SAMPYO_APP_UPDATE_CHECK_DELAY = 1000L * 5; // 5분

    /**
     * OKHTTP 통신 Response 타임아웃
     * - 10 초
     */
        public static final Long OK_HTTP_CONNECT_TIMEOUT = 1000 * 10L;
        public static final Long OK_HTTP_READ_TIMEOUT = 1000 * 20L;
        public static final Long OK_HTTP_WRITE_TIMEOUT = 1000 * 20L;

    /**
     * 서버와 통신에 사용되는 앱 고유 코드값
     */
    public static final String OK_HTTP_UNIQUE_APP_CODE = "b1ueC0n&ApqAut0Upd!te_dF0!uS";

    /**
     *  프리퍼런스 키
     *  */
    public static final String BLUECON_PREFERENCE_NAME = "BLUECON_PREFERENCE_NAME";
    public static final String BLUECON_PERMISSION_INSTALL_APK = "BLUECON_PERMISSION_INSTALL_APK";
    public static final String BLUECON_PERMISSION_DRAW = "BLUECON_PERMISSION_DRAW";
    public static final String BLUECON_PERMISSION_NOTIFICATION = "BLUECON_PERMISSION_NOTIFICATION";
    public static final String BLUECON_PERMISSION_STORAGE = "BLUECON_PERMISSION_STORAGE";

    /**
     * OKHTTP 통신 Response 타임아웃
     * - 10 초
     */
        public static final Integer okHttpTConnectTimeout = 1000 * 10;
        public static final Long okHttpReadTimeout = 1000 * 20L;
        public static final Long okHttpWriteTimeout = 1000 * 20L;
}
