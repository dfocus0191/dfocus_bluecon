package com.example.remix.ui

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Window
import android.widget.Button
import com.example.remix.R

class AppFinishDialog(context: Context) {

    lateinit var listener: LessonDeleteDialogClickedListener
    lateinit var btnOk: Button
    lateinit var btnCancel: Button

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

        dlg.setContentView(R.layout.dialog_app_finish)

        // 취소버튼 연결
        btnOk = dlg.findViewById(R.id.btn_ok)
        btnOk.setOnClickListener {
            dlg.dismiss()
            activity.finish()
        }

        //
        btnCancel = dlg.findViewById(R.id.btn_detail_delete_cancel)
        btnCancel.setOnClickListener {
            dlg.dismiss()
        }
        dlg.show()
    }
}