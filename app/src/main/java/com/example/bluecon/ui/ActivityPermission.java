package com.example.bluecon.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;


import com.example.bluecon.R;
import com.example.bluecon.databinding.ActivityPermissionBinding;
import com.example.bluecon.defines.Defines;
import com.example.bluecon.defines.Globals;
import com.example.bluecon.utils.DfocusUtils;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

/**
 * 안드로이드 권한을 관리하는 액티비티
 */
public class ActivityPermission extends AppCompatActivity implements View.OnClickListener {

    // region [내부 변수 부]
    /**
     * 뷰 바인딩 변수
     */
    private ActivityPermissionBinding binding;

    /**
     * 앱 콘텍스트
     */
    private Context context;

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private String[] permission33 = new String[] {
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_AUDIO,
            Manifest.permission.READ_MEDIA_VIDEO
    };

    private String[] permission = new String[] {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    // endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // ViewBinding 초기화
        binding = ActivityPermissionBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());

        context = this;

        // 스위치 상태 초기화
        binding.switchSpecialPermissionDraw.setChecked(Globals.isDrawPermissionAllowed);
        binding.switchSpecialPermissionInstall.setChecked(Globals.isInstallPermissionAllowed);
        binding.switchSpecialPermissionNotification.setChecked( Globals.isNotificationPermissionAllowed);
        binding.switchStoragePermission.setChecked(Globals.isStoragePermissionAllowed);

        // 하위 버전에선 퍼미션 활성화 숨기기
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O)
            binding.llUnknownAppsInstall.setVisibility(View.GONE);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU)
            binding.llNotification.setVisibility(View.GONE);

        binding.switchSpecialPermissionDraw.setOnClickListener(this);
        binding.switchSpecialPermissionInstall.setOnClickListener(this);
        binding.switchSpecialPermissionNotification.setOnClickListener(this);
        binding.switchStoragePermission.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(Settings.canDrawOverlays(context)) {
            // Preference에 저장
            DfocusUtils.Companion.saveBoolean(context, Defines.BLUECON_PREFERENCE_NAME, Defines.BLUECON_PERMISSION_DRAW, true);
            binding.switchSpecialPermissionDraw.setChecked(true);
            binding.switchSpecialPermissionDraw.setTrackTintList(
                    ColorStateList.valueOf(Color.parseColor("#2196F3"))
            );
            binding.switchSpecialPermissionDraw.setThumbTintList(
                    ColorStateList.valueOf(Color.parseColor("#ffffff"))
            );
            Globals.isDrawPermissionAllowed = true;
        }
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && getPackageManager().canRequestPackageInstalls()){
            // Preference에 저장
            DfocusUtils.Companion.saveBoolean(context, Defines.BLUECON_PREFERENCE_NAME, Defines.BLUECON_PERMISSION_INSTALL_APK, true);
            binding.switchSpecialPermissionInstall.setChecked(true);
            binding.switchSpecialPermissionInstall.setTrackTintList(
                    ColorStateList.valueOf(Color.parseColor("#2196F3"))
            );
            binding.switchSpecialPermissionInstall.setThumbTintList(
                    ColorStateList.valueOf(Color.parseColor("#ffffff"))
            );
            Globals.isInstallPermissionAllowed = true;
        }
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU
                || ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        ){
            // Preference에 저장
            DfocusUtils.Companion.saveBoolean(context, Defines.BLUECON_PREFERENCE_NAME, Defines.BLUECON_PERMISSION_NOTIFICATION, true);
            binding.switchSpecialPermissionNotification.setChecked(true);
            binding.switchSpecialPermissionNotification.setTrackTintList(
                    ColorStateList.valueOf(Color.parseColor("#2196F3"))
            );
            binding.switchSpecialPermissionNotification.setThumbTintList(
                    ColorStateList.valueOf(Color.parseColor("#ffffff"))
            );
            Globals.isNotificationPermissionAllowed = true;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            System.out.println(checkSelfPermission(Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED);

            if ((checkSelfPermission(Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED) ) {
                // Preference에 저장
                DfocusUtils.Companion.saveBoolean(context, Defines.BLUECON_PREFERENCE_NAME, Defines.BLUECON_PERMISSION_STORAGE, true);
                binding.switchStoragePermission.setChecked(true);
                binding.switchStoragePermission.setTrackTintList(
                        ColorStateList.valueOf(Color.parseColor("#2196F3"))
                );
                binding.switchStoragePermission.setThumbTintList(
                        ColorStateList.valueOf(Color.parseColor("#ffffff"))
                );
                Globals.isStoragePermissionAllowed = true;
            }
        } else {
            if (!(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED  ||
                    checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED )) {
                // Preference에 저장
                DfocusUtils.Companion.saveBoolean(context, Defines.BLUECON_PREFERENCE_NAME, Defines.BLUECON_PERMISSION_STORAGE, true);
                binding.switchStoragePermission.setChecked(true);
                binding.switchStoragePermission.setTrackTintList(
                        ColorStateList.valueOf(Color.parseColor("#2196F3"))
                );
                binding.switchStoragePermission.setThumbTintList(
                        ColorStateList.valueOf(Color.parseColor("#ffffff"))
                );
                Globals.isStoragePermissionAllowed = true;
            }
        }

        Log.d("Globals.isDrawPermissionAllowed", Globals.isDrawPermissionAllowed.toString());
        Log.d("Globals.isInstallPermissionAllowed", Globals.isInstallPermissionAllowed.toString());
        Log.d("Globals.isNotificationPermissionAllowed", Globals.isNotificationPermissionAllowed.toString());
        Log.d("Globals.isDrawPermissionAllowed", Globals.isStoragePermissionAllowed.toString());

        // 권한이 모두 허용되었다면 App Start
        if(Globals.isDrawPermissionAllowed && Globals.isInstallPermissionAllowed && Globals.isNotificationPermissionAllowed && Globals.isStoragePermissionAllowed)
            startApp();
    }

    @Override
    public void onClick(View view) {
        // 다른앱 그리기 권한 스위치 클릭 시
        if(view == binding.switchSpecialPermissionDraw) {
            // 권한 허용이 안된 상태라면
            if (binding.switchSpecialPermissionDraw.isChecked()) {
                binding.switchSpecialPermissionDraw.setChecked(false);
                checkOverlayPermission();

            } else {
                Toast.makeText(context, getString(R.string.text_permission_overlay_already_activityed_msg), Toast.LENGTH_SHORT).show();
                binding.switchSpecialPermissionDraw.setChecked(true);
            }
        }

        // 알수없는 앱 설치 권한
        if(view == binding.switchSpecialPermissionInstall) {
            // 권한 허용이 안된 상태라면
            if (binding.switchSpecialPermissionInstall.isChecked()) {
                binding.switchSpecialPermissionInstall.setChecked(false);
                checkUnknownAppsPermission();

            } else {
                Toast.makeText(context, getString(R.string.text_permission_unknown_install_already_activityed_msg), Toast.LENGTH_SHORT).show();
                binding.switchSpecialPermissionInstall.setChecked(true);
            }
        }

        // 앱 알림 권한
        if(view == binding.switchSpecialPermissionNotification) {
            // 권한 허용이 안된 상태라면
            if (binding.switchSpecialPermissionNotification.isChecked()) {
                binding.switchSpecialPermissionNotification.setChecked(false);
                checkNotificationPermission();

            } else {
                Toast.makeText(context, getString(R.string.text_permission_notification_already_activityed_msg), Toast.LENGTH_SHORT).show();
                binding.switchSpecialPermissionNotification.setChecked(true);
            }
        }

        if(view == binding.switchStoragePermission) {
            if (binding.switchStoragePermission.isChecked()) {
                binding.switchStoragePermission.setChecked(false);
                checkStoragePermission();
            } else {
                Toast.makeText(context, getString(R.string.text_permission_storage_already_activityed_msg), Toast.LENGTH_SHORT).show();
                binding.switchStoragePermission.setChecked(true);
            }
        }
    }

    /**
     * 다른 앱 위에 그리기 활성화
     */
    private void checkOverlayPermission() {
        if (!Settings.canDrawOverlays(context)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getApplicationInfo().packageName));
            startActivity(intent);
        }
    }

    /**
     * 알 수 없는 앱 설치 활성화
     */
    private void checkUnknownAppsPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !getPackageManager().canRequestPackageInstalls()) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, Uri.parse("package:" + getApplicationInfo().packageName));
            startActivity(intent);
        }
    }

    /**
     * 저장소 일기, 쓰기 접근권한 활성화
     */
    private void checkStoragePermission() {
        Log.d("함수", "checkStoragePermission");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED  ||
                    checkSelfPermission(Manifest.permission.READ_MEDIA_AUDIO) != PackageManager.PERMISSION_GRANTED  ||
                    checkSelfPermission(Manifest.permission.READ_MEDIA_VIDEO) != PackageManager.PERMISSION_GRANTED ) {
                requestPermissions(permission33, 100);
            }
        } else {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED  ||
                    checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ) {
                requestPermissions(permission, 101);
            }
        }

