package com.example.bluecon.service;

import static androidx.core.app.ActivityCompat.requestPermissions;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.content.pm.PackageInfoCompat;

import com.example.bluecon.data.AppVersionInfo;
import com.example.bluecon.R;
import com.example.bluecon.defines.Defines;
import com.example.bluecon.defines.Globals;
import com.example.bluecon.security.Cipher;
import com.example.bluecon.ui.AppUpdateDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.*;

public class UpdateService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // 서비스를 포어그라운드로 등록
        runForegroundMode();
        Runnable logRunnable;
        // Initialize the handler and runnable for logging
        Handler handler = new Handler();
        logRunnable = new Runnable() {
            @Override
            public void run() {
                //handler.postDelayed(this, Defines.BLUECON_SAMPYO_APP_UPDATE_CHECK_DELAY); // 5분에 한번씩 반복
            }
        };

        // Start the periodic logging
        handler.post(logRunnable);

        return START_STICKY; // or other appropriate return value
    }

    /**
     * Foreground 서비스 시작 함수
     */
    @SuppressLint("ForegroundServiceType")
    public void runForegroundMode() {
        try {
            String notificationChannelID = getString(R.string.text_service_notification_channel_id);
            int notificationID = 101;

            // 오레오버전 이상이면 알림 채널 설정
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                String name = getString(R.string.text_service_notification_channel_name);
                String descriptionText = getString(R.string.text_service_notification_channel_description);
                int importance = NotificationManager.IMPORTANCE_DEFAULT;

                // 알림 채널 생성 및 채널 세부사항 설정
                NotificationChannel channel = new NotificationChannel(notificationChannelID, name, importance);
                channel.setDescription(descriptionText);
                channel.setLightColor(Color.RED);
                channel.enableLights(true);
                channel.enableVibration(true);
                channel.setVibrationPattern(new long[]{100, 200, 100, 200});

                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.createNotificationChannel(channel);
            }

            Notification notification = new NotificationCompat.Builder(this, notificationChannelID)
                    .setContentTitle(getString(R.string.text_service_notification_title))
                    .setContentText(getString(R.string.text_service_notification_contents))
                    .setSmallIcon(
                            R.drawable.dfocus_logo
                    )
                    .setOnlyAlertOnce(true)
                    .setAutoCancel(true)
                    .build();

            startForeground(notificationID, notification);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 포어그라운드 서비스를 종료하는 함수
     */
    public static void stopForegroundMode(Context context) {
        try {
            Intent stopIntent = new Intent(context, UpdateService.class);
            context.stopService(stopIntent); // 서비스 중단 요청
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 안드로이드 버전과 서버에 업로드된 apk 버전을 비교하는 함수
     * @param context
     */
    public static void hasNewVesionCheck(final Context context, final Activity activity) {
        System.out.println("암호화 전 : " + Globals.g_app_version_code.toString());
        System.out.println("암호화 후 : " + Cipher.Encode( Globals.g_app_version_code.toString()));
        System.out.println("복호화 후 : " + Cipher.Decode(Cipher.Encode( Globals.g_app_version_code.toString())));

        Log.d("함수", "hasNewVesionCheck");
        AppVersionInfo myAppVersionInfo = appVersionInfo(context);
        if (myAppVersionInfo != null) {
            // OkHttpClient 생성
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(Defines.OK_HTTP_CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
                    .readTimeout(Defines.OK_HTTP_READ_TIMEOUT, TimeUnit.MILLISECONDS)
                    .writeTimeout(Defines.OK_HTTP_WRITE_TIMEOUT, TimeUnit.MILLISECONDS)
                    .build();

            // OkHttp request 요청 생성
            FormBody.Builder formBodyBuilder = new FormBody.Builder();
            formBodyBuilder.add("app_code", Defines.OK_HTTP_UNIQUE_APP_CODE)
                    .add("fileVersion", String.valueOf(Globals.g_app_version_code))
                    .add("app_package_name", Globals.g_app_package_name);

            Request request = new Request.Builder()
                    .url(Globals.g_download_server_version_check_url_header  + "?fileVersion=" + Globals.g_app_version_code)
                    .get()
                    .build();

            try {
                // OkHttp 요청에 대한 응답 response 처리
                okHttpClient.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        Log.i(context.getString(R.string.app_name), context.getString(R.string.text_http_error));
                        Log.i(context.getString(R.string.app_name), "응답도");
                        e.printStackTrace();
                        stopForegroundMode(context);
                    }
                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        if (response.isSuccessful()) {
                            try {
                                // 응답 본문을 문자열로 가져오기
                                String responseBody = response.body().string();
                                Log.d("응답", responseBody);

                                if (response.isSuccessful()) {
                                    try {
                                        // JSON 객체로 파싱
                                        JSONObject jsonResponse = new JSONObject(responseBody);

                                        // "status" 값 추출
                                        String EncodedNetVersion = jsonResponse.getString("status");
                                        int netVersion = Integer.parseInt(Cipher.Decode(EncodedNetVersion)) ;
                                        String netApkName = jsonResponse.getString("apkName");
                                        Log.d("netApkName", netApkName);
                                        Log.d("netVersion", String.valueOf(netVersion));

                                        // 서버에 있는 APK 버전이 더 높다면
                                        if(netVersion > myAppVersionInfo.getVersionCode()) {
                                            // APK 다운로드 로직 실행
                                            hasNewUpdateCheck(context, activity);
                                        }else {
                                             stopForegroundMode(context);
                                        }
                                    } catch (JSONException e) {
                                        Log.e("JSON 파싱 오류", e.getMessage());
                                         stopForegroundMode(context);
                                    }
                                } else {
                                    Log.e("응답 오류", "응답 실패: " + response.message());
                                    stopForegroundMode(context);
                                }
                            } catch (Exception e) {
                                stopForegroundMode(context);
                                throw new RuntimeException(e);
                            }
                        } else {
                            Log.i(context.getString(R.string.app_name), context.getString(R.string.text_http_error));
                             stopForegroundMode(context);
                        }
                    }
                });
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void hasNewUpdateCheck(final Context context, final Activity activity) {
        {

            AppVersionInfo myAppVersionInfo = appVersionInfo(context);
            if (myAppVersionInfo != null) {
                Log.d("MY", String.valueOf(myAppVersionInfo.getVersionCode()));
                // OkHttpClient 생성
                OkHttpClient okHttpClient = new OkHttpClient.Builder()
                        .connectTimeout(Defines.OK_HTTP_CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
                        .readTimeout(Defines.OK_HTTP_READ_TIMEOUT, TimeUnit.MILLISECONDS)
                        .writeTimeout(Defines.OK_HTTP_WRITE_TIMEOUT, TimeUnit.MILLISECONDS)
                        .build();

                // OkHttp request 요청 생성
                FormBody.Builder formBodyBuilder = new FormBody.Builder();
                formBodyBuilder.add("app_code", Defines.OK_HTTP_UNIQUE_APP_CODE)
                        .add("app_version_code", String.valueOf(Globals.g_app_version_code))
                        .add("app_package_name", Globals.g_app_package_name)
                        .add("fileVersion", String.valueOf(Globals.g_app_version_code));

//                Request request = new Request.Builder()
//                        .url(Globals.g_download_server_url_header) // 수정 필요
//                        .post(formBodyBuilder.build())
//                        .build();

//                Request request = new Request.Builder()
//                        .url(Globals.g_download_server_url_header) // 수정 필요
//                        .get()
//                        .build();

                Request request = new Request.Builder()
                        .url(Globals.g_download_server_download_url_header + "?fileVersion=" + Globals.g_app_version_code) // fileVersion 추가
                        .get()
                        .build();

                try {
                    // OkHttp 요청에 대한 응답 response 처리
                    okHttpClient.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(@NonNull Call call, @NonNull IOException e) {
                            Log.i(context.getString(R.string.app_name), context.getString(R.string.text_http_error));
                            Log.i(context.getString(R.string.app_name), "응답도");
                            e.printStackTrace();
                        }
                        @Override
                        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                            if (response.isSuccessful()) {
                                Log.d("헤더", Objects.requireNonNull(response.headers().get("Content-Disposition")));
                                Log.d("헤더", Objects.requireNonNull(response.headers().get("X-MD5")));
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                    if(ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                                        requestPermissions(activity,new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 2);
                                    }
                                }
                                // 파일을 저장할 위치
                                File downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                                File file = new File(downloadDir, "Bluecon.apk");

                                // 파일이 이미 존재하면 삭제
                                if (file.exists()) {
                                    if(file.isDirectory()) {
                                        Log.d("파일 체크", "파일이 디렉토리입니다.");
                                    }
                                    System.out.println("파일 이름 : " +file.getName());
                                    boolean deleted = file.delete();
                                    if (deleted) {
                                        Log.d("파일 삭제", "기존 파일이 삭제되었습니다: " + file.getAbsolutePath());
                                    } else {
                                        Log.e("파일 삭제 오류", "파일 삭제 실패: " + file.getAbsolutePath());
                                         stopForegroundMode(context);
                                    }
                                }

//                                // 암호화된 내용을 파일에 저장
//                                String encryptedContent = response.body().string();
//
//                                Log.d("복호화 전", encryptedContent.substring(0,30));
//                                Log.d("복호화 전", encryptedContent.substring(encryptedContent.length() - 30));
//
//                                // 복호화
//                                String decryptedContent = Cipher.Decode(encryptedContent); // 가정: Cipher 클래스에 Decode 메서드가 있음
//                                Log.d("복호화 후", decryptedContent.substring(0,30));
//                                Log.d("복호화 후", decryptedContent.substring(decryptedContent.length() - 30));
//
//                                try (FileOutputStream fos = new FileOutputStream(file)) {
//                                    fos.write(decryptedContent.getBytes(StandardCharsets.UTF_8));
//                                    Log.d("파일 저장", "암호화된 파일이 저장되었습니다: " + file.getAbsolutePath());
//                                } catch (IOException e) {
//                                    Log.e("파일 저장 오류", "파일 저장 실패: " + e.getMessage());
//                                }

                                 //파일 쓰기
                                try (InputStream inputStream = response.body().byteStream();
                                     OutputStream outputStream = new FileOutputStream(file)) {

                                    byte[] buffer = new byte[4096];
                                    int bytesRead;
                                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                                        outputStream.write(buffer, 0, bytesRead);
                                    }

                                    // 파일 무결성 체크
                                    if(!calculateMD5(file).equals(Objects.requireNonNull(response.headers().get("X-MD5")))) {
                                        return;
                                    }

                                    // 앱 업데이트 팝업 Start
                                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                                        @Override
                                        public void run() {
                                            // 여기서 UI 업데이트 작업 수행
                                            showAppUpdateDialog(context, activity);
                                        }
                                    });

                                } catch (IOException e) {
                                    Log.e("다운로드 오류", "파일 저장 실패", e);
                                    stopForegroundMode(context);
                                } catch (NoSuchAlgorithmException e) {
                                    throw new RuntimeException(e);
                                    //이야이햐호
                                }

                            } else {
                                Log.i(context.getString(R.string.app_name), context.getString(R.string.text_http_error));
                                stopForegroundMode(context);
                            }
                        }
                    });
                } catch (Exception ex) {
                    stopForegroundMode(context);
                    ex.printStackTrace();
                }
            }
        }
    }

    /**
     * 앱 버전 정보를 불러오는 함수
     * @param context
     * @return
     */
    public static AppVersionInfo appVersionInfo(Context context) {
        AppVersionInfo versionInfo = null;

        try {
            // 패키지매니저에서 앱 버전이름을 불러옴
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);

            String versionName = packageInfo.versionName;

            // 앱 버전 코드를 불러옴
            long versionCode;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                versionCode = PackageInfoCompat.getLongVersionCode(packageInfo);
            } else {
                versionCode = packageInfo.versionCode;
            }

            versionInfo = new AppVersionInfo(versionCode, versionName);

        } catch (PackageManager.NameNotFoundException ex) {
            stopForegroundMode(context);
            ex.printStackTrace();
        }

        return versionInfo;
    }

    public static void downloadUpdatedApkFile(Context context, File apkFile) {
        if (apkFile.exists()) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri apkUri;

            // Android 7.0 이상에서는 FileProvider를 사용해야 합니다.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                apkUri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", apkFile);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } else {
                apkUri = Uri.fromFile(apkFile);
            }

            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } else {
            stopForegroundMode(context);
            Log.e("APK 설치 오류", "APK 파일이 존재하지 않습니다: " + apkFile.getAbsolutePath());
        }
    }

    public static void showAppUpdateDialog(Context context, Activity activity) {
        AppUpdateDialog appUpdateDialog = new AppUpdateDialog(context);
        appUpdateDialog.start(activity);
    }

    public static String calculateMD5(File file) throws IOException, NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        try {
            FileInputStream fis = new FileInputStream(file);
            byte[] dataBytes = new byte[1024];
            int bytesRead;

            while ((bytesRead = fis.read(dataBytes)) != -1) {
                md.update(dataBytes, 0, bytesRead);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }

        // MD5 해시를 바이트 배열로 가져오기
        byte[] digestBytes = md.digest();

        // 바이트 배열을 16진수 문자열로 변환
        StringBuilder sb = new StringBuilder();
        for (byte b : digestBytes) {
            sb.append(String.format("%02x", b));
        }

        return sb.toString();
    }
}
