package com.ksajja.newone.adapter

import android.content.Context
import android.graphics.Paint
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.cardview.widget.CardView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemConstants
import com.h6ah4i.android.widget.advrecyclerview.draggable.ItemDraggableRange
import com.h6ah4i.android.widget.advrecyclerview.expandable.*
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractDraggableItemViewHolder
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractExpandableItemAdapter
import com.ksajja.newone.Constants
import com.ksajja.newone.R
import com.ksajja.newone.data_provider.TasksExpandableDataProvider
import com.ksajja.newone.extension.*
import com.ksajja.newone.helper.AppHelper
import com.ksajja.newone.interfaces.TaskActionListener
import com.ksajja.newone.model.HeaderModel
import com.ksajja.newone.model.Package
import com.ksajja.newone.model.Task
import com.ksajja.newone.model.enums.TaskStatus
import com.ksajja.newone.util.DrawableUtils
import com.ksajja.newone.util.ViewUtils
import com.ksajja.newone.widgets.ExpandableItemIndicator
import java.util.*


/**
 * Created by ksajja on 2/28/18.
 */

class TasksAdapter(private var mProvider: TasksExpandableDataProvider,
                   private val mListener: TaskActionListener) :
        AbstractExpandableItemAdapter<TasksAdapter.MyGroupViewHolder,
                TasksAdapter.MyChildViewHolder>(),
        ExpandableDraggableItemAdapter<TasksAdapter.MyGroupViewHolder,
                TasksAdapter.MyChildViewHolder> {

    companion object {

        private const val GROUP_ITEM_VIEW_TYPE_SECTION_HEADER = 0
        private const val GROUP_ITEM_VIEW_TYPE_SECTION_ITEM = 1

        private fun isSectionHeader(holder: MyGroupViewHolder): Boolean {
            val groupViewType = RecyclerViewExpandableItemManager.getGroupViewType(holder.itemViewType)
            return groupViewType != GROUP_ITEM_VIEW_TYPE_SECTION_HEADER
        }
    }

    private var mAllowItemsMoveAcrossSections: Boolean = false

    abstract class MyBaseViewHolder(v: View) : AbstractDraggableItemViewHolder(v), ExpandableItemViewHolder {
        var mContainer: ConstraintLayout = v.findViewById(R.id.taskListItemContainer)
        var mDragHandle: View = v.findViewById(R.id.drag_handle)
        private var mExpandStateFlags: Int = 0

        override fun getExpandStateFlags(): Int {
            return mExpandStateFlags
        }

        override fun setExpandStateFlags(flag: Int) {
            mExpandStateFlags = flag
        }
    }

    class MyGroupViewHolder(v: View) : MyBaseViewHolder(v) {
        var mIndicator: ExpandableItemIndicator = v.findViewById(R.id.indicator)
        var mTextView: TextView = v.findViewById(R.id.listGroupItemTitleText)
    }

    class MyChildViewHolder(v: View) : MyBaseViewHolder(v) {
        var mTitleTextView: TextView = v.findViewById(R.id.taskTitleTextView)
        var mSubTitleTextView: TextView = v.findViewById(R.id.taskSubTitleTextView)
        var mCheckBox: CheckBox = v.findViewById(R.id.taskListItemCheckbox)
        var mCardView: CardView = v.findViewById(R.id.taskListCardView)
        var mExtraMenu: LinearLayout = v.findViewById(R.id.taskListExtraMenu)
        var mDeleteButton: ImageView = v.findViewById(R.id.taskListDeleteButton)
        var mAlertButton: ImageView = v.findViewById(R.id.taskListAlertButton)
        var mViewTaskDetails: ImageView = v.findViewById(R.id.taskListViewDetails)
    }

    init {
        // ExpandableItemAdapter, ExpandableDraggableItemAdapter require stable ID,
        // and also have to implement the getGroupItemId()/getChildItemId() methods appropriately.
        setHasStableIds(true)
    }

    override fun getGroupCount(): Int {
        return mProvider.groupCount
    }

    override fun getChildCount(groupPosition: Int): Int {
        return mProvider.getChildCount(groupPosition)
    }

    override fun getGroupId(groupPosition: Int): Long {
        return mProvider.getGroupItem(groupPosition).groupId
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return mProvider.getChildItem(groupPosition, childPosition).childId
    }

    override fun getGroupItemViewType(groupPosition: Int): Int {
        val item = mProvider.getGroupItem(groupPosition)

        return if (item.isSectionHeader) {
            GROUP_ITEM_VIEW_TYPE_SECTION_HEADER
        } else {
            GROUP_ITEM_VIEW_TYPE_SECTION_ITEM
        }
    }

    override fun getChildItemViewType(groupPosition: Int, childPosition: Int): Int {
        return 0
    }

    override fun onCreateGroupViewHolder(parent: ViewGroup, viewType: Int): MyGroupViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        val v: View
        v = when (viewType) {
            GROUP_ITEM_VIEW_TYPE_SECTION_HEADER -> inflater.inflate(R.layout.list_section_header, parent, false)
            GROUP_ITEM_VIEW_TYPE_SECTION_ITEM -> inflater.inflate(R.layout.list_group_item_draggable, parent, false)
            else -> throw IllegalStateException("Unexpected viewType (= $viewType)")
        }

        return MyGroupViewHolder(v)
    }

    override fun onCreateChildViewHolder(parent: ViewGroup, viewType: Int): MyChildViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val v = inflater.inflate(R.layout.list_item_draggable, parent, false)
        return MyChildViewHolder(v)
    }

    override fun onBindGroupViewHolder(holder: MyGroupViewHolder, groupPosition: Int, viewType: Int) {
        when (viewType) {
            GROUP_ITEM_VIEW_TYPE_SECTION_HEADER -> onbBindSectionHeaderGroupViewHolder(holder, groupPosition)
            GROUP_ITEM_VIEW_TYPE_SECTION_ITEM -> onBindItemGroupViewHolder(holder, groupPosition)
        }
    }

    private fun onbBindSectionHeaderGroupViewHolder(holder: MyGroupViewHolder, groupPosition: Int) {
        // group item
        val item = mProvider.getGroupItem(groupPosition)

    }

    private fun onBindItemGroupViewHolder(holder: MyGroupViewHolder, groupPosition: Int) {
        // group item
        val item = mProvider.getGroupItem(groupPosition)

        // set text
        holder.mTextView.text = item.text


        // set background resource (target view ID: container)
        val dragState = holder.dragStateFlags
        val expandState = holder.expandStateFlags

        if (dragState and DraggableItemConstants.STATE_FLAG_IS_UPDATED != 0 || expandState and ExpandableItemConstants.STATE_FLAG_IS_UPDATED != 0) {
            val bgResId: Int
            val isExpanded: Boolean = expandState and ExpandableItemConstants.STATE_FLAG_IS_EXPANDED != 0
            val animateIndicator = expandState and ExpandableItemConstants.STATE_FLAG_HAS_EXPANDED_STATE_CHANGED != 0

            if (dragState and DraggableItemConstants.STATE_FLAG_IS_ACTIVE != 0) {
                bgResId = R.drawable.bg_group_item_dragging_active_state

                // need to clear drawable state here to get correct appearance of the dragging item.
                DrawableUtils.clearState(holder.mContainer.foreground)
            } else if (dragState and DraggableItemConstants.STATE_FLAG_DRAGGING != 0 && dragState and DraggableItemConstants.STATE_FLAG_IS_IN_RANGE != 0) {
                bgResId = R.drawable.bg_group_item_dragging_state
            } else if (expandState and ExpandableItemConstants.STATE_FLAG_IS_EXPANDED != 0) {
                bgResId = R.drawable.bg_group_item_expanded_state
            } else {
                bgResId = R.drawable.bg_group_item_normal_state
            }

            holder.mContainer.setBackgroundResource(bgResId)
            holder.mIndicator.setExpandedState(isExpanded, animateIndicator)
        }
    }

    override fun onBindChildViewHolder(holder: MyChildViewHolder, groupPosition: Int, childPosition: Int, viewType: Int) {
        // child item
        val item = mProvider.getChildItem(groupPosition, childPosition)

        // set text
        if (item.task != null && item.task?.title != null) {
            holder.mTitleTextView.text = item.task?.title
            holder.mTitleTextView.visibility = View.VISIBLE
            if (item.task?.taskStatus == TaskStatus.COMPLETED) {
                holder.mTitleTextView.paintFlags = holder.mTitleTextView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            } else {
                if (item.task?.packages?.size == 1) {
                    var packageValue: Package? = item.task?.packages?.get(0)

                    //Todo convert to for each
                    for (template in packageValue?.templates!!) {
                        if (Constants.TemplateTypes.QUESTION == template.templateType ||
                                Constants.TemplateTypes.CANT_HELP == template.templateType ||
                                Constants.TemplateTypes.INAPPROPRIATE == template.templateType ||
                                Constants.TemplateTypes.COMMENTARY == template.templateType) {
                            item.task?.cannedMessage = template
                            break;
                        }
                    }
                }
            }
        } else {
            holder.mTitleTextView.visibility = View.GONE
        }

        if (item.task != null && item.task?.dueDate != null) {
            holder.mSubTitleTextView.text = getDisplayDueDate(item.task?.dueDate!!, holder.mSubTitleTextView.context) //DateUtils.formatDateToDisplay(item.task?.dueDate)
            holder.mSubTitleTextView.visibility = View.VISIBLE
            if (item.task?.taskStatus == TaskStatus.COMPLETED) {
                holder.mSubTitleTextView.paintFlags = holder.mSubTitleTextView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            }
        } else {
            holder.mSubTitleTextView.visibility = View.GONE
        }

        holder.mCheckBox.isChecked = item.task?.taskStatus == TaskStatus.COMPLETED
        holder.mCheckBox.setOnClickListener { v ->
            if ((v as CheckBox).isChecked) {
                holder.mTitleTextView.paintFlags = holder.mTitleTextView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                holder.mSubTitleTextView.paintFlags = holder.mSubTitleTextView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                mListener.onTaskDone(item.task!!)
            } else {
                holder.mTitleTextView.paintFlags = 0
                holder.mSubTitleTextView.paintFlags = 0
                mListener.onTaskUndone(item.task!!)
            }
        }

        holder.mExtraMenu.visibility = if (item.task!!.isSelected) View.VISIBLE else View.GONE
        setUpExtraMenu(item.task!!, holder, item.task!!.isSelected)

        holder.mContainer.setOnClickListener {
            item.task!!.isSelected = !item.task!!.isSelected
            setUpExtraMenu(item.task!!, holder, item.task!!.isSelected)
        }

        holder.mViewTaskDetails.setOnClickListener {
            mListener.onTaskClick(item.task!!)
        }

        holder.mDeleteButton.setOnClickListener {
            mListener.onTaskDelete(item.task!!, groupPosition, childPosition)
        }

        holder.mAlertButton.setOnClickListener{
            if(item.task?.cannedMessage != null){
                mListener.onCannedMessageClick(item.task!!)
            }
        }


        val dragState = holder.dragStateFlags

        if (dragState and DraggableItemConstants.STATE_FLAG_IS_UPDATED != 0) {
            val bgResId: Int

            if (dragState and DraggableItemConstants.STATE_FLAG_IS_ACTIVE != 0) {
                bgResId = R.drawable.bg_item_dragging_active_state

                // need to clear drawable state here to get correct appearance of the dragging item.
                DrawableUtils.clearState(holder.mContainer.foreground)
            } else if (dragState and DraggableItemConstants.STATE_FLAG_DRAGGING != 0 && dragState and DraggableItemConstants.STATE_FLAG_IS_IN_RANGE != 0) {
                bgResId = R.drawable.bg_item_dragging_state
            } else {
                bgResId = R.drawable.bg_item_normal_state
            }
            holder.mContainer.setBackgroundResource(bgResId)
        }
    }

    private fun setUpExtraMenu(task: Task, holder: MyChildViewHolder, shouldSelect: Boolean) {
        if (shouldSelect) {
            holder.mCardView.cardElevation = AppHelper.dpToPx(10).toFloat()
            holder.mCardView.useCompatPadding = true
            holder.mExtraMenu.visibility = View.VISIBLE
            holder.mDeleteButton.visibility = View.VISIBLE
            holder.mAlertButton.visibility = View.GONE
        } else {
            holder.mCardView.cardElevation = AppHelper.dpToPx(0).toFloat()
            holder.mCardView.useCompatPadding = false
            holder.mExtraMenu.visibility = View.GONE
            holder.mDeleteButton.visibility = View.GONE
            if (task.cannedMessage != null) {
                holder.mAlertButton.visibility = View.VISIBLE
            } else {
                holder.mAlertButton.visibility = View.GONE
            }
        }
    }

    private fun getDisplayDueDate(dueDate: Date, context: Context): String? {
        return when {
            dueDate.isInPast() -> context.getString(R.string.was_due_on, dueDate.formatDateToDisplay(Constants.DATE_AND_TIME))
            dueDate.isToday() -> context.getString(R.string.due_today, dueDate.formatDateToDisplay())
            dueDate.isTomorrow() -> context.getString(R.string.due_tomorrow, dueDate.formatTimeToDisplay())
            dueDate.isInThisWeek() -> context.getString(R.string.due, dueDate.formatDateToDisplay(Constants.DISPLAY_WEEKDAY), dueDate.formatTimeToDisplay())
            else -> context.getString(R.string.due_on, dueDate.formatDateToDisplay(Constants.DATE_AND_TIME))
        }
    }

    override fun onCheckCanExpandOrCollapseGroup(holder: MyGroupViewHolder, groupPosition: Int, x: Int, y: Int, expand: Boolean): Boolean {
        // check is normal item
        if (!isSectionHeader(holder)) {
            return false
        }

        // check is enabled
        if (!holder.itemView.isEnabled) {
            return false
        }

        val containerView = holder.mContainer
        val dragHandleView = holder.mDragHandle

        val offsetX = containerView.left + (containerView.translationX + 0.5f).toInt()
        val offsetY = containerView.top + (containerView.translationY + 0.5f).toInt()

        return !ViewUtils.hitTest(dragHandleView, x - offsetX, y - offsetY)
    }

    override fun onCheckGroupCanStartDrag(holder: MyGroupViewHolder, groupPosition: Int, x: Int, y: Int): Boolean {
        // check is normal item
        if (!isSectionHeader(holder)) {
            return false
        }

        // x, y --- relative from the itemView's top-left
        val containerView = holder.mContainer
        val dragHandleView = holder.mDragHandle

        val offsetX = containerView.left + (containerView.translationX + 0.5f).toInt()
        val offsetY = containerView.top + (containerView.translationY + 0.5f).toInt()

        return ViewUtils.hitTest(dragHandleView, x - offsetX, y - offsetY)
    }

    override fun onCheckChildCanStartDrag(holder: MyChildViewHolder, groupPosition: Int, childPosition: Int, x: Int, y: Int): Boolean {
        // x, y --- relative from the itemView's top-left
        val containerView = holder.mContainer
        val dragHandleView = holder.mDragHandle

        val offsetX = containerView.left + (containerView.translationX + 0.5f).toInt()
        val offsetY = containerView.top + (containerView.translationY + 0.5f).toInt()

        return ViewUtils.hitTest(dragHandleView, x - offsetX, y - offsetY)
    }

    override fun onGetGroupItemDraggableRange(holder: MyGroupViewHolder, groupPosition: Int): ItemDraggableRange? {
        if (mAllowItemsMoveAcrossSections) {
            return null
        } else {
            // sort within the same section
            val start = findFirstSectionItem(groupPosition)
            val end = findLastSectionItem(groupPosition)

            return GroupPositionItemDraggableRange(start, end)
        }
    }

    override fun onGetChildItemDraggableRange(holder: MyChildViewHolder, groupPosition: Int, childPosition: Int): ItemDraggableRange? {
        return if (mAllowItemsMoveAcrossSections) {
            null
        } else {
            // sort within the same group
            GroupPositionItemDraggableRange(groupPosition, groupPosition)

            //            // sort within the same section
            //            final int start = findFirstSectionItem(groupPosition);
            //            final int end = findLastSectionItem(groupPosition);
            //
            //            return new GroupPositionItemDraggableRange(start, end);
            //
            //            // sort within the specified child range
            //            final int start = 0;
            //            final int end = 2;
            //
            //            return new GroupPositionItemDraggableRange(start, end);
        }
    }

    override fun onCheckGroupCanDrop(draggingGroupPosition: Int, dropGroupPosition: Int): Boolean {
        return true
    }

    override fun onCheckChildCanDrop(draggingGroupPosition: Int, draggingChildPosition: Int, dropGroupPosition: Int, dropChildPosition: Int): Boolean {
        val item = mProvider.getGroupItem(dropGroupPosition)

        return !item.isSectionHeader
    }

    override fun onMoveGroupItem(fromGroupPosition: Int, toGroupPosition: Int) {
        mProvider.moveGroupItem(fromGroupPosition, toGroupPosition)
    }

    override fun onMoveChildItem(fromGroupPosition: Int, fromChildPosition: Int, toGroupPosition: Int, toChildPosition: Int) {
        mProvider.moveChildItem(fromGroupPosition, fromChildPosition, toGroupPosition, toChildPosition)
    }

    override fun onGroupDragStarted(groupPosition: Int) {
        notifyDataSetChanged()
    }

    override fun onChildDragStarted(groupPosition: Int, childPosition: Int) {
        notifyDataSetChanged()
    }

    override fun onGroupDragFinished(fromGroupPosition: Int, toGroupPosition: Int, result: Boolean) {
        notifyDataSetChanged()
    }

    override fun onChildDragFinished(fromGroupPosition: Int, fromChildPosition: Int, toGroupPosition: Int, toChildPosition: Int, result: Boolean) {
        notifyDataSetChanged()
    }

    private fun findFirstSectionItem(position: Int): Int {
        var position = position
        val item = mProvider.getGroupItem(position)

        if (item.isSectionHeader) {
            throw IllegalStateException("section item is expected")
        }

        while (position > 0) {
            val prevItem = mProvider.getGroupItem(position - 1)

            if (prevItem.isSectionHeader) {
                break
            }

            position -= 1
        }

        return position
    }

    private fun findLastSectionItem(position: Int): Int {
        var position = position
        val item = mProvider.getGroupItem(position)

        if (item.isSectionHeader) {
            throw IllegalStateException("section item is expected")
        }

        val lastIndex = groupCount - 1

        while (position < lastIndex) {
            val nextItem = mProvider.getGroupItem(position + 1)

            if (nextItem.isSectionHeader) {
                break
            }

            position += 1
        }

        return position
    }

    fun setAllowItemsMoveAcrossSections(allowed: Boolean) {
        mAllowItemsMoveAcrossSections = allowed
    }

    fun setProvider(provider: TasksExpandableDataProvider) {
        mProvider = provider
    }

    fun filter(str: String? = null) {
        mProvider.processData(str)
        notifyDataSetChanged()
    }
}