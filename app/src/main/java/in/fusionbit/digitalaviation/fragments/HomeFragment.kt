package `in`.fusionbit.digitalaviation.fragments

import `in`.fusionbit.digitalaviation.R
import `in`.fusionbit.digitalaviation.databinding.FragmentHomeBinding
import `in`.fusionbit.digitalaviation.extras.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeFragment : Fragment() {

    private val TAG = HomeFragment::class.java.simpleName

    private lateinit var fragmentHomeBinding: FragmentHomeBinding
    private lateinit var progressDialog: ProgressDialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentHomeBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        (activity as AppCompatActivity).supportActionBar?.title = "Digital Aviation"
        progressDialog = ProgressDialog(activity!!)

        fragmentHomeBinding.bottomNavigationView.setOnNavigationItemSelectedListener(
            mOnNavigationItemSelectedListener
        )

        childFragmentManager.beginTransaction()
            .replace(R.id.other_container, AboutFragment())
            .commit()

        return fragmentHomeBinding.root
    }


    private val mOnNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.action_about -> {
                    childFragmentManager.beginTransaction()
                        .replace(R.id.other_container, AboutFragment())
                        .commit()
                    return@OnNavigationItemSelectedListener true
                }
                R.id.action_course -> {
                    childFragmentManager.beginTransaction()
                        .replace(R.id.other_container, CourseFragment()).commit()
                    return@OnNavigationItemSelectedListener true
                }
                R.id.action_quiz -> {
                    childFragmentManager.beginTransaction()
                        .replace(R.id.other_container, QuizCourseFragment())
                        .commit()
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        }


}