package com.example.hayoung.yongchat.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.hayoung.yongchat.R;
import com.example.hayoung.yongchat.model.ChatRoom;
import com.example.hayoung.yongchat.model.User;
import com.example.hayoung.yongchat.session.UserSession;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.Arrays;
import java.util.Date;

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
                            //FirebaseUser user = mAuth.getCurrentUser();
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
        final DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
        final DatabaseReference roomsRef = FirebaseDatabase.getInstance().getReference("rooms");
        usersRef.orderByChild("uid").equalTo(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() == 0) {
                    // 가입시켜야함
                    usersRef.push().setValue(user);

                    // 로컬 사용자 세션에 사용자 등록
                    UserSession.getInstance().setCurrentUser(user);

                    ChatRoom chatRoom = new ChatRoom();
                    chatRoom.setUnreadCount(0);
                    chatRoom.setMembers(Arrays.asList(user));
                    chatRoom.setMessage("나와의 즐거운 대화를 시작해보자.");
                    chatRoom.setDateTime(new Date());
                    chatRoom.setUserId(user.getUid());
                    chatRoom.setTag(user.getUid());

                    String roomId = roomsRef.push().getKey();
                    roomsRef.child(roomId).setValue(chatRoom);


                    goToMain();
                } else {
                    // 이미 가입되어있음
                    goToMain();
                }
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
