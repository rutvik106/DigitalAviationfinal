package `in`.fusionbit.digitalaviation

import `in`.fusionbit.digitalaviation.extras.USER_NAME
import `in`.fusionbit.digitalaviation.extras.getPref
import `in`.fusionbit.digitalaviation.fragments.HomeFragment
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.crashlytics.android.Crashlytics
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private val TAG = MainActivity::class.java.simpleName
    private lateinit var toolbar: MaterialToolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
                startActivity(Intent(this@MainActivity, QuizCourseActivity::class.java))
            }
            R.id.nav_logout -> {

            }

            R.id.nav_login -> {
                startActivity(Intent(this@MainActivity, LoginActivity::class.java))
            }

            R.id.nav_sub -> {
                if (getPref(this@MainActivity!!, USER_NAME) == "") {
                    Toast.makeText(
                        this@MainActivity,
                        "No subscription available.",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    startActivity(Intent(this@MainActivity, SubscriptionActivity::class.java))
                }

            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }
}