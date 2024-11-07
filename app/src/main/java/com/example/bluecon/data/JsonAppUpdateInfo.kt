package com.example.bluecon.data

/**
 * JsonAppUpdateInfo 클래스
 * - 서버에서 수신받는 서버의 앱 정보
 */
data class JsonAppUpdateInfo(
    var idx: Int,                   // DB IDX
    var app_version_code: Int,      // 앱 버전 코드
    var app_version_name: String,   // 앱 버전 이름
    var app_package_name: String,   // 앱 패키지명
    var app_name: String,           // 앰 이름
    var apk_file_name: String,      // 등록된 앱 파일 명
    var apk_file_size: Long,        // 등록된 앱 파일 사이즈
    var apk_file_md5: String,       // 등록된 앱 파일 MD5
    var download_hash: String,      // 다운로드를 할 수 있는 헤시코드
    var update_date: String         // 등록 또는 수정일시
)