package com.example.bluecon.ui

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Environment
import android.view.Window
import android.widget.Button
import com.example.bluecon.R
import com.example.bluecon.service.UpdateService
import java.io.File

class AppUpdateDialog(context: Context) {
    lateinit var listener: LessonDeleteDialogClickedListener
    lateinit var btnOk: Button
    lateinit var btnCancel: Button
    var mContext = context

    interface LessonDeleteDialogClickedListener {
        fun onDeleteClicked()
    }

    private val dlg = Dialog(context)

    /**
     * @param activity : Activity
     * 앱 종료 안내 다이어로그를 실행시키는 함수
     */
    fun start(activity: Activity) {
        // 타이틀바 제거
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE)
        // 배경 투명
        dlg.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dlg.setContentView(R.layout.dialog_app_update)

        // 취소버튼 연결
        btnOk = dlg.findViewById(R.id.btn_ok)
        btnOk.setOnClickListener {
            // 파일을 저장할 위치
            val downloadDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val file = File(downloadDir, "Bluecon.apk")
            UpdateService.downloadUpdatedApkFile(mContext, file)
        }

        //
        btnCancel = dlg.findViewById(R.id.btn_detail_delete_cancel)
        btnCancel.setOnClickListener {
            dlg.dismiss()
        }
        dlg.show()
    }
}