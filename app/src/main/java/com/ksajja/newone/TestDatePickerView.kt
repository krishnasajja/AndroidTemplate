package com.ksajja.newone

import android.content.Context
import androidx.databinding.ObservableField
import androidx.appcompat.app.AppCompatActivity
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import com.ksajja.newone.widgets.reminder_composer.interfaces.SecondaryEditView
import com.ksajja.newone.databinding.TestViewOneBinding
import com.ksajja.newone.extension.getDate
import com.ksajja.newone.extension.getTime
import com.ksajja.newone.fragment.DatePickerFragment
import com.ksajja.newone.fragment.TimePickerFragment
import com.ksajja.newone.util.DateUtils
import java.util.*

/**
 * Created by ksajja on 3/2/18.
 */
class TestDatePickerView(override var TAG: String, context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : SecondaryEditView<Date?>(context, attrs, defStyleAttr) {
    var mDateText: ObservableField<String> = ObservableField()
    var mTimeText: ObservableField<String> = ObservableField()
    var date: Date? = null

    init {
        mDateText.set(DateUtils.currentDateToDisplay)
        mTimeText.set(DateUtils.currentTimeToDisplay)
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val binding = TestViewOneBinding.inflate(inflater, this, true)
        binding.view = this
    }

    override fun getData(): Date? {
        return getDateToStore(mDateText.get(), mTimeText.get());
    }

    override fun setData(dataInput: Date?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun onDatePickClicked(v: View) {
        DatePickerFragment.newInstance(null
                , object : DatePickerFragment.DatePickerListener {
            override fun onDateSet(year: Int, monthOfYear: Int, dayOfMonth: Int) {
                mDateText.set(DateUtils.formatDateToDisplay(year, monthOfYear, dayOfMonth))
                if (mTimeText.get().isEmpty())
                    mTimeText.set(DateUtils.currentTimeToDisplay)
            }

        }).show((context as AppCompatActivity).supportFragmentManager, "PICK A DATE")
    }

    fun onTimePickClicked(v: View) {
//        TimePickerFragment.newInstance(DateUtils.getDateFromString(null, Constants.DISPLAY_TIME_FORMAT), object : TimePickerFragment.TimePickerListener {
        TimePickerFragment.newInstance(null, object : TimePickerFragment.TimePickerListener {
            override fun onTimePicked(hour: Int, minute: Int) {
                mTimeText.set(DateUtils.formatTimeToDisplay(hour, minute))
            }
        }).show((context as AppCompatActivity).supportFragmentManager, "TaskDueTime")
    }

    private fun getDateToStore(dateText: String, timeText: String): Date? {
        val calendar = Calendar.getInstance()
        //read date
        calendar.time = dateText.getDate()

        //Read hours and minutes
        val timeCalendar = Calendar.getInstance()
        timeCalendar.time = timeText.getTime()
        //set hours and minutes
        calendar.set(Calendar.HOUR_OF_DAY, timeCalendar.get(Calendar.HOUR_OF_DAY))
        calendar.set(Calendar.MINUTE, timeCalendar.get(Calendar.MINUTE))
        //return DateUtils.formatDateToStore(calendar);
        return calendar.time
    }
}