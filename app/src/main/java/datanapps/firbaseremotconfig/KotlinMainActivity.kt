package datanapps.firbaseremotconfig

import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.widget.TextView
import android.widget.Toast

import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings

import java.util.HashMap


/*
 * This is mainActivity which get data from remote config and display here
 * */
class KotlinMainActivity : AppCompatActivity() {

    internal var cacheExpiration = 3600
    private var tvRemoteMsg: TextView? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tvRemoteMsg = findViewById(R.id.tv_remote_msg)
    }

    override fun onResume() {
        super.onResume()
        remoteConfigTellCna()
    }
    /*
    * Remote config
    *
    * */
    private fun remoteConfigTellCna() {
        val mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
        val configSettings = FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build()
        mFirebaseRemoteConfig.setConfigSettings(configSettings)

        mFirebaseRemoteConfig.setDefaults(defaultValues)

        if (mFirebaseRemoteConfig.info.configSettings.isDeveloperModeEnabled) {
            cacheExpiration = 0
        }

        Handler().postDelayed({
            mFirebaseRemoteConfig.fetch(cacheExpiration.toLong())
                    .addOnCompleteListener(this@KotlinMainActivity) { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this@KotlinMainActivity, "Fetch Succeeded",
                                    Toast.LENGTH_SHORT).show()

                            val configGithubLink = mFirebaseRemoteConfig.getString("config_github_link")
                            val configMsg = mFirebaseRemoteConfig.getString("config_welcome_msg")
                            tvRemoteMsg!!.text = "Link: $configGithubLink\n\n\n\n Name$configMsg"
                            mFirebaseRemoteConfig.activateFetched()
                        } else {
                            Toast.makeText(this@KotlinMainActivity, "Fetch Failed",
                                    Toast.LENGTH_SHORT).show()
                            tvRemoteMsg!!.text = "Fetch Failed"
                        }
                    }
        }, 1000)

    }

    /*
* Local default value to set in remote config
*
* */
    val defaultValues: Map<String, Any>
        get() {
            val defaultValues = HashMap<String, Any>()
            defaultValues.put("config_github_link", "Tell DataNapps");
            defaultValues.put("config_welcome_msg", "msg");

            return defaultValues
        }
}
