package com.example.remix.ui

import android.graphics.Bitmap
import android.graphics.Rect
import android.os.Bundle
import android.os.Message
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.webkit.ConsoleMessage
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.LinearLayout
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.marginTop
import com.example.remix.Defines.Define
import com.example.remix.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    /* region 내부변수 */

    // 웹 뷰 변수
    private lateinit var webView : WebView

    // 중복 url 방지를 위한 이전 url 저장 변수
    private var previousUrl: String? = null

    // 루트 레이아웃 변수
    private lateinit var rootLayout : LinearLayoutCompat

    // 웹 뷰 뒤로 가기 제어 콜백 변수
    private val callback = object : OnBackPressedCallback(true) {
        // 뒤로가기 버튼을 눌렀을 때
        override fun handleOnBackPressed() {
            if (webView.url.equals(Define.BLUECON_SAMPYO_WEB_HOME_URL) ||
                webView.url.equals(Define.BLUECON_SAMPYO_WEB_URL)){
                    // 앱 종료 팝업 start
                    val appFinishDialog = AppFinishDialog(this@MainActivity)
                    appFinishDialog.start(this@MainActivity)
            }
            else if(webView.canGoBack()){
                webView.goBack();
            }else{
                // 앱 종료 팝업 start
                val appFinishDialog = AppFinishDialog(this@MainActivity)
                appFinishDialog.start(this@MainActivity)
            }
        }
    }

    /* endregion */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 뒤로가기 콜백 인스턴스 적용
        this.onBackPressedDispatcher.addCallback(this, callback)

        // 뷰 연결
        webView = findViewById(R.id.webView)
        //웹 뷰 사용 세팅 함수 start
        setWebView()
    }

    /* 웹 뷰 사용 세팅함수 */
    private fun setWebView() {
        webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                // 페이지가 시작될 때 이전 URL과 현재 URL을 비교하여 중복 여부를 확인
                if (url != null && !url.equals(previousUrl, ignoreCase = true) && !url.equals(previousUrl + "#", ignoreCase = true)) {
                    // 현재 URL과 이전 URL이 다른 경우에만 이전 URL을 업데이트
                    previousUrl = url
                }
            }
        }

        webView.settings.apply {
            javaScriptEnabled = true // 자바 스크립트 사용 여부 true
            setSupportMultipleWindows(true) // 새창 띄우기 true
            javaScriptCanOpenWindowsAutomatically = true // 자바스크립트가 window.open 사용 true
            loadWithOverviewMode = true // 스크린 크기 조절
            useWideViewPort = true // 화면 사이즈 맞추기
            domStorageEnabled = true

            // Root layout
            rootLayout = findViewById<LinearLayoutCompat>(R.id.main)

            // Set a global layout listener
            rootLayout.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    val rect = Rect()
                    rootLayout.getWindowVisibleDisplayFrame(rect)
                    val screenHeight = rootLayout.rootView.height
                    val keypadHeight = screenHeight - rect.bottom

                    if (keypadHeight > screenHeight * 0.15) {
                        // 키보드가 올라왔을 때 처리
                        onKeyboardVisibilityChanged(true)
                    } else {
                        // 키보드가 내려갔을 때 처리
                        onKeyboardVisibilityChanged(false)
                    }
                }
            })

            // 파일 허용
            allowContentAccess = true
            allowFileAccess = true
            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            loadsImagesAutomatically = true
        }

        // 웹 뷰에서 사용할 다이어로그 or 팝업창을 위한 설정
        webView.webChromeClient = object : WebChromeClient() {

            override fun onConsoleMessage(message: ConsoleMessage?): Boolean {
                Log.d("WebView", message?.message() ?: "")
                return true
            }

            override fun onCreateWindow(
                view: WebView?,
                isDialog: Boolean,
                isUserGesture: Boolean,
                resultMsg: Message?
            ): Boolean {
                val newWebView = WebView(this@MainActivity)
                newWebView.settings.javaScriptEnabled = true
                newWebView.webChromeClient = this

                val dialog = AlertDialog.Builder(this@MainActivity)
                    .setView(newWebView)
                    .setPositiveButton("Close") { dialog, _ ->
                        newWebView.destroy()
                        dialog.dismiss()
                    }
                    .create()

                dialog.show()

                val transport = resultMsg?.obj as WebView.WebViewTransport
                transport.webView = newWebView
                resultMsg.sendToTarget()

                return true
            }
        }

        // 웹 뷰 로드
        webView.loadUrl(Define.BLUECON_SAMPYO_WEB_URL)
    }

    /**
     * @param visible : Boolean   true면 키패드 ON false면 키패드 off
     * 키패드 이벤트 설정 함수(임시)
     */
    private fun onKeyboardVisibilityChanged(visible: Boolean) {
        // 키보드 상태에 따라 원하는 작업 수행
        if (visible) {
            CoroutineScope(Dispatchers.Main).launch {
                val params = webView.layoutParams as LinearLayoutCompat.LayoutParams
                // 웹뷰 하단 마진 450dp 고정
                params.bottomMargin = 450
                webView.layoutParams = params
            }
        } else {
            CoroutineScope(Dispatchers.Main).launch {
                val params = webView.layoutParams as LinearLayoutCompat.LayoutParams
                // 웹뷰 하단 마진 0dp 고정
                params.bottomMargin = 0
                webView.layoutParams = params
            }
        }
    }
}

