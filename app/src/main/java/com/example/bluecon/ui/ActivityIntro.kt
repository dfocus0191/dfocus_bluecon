package com.example.bluecon.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.bluecon.R
import com.example.bluecon.databinding.ActivityIntroBinding
import com.example.bluecon.defines.Defines
import com.example.bluecon.defines.Globals


class ActivityIntro : AppCompatActivity() {

    // region [내부 변수 부]
    /**
     * 뷰 바인딩 변수
     */
    private val binding: ActivityIntroBinding by lazy {
        ActivityIntroBinding.inflate(layoutInflater)
    }

    /**
     * 컨텍스트 변수
     */
    private lateinit var context: Context
    // endregion

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        context = this

        // 앱 기존 퍼미션 권한 상태 불러오기
        loadPermissionSetting(context)

        // 권한 허용이 전부 되어있는 상태라면
        if (checkPermission()) {
            //  일정시간 후 자동으로 MainActivity 전환
            Handler(Looper.getMainLooper()).postDelayed({
                val intent = Intent(this, ActivityMain::class.java)
                startActivity(intent)
                finish()
            }, Defines.BLUECON_SAMPYO_INTRO_DELAY)
        } else {
            // 하나라도 권한 허용이 되어있지 않다면
            //  일정시간 후 자동으로 PermissionActivity 전환
            Handler(Looper.getMainLooper()).postDelayed({
                val intent = Intent(this, ActivityPermission::class.java)

                startActivity(intent)
                finish()
            }, Defines.BLUECON_SAMPYO_INTRO_DELAY)
        }
    }

    /**
     * 런타임 권한 허용 여부를 확인하는 함수
     * @return : Boolean
     */
    private fun checkPermission(): Boolean {
        return Globals.isDrawPermissionAllowed
                && Globals.isInstallPermissionAllowed
                && Globals.isNotificationPermissionAllowed
                && Globals.isStoragePermissionAllowed
    }

    /**
     * 사용자의 기존 앱 퍼미션 정보를 가져오는 함수
     */
    private fun loadPermissionSetting(context: Context) {
        // 다른앱 위에 그리기 권한
        Globals.isDrawPermissionAllowed = Settings.canDrawOverlays(context)

        // 알수없는 앱 설치 권한
        Globals.isInstallPermissionAllowed =
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && packageManager.canRequestPackageInstalls()
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.O) Globals.isInstallPermissionAllowed = true

        // 알림 권한
        Globals.isNotificationPermissionAllowed = checkNotificationPermission()

        // 파일 접근 권한
        Globals.isStoragePermissionAllowed = checkReadWritePermissions(context)
    }

    /**
     * 알림 권한 허용 여부 확인 함수
     *
     */
    private fun checkNotificationPermission() : Boolean{
        // return 변수
        var result = true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            result = ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        }
        Log.d("Test", "Notification Result =" + result.toString())
        return result
    }

    // 읽기 및 쓰기 권한 확인 함수
    private fun checkReadWritePermissions(context: Context?): Boolean {
        // 읽기 및 쓰기 권한 확인
        val hasReadPermission = ActivityCompat.checkSelfPermission(
            context!!,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
        val hasWritePermission = ActivityCompat.checkSelfPermission(
            context!!,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED

        Log.d("READ", hasReadPermission.toString())
        Log.d("WRITE", hasWritePermission.toString())

        return hasReadPermission && hasWritePermission
    }

    private fun requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this@ActivityIntro,
                Manifest.permission.READ_MEDIA_IMAGES
            )
        ) {
            Toast.makeText(
                this@ActivityIntro, "권한 수락이 필요합니다.",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            ActivityCompat.requestPermissions(
                this@ActivityIntro,
                arrayOf<String>(Manifest.permission.READ_MEDIA_IMAGES), 500
            )
        }
    }

    private fun checkPermission22(): Boolean {
        val result = ContextCompat.checkSelfPermission(
            this@ActivityIntro,
            Manifest.permission.READ_MEDIA_IMAGES
        )
        return if (result == PackageManager.PERMISSION_DENIED) {
            false
        } else {
            true
        }
    }
}