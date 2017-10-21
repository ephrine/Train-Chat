package devesh.ephrine.train.chat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

public class WebActivity extends AppCompatActivity {
public String WebInfo;

    private AdView mAdView;

    public String Ad_Banner_ID;
    public String Ad_Int_ID;
    public String Ad_App_ID;

    public String AdStatus="enable";

    public String TAG="Ephrine Apps";

    public String Test_Ad_Banner_ID="ca-app-pub-3940256099942544/6300978111";
    public String Test_Ad_App_ID="ca-app-pub-3940256099942544~3347511713";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        boolean isAppInstalled = appInstalledOrNot("devesh.ephrine.train.chat.pro");

        if(isAppInstalled) {
            //This intent will help you to launch if the package is already installed
            AdStatus="disable";

            Log.i(TAG,"Ads Disabled");
        } else {
            // Do whatever we want to do if application not installed
            // For example, Redirect to play store
            AdStatus="enable";
            Log.i(TAG,"Ads Enable");
        }


        Intent intent = getIntent();
        WebInfo = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

        WebView myWebView = (WebView) findViewById(R.id.WebView);

        if(WebInfo.equals("B")){
            myWebView.loadUrl("http://www.irctchelp.in/p/mumbai-mega-block.html");
        }
        if(WebInfo.equals("F")){
            myWebView.loadUrl("https://goo.gl/forms/vtoFgn2vugu599wG3");
        }
        if(WebInfo.equals("BUG")){
            myWebView.loadUrl("https://goo.gl/forms/qO1w9DYII7wQr7tP2");
        }
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        myWebView.setWebViewClient(new MyWebViewClient());

        if(AdStatus.equals("enable")){
            AdLoad();

        }
    }

    private boolean appInstalledOrNot(String uri) {
        PackageManager pm = getPackageManager();
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
        }

        return false;
    }


    private class MyWebViewClient extends WebViewClient {

        LinearLayout progress=(LinearLayout)findViewById(R.id.loadingView);
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon ) {
            // TODO Auto-generated method stub
            progress.setVisibility(View.VISIBLE);
            //	super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            // TODO Auto-generated method stub
            //super.onPageFinished(view, url);
            progress.setVisibility(View.GONE);        }
    }


    public void AdLoad(){

        Ad_Banner_ID=getString(R.string.banner_ad_unit_id);
        Ad_Int_ID=getString(R.string.int_ad_unit_id);
        Ad_App_ID=getString(R.string.app_id);



        MobileAds.initialize(this, Ad_App_ID);

        //Banner
        mAdView = (AdView) findViewById(R.id.adView2);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);




    }

}
