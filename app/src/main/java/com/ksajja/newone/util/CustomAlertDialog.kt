package com.ksajja.newone.util

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.TextView
import com.ksajja.newone.R
import com.ksajja.newone.extension.isNotNullNorEmpty


/**
 * Created by shivakartik on 3/14/18.
 */
class CustomAlertDialog(context: Context?) : AlertDialog(context) {
    private var mDialogOnClickListener: DialogOnClickListener? = null
    lateinit var titleTextView: TextView
    lateinit var messageTextView: TextView
    lateinit var positiveButton: Button
    lateinit var negativeButton: Button
    var mTitle: String? = null
    var mMessage: String? = null
    var mPositiveButtonText: String? = null
    var mNegativeButtonText: String? = null

    companion object {
        fun newInstance(context: Context, title: String?, message: String?, positiveButtonText: String?,
                        dialogOnClickListener: DialogOnClickListener): CustomAlertDialog {
            val dialog = CustomAlertDialog(context)
            dialog.mTitle = title
            dialog.mMessage = message
            dialog.mPositiveButtonText = positiveButtonText
            dialog.setOnDialogOnClickListener(dialogOnClickListener)
            return dialog
        }

        fun newInstance(context: Context, title: String?, message: String?,
                        positiveButtonText: String?, negativeButtonText: String?,
                        dialogOnClickListener: DialogOnClickListener): CustomAlertDialog {
            val dialog = CustomAlertDialog(context)
            dialog.mTitle = title
            dialog.mMessage = message
            dialog.mPositiveButtonText = positiveButtonText
            dialog.mNegativeButtonText = negativeButtonText
            dialog.setOnDialogOnClickListener(dialogOnClickListener)
            return dialog
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        init()
    }

    fun init() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_custom_alert)
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        titleTextView = findViewById(R.id.alertTitleTextView)
        messageTextView = findViewById(R.id.alertMessageTextView)
        positiveButton = findViewById(R.id.alertPositiveButton)
        positiveButton.setOnClickListener {
            if (mDialogOnClickListener != null)
                mDialogOnClickListener?.onPositiveButtonClick(this)
        }

        negativeButton = findViewById(R.id.alertNegativeButton)
        negativeButton.setOnClickListener {
            if (mDialogOnClickListener != null)
                mDialogOnClickListener?.onNegativeButtonClick(this)
        }

        setData(mTitle, mMessage, mPositiveButtonText, mNegativeButtonText)
    }

    fun setOnDialogOnClickListener(dialogOnClickListener: DialogOnClickListener?) {
        mDialogOnClickListener = dialogOnClickListener
    }

    fun setData(title: String?, message: String?, positiveButtonText: String?, negativeButtonText: String?) {
        if (title.isNotNullNorEmpty()) {
            titleTextView.text = title
        } else {
            titleTextView.visibility = View.GONE
        }

        if (message.isNotNullNorEmpty()) {
            messageTextView.text = message
        } else {
            messageTextView.visibility = View.GONE
        }

        if (positiveButtonText.isNotNullNorEmpty()) {
            positiveButton.text = positiveButtonText
        } else {
            positiveButton.visibility = View.GONE
        }

        if (negativeButtonText.isNotNullNorEmpty()) {
            negativeButton.text = negativeButtonText
            negativeButton.visibility = View.VISIBLE
        } else {
            negativeButton.visibility = View.GONE
        }

    }

    abstract class DialogOnClickListener {
        abstract fun onPositiveButtonClick(dialog: CustomAlertDialog)

        fun onNegativeButtonClick(dialog: CustomAlertDialog) {

        }

        fun onNeutralButtonClick(dialog: CustomAlertDialog) {

        }
    }

}