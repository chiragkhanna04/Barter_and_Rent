package info.loginp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.digits.sdk.android.Digits;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import io.fabric.sdk.android.Fabric;

public class SplashScreen extends AppCompatActivity {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "VR3OSgpSZyTPkJtH3e0waItAj";
    private static final String TWITTER_SECRET = "3YXqZJ4bmdZZilOHnNLV7iGg4Nk5KHUjennjHDXV9Vm8j7RzT2";

private FirebaseAuth mauth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new TwitterCore(authConfig), new Digits.Builder().build());
        setContentView(R.layout.activity_splash_screen);
        mauth= FirebaseAuth.getInstance();
        Thread mythread = new Thread(){
            @Override
            public void run() {
                try {
                    sleep(3000);
                    if(mauth.getCurrentUser()==null) {
                        Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(i);
                    }
                    else
                    {
                        Intent intent= new Intent(getApplicationContext(),Login_success.class);
                        startActivity(intent);
                    }
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        mythread.start();
    }
}
