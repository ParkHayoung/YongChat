package com.example.hayoung.yongchat.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.hayoung.yongchat.R;
import com.example.hayoung.yongchat.db.Database;
import com.example.hayoung.yongchat.model.User;
import com.example.hayoung.yongchat.service.UserService;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.wang.avi.AVLoadingIndicatorView;

public class LoginActivity extends AppCompatActivity {
    private CallbackManager mCallbackManager;
    private static final String TAG = "Login";
    private FirebaseAuth mAuth;

    private LoginButton mLoginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        mCallbackManager = CallbackManager.Factory.create();
        mLoginButton = (LoginButton)findViewById(R.id.login_button);
        mLoginButton.setReadPermissions("email", "public_profile");
        mLoginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
            }
        });

        // Initialize Facebook Login button
        //LoginManager.getInstance().logOut();
        if (isFacebookLoggedIn()) {
            handleFacebookAccessToken(AccessToken.getCurrentAccessToken());
        } else {
            mLoginButton.setVisibility(View.VISIBLE);
        }
    }

    public boolean isFacebookLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
    }

    private void handleFacebookAccessToken(AccessToken accessToken) {
        Log.d(TAG, "facebook:onSuccess, AccessToken = " + accessToken.getToken());
        mLoginButton.setVisibility(View.GONE);

        final AVLoadingIndicatorView indicatorView = (AVLoadingIndicatorView)findViewById(R.id.indicator_view);
        indicatorView.post(new Runnable() {
            @Override
            public void run() {
                indicatorView.show();
            }
        });
        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            User user = new User(mAuth.getCurrentUser());
                            signUpOrSignInUser(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this, "Firebase 로그인에 실패하였습니다. 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                            LoginActivity.this.finish();
                        }
                    }
                });
    }

    private void signUpOrSignInUser(final User user) {
        Database.users().orderByChild("uid").equalTo(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String token = FirebaseInstanceId.getInstance().getToken();
                user.setToken(token);

                UserService userService = new UserService();

                if (dataSnapshot.getChildrenCount() == 0) {
                    // 신규가입
                    userService.signUpUser(user);
                } else {
                    userService.updateUser(user);
                }

                // 앱 메인 화면 이동
                goToMain();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void goToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result back to the Facebook SDK
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
