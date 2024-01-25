package com.ksajja.newone.fragment

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import android.widget.DatePicker
import java.util.*

/**
 * Created by ksajja on 2/12/18.
 */

class DatePickerFragment : DialogFragment(), DatePickerDialog.OnDateSetListener {

    private var mDateToBeSet: Date? = null
    private var listener: DatePickerListener? = null

    interface DatePickerListener {
        fun onDateSet(year: Int, monthOfYear: Int, dayOfMonth: Int)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val year: Int
        val month: Int
        val day: Int
        if (mDateToBeSet == null) {
            // Use the current date as the default date in the picker
            val c = Calendar.getInstance()
            year = c.get(Calendar.YEAR)
            month = c.get(Calendar.MONTH)
            day = c.get(Calendar.DAY_OF_MONTH)
        } else {
            val calendar = Calendar.getInstance()
            calendar.time = mDateToBeSet
            year = calendar.get(Calendar.YEAR)
            month = calendar.get(Calendar.MONTH)
            day = calendar.get(Calendar.DAY_OF_MONTH)
        }
        // Create a new instance of DatePickerDialog and return it
        return DatePickerDialog(activity!!, this, year, month, day)
    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
        // Do something with the date chosen by the user
        listener!!.onDateSet(year, month, day)
    }

    companion object {

        fun newInstance(dateToBeSet: Date?, listener: DatePickerListener): DatePickerFragment {
            val datePickerFragment = DatePickerFragment()
            datePickerFragment.listener = listener
            datePickerFragment.mDateToBeSet = dateToBeSet
            return datePickerFragment
        }

        fun newInstance(listener: DatePickerListener): DatePickerFragment {
            val datePickerFragment = DatePickerFragment()
            datePickerFragment.listener = listener
            return datePickerFragment
        }
    }
}