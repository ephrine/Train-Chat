package devesh.ephrine.train.chat;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.Login;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import org.json.JSONObject;

public class LoginActivity extends Activity {
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    public String TAG="Ephrine Apps";
    public ProgressDialog dialog;
public View SigninView;
    public View SignupView;
    public CardView EmailLoginView;
    public String LoginStatusCode="0";

public CallbackManager mCallbackManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        EmailLoginView=(CardView)findViewById(R.id.EmailLoginView);
        EmailLoginView.setVisibility(View.GONE);



        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
StartApp();

                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };


        mCallbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("email", "public_profile");
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
                dialog = ProgressDialog.show(LoginActivity.this, "",
                        "Please wait....", true);

            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
                // ...
               if(dialog.isShowing()){
                   dialog.hide();
               }
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
                // ...

                if(dialog.isShowing()){
                    dialog.hide();
                }
            }
        });

    }

    public void StartApp(){
        if(LoginStatusCode.equals("0")){
            finish();
         //   Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            LoginStatusCode="1";
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result back to the Facebook SDK
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        mAuth.addAuthStateListener(mAuthListener);

    }
    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token.getToken());

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();

dialog.dismiss();
                            StartApp();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });


    }


    public void EmailLogin(View v){
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slideup);
        EmailLoginView=(CardView)findViewById(R.id.EmailLoginView);
        EmailLoginView.startAnimation(animation);
        EmailLoginView.setVisibility(View.VISIBLE);

        SigninView=(View)findViewById(R.id.ViewSignin);
        SigninView.setVisibility(View.VISIBLE);



        SignupView=(View)findViewById(R.id.ViewSignup);
        SignupView.setVisibility(View.GONE);


        LinearLayout LLLogin=(LinearLayout)findViewById(R.id.LLViewLogin);
        LinearLayout LLPasswordReset=(LinearLayout)findViewById(R.id.LLViewPasswordReset);

        LLLogin.setVisibility(View.VISIBLE);
        LLPasswordReset.setVisibility(View.GONE);


    }

    public void EmailLogin1(View v){
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.fade);
        EmailLoginView=(CardView)findViewById(R.id.EmailLoginView);
        EmailLoginView.setVisibility(View.VISIBLE);

        SigninView=(View)findViewById(R.id.ViewSignin);
     //   SigninView.startAnimation(animation);
        SigninView.setVisibility(View.VISIBLE);

   //     TextView PWreset=(TextView)findViewById(R.id.TxPasswordReset);
     //   Button PWresetBT=(Button)findViewById(R.id.buttonPasswordReset);
     //   PWreset.setVisibility(View.GONE);
       // PWresetBT.setVisibility(View.GONE);

        SignupView=(View)findViewById(R.id.ViewSignup);
      //  SignupView.startAnimation(animation);
        SignupView.setVisibility(View.GONE);


        LinearLayout LLLogin=(LinearLayout)findViewById(R.id.LLViewLogin);
        LinearLayout LLPasswordReset=(LinearLayout)findViewById(R.id.LLViewPasswordReset);

        LLLogin.setVisibility(View.VISIBLE);
        LLPasswordReset.setVisibility(View.GONE);



    }

    public void back(View v){
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slidedown);

        EmailLoginView=(CardView)findViewById(R.id.EmailLoginView);
        EmailLoginView.startAnimation(animation);
        EmailLoginView.setVisibility(View.GONE);


    }
    public void Emailsignup(View v){
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.fade);
        EmailLoginView=(CardView)findViewById(R.id.EmailLoginView);
        EmailLoginView.setVisibility(View.VISIBLE);

        SigninView=(View)findViewById(R.id.ViewSignin);
      //  SigninView.startAnimation(animation);
        SigninView.setVisibility(View.GONE);


        SignupView=(View)findViewById(R.id.ViewSignup);
        //SignupView.startAnimation(animation);
        SignupView.setVisibility(View.VISIBLE);

    }



    public void emailsignin(View v){
        dialog = ProgressDialog.show(LoginActivity.this, "",
                "Please wait....", true);

        EditText email=(EditText)findViewById(R.id.editTextEmail);
        EditText password=(EditText)findViewById(R.id.editTextPassword);
String TxEmail=email.getText().toString();
        String TxPassword=password.getText().toString();
        Log.d(TAG, "signInWithEmail:"+TxEmail+TxPassword);

        mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(TxEmail, TxPassword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            dialog.dismiss();
                            StartApp();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });


    }

    public void EmailAccountCreate(View v){
        dialog = ProgressDialog.show(LoginActivity.this, "",
                "Creating Account, Please wait....", true);

        EditText email=(EditText)findViewById(R.id.editText);
        EditText password=(EditText)findViewById(R.id.editText2);
        final EditText name=(EditText)findViewById(R.id.editText3);

        mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Failed !",
                                    Toast.LENGTH_SHORT).show();
                            dialog.dismiss();

                        }

                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        dialog.dismiss();

                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(name.getText().toString())
                               // .setPhotoUri(Uri.parse("https://example.com/jane-q-user/profile.jpg"))
                                .build();

                        user.updateProfile(profileUpdates)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            StartApp();
                                            Log.d(TAG, "User profile created Successful.");
                                        }
                                    }
                                });

                    }
                });

    }

    public void PasswordResetEmail(){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String emailAddress = "user@example.com";

        auth.sendPasswordResetEmail(emailAddress)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Password Reset Email sent.");
                        }
                    }
                });
    }

    public void PasswordReset(View v){
        LinearLayout LLLogin=(LinearLayout)findViewById(R.id.LLViewLogin);
        LinearLayout LLPasswordReset=(LinearLayout)findViewById(R.id.LLViewPasswordReset);

        LLLogin.setVisibility(View.GONE);
        LLPasswordReset.setVisibility(View.VISIBLE);

    }

    public void PasswordResetEmailSend(View v){
        final EditText Edemail=(EditText)findViewById(R.id.editText4);
        final TextView PWreset=(TextView)findViewById(R.id.TxPasswordReset);
        final Button PWresetBT=(Button)findViewById(R.id.buttonPasswordReset1);

        FirebaseAuth auth = FirebaseAuth.getInstance();

        final String emailAddress = Edemail.getText().toString();

        auth.sendPasswordResetEmail(emailAddress)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Password Reset Email sent.");
                            PWreset.setText("Password Reset Email send to "+emailAddress);
Edemail.setVisibility(View.GONE);
PWresetBT.setVisibility(View.GONE);
                        }
                    }
                });

    }

    public void privacy(View v){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("https://ephrine.blogspot.com/p/privacy-policy.html")); //Google play store
        startActivity(intent);
    }
}
