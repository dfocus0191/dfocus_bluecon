package com.example.bluecon.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.bluecon.defines.Defines;
import com.example.bluecon.R;
import com.example.bluecon.service.UpdateService;

public class ActivityMain extends AppCompatActivity {
    // region 내부변수
    // 웹 뷰 변수
    private WebView webView;

    // 중복 url 방지를 위한 이전 url 저장 변수
    private String previousUrl = null;

    // 루트 레이아웃 변수
    private LinearLayoutCompat rootLayout;

    // 에러코드 변수
    String MENU_ERROR = "0";
    String UPDATE_ERROR = "0";

    // 프로그레스 바 변수
    private ProgressBar mProgBar;

    // 다이어로그 변수
    private AlertDialog Mdialog;

    // 앱 콘텍스트 변수
    private Context context;
    private Activity activity;

    // 테스트 버튼 변수
    private Button btnTest;

    // 웹 뷰 뒤로 가기 제어 콜백 변수
    private final OnBackPressedCallback callback = new OnBackPressedCallback(true) {
        @Override
        public void handleOnBackPressed() {
            if (webView.getUrl().equals(Defines.BLUECON_SAMPYO_WEB_HOME_URL) ||
                    webView.getUrl().equals(Defines.BLUECON_SAMPYO_WEB_URL)) {
                // 앱 종료 팝업 start
                AppFinishDialog appFinishDialog = new AppFinishDialog(ActivityMain.this);
                appFinishDialog.start(ActivityMain.this);
            } else if (webView.canGoBack()) {
                webView.goBack();
            } else {
                // 앱 종료 팝업 start
                AppFinishDialog appFinishDialog = new AppFinishDialog(ActivityMain.this);
                appFinishDialog.start(ActivityMain.this);
            }
        }
    };
    // endregion

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        context = this;
        activity = this;

        // 뒤로가기 콜백 인스턴스 적용
        this.getOnBackPressedDispatcher().addCallback(this, callback);

        // 뷰 연결
        webView = findViewById(R.id.webView);
        // 웹 뷰 사용 세팅 함수 start
        setWebView();
        // 앱 업데이트 확인 서비스 시작
        Log.d("Test", "startAppAutoUpdaterService 실행 전");
        startAppAutoUpdaterService();
        Log.d("Test", "startAppAutoUpdaterService 실행 후");

        //테스트버튼
        btnTest = findViewById(R.id.btnTest);

        btnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UpdateService.hasNewVesionCheck(context, activity);
                // 파일을 저장할 위치
//                File downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
//                File file = new File(downloadDir, "Bluecon_v1.0.apk");
//                UpdateService.downloadUpdatedApkFile(context, file);
            }
        });
    }

    // 웹 뷰 사용 세팅함수
    private void setWebView() {
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                // 페이지가 시작될 때 이전 URL과 현재 URL을 비교하여 중복 여부를 확인
                if (url != null && !url.equals(previousUrl) && !url.equals(previousUrl + "#")) {
                    // 현재 URL과 이전 URL이 다른 경우에만 이전 URL을 업데이트
                    previousUrl = url;
                }
            }
        });

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true); // 자바 스크립트 사용 여부 true
        webSettings.setSupportMultipleWindows(true); // 새창 띄우기 true
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true); // 자바스크립트가 window.open 사용 true
        webSettings.setLoadWithOverviewMode(true); // 스크린 크기 조절
        webSettings.setUseWideViewPort(true); // 화면 사이즈 맞추기
        webSettings.setDomStorageEnabled(true);

        // Root layout
        rootLayout = findViewById(R.id.main);

        // Set a global layout listener
        rootLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect rect = new Rect();
                rootLayout.getWindowVisibleDisplayFrame(rect);
                int screenHeight = rootLayout.getRootView().getHeight();
                int keypadHeight = screenHeight - rect.bottom;

                if (keypadHeight > screenHeight * 0.15) {
                    // 키보드가 올라왔을 때 처리
                    onKeyboardVisibilityChanged(true);
                } else {
                    // 키보드가 내려갔을 때 처리
                    onKeyboardVisibilityChanged(false);
                }
            }
        });

        // 파일 허용
        webSettings.setAllowContentAccess(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        webSettings.setLoadsImagesAutomatically(true);

        // 웹 뷰에서 사용할 다이어로그 or 팝업창을 위한 설정
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onConsoleMessage(ConsoleMessage message) {
                Log.d("WebView", message != null ? message.message() : "");
                return true;
            }

            @Override
            public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
                WebView newWebView = new WebView(ActivityMain.this);
                newWebView.getSettings().setJavaScriptEnabled(true);
                newWebView.setWebChromeClient(this);

                AlertDialog dialog = new AlertDialog.Builder(ActivityMain.this)
                        .setView(newWebView)
                        .setPositiveButton("Close", (dialogInterface, i) -> {
                            newWebView.destroy();
                            dialogInterface.dismiss();
                        })
                        .create();

                dialog.show();

                WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
                transport.setWebView(newWebView);
                resultMsg.sendToTarget();

                return true;
            }
        });

        // 웹 뷰 로드
        webView.loadUrl(Defines.BLUECON_SAMPYO_WEB_URL);
    }

    /**
     * 키보드가 보여질 때 웹뷰 위치를 조정하는 함수
     * @param visible : Boolean 키보드 보여짐 상태
     */
    private void onKeyboardVisibilityChanged(boolean visible) {
        // 키보드 상태에 따라 원하는 작업 수행
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
            LinearLayoutCompat.LayoutParams params = (LinearLayoutCompat.LayoutParams) webView.getLayoutParams();
            if (visible) {
                // 웹뷰 하단 마진 450dp 고정
                params.bottomMargin = 450;
            } else {
                // 웹뷰 하단 마진 0dp 고정
                params.bottomMargin = 0;
            }
            webView.setLayoutParams(params);
        });
    }

    /**
     * 업데이트 확인 함수
     */
    private void CheckUpdate() {
        try {
            Update_CheckClient();
        } catch (Exception e) {
            MENU_ERROR = "2";
        }
    }

    private void Update_CheckClient() {
        //버전을 업데이트 하고서는 다른데서 수행하도록 한다.
        AsyncCallWS task = new ActivityMain.AsyncCallWS();
        task.execute();
    }

    /**
     *  웹에서 apk 정보를 수신하는 함수
     *  Soap를 통해 통신 -> 크롤링이나 http 통신으로 변경 필요
     */
    private void WebServicePost() {
        // 1. 업데이트 Ver정보 서버로 부터 수신
//        try {
//            SoapObject Request = new SoapObject(NAMESPACE, METHOD_NAME_WS_OSS_MB_UPDATE_NEWS);
//            //Soap 버전및 .donet 지원여부
//            SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
//            soapEnvelope.dotNet = true;
//            soapEnvelope.setOutputSoapObject(Request);
//            HttpTransportSE aht = new HttpTransportSE(URL);
//            aht.call(SOAP_ACTION_WS_OSS_MB_UPDATE_NEWS, soapEnvelope);
//            SoapObject result  = (SoapObject) soapEnvelope.bodyIn;
//            SoapObject nameResult = (SoapObject) result.getProperty(0);
//            SoapObject nameResult2 = (SoapObject) nameResult.getProperty(1);
//            SoapObject nameResult3 = (SoapObject) nameResult2.getProperty(0);
//            int count = nameResult3.getPropertyCount();
//            for (int i = 0; i <= count - 1; i++) {
//                SoapObject simpleSuggestion = (SoapObject) nameResult3.getProperty(i);
//                String U_TITLE = simpleSuggestion.getProperty("U_TITLE").toString(); //제목
//                if ( U_TITLE.equals("anyType{}")) { U_TITLE = ""; 	}
//                UpdateMessage_Title = U_TITLE;
//                String U_CONTENT = simpleSuggestion.getProperty("U_CONTENT").toString(); //내용
//                if ( U_CONTENT.equals("anyType{}")) { U_CONTENT = ""; 	}
//                UpdateMessage_Content = U_CONTENT;
//                String U_VER = simpleSuggestion.getProperty("U_VER").toString(); //버전
//                if ( U_VER.equals("anyType{}")) { U_VER = ""; 	}
//                UpdateMessage_Ver = U_VER;
//            }
//        } catch (Exception e) {
//            UPDATE_ERROR = "2";
//        }
    }

    /**
     * APK 파일을 설치하는 함수
     */
    private void InstallApkPost() {
//        try
//        {
//            //접속
//            java.net.URL url = new URL("http://smartphone.sampyo.co.kr/MobileOfficeDownLoad/ossremixapp.apk"); //다운받을 웹경로
//
//            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
//            urlConnection.connect();
//            File SDCardRoot = Environment.getExternalStorageDirectory();
//
//
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                path1 = new File(String.valueOf(MainActivity.this.getFilesDir()));
//            }else {
//                path1 = new File("/"+SDCardRoot+"/OSS_REMIX/FileDownLoad");
//            }
//
//            //내부 메모리 생성(SD카드가 없을 시)
//            if(! path1.isDirectory()) {
//                path1.mkdirs();
//            }
//
//            File file = new File(path1,"ossremixapp.apk");
//            fileOutput = new FileOutputStream(file);
//
//            inputStream = urlConnection.getInputStream();
//
//            totalSize = urlConnection.getContentLength();
//            downloadedSize = 0;
//
//
//            bufferLength = 0;
//
//            mProgBar.setMax(totalSize);
//            mProgBar.setProgress(0);
//
//            new Thread(){
//
//                public void run() {
//                    try {
//
//                        while ( (bufferLength = inputStream.read(buffer)) > 0 )
//                        {
//                            fileOutput.write(buffer, 0, bufferLength);
//                            downloadedSize += bufferLength;
//                            mProgBar.setProgress(downloadedSize);
//                            mProgBar.postInvalidate();
//                        }
//
//                        if ( downloadedSize == totalSize )
//                        {
//                            installAPK();
//                        }else
//                        {
//                            Mdialog.dismiss();
//                        }
//
//                    } catch (IOException e) {
//                        // TODO Auto-generated catch block
//                        e.printStackTrace();
//                    }
//
//                }
//            }.start();
//
//        }catch (MalformedURLException e){
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    /**
     * 업데이트 진행 함수
     */
    private void UpdateProgress() {
//        LinearLayout linear = (LinearLayout) View.inflate(ActivityMain.this, R.layout.update_confirm_dialog, null);
//        mProgBar  = (ProgressBar)linear.findViewById(R.id.installdownloadProgressBar);
//        TextView update_text_Info = (TextView)linear.findViewById(R.id.update_text);
//        Update_Btn  = (Button)linear.findViewById(R.id.update_btn);
//        update_text_Info.setText(UpdateMessage_Content);
//        //업데이트 진행 textview
//        s_state_text = (TextView)linear.findViewById(R.id.state_text);
//        s_state_text.setText("프로그램 다운로드중..");
//        AlertDialog.Builder MDig = new AlertDialog.Builder( MainActivity.this)
//                .setTitle("Ver."+UpdateMessage_Ver+" "+UpdateMessage_Title+"")
//                .setView(linear);
//        Mdialog = MDig.create();
//        Mdialog.show();
//        Update_Btn.setOnClickListener(new View.OnClickListener() {
//
//            public void onClick(View v) {
//                // TODO Auto-generated method stub
//                //업데이트버튼 비활성화
//                Update_Btn.setClickable(false);
//                //다운로드 진행
//                DownloadProcess();
//            }
//            //다운로드
//        });
    }

    /**
     * 다운로드 진행 처리 함수
     */
    public void DownloadProcess() {
        //메인 스레드에서 처리할수 없다.
        AsyncCallInstall task = new ActivityMain.AsyncCallInstall();
        task.execute();
    }

    /**
     * 앱 업데이트 서비스 start
     */
    public Boolean startAppAutoUpdaterService() {
        Intent serviceIntent = new Intent(context, UpdateService.class);
        try{
            // 서비스 시작
            context.startService(serviceIntent);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    UpdateService.hasNewVesionCheck(context, activity);
                }
            }, 1000);
            return true;
        }catch(Exception e) {
            // 오류시 서비스 종료
            context.stopService(serviceIntent);
            e.printStackTrace();
        }
        return false;
    }

    // region 내부 클래스 부
    /**
     * 업데이트 진행 클래스
     */
    private class AsyncCallWS extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            WebServicePost();
            return null;
        }

        //완료됨.
        protected void onPostExecute(Void result)
        {
//            try {
//                if (! UpdateMessage_Ver.equals(MOBILE_VERSION))
//                {
//                    checkversion = 1;
//                    try {
//                        UpdateProgress(); //업데이트 진행
//
//                    }catch (Exception e) {
//                        MENU_ERROR = "3";
//                    }
//                }else
//                {
//                    if (UpdateMessage_Ver.equals(MOBILE_VERSION))
//                    {
//                        checkversion = 0;
//                    }
//                }
//            }catch (Exception e) {
//                MENU_ERROR = "4";
//            }
        }
    }

    /**
     * Apk 설치 클래스
     */
    private class AsyncCallInstall extends AsyncTask<Void, Void, Void>
    {
        //배경실행행
        protected Void doInBackground(Void... voids) {
            InstallApkPost();
            return null;
        }
    }
    //endregion
}