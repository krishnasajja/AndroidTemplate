package com.ksajja.newone.adapter

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import com.ksajja.newone.Constants
import com.ksajja.newone.R
import com.ksajja.newone.extension.formatDateToDisplay
import com.ksajja.newone.extension.formatTimeToDisplay
import com.ksajja.newone.extension.isNotNullNorEmpty
import com.ksajja.newone.interfaces.TaskTemplateListener
import com.ksajja.newone.model.HeaderModel
import com.ksajja.newone.model.Task
import com.ksajja.newone.model.Template
import com.ksajja.newone.model.TemplateItem
import com.ksajja.newone.model.enums.TaskStatus
import com.ksajja.newone.util.GlideApp
import java.util.*

/**
 * Created by shivakartik on 3/1/18.
 */

class TaskTemplatesAdapter(private val mArrayList: ArrayList<Any?>?)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mListener: TaskTemplateListener? = null

    companion object {
        private val HEADER_TYPE: Int = 1
        private val WEB_ARTICLE_TYPE: Int = 2
        private val HORIZONTAL_SCROLL_TYPE: Int = 3
        private val TASK_SUMMARY_TYPE: Int = 4
    }

    fun setListener(listener: TaskTemplateListener?) {
        mListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder? {
        return when (viewType) {
            HEADER_TYPE -> HeaderViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.list_item_task_template_header, parent, false))
            WEB_ARTICLE_TYPE -> WebArticleViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.list_item_task_template_web_article, parent, false))
            HORIZONTAL_SCROLL_TYPE -> HorizontalScrollViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.list_item_task_horizontal_scroll, parent, false))
            TASK_SUMMARY_TYPE -> TaskSummaryViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.list_item_task_summary, parent, false))
            else -> EmptyViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.list_item_empty_view, parent, false))
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            mArrayList?.get(position) is HeaderModel -> 1
            mArrayList?.get(position) is Template -> 3
            mArrayList?.get(position) is Task -> 4
            mArrayList?.get(position) is TemplateItem &&
                    Constants.TemplateTypes.WEB_ARTICLES == ((mArrayList?.get(position) as TemplateItem).templateType) -> 2
            else -> 0
        }
    }

    override fun getItemCount(): Int {
        // if arrayList is not null we are getting the size else we are returning 0
        return mArrayList?.size ?: 0
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        when {
            holder is HeaderViewHolder -> holder.setData(mArrayList?.get(position) as HeaderModel?)
            holder is WebArticleViewHolder -> holder.setData(mArrayList?.get(position) as TemplateItem?, mListener)
            holder is HorizontalScrollViewHolder -> holder.setData(mArrayList?.get(position) as Template?, mListener)
            holder is TaskSummaryViewHolder -> holder.setData(mArrayList?.get(position) as Task?,mListener)
        }
    }

    internal inner class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var mTitleTextView: TextView = view.findViewById(R.id.taskTemplateHeaderTextView)
        var mSubTitleTextView: TextView = view.findViewById(R.id.taskTemplateSubHeaderTextView)

        fun setData(headerModel: HeaderModel?) {
            mTitleTextView.text = headerModel?.header
            mSubTitleTextView.text = headerModel?.subHeader
            mSubTitleTextView.visibility =
                    if (headerModel?.subHeader?.isNullOrBlank() != false) View.GONE else View.VISIBLE

        }
    }

    internal inner class TaskSummaryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var mTaskNameTextView: TextView = view.findViewById(R.id.taskNameTextView)
        var mTaskDueDateTextView: TextView = view.findViewById(R.id.taskDueDateTextView)
        var mReminderTimeTextView: TextView = view.findViewById(R.id.reminderTimeTextView)
        var mReminderDayTextView: TextView = view.findViewById(R.id.reminderDayTextView)
        var mMarkAsDoneRadioButton: RadioButton = view.findViewById(R.id.markAsDoneRadioButton)

        fun setData(task: Task?, listener: TaskTemplateListener?) {
            mTaskNameTextView.text = task?.title

            var dueDateText: String? = "";
            if(task?.dueDate?.formatDateToDisplay().isNotNullNorEmpty()){
                dueDateText = "Due " + task?.dueDate?.formatDateToDisplay()
                if(task?.dueDate?.formatTimeToDisplay().isNotNullNorEmpty())
                    dueDateText = dueDateText + " @ " + task?.dueDate?.formatTimeToDisplay()
            } else{
                mTaskDueDateTextView.visibility = View.GONE
            }
            mTaskDueDateTextView.text = dueDateText
            if(task?.reminderDate?.formatTimeToDisplay().isNotNullNorEmpty())
            mReminderTimeTextView.text = "Remind me @ " + task?.reminderDate?.formatTimeToDisplay()
            else {
                if (task?.reminderDate?.formatDateToDisplay().isNotNullNorEmpty())
                    mReminderTimeTextView.text = "Remind me on"
                else{
                    mReminderTimeTextView.visibility = View.GONE
                    mReminderDayTextView.visibility = View.GONE
                }
            }
            mReminderDayTextView.text = task?.reminderDate?.formatDateToDisplay()
            mMarkAsDoneRadioButton.setOnClickListener(null)
            if(task?.taskStatus == TaskStatus.COMPLETED){
                mMarkAsDoneRadioButton.isChecked = true
                mMarkAsDoneRadioButton.text = "Marked as done"
            } else{
                mMarkAsDoneRadioButton.isChecked = false
                mMarkAsDoneRadioButton.text = "Mark as done"
            }
            mMarkAsDoneRadioButton.setOnClickListener({
                mMarkAsDoneRadioButton.isChecked = !mMarkAsDoneRadioButton.isChecked
                if(listener != null)
                    listener.onMarkAsDoneClicked(mMarkAsDoneRadioButton.isChecked)
            })

        }

    }


    internal inner class WebArticleViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var mRootView: View = view
        var mWebArticleImageView: ImageView = view.findViewById(R.id.webArticleImageView)
        var mHeadingTextView: TextView = view.findViewById(R.id.webArticleHeadingTextView)
        var mDescriptionTextView: TextView = view.findViewById(R.id.webArticleDescriptionTextView)

        fun setData(templateItem: TemplateItem?, listener: TaskTemplateListener?) {
            mHeadingTextView.text = templateItem?.title
            mDescriptionTextView.text = templateItem?.description

            if (templateItem?.previewImage.isNotNullNorEmpty()) {
                GlideApp.with(mWebArticleImageView.context)
                        .asBitmap()
                        .load(templateItem?.previewImage)
                        .into(mWebArticleImageView)
            }

            mRootView.setOnClickListener(View.OnClickListener {
                if (listener != null)
                    listener.onTemplateItemClick(templateItem)
            })
        }
    }

    internal inner class HorizontalScrollViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var mAdapter: TaskHorizontalTemplateAdapter? = null
        var linearLayoutManager: LinearLayoutManager = LinearLayoutManager(view.context, LinearLayoutManager.HORIZONTAL, false)
        var mTitleTextView: TextView = view.findViewById(R.id.horizontalScrollHeaderTextView)
        var mSubTitleTextView: TextView = view.findViewById(R.id.horizontalScrollSubHeaderTextView)
        var mHorizontalScrollRecyclerView: RecyclerView = view.findViewById(R.id.horizontalScrollRecyclerView)

        fun setData(template: Template?, listener: TaskTemplateListener?) {
            mTitleTextView.text = template?.title
            mSubTitleTextView.text = template?.commentary
            mSubTitleTextView.visibility =
                    if (template?.commentary?.isNullOrBlank() == true) View.GONE else View.VISIBLE

            if (mAdapter == null)
                mAdapter = TaskHorizontalTemplateAdapter()
            mAdapter?.setListener(listener)
            mAdapter?.setData(template?.items)
            mHorizontalScrollRecyclerView.layoutManager = linearLayoutManager
            mHorizontalScrollRecyclerView.adapter = mAdapter
        }
    }

    internal inner class EmptyViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    }
}