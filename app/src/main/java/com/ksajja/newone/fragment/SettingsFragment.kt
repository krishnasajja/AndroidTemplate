package com.ksajja.newone.fragment


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import butterknife.OnClick
import com.ksajja.newone.BuildConfig
import com.ksajja.newone.R
import com.ksajja.newone.activity.LoginActivity
import com.ksajja.newone.base.BaseFragment
import com.ksajja.newone.helper.AppHelper
import com.ksajja.newone.helper.SharedPrefsHelper
import kotlinx.android.synthetic.main.fragment_settings.*

/**
 * A simple [Fragment] subclass.
 */
class SettingsFragment : BaseFragment(R.layout.fragment_settings) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        appVersionTextView.text = getString(R.string.app_version, BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE)
        loggedInTextView.text = getString(R.string.logged_in_user, SharedPrefsHelper.loggedInksajjaId)
    }

    @OnClick(R.id.logoutButton)
    fun logout() {
        AppHelper.logout()
        val intent = Intent(activity, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    override fun getToolBarTitle(): CharSequence? {
        return getString(R.string.settings)
    }
}
