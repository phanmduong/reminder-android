package com.example.phanminhduong.reminder;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.example.phanminhduong.reminder.graphql.MyApolloClient;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    CallbackManager callbackManager;
    LoginButton loginButton;
    ProgressDialog pd;

    GoogleSignInClient mGoogleSignInClient;
    int RC_SIGN_IN = 99;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View layoutLogin = findViewById(R.id.layoutLogin);
        layoutLogin.getBackground().setAlpha(150);

        pd = new ProgressDialog(MainActivity.this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setMessage("Đang đăng nhập....");
        pd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFD4D9D0")));

        pd.setIndeterminate(false);

        signInFacebook();
        signInGoogle();

    }

    private void signInGoogle() {
//        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestEmail()
//                .build();
//        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
//        SignInButton signInButton = findViewById(R.id.sign_in_button);
//        signInButton.setSize(SignInButton.SIZE_STANDARD);
//        signInButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
////                startActivityForResult(signInIntent, RC_SIGN_IN);
//                Toast.makeText(MainActivity.this, "Đăng nhập Google đang phát triển", Toast.LENGTH_LONG).show();
//            }
//        });
    }

    private void signInFacebook() {
        callbackManager = CallbackManager.Factory.create();

        loginButton = findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList("public_profile", "email"));
        checkLoggedIn();

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                loginFBServer(loginResult.getAccessToken().getUserId(), loginResult.getAccessToken().getToken());

            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                Toast.makeText(MainActivity.this, "Đăng nhập Facebook thất bại!!!", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void loginFBServer(String fbID, String tokenFB) {
        pd.show();
        LoginUserMutation loginUserMutation = LoginUserMutation.builder().fbID(fbID).token(tokenFB).build();
        MyApolloClient.getApolloClient().mutate(loginUserMutation).enqueue(new ApolloCall.Callback<LoginUserMutation.Data>() {
            @Override
            public void onResponse(@NotNull Response<LoginUserMutation.Data> response) {
                Data.token = response.data().login().token();
                Log.e("token", Data.token);
                pd.dismiss();
                startTodayActivity();
            }

            @Override
            public void onFailure(@NotNull ApolloException e) {
                Toast.makeText(MainActivity.this, "Đăng nhập thất bại!!!", Toast.LENGTH_LONG).show();
                pd.dismiss();
            }
        });
    }

    private void checkLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
        if (isLoggedIn) {
            loginFBServer(accessToken.getUserId(), accessToken.getToken());
        } else {
            LoginManager.getInstance().logOut();
        }
    }

    private void startTodayActivity() {
        Intent intent = new Intent(this, TodayActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
//            updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
//            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
//            updateUI(null);
        }
    }
}