//        // 읽기 및 쓰기 권한 확인
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
//                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//
//            Log.d("함수", "checkStoragePermission2");
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//                if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 2);
//                }
//            }
//        }
    }

    /**
     * 앱 처음 액티비티로 이동 및 현재 액티비티 종료
     */
    private void startApp() {
        // 앱 처음 액티비티로 이동
        Intent intent = new Intent(context, ActivityMain.class);
        startActivity(intent);

        // 현재 액티비티 종료
        finish();
    }

    private void checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Dexter.withContext(this)
                    .withPermission(Manifest.permission.POST_NOTIFICATIONS)
                    .withListener(new PermissionListener() {
                        @Override
                        public void onPermissionGranted(PermissionGrantedResponse response) {
                            // 권한이 허용된 상태
                            Log.d("Test", "알림권한 허용 완료상태");
                        }

                        @Override
                        public void onPermissionDenied(PermissionDeniedResponse response) {
                            // 권한이 허용되지 않은 상태
                            Log.d("Test", "알림권한 허용 안된상태");
                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                            // 권한 요청 이유를 설명해야 하는 경우
                            token.continuePermissionRequest();
                        }
                    })
                    .check();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            switch (requestCode) {
                case 100:
                    Toast.makeText(this, "33 이상에서 쓰이는 권한 허용됨", Toast.LENGTH_SHORT).show();
                    break;
                case 101:
                    Toast.makeText(this, "33 이전에서 쓰이는 권한 허용됨", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }
}