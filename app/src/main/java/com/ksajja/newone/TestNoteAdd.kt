package com.ksajja.newone

import android.content.Context
import androidx.databinding.ObservableField
import androidx.appcompat.app.AppCompatActivity
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import com.ksajja.newone.widgets.reminder_composer.interfaces.SecondaryEditView
import com.ksajja.newone.databinding.TestNoteAddBinding
import com.ksajja.newone.fragment.DatePickerFragment
import com.ksajja.newone.util.DateUtils

/**
 * Created by ksajja on 3/6/18.
 */
class TestNoteAdd(override var TAG: String, context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : SecondaryEditView<String>(context, attrs, defStyleAttr) {

    var mText: ObservableField<String> = ObservableField()

    init {
        mText.set("")
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val binding = TestNoteAddBinding.inflate(inflater, this, true)
        binding.view = this
        binding.noteEditContent.onFocusChangeListener = OnFocusChangeListener { _, hasFocus ->
            mIsInEdit = hasFocus
            if(hasFocus)
                mOnEditListener?.onEditStart()
        }
    }

    override fun getData(): String {
        return mText.get();
    }

    override fun setData(dataInput: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun onItemClicked(v: View) {
        DatePickerFragment.newInstance(null
                , object : DatePickerFragment.DatePickerListener {
            override fun onDateSet(year: Int, monthOfYear: Int, dayOfMonth: Int) {
                mText.set(DateUtils.formatDateToDisplay(year, monthOfYear, dayOfMonth))
            }

        }).show((context as AppCompatActivity).supportFragmentManager, "PICK A DATE")
    }

    override fun clearAllFocus() {
        focusedChild?.clearFocus()
    }
}