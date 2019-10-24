package `in`.fusionbit.digitalaviation

import `in`.fusionbit.digitalaviation.extras.TOKEN
import `in`.fusionbit.digitalaviation.extras.getPref
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_splash)

        val token = getPref(this@SplashActivity, TOKEN)

        if (token == "" || token == null) {
            val handler = Handler().postDelayed(Runnable {
                finish()
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            }, 3000)
        } else {
            val handler = Handler().postDelayed(Runnable {
                finish()
                startActivity(Intent(this@SplashActivity, DashboardActivity::class.java))
            }, 3000)
        }


    }


}
