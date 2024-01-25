package com.ksajja.newone.activity

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.ksajja.newone.R
import com.ksajja.newone.base.BaseActivity
import com.ksajja.newone.base.BaseFragment
import com.ksajja.newone.fragment.TasksFragment
import com.ksajja.newone.helper.AppHelper
import com.ksajja.newone.network.APICallback
import com.ksajja.newone.network.APIService


class MainActivity : BaseActivity(R.layout.activity_main) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            replaceFragment(TasksFragment())
        }
    }

    override fun onStart() {
        super.onStart()
        //Send user device info along with device token
        APIService.instance.sendDeviceInfo(AppHelper.deviceInfo).enqueue(APICallback())
    }

    override fun onBackPressed() {

        if (supportFragmentManager?.backStackEntryCount!! > 1) {
            super.onBackPressed()
        } else {
            finish()
        }
    }

    /**
     * Get the topmost fragment in the backstack and call its onHomeButtonPressed method.
     * This way each fragment will be able to handle its own home button press function.
     * By default function will be there in BaseFragment which will pop from BackStack.
     */
    override fun onSupportNavigateUp(): Boolean {
        var fragment = BaseFragment.mCurrentFragment
        return if (fragment != null) {
            fragment.onHomeButtonPressed()
            true
        } else
            super.onSupportNavigateUp()
    }


    fun replaceFragment(fragment: Fragment) {
        if (supportFragmentManager != null) {
            val fragmentTransaction = supportFragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.container, fragment, fragment.javaClass.simpleName)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }
    }
}
