package devesh.ephrine.train.chat;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class ChatActivity extends AppCompatActivity {

    public String TrainLine;
    public String TAG="Ephrine Apps";
    public String Date;
    public String Month;
    public String Year;
    public String Hour;
    public String Min;
    public String Time;
    public String ChatDate;
    public int ChatTotal;
    public int PrevChatTotal;
    public String Username;
    public String UID;
    public int J=-1;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private AdView mAdView;
    private InterstitialAd mInterstitialAd;

    public String Ad_Banner_ID;
    public String Ad_Int_ID;
    public String Ad_App_ID;

    public String Test_Ad_Banner_ID="ca-app-pub-3940256099942544/6300978111";
    public String Test_Ad_Int_ID="ca-app-pub-3940256099942544/1033173712";
    public String Test_Ad_App_ID="ca-app-pub-3940256099942544~3347511713";

    public String AdStatus="enable";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

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


        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
InternetCheck();
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Username=user.getDisplayName();
                    UID=user.getUid();
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };

        Intent intent = getIntent();
        TrainLine = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
String BarTitle="app";
        if(TrainLine.equals("W")){
            BarTitle="Western Railway Chat";

}else if(TrainLine.equals("C")){
            BarTitle="Central Railway Chat";


        }else if(TrainLine.equals("H")){
            BarTitle="Harbour Railway Chat";


        }else if(TrainLine.equals("TH")){
            BarTitle="Trans Harbour Railway Chat";


        }else if(TrainLine.equals("M")){
            BarTitle="Metro Chat";


        }

        ((AppCompatActivity)this).getSupportActionBar().setTitle(BarTitle);

        Calendar calender = Calendar.getInstance();
Date=String.valueOf(calender.get(Calendar.DATE));
        Month=String.valueOf(calender.get(Calendar.MONTH)+1);
        Year=String.valueOf(calender.get(Calendar.YEAR));
ChatDate=Date+Month+Year;
        Log.d(TAG, "Date : " +ChatDate);
GetTotal();

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


    public void AdLoad(){

        Ad_Banner_ID=getString(R.string.banner_ad_unit_id);
        Ad_Int_ID=getString(R.string.int_ad_unit_id);
        Ad_App_ID=getString(R.string.app_id);



        MobileAds.initialize(this, Ad_App_ID);

        //Banner
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);




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
    public void GetTotal(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference GetT = database.getReference("app/chat/"+TrainLine+"/"+ChatDate+"/total");
        GetT.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
                Log.d(TAG, "Total Value is: " + value);
                if(value!=null){
                    ChatTotal=Integer.parseInt(value);
                    CreateChatView();
                }else{
                   CreateChatThread();

                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read Total value.", error.toException());
            }
        });
      //  GetT.keepSynced(true);

    }

    public void CreateChatView(){
          int t= ChatTotal;
        if(J==-1){
            J=0;
        }else {
            J=PrevChatTotal;
        }


        for (int j = J; j < t; j++) {
            LinearLayout ChatViewScroll=(LinearLayout)findViewById(R.id.ChatViewScroll);
            final CardView ChatCard = new CardView(ChatActivity.this);
            ChatCard.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            ChatCard.setPadding(30, 20, 30, 20);
            ChatCard.setCardElevation(4);
            ChatCard.setUseCompatPadding(true);
           ChatCard.setId(j);
            ChatViewScroll.addView(ChatCard);

            LinearLayout LL1 = new LinearLayout(ChatActivity.this);
            LL1.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            LL1.setOrientation(LinearLayout.VERTICAL);
            LL1.setPadding(10,10,10,10);
            ChatCard.addView(LL1);

            final TextView txUserName = new TextView(ChatActivity.this);
            txUserName.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));


            txUserName.setTypeface(null, Typeface.BOLD);
            txUserName.setTextSize(18);
            txUserName.setText("Loading....");  // Name
            //txUserName.setGravity(View.TEXT_ALIGNMENT_CENTER);
            LL1.addView(txUserName);


            final TextView txMsg = new TextView(ChatActivity.this);
            txMsg.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            txMsg.setText("Loading....");   // msg
            LL1.addView(txMsg);

            final TextView txTime = new TextView(ChatActivity.this);
            txTime.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            txTime.setTextSize(10);
            txTime.setText("Loading....");   // Time
            LL1.addView(txTime);


            // Write a message to the database
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            // final int idj = j;
            DatabaseReference name = database.getReference("app/chat/"+TrainLine+"/"+ChatDate+"/"+j+"/name");
            name.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    String value = dataSnapshot.getValue(String.class);
                    Log.d(TAG, "Name: " + value);
                    if (value != null) {
                       txUserName.setText(value);

                    } else {



                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.w(TAG, "Failed to read value.", error.toException());
                }
            });
  //          name.keepSynced(true);

            DatabaseReference Msg = database.getReference("app/chat/"+TrainLine+"/"+ChatDate+"/"+j+"/msg");
            Msg.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    String value = dataSnapshot.getValue(String.class);
                    Log.d(TAG, "Name: " + value);
                    if (value != null) {
                        txMsg.setText(value);

                    } else {



                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.w(TAG, "Failed to read value.", error.toException());
                }
            });

            DatabaseReference Time = database.getReference("app/chat/"+TrainLine+"/"+ChatDate+"/"+j+"/time");
            Time.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    String value = dataSnapshot.getValue(String.class);
                    Log.d(TAG, "Time: " + value);
                    if (value != null) {

                        txTime.setText(value);

                    } else {



                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.w(TAG, "Failed to read value.", error.toException());
                }
            });
//            Msg.keepSynced(true);

            final String userUID=UID;
            final int Gid=j;
            DatabaseReference UID = database.getReference("app/chat/"+TrainLine+"/"+ChatDate+"/"+j+"/uid");
            UID.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    String value = dataSnapshot.getValue(String.class);
                    Log.d(TAG, "Name: " + value);
                    if (value != null) {
                           if(value.equals(userUID)){
                               ChatCard.getId();
                               ChatCard.setCardBackgroundColor(getResources().getColor(R.color.ChatBG));
                           }
                    } else {

                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.w(TAG, "Failed to read value.", error.toException());
                }
            });
UID.keepSynced(true);
            final ScrollView sc=(ScrollView)findViewById(R.id.ScrollView);
            sc.scrollTo(0, sc.getBottom());
            // Footer.requestFocus();
            sc.fullScroll(View.FOCUS_DOWN);
            sc.post(new Runnable() {
                @Override
                public void run() {
                    sc.fullScroll(ScrollView.FOCUS_DOWN);
                }
            });

        }

        }

        public void CreateChatThread(){
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference CreateDB = database.getReference("app/chat/"+TrainLine+"/"+ChatDate+"/total");
            CreateDB.setValue("0");

        }

        public void SendMsg(View v){
String AMPM="AM";
            Calendar calender = Calendar.getInstance();
            int hr=calender.get(Calendar.HOUR_OF_DAY);
            int m=calender.get(Calendar.MINUTE);
            if(hr>12){
                hr=hr-12;
                Hour=String.valueOf(hr);

                    AMPM="PM";


            }else {
                if(hr==12){
                    AMPM="PM";
                    Hour=String.valueOf(hr);

                }else{
                    Hour=String.valueOf(hr);
                    AMPM="AM";
                }


            }
//Hour=String.valueOf(hr);
            if(m<=9){
                Min="0"+String.valueOf(calender.get(Calendar.MINUTE));

            }else{
                Min=String.valueOf(calender.get(Calendar.MINUTE));

            }
            Time=Hour+":"+Min+" "+AMPM;

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            EditText MsgBox=(EditText)findViewById(R.id.MsgBox);
            String box=MsgBox.getText().toString();
if(box==null || box.equals(" ") || box.equals("") || box.equals(null)){

}else{
    int ChatT=ChatTotal+1;
    PrevChatTotal=ChatTotal;

    DatabaseReference CreateTotal = database.getReference("app/chat/"+TrainLine+"/"+ChatDate+"/total");
    CreateTotal.setValue(String.valueOf(ChatT));

    //  EditText MsgBox=(EditText)findViewById(R.id.MsgBox);


    DatabaseReference Createusername = database.getReference("app/chat/"+TrainLine+"/"+ChatDate+"/"+ChatTotal+"/name");
    Createusername.setValue(Username);
    DatabaseReference CreateMSG = database.getReference("app/chat/"+TrainLine+"/"+ChatDate+"/"+ChatTotal+"/msg");
    CreateMSG.setValue(MsgBox.getText().toString());
    DatabaseReference CreateUID = database.getReference("app/chat/"+TrainLine+"/"+ChatDate+"/"+ChatTotal+"/uid");
    CreateUID.setValue(UID);
    DatabaseReference CreateTime = database.getReference("app/chat/"+TrainLine+"/"+ChatDate+"/"+ChatTotal+"/time");
    CreateTime.setValue(Time);


    MsgBox.setText("");
}

//GetTotal();
            //finish();
           // startActivity(getIntent());

            InputMethodManager inputManager = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);

            inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }

    public void InternetCheck(){
        DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");

        final TextView LoadingTX=(TextView)findViewById(R.id.textViewLoading);

        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);
                if (connected) {
                    System.out.println("connected");
                    GreenOnline();
                    LoadingTX.setText("Connected");

                } else {
                    System.out.println("not connected");
                    RedOffline();
                    LoadingTX.setText("Connecting.....");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.err.println("Listener was cancelled");
            }
        });
    }



    public void RedOffline(){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP){
            // Do something for lollipop and above versions
            final ImageView IMGloading=(ImageView)findViewById(R.id.imageViewLoading);
            final TextView LoadingTX=(TextView)findViewById(R.id.textViewLoading);
            final Drawable RedImg=getDrawable(R.drawable.reddot);
            final Drawable GreenImg=getDrawable(R.drawable.greendot);
            IMGloading.setImageDrawable(RedImg);
            LoadingTX.setText("Connecting.....");


        } else{
            final ImageView IMGloading=(ImageView)findViewById(R.id.imageViewLoading);
IMGloading.setVisibility(View.GONE);
            // do something for phones running an SDK before lollipop
        }
    }

    public void GreenOnline(){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP){
            // Do something for lollipop and above versions
            final ImageView IMGloading=(ImageView)findViewById(R.id.imageViewLoading);
            final TextView LoadingTX=(TextView)findViewById(R.id.textViewLoading);
            final Drawable RedImg=getDrawable(R.drawable.reddot);
            final Drawable GreenImg=getDrawable(R.drawable.greendot);
            IMGloading.setImageDrawable(GreenImg);
            LoadingTX.setText("Connected ");


        } else{
            final ImageView IMGloading=(ImageView)findViewById(R.id.imageViewLoading);
            IMGloading.setVisibility(View.GONE);
            // do something for phones running an SDK before lollipop
        }
    }
}






