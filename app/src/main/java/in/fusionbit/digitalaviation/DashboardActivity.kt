package `in`.fusionbit.digitalaviation

import `in`.fusionbit.digitalaviation.extras.PREF
import `in`.fusionbit.digitalaviation.extras.USER_NAME
import `in`.fusionbit.digitalaviation.extras.getPref
import `in`.fusionbit.digitalaviation.fragments.HomeFragment
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.core.view.GravityCompat
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*

class DashboardActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private val TAG = MainActivity::class.java.simpleName
    private lateinit var toolbar: MaterialToolbar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )

        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        supportFragmentManager.beginTransaction().add(R.id.main_frame, HomeFragment()).commit()

    }

    override fun onBackPressed() {
        val count = supportFragmentManager.backStackEntryCount

        if (count == 0) {
            super.onBackPressed()
            if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
                drawer_layout.closeDrawer(GravityCompat.START)
            } else {
                super.onBackPressed()
            }
        } else {
            supportFragmentManager.popBackStack()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

            R.id.nav_home -> {
                supportFragmentManager.beginTransaction().replace(R.id.main_frame, HomeFragment())
                    .commit()
            }

            R.id.nav_quiz -> {
                startActivity(Intent(this@DashboardActivity, QuizCourseActivity::class.java))
            }

            R.id.nav_logout -> {

                val builder = AlertDialog.Builder(this@DashboardActivity)

                builder.setTitle("Logout")

                builder.setMessage("Are you sure you want to logout?")

                builder.setPositiveButton("Yes"){dialog, which ->
                    this@DashboardActivity.getSharedPreferences(PREF, 0).edit().clear().apply()
                    finish()
                    startActivity(Intent(this@DashboardActivity, SplashActivity::class.java))
                }


                // Display a negative button on alert dialog
                builder.setNegativeButton("No"){dialog,which ->

                }


                // Finally, make the alert dialog using builder
                val dialog: AlertDialog = builder.create()

                // Display the alert dialog on app interface
                dialog.show()


            }

            R.id.nav_sub -> {
                if (getPref(this@DashboardActivity!!, USER_NAME) == "") {
                    Toast.makeText(
                        this@DashboardActivity,
                        "No subscription available.",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    startActivity(Intent(this@DashboardActivity, SubscriptionActivity::class.java))
                }

            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }
}