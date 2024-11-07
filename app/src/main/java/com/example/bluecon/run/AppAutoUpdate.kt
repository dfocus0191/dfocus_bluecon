package com.example.bluecon.run

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.content.pm.PackageInfoCompat
import com.example.bluecon.R
import com.example.bluecon.defines.Defines
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

/**
 * AppAutoUpdate 클래스
 * - 앱 업데이트를 총괄하는 클래스
 *
 * @author Changoh.noh by Kns1
 * @since 2023
 */
class AppAutoUpdate {
    companion object {
        /**
         * 업데이트 버전 존재 확인 하는 함수
         */
        fun hasNewUpdateCheck(context: Context) {
            CoroutineScope(Dispatchers.IO + Job()).launch {

                val myAppVersionInfo = appVersionInfo(context)
                if (myAppVersionInfo != null) {

                    // OkHttp Client 생성
                    val okHttpClient = OkHttpClient.Builder()
                        .connectTimeout(Defines.okHttpTConnectTimeout.toLong(), TimeUnit.MILLISECONDS)
                        .readTimeout(Defines.okHttpReadTimeout, TimeUnit.MILLISECONDS)
                        .writeTimeout(Defines.okHttpWriteTimeout, TimeUnit.MILLISECONDS)
                        .build()

                    // OkHttp request 요청 생성
                    val request = Request.Builder()
                        .url("https://")
                        .post(FormBody.Builder().apply {
                            add("app_code", "TEST테스트입니다.")
                        }.build()).build()

                    try {
                        // OkHttp 요청에 대한 응답 response 처리
                        val response = okHttpClient.newCall(request).execute()
                        if (response.isSuccessful) {

                            val resStr = response.body?.string()
                            Log.d("응답", resStr!!);
//                            // JSON 유효성 검사
//                            if (NJson.isValidJson(resStr)) {
//
//                                // 객체로 변환
//                                val res = Gson().fromJson(resStr, ResAppVersionCheck::class.java)
//                                if (res != null) {
//
//                                    if (res.RESULT == ResDefault.result_success) {
//                                        when (res.CODE) {
//                                            10 -> {
//                                                // 성공
//                                                if (res.APP_UPDATE_INFO != null) {
//                                                    if (myAppVersionInfo.versionCode.toInt() < res.APP_UPDATE_INFO!!.app_version_code) {
//                                                        downloadUpdatedApkFile(context, res.APP_UPDATE_INFO!!)
//                                                    }
//                                                }
//                                            }
//
//                                            else -> {
//                                                Log.i(context.getString(R.string.app_name), context.getString(R.string.text_http_success_message_code_20))
//                                            }
//                                        }
//
//                                    } else if (res.RESULT == ResDefault.result_failed) {
//                                        when (res.CODE) {
//                                            ResDefault.code_input_check, 1 -> {
//                                                // 파라미터의 일부가 비어 있음
//                                                Log.i(context.getString(R.string.app_name), context.getString(R.string.text_http_failed_message_code_1))
//                                            }
//
//                                            2 -> {
//                                                // 앱 코드가 일치하지 않음
//                                                Log.i(context.getString(R.string.app_name), context.getString(R.string.text_http_failed_message_code_2))
//                                            }
//                                        }
//                                    }
//                                }
//                            }

                        } else {
                            // 통신 에러 알림
                            Log.i(context.getString(R.string.app_name), context.getString(R.string.text_http_error))
                        }

                    } catch (ex: Exception) {
                        ex.printStackTrace()
                    }
                }
            }
        }

        /**
         * 앱 버전 정보
         */
        data class AppVersionInfo(var versionCode: Long, var versionName: String)

        fun appVersionInfo(context: Context): AppVersionInfo? {
            var versionInfo: AppVersionInfo? = null

            try {
                val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)

                val versionName: String = packageInfo.versionName

                val versionCode: Long = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    PackageInfoCompat.getLongVersionCode(packageInfo)
                } else {
                    packageInfo.versionCode.toLong()
                }

                // 버전정보
                versionInfo = AppVersionInfo(versionCode, versionName)

            } catch (ex: PackageManager.NameNotFoundException) {
                ex.printStackTrace()
            }

            return versionInfo
        }
    }
}