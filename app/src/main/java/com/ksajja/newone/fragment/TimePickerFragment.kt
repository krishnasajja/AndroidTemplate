package com.ksajja.newone.fragment

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import android.text.format.DateFormat
import android.widget.TimePicker
import java.util.*

/**
 * Created by ksajja on 2/12/18.
 */

class TimePickerFragment : DialogFragment(), TimePickerDialog.OnTimeSetListener {

    private var mTimeToBeSet: Date? = null
    private var listener: TimePickerListener? = null

    interface TimePickerListener {
        fun onTimePicked(hour: Int, minute: Int)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val hour: Int
        val minute: Int
        if (mTimeToBeSet == null) {
            // Use the current time as the default values for the picker
            val c = Calendar.getInstance()
            hour = c.get(Calendar.HOUR_OF_DAY)
            minute = c.get(Calendar.MINUTE)
        } else {
            val calendar = Calendar.getInstance()
            calendar.time = mTimeToBeSet
            hour = calendar.get(Calendar.HOUR_OF_DAY)
            minute = calendar.get(Calendar.MINUTE)
        }

        // Create a new instance of TimePickerDialog and return it
        return TimePickerDialog(activity, this, hour, minute,
                DateFormat.is24HourFormat(activity))
    }

    override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
        if(listener != null)
        listener?.onTimePicked(hourOfDay, minute)
    }

    companion object {

        fun newInstance(timeToBeSet: Date?, listener: TimePickerListener): TimePickerFragment {
            val fragment = TimePickerFragment()
            fragment.mTimeToBeSet = timeToBeSet
            fragment.listener = listener
            return fragment
        }
    }
}