package devesh.ephrine.train.chat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.customtabs.CustomTabsIntent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.widget.ProfilePictureView;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.google.android.gms.ads.MobileAds;

import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "X";
    public static final String IntentUserName = "X";

    public String TAG="Ephrine Apps";
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
public String FBUserName;
    private AdView mAdView;
    private InterstitialAd mInterstitialAd;

    public String Ad_Banner_ID;
    public String Ad_Int_ID;
    public String Ad_App_ID;

    public String AdStatus="enable";
    public String Test_Ad_Banner_ID="ca-app-pub-3940256099942544/6300978111";
    public String Test_Ad_Int_ID="ca-app-pub-3940256099942544/1033173712";
    public String Test_Ad_App_ID="ca-app-pub-3940256099942544~3347511713";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        final String VersionCode=getString(R.string.App_Version_Code);
        final CardView UpdateCardView=(CardView)findViewById(R.id.UpdateCardView);
        UpdateCardView.setVisibility(View.GONE);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference CheckUpdate = database.getReference("app/update");
// Read from the database
        CheckUpdate.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
                Log.d(TAG, "Update Status " + value);
                if(value!=null){
                    if(value.equals(VersionCode)){
                        UpdateCardView.setVisibility(View.GONE);
                    }else{
                        UpdateCardView.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to Update Status", error.toException());
            }
        });



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
        mAuth = FirebaseAuth.getInstance();
        FacebookSdk.sdkInitialize(getApplicationContext());


final TextView TxUserName=(TextView)findViewById(R.id.textViewUserName);

        mAuthListener = new FirebaseAuth.AuthStateListener() {

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    FBUserName=user.getDisplayName();
TxUserName.setText(FBUserName);

                    Log.d(TAG, "FB User Name: "+FBUserName);

                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };

        FirebaseAuth.getInstance().getCurrentUser().getProviderId();
        for (UserInfo user: FirebaseAuth.getInstance().getCurrentUser().getProviderData()) {
            if (user.getProviderId().equals("facebook.com")) {
                System.out.println("User is signed in with Facebook");
                String ProfileID= Profile.getCurrentProfile().getId().toString();
                String ProfilePicUrl="http://graph.facebook.com/"+ProfileID+"+/picture?type=small";
                // Picasso.with(this).load(ProfilePicUrl).into(ProfilePic);
                ProfilePictureView profilePictureView;
                profilePictureView = (ProfilePictureView) findViewById(R.id.friendProfilePicture);
                profilePictureView.setProfileId(ProfileID);

            }
        }



        //  Log.d(TAG, "Userid: " + ProfileID);

        // Sample AdMob app ID: ca-app-pub-3940256099942544~3347511713
if(AdStatus.equals("enable")){
    AdLoad();

}


    }

    public void AdLoad(){

      Ad_Banner_ID=getString(R.string.banner_ad_unit_id);
      Ad_Int_ID=getString(R.string.int_ad_unit_id);
      Ad_App_ID=getString(R.string.app_id);
        if(AdStatus.equals("enable")){

            MobileAds.initialize(this, Ad_App_ID);

            //Banner
            mAdView = (AdView) findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);


            //Interstitial
            mInterstitialAd = new InterstitialAd(this);
            mInterstitialAd.setAdUnitId(Ad_Int_ID);
            mInterstitialAd.loadAd(new AdRequest.Builder().build());
            mInterstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    // Load the next interstitial.
                    mInterstitialAd.loadAd(new AdRequest.Builder().build());
                }

            });

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




    @Override
    public void onBackPressed() {

        if (AdStatus.equals("enable")) {
            if(mInterstitialAd.isLoaded()){
                mInterstitialAd.show();

            }else{
                super.onBackPressed();

            }
        } else {
            Log.d("TAG", "The interstitial wasn't loaded yet.");
        }
        super.onBackPressed();
    }
    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }
    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_home, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.about:

                Intent intent = new Intent(this, AboutActivity.class);
                startActivity(intent);

                return true;

            case R.id.signout:
                LoginManager.getInstance().logOut();
                FirebaseAuth.getInstance().signOut();
                deleteAppData();
                finish();
                return true;
            case R.id.share:
                onInviteClicked();
                return true;
            case R.id.feedback:
                Intent intent1 = new Intent(this, WebActivity.class);
                intent1.putExtra(EXTRA_MESSAGE, "F");
                startActivity(intent1);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public void HarbourClick(View v){
        Intent intent = new Intent(this, ChatActivity.class);
         intent.putExtra(EXTRA_MESSAGE, "H");
        startActivity(intent);
    }

    public void CentralClick(View v){
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra(EXTRA_MESSAGE, "C");
        startActivity(intent);
    }

    public void WesternClick(View v){
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra(EXTRA_MESSAGE, "W");
        startActivity(intent);
    }

    public void TransHarbourClick(View v){
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra(EXTRA_MESSAGE, "TH");
        startActivity(intent);
    }

    public void MetroClick(View v){
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra(EXTRA_MESSAGE, "M");
        startActivity(intent);

    }

    public void megablock(View v){
        Intent intent = new Intent(this, WebActivity.class);
        intent.putExtra(EXTRA_MESSAGE, "B");
        startActivity(intent);

    }
    private void deleteAppData() {
        try {
            // clearing app data
            String packageName = getApplicationContext().getPackageName();
            Runtime runtime = Runtime.getRuntime();
            runtime.exec("pm clear "+packageName);
            Log.d(TAG, "App Data Cleared !!");

        } catch (Exception e) {
            e.printStackTrace();
        } }

    public void GRP(View v){
        String no = getString(R.string.Railway_Acidents_Emergency_events);
        Uri number = Uri.parse("tel:" + no);
        Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
        startActivity(callIntent);
    }


    public void wRPF(View v){
        String no = getString(R.string.W_RPF);
        Uri number = Uri.parse("tel:" + no);
        Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
        startActivity(callIntent);
    }


    public void cRPF(View v){
        String no = getString(R.string.C_RPF);
        Uri number = Uri.parse("tel:" + no);
        Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
        startActivity(callIntent);
    }


    public void IRS(View v){
        String no = getString(R.string.IRS);
        Uri number = Uri.parse("tel:" + no);
        Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
        startActivity(callIntent);
    }


    public void cushelp(View v){
        String no = getString(R.string.cus_helpline);
        Uri number = Uri.parse("tel:" + no);
        Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
        startActivity(callIntent);
    }

    public int REQUEST_INVITE;
    private void onInviteClicked() {
        Intent intent = new AppInviteInvitation.IntentBuilder("Share With Friends")
                .setMessage("Train Chat For Mumbaikars: Get Instant Train Alerts from fellow passengers ")
                .setDeepLink(Uri.parse("market://details?id=devesh.ephrine.train.chat"))
                .build();
        startActivityForResult(intent, REQUEST_INVITE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode);

        if (requestCode == REQUEST_INVITE) {
            if (resultCode == RESULT_OK) {
                // Get the invitation IDs of all sent messages
                String[] ids = AppInviteInvitation.getInvitationIds(resultCode, data);
                for (String id : ids) {
                    Log.d(TAG, "onActivityResult: sent invitation " + id);
                }
            } else {
                // Sending failed or it was canceled, show failure message to the user
                // ...
            }
        }
    }

}
