# FirbaseRemoteConfigAndroid
Firbase remote config android

//===========================================//

This is sample in android (java/kotlin):

//===========================================//

    /*
     * This is mainActivity which get data from remote config and display here
     * */
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


        /*
        * Remote config
        *
        * */
        private void remoteConfigTellCna() {
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
                                        tvRemoteMsg.setText("Link: " + configGithubLink + "\n\n\n\n Name" + configMsg);
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

    /*
    * Local default value to set in remote config
    *
    * */
        public Map<String, Object> getDefaultValues() {
            Map<String, Object> defaultValues = new HashMap<>();
            defaultValues.put("config_github_link", "Tell DataNapps");
            defaultValues.put("config_welcome_msg", "msg");
            return defaultValues;
        }
    }

