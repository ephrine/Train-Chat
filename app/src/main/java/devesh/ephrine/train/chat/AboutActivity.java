package devesh.ephrine.train.chat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

public class AboutActivity extends AppCompatActivity {
    public String TAG="Ephrine Apps";
    public String AdStatus="enable";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        final Button UpdateButton=(Button)findViewById(R.id.updatebutton);
        UpdateButton.setVisibility(View.GONE);
        TextView premiumTx=(TextView)findViewById(R.id.premiumTx);
        premiumTx.setVisibility(View.GONE);

        boolean isAppInstalled = appInstalledOrNot("devesh.ephrine.train.chat.pro");

        if(isAppInstalled) {
            //This intent will help you to launch if the package is already installed
            AdStatus="disable";
            premiumTx.setVisibility(View.VISIBLE);

            Log.i(TAG,"Ads Disabled");
        } else {
            // Do whatever we want to do if application not installed
            // For example, Redirect to play store
            AdStatus="enable";
            premiumTx.setVisibility(View.GONE);

            Log.i(TAG,"Ads Enable");
        }

        final String VersionCode=getString(R.string.App_Version_Code);

        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("app/update");
// Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
                Log.d(TAG, "Update Status " + value);
                if(value!=null){
                    if(value.equals(VersionCode)){
                        UpdateButton.setVisibility(View.GONE);
                    }else{
                       UpdateButton.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to Update Status", error.toException());
            }
        });

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


    public void update(View v){
    Intent intent = new Intent(Intent.ACTION_VIEW);
    intent.setData(Uri.parse("market://details?id=devesh.ephrine.train.chat")); //Google play store
    startActivity(intent);

}
    public void buy(View v){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("market://details?id=devesh.ephrine.train.chat.pro")); //Google play store
        startActivity(intent);

    }
    public void website(View v){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("https://ephrine.blogspot.com"));
        startActivity(intent);

    }

    public void privacy(View v){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("https://ephrine.blogspot.com/p/privacy-policy.html"));
        startActivity(intent);
    }
}
