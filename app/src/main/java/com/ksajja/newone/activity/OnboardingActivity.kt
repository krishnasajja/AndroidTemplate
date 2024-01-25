package com.ksajja.newone.activity

import android.os.Bundle
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.codemybrainsout.onboarder.AhoyOnboarderActivity
import com.codemybrainsout.onboarder.AhoyOnboarderCard
import com.ksajja.newone.Constants
import com.ksajja.newone.R
import com.ksajja.newone.helper.SharedPrefsHelper
import java.util.*

/**
 * Created by ksajja on 2/8/18.
 */

class OnboardingActivity : AhoyOnboarderActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val pages = ArrayList<AhoyOnboarderCard>()
        pages.add(createCard(getString(R.string.onboard_title_1), getString(R.string.onboard_desc_1), R.drawable.im_onboard_1))
        pages.add(createCard(getString(R.string.onboard_title_2), getString(R.string.onboard_desc_2), R.drawable.im_onboard_2))
        pages.add(createCard(getString(R.string.onboard_title_3), getString(R.string.onboard_desc_3), R.drawable.im_onboard_3))
        setOnboardPages(pages)

        setGradientBackground()

        //        List<Integer> colorList = new ArrayList<>();
        //        colorList.add(android.R.color.holo_green_light);
        //        colorList.add(android.R.color.holo_blue_light);
        //        colorList.add(android.R.color.holo_orange_light);
        //        setColorBackground(colorList);

        //Set finish button text
        setFinishButtonTitle("Let's ksajja!")
        setFinishButtonDrawableStyle(ContextCompat.getDrawable(this, R.drawable.onboard_rounded_button))

    }

    private fun createCard(title: String, desc: String, @DrawableRes resId: Int): AhoyOnboarderCard {
        val margin = dpToPixels(40, this).toInt()
        val iconDimens = dpToPixels(200, this).toInt()
        val ahoyOnboarderCard1 = AhoyOnboarderCard(title, desc, resId)
        ahoyOnboarderCard1.setBackgroundColor(R.color.black_transparent)
        ahoyOnboarderCard1.setTitleColor(R.color.white)
        ahoyOnboarderCard1.setDescriptionColor(R.color.grey_200)
        ahoyOnboarderCard1.setTitleTextSize(dpToPixels(10, this))
        ahoyOnboarderCard1.setDescriptionTextSize(dpToPixels(8, this))
        ahoyOnboarderCard1.setIconLayoutParams(iconDimens, iconDimens, margin, margin, margin, margin)
        return ahoyOnboarderCard1
    }

    override fun onFinishButtonPressed() {
        SharedPrefsHelper.appPreferences.edit().putBoolean(Constants.SharedPrefKeys.SHOULD_SHOW_ONBOARDING, false).apply()
        finish()
    }
}
