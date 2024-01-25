package com.ksajja.newone.widgets


import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.appcompat.app.AlertDialog
import android.widget.ProgressBar
import com.ksajja.newone.R

/*
 * Copyright 2017 Qi Li
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



// default constructor. Needed so rotation doesn't crash
class DelayedProgressDialog : DialogFragment() {

    private var mProgressBar: ProgressBar? = null
    private var startedShowing: Boolean = false
    private var mStartMillisecond: Long = 0
    private var mStopMillisecond: Long = 0

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity!!)
        val inflater = activity!!.layoutInflater

        builder.setView(inflater.inflate(R.layout.dialog_progress, null))
        return builder.create()
    }

    override fun onStart() {
        super.onStart()
        mProgressBar = dialog.findViewById(R.id.progress)

        if (dialog.window != null) {
            val px = (PROGRESS_CONTENT_SIZE_DP * resources.displayMetrics.density).toInt()
            dialog.window!!.setLayout(px, px)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }

    override fun show(fm: FragmentManager, tag: String) {
        mStartMillisecond = System.currentTimeMillis()
        startedShowing = false
        mStopMillisecond = java.lang.Long.MAX_VALUE

        val handler = Handler()
        handler.postDelayed({
            if (mStopMillisecond > System.currentTimeMillis())
                showDialogAfterDelay(fm, tag)
        }, DELAY_MILLISECOND.toLong())
    }

    private fun showDialogAfterDelay(fm: FragmentManager, tag: String) {
        startedShowing = true
        val ft = fm.beginTransaction()
        ft.add(this, tag)
        ft.commitAllowingStateLoss()
    }

    fun cancel() {
        mStopMillisecond = System.currentTimeMillis()

        if (startedShowing) {
            if (mProgressBar != null) {
                cancelWhenShowing()
            } else {
                cancelWhenNotShowing()
            }
        }
    }

    private fun cancelWhenShowing() {
        if (mStopMillisecond < mStartMillisecond + DELAY_MILLISECOND.toLong() + SHOW_MIN_MILLISECOND.toLong()) {
            val handler = Handler()
            handler.postDelayed({ dismissAllowingStateLoss() }, SHOW_MIN_MILLISECOND.toLong())
        } else {
            dismissAllowingStateLoss()
        }
    }

    private fun cancelWhenNotShowing() {
        val handler = Handler()
        handler.postDelayed({ dismissAllowingStateLoss() }, DELAY_MILLISECOND.toLong())
    }

    companion object {
        private val DELAY_MILLISECOND = 450
        private val SHOW_MIN_MILLISECOND = 300
        private val PROGRESS_CONTENT_SIZE_DP = 80
    }
}