package datanapps.firbaseremotconfig;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    int cacheExpiration = 3600;

    private TextView tvRemoteMsg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvRemoteMsg = findViewById(R.id.tv_remote_msg);
    }

    @Override
    protected void onResume() {
        super.onResume();
        remoteConfigTellCna();
    }

    private void remoteConfigTellCna() {
        if(!isNetworkConnected()){
            tvRemoteMsg.setText("Internet not working.");
            return;
        }


        final FirebaseRemoteConfig mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build();
        mFirebaseRemoteConfig.setConfigSettings(configSettings);

        mFirebaseRemoteConfig.setDefaults(getDefaultValues());

        if (mFirebaseRemoteConfig.getInfo().getConfigSettings().isDeveloperModeEnabled()) {
            cacheExpiration = 0;
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mFirebaseRemoteConfig.fetch(cacheExpiration)
                        .addOnCompleteListener(MainActivity.this, new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if (task.isSuccessful()) {
                                    Toast.makeText(MainActivity.this, "Fetch Succeeded",
                                            Toast.LENGTH_SHORT).show();

                                    String configGithubLink = mFirebaseRemoteConfig.getString("config_github_link");
                                    String configMsg = mFirebaseRemoteConfig.getString("config_welcome_msg");
                                    tvRemoteMsg.setText(configGithubLink+"\n"+configMsg);
                                    mFirebaseRemoteConfig.activateFetched();
                                } else {
                                    Toast.makeText(MainActivity.this, "Fetch Failed",
                                            Toast.LENGTH_SHORT).show();
                                    tvRemoteMsg.setText("Fetch Failed");
                                }
                            }
                        });
            }
        }, 1000);


    }


    public Map<String, Object> getDefaultValues() {
        Map<String, Object> defaultValues = new HashMap<>();
        defaultValues.put("config_github_link", "Tell DataNapps");
        defaultValues.put("config_welcome_msg", "msg");

        return defaultValues;
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }
}