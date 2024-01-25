package com.ksajja.newone.fragment


import android.os.Bundle
import androidx.annotation.Nullable
import androidx.fragment.app.Fragment
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import butterknife.OnClick
import com.livinglifetechway.quickpermissions.annotations.WithPermissions
import com.ksajja.newone.Constants
import com.ksajja.newone.R
import com.ksajja.newone.base.BaseFragment
import com.ksajja.newone.extension.formatDateToDisplay
import com.ksajja.newone.extension.formatTimeToDisplay
import com.ksajja.newone.extension.getDate
import com.ksajja.newone.extension.getTime
import com.ksajja.newone.model.Task
import com.ksajja.newone.network.APICallback
import com.ksajja.newone.network.APIService
import com.ksajja.newone.util.DateUtils
import kotlinx.android.synthetic.main.fragment_task_detail.*
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class TaskEditFragment : BaseFragment(R.layout.fragment_task_detail) {


    private var mTask: Task? = null
    private var taskId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        if (arguments != null) {
            taskId = arguments!!.getString(Constants.Keys.TASK_ID)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater!!.inflate(R.menu.generic_save_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.action_save -> {
                saveTask()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        getTaskDetails()
    }

    private fun getTaskDetails() {
        if (taskId.isNullOrBlank()) return

        APIService.instance.getTaskDetails(taskId!!).enqueue(object : APICallback<Task>() {
            override fun onSuccess(value: Task) {
                setData(value)
            }
        })
    }

    @WithPermissions(permissions = arrayOf(android.Manifest.permission.CAMERA))
    fun methodWithPermissions() {
        Toast.makeText(context, "Permissions granted", Toast.LENGTH_SHORT).show()
        //
        // Do the stuff with permissions safely
        //
        // .....
    }

    private fun setData(task: Task) {
        if (task == null) return
        mTask = task

        taskEditText.setText(mTask?.title ?: "")
        val recommendationText = StringBuilder()
        if (mTask!!.packages != null) {
            for (recommendation in mTask!!.packages!!) {
                if (!recommendation.info.isNullOrBlank()) {
                    recommendationText.append(recommendation.info).append("\n")
                }
            }
        }

        assistantNotesTextView.text = recommendationText.toString()

        notesEditText.setText(mTask?.note)
        prioritySpinner.setSelection(getSelection(mTask?.priority ?: ""))
        setDateToDisplay(mTask?.dueDate, dueDateTextView, dueDateTimeTextView)
        setDateToDisplay(mTask?.reminderDate, reminderDateTextView, reminderDateTimeTextView)
        toggleDateTitles()

        setToolBarTitle(mTask?.title)
    }

    private fun getSelection(priority: String): Int {
        if (priority.isNullOrBlank()) return 0
        return when (priority) {
            "Low" -> 1
            "Medium" -> 2
            "High" -> 3
            else -> 0
        }
    }

    private fun getPriority(i: Int): String {
        return when (i) {
            1 -> "Low"
            2 -> "Medium"
            3 -> "High"
            else -> ""
        }
    }


    private fun toggleDateTitles() {
        if (dueDateTextView.text.isNotEmpty()) {
            dueDateTitleTextView.text = "Due date"
            dueDateTextView.visibility = View.VISIBLE
        } else {
            dueDateTitleTextView.text = "Add due date"
            dueDateTextView.visibility = View.GONE
        }

        if (reminderDateTextView.text.isNotEmpty()) {
            reminderDateTitleTextView.text = "Reminder date"
            removeReminderDate.visibility = View.VISIBLE
        } else {
            reminderDateTitleTextView.text = "Add reminder date"
            removeReminderDate.visibility = View.GONE
        }
    }

    private fun setDateToDisplay(dateTime: Date?, dateTextView: TextView, timeTextView: TextView) {
        if (dateTime == null) {
            dateTextView.text = ""
            timeTextView.text = ""
            return
        }
        dateTextView.text = dateTime.formatDateToDisplay()
        timeTextView.text = dateTime.formatTimeToDisplay()
    }


    private fun getDateToStore(dateTextView: TextView, timeTextView: TextView): Date? {
        val calendar = Calendar.getInstance()
        val dueDate = dateTextView.text.toString()
        val dueDateTime = timeTextView.text.toString()
        //read date
        val dueDateObject = dueDate.getDate()
        calendar.time = dueDateObject ?: return null

        //Read hours and minutes
        val timeCalendar = Calendar.getInstance()
        val dueDateTimeObject = dueDateTime.getTime()
        timeCalendar.time = dueDateTimeObject ?: return null
        //set hours and minutes
        calendar.set(Calendar.HOUR_OF_DAY, timeCalendar.get(Calendar.HOUR_OF_DAY))
        calendar.set(Calendar.MINUTE, timeCalendar.get(Calendar.MINUTE))
        //return DateUtils.formatDateToStore(calendar);
        return calendar.time
    }

    private fun saveTask() {
        if (mTask == null || taskEditText.text.isEmpty()) return
        mTask!!.title = taskEditText.text.toString()
        mTask!!.dueDate = getDateToStore(dueDateTextView, dueDateTimeTextView)
        mTask!!.reminderDate = getDateToStore(reminderDateTextView, reminderDateTimeTextView)
        mTask!!.note = notesEditText.text.toString()
        mTask!!.priority = getPriority(prioritySpinner.getSelectedItemPosition())
        APIService.instance.updateTask(mTask!!.taskId!!, mTask!!).enqueue(object : APICallback<Task>() {
            override fun onSuccess(value: Task) {
                if (fragmentManager != null) {
                    fragmentManager!!.popBackStack()
                }
            }
        })
    }

    @OnClick(R.id.deleteTaskButton)
    fun deleteTask() {
        if (mTask == null) return
        APIService.instance.deleteTask(mTask!!.taskId!!).enqueue(object : APICallback<Boolean>() {
            override fun onSuccess(value: Boolean) {
                if (fragmentManager != null) {
                    fragmentManager!!.popBackStack()
                }
            }
        })
    }

    @Nullable
    @OnClick(R.id.dueDateTitleTextView, R.id.dueDateTextView)
    fun onMDueDateTitleTextViewClicked() {
        val datePickerFragment = DatePickerFragment.newInstance(dueDateTextView.text.toString().getDate()
                , object : DatePickerFragment.DatePickerListener {
            override fun onDateSet(year: Int, monthOfYear: Int, dayOfMonth: Int) {
                dueDateTextView.text = DateUtils.formatDateToDisplay(year, monthOfYear, dayOfMonth)
                toggleDateTitles()
                //Add current time as default if due time is not set yet
                if (dueDateTimeTextView.text.isEmpty())
                    dueDateTimeTextView.text = (DateUtils.currentTimeToDisplay)
            }

        })
        datePickerFragment.show(childFragmentManager, "TaskDueDate")
    }

    @Nullable
    @OnClick(R.id.dueDateTimeTextView)
    fun onMDueDateTimeTextViewClicked() {
        val fragment = TimePickerFragment.newInstance(dueDateTimeTextView.text.toString().getDate(), object : TimePickerFragment.TimePickerListener {
            override fun onTimePicked(hour: Int, minute: Int) {
                dueDateTimeTextView.text = (DateUtils.formatTimeToDisplay(hour, minute))
            }
        })
        fragment.show(childFragmentManager, "TaskDueTime")
    }

    @Nullable
    @OnClick(R.id.reminderDateTitleTextView, R.id.reminderDateTextView)
    fun onMReminderDateTitleTextViewClicked() {
        val datePickerFragment = DatePickerFragment
                .newInstance(reminderDateTextView.text.toString().getDate(), object : DatePickerFragment.DatePickerListener {
                    override fun onDateSet(year: Int, monthOfYear: Int, dayOfMonth: Int) {
                        reminderDateTextView.text = DateUtils.formatDateToDisplay(year, monthOfYear, dayOfMonth)
                        toggleDateTitles()
                        //Add current time as default if due time is not set yet
                        if (reminderDateTimeTextView.text.isEmpty())
                            reminderDateTimeTextView.text = (DateUtils.currentTimeToDisplay)
                    }
                })
        datePickerFragment.show(childFragmentManager, "TaskReminderDate")
    }

    @Nullable
    @OnClick(R.id.reminderDateTimeTextView)
    fun onMReminderDateTimeTextViewClicked() {
        val fragment = TimePickerFragment.newInstance(
                reminderDateTimeTextView.text.toString().getTime(), object : TimePickerFragment.TimePickerListener {
            override fun onTimePicked(hour: Int, minute: Int) {
                reminderDateTimeTextView.text = DateUtils.formatTimeToDisplay(hour, minute)
            }
        })
        fragment.show(childFragmentManager, "TaskReminderTime")
    }

    @Nullable
    @OnClick(R.id.removeReminderDate)
    fun onViewClicked() {
        if (mTask != null) {
            mTask!!.reminderDate = null
        }
        reminderDateTextView.text = ""
        reminderDateTimeTextView.text = ""
        toggleDateTitles()
    }

    companion object {

        fun newInstance(taskId: String): TaskEditFragment {
            val fragment = TaskEditFragment()
            val bundle = Bundle()
            bundle.putString(Constants.Keys.TASK_ID, taskId)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun getToolBarTitle(): CharSequence? {
        return mTask?.title
    }
}
