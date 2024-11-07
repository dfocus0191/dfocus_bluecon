package com.example.bluecon.defines;

import android.content.Context;

public class Globals {
    /* 권한 허용 여부 변수 */
    public static Boolean isDrawPermissionAllowed = false;
    public static Boolean isInstallPermissionAllowed = false;
    public static Boolean isNotificationPermissionAllowed = false;
    public static Boolean isStoragePermissionAllowed = false;

    /**
     * 앱에서 전달될 초기 정보
     */
    public static Context g_app_context= null;
    public static String g_app_name = "";                 // 앱 이름
    public static Long g_app_version_code = -1L;        // 버전 코드
    public static String g_app_version_name = "";         // 버전 이름
    public static String g_app_package_name = "";        // 앱 패키지명
    public static String g_server_http_address = "";      // 서버 메인 주소

    /**
     * okHttp3 통신 url
     */
    //public static String g_download_server_version_check_url_header = "http://221.156.216.210:8085/apkVersion";
    public static String g_download_server_version_check_url_header = "http://58.228.245.178:8085/apkVersion";
    //public static String g_download_server_download_url_header = "http://221.156.216.210:8085/apkDownload";
    public static String g_download_server_download_url_header = "http://58.228.245.178:8085/apkDownload";
    public static String g_download_server_url_sub_app_version_check = "";
    public static String g_download_server_url_sub_app_download = "";
}
