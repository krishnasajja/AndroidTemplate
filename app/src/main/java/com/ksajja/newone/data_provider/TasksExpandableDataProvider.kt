package com.ksajja.newone.data_provider


import android.content.Context
import com.h6ah4i.android.widget.advrecyclerview.expandable.RecyclerViewExpandableItemManager
import com.ksajja.newone.R
import com.ksajja.newone.extension.isInNextWeek
import com.ksajja.newone.extension.isInThisWeek
import com.ksajja.newone.extension.isTodayOrPast
import com.ksajja.newone.extension.isTomorrow
import com.ksajja.newone.model.Task
import java.util.*

/**
 * This class sorts the data in ascending order based on date. Fills the mData with Groups and
 * respective children.
 * Created by ksajja on 2/28/18.
 */

class TasksExpandableDataProvider(private var mTaskList: List<Task>?, private var mContext: Context) : AbstractExpandableDataProvider() {

    companion object {
        private const val TODAY_OR_PAST = "todayOrPast"
        private const val TOMORROW = "tomorrow"
        private const val IN_THIS_WEEK = "inThisWeek"
        private const val IN_NEXT_WEEK = "inNextWeek"
        private const val LATER = "later"
    }

    private val mData: MutableList<Pair<ConcreteGroupData, ArrayList<ConcreteChildData>>> = ArrayList()

    // for undo group item
    private var mLastRemovedGroup: Pair<TasksExpandableDataProvider.ConcreteGroupData, ArrayList<TasksExpandableDataProvider.ConcreteChildData>>? = null
    private var mLastRemovedGroupPosition = -1

    // for undo child item
    private var mLastRemovedChild: TasksExpandableDataProvider.ConcreteChildData? = null
    private var mLastRemovedChildParentGroupId: Long = -1
    private var mLastRemovedChildPosition = -1

    override val groupCount: Int
        get() = mData.size

    fun setData(taskList: ArrayList<Task>) {
        mTaskList = taskList
        processData()
    }

    init {
        processData()
    }

    fun processData(filterText: String? = null) {
        val sortedList = mTaskList?.sortedWith(compareBy({ it.dueDate }))
        var index = 0
        if (sortedList?.isNotEmpty()!!) {
            mData.clear()
            var groupId = 101L
            index = fillData(groupId++, mContext.getString(R.string.today), sortedList, index, filterText, TODAY_OR_PAST)
            index = fillData(groupId++, mContext.getString(R.string.tomorrow), sortedList, index, filterText, TOMORROW)
            index = fillData(groupId++, mContext.getString(R.string.this_week), sortedList, index, filterText, IN_THIS_WEEK)
            index = fillData(groupId++, mContext.getString(R.string.next_week), sortedList, index, filterText, IN_NEXT_WEEK)
            fillData(groupId++, mContext.getString(R.string.others), sortedList, index, filterText, "")
        }
    }

    private fun fillData(groupId: Long, groupText: String, sortedList: List<Task>, ind: Int, filterText: String?, dateRange: String): Int {
        var index = ind
        if (index < sortedList.size && (sortedList[index].dueDate == null || isDateInRange(sortedList[index].dueDate, dateRange))) {
            val group = ConcreteGroupData(groupId, groupText)
            val children = ArrayList<TasksExpandableDataProvider.ConcreteChildData>()
            while (index < sortedList.size && (sortedList[index].dueDate == null || isDateInRange(sortedList[index].dueDate, dateRange))) {
                if (filterText == null || sortedList[index].title.toLowerCase().contains(filterText.toLowerCase())) {
                    val childId = group.generateNewChildId()
                    children.add(ConcreteChildData(childId, "", sortedList[index]))
                }
                index++
            }
            mData.add(Pair(group, children))
        }
        return index
    }

    private fun isDateInRange(dueDate: Date?, dateRange: String?): Boolean {
        return when (dateRange) {
            TODAY_OR_PAST -> dueDate!!.isTodayOrPast()
            TOMORROW -> dueDate!!.isTomorrow()
            IN_THIS_WEEK -> dueDate!!.isInThisWeek()
            IN_NEXT_WEEK -> dueDate!!.isInNextWeek()
            else -> true
        }
    }

    override fun getChildCount(groupPosition: Int): Int {
        return mData[groupPosition].second.size
    }

    override fun getGroupItem(groupPosition: Int): ConcreteGroupData {
        if (groupPosition < 0 || groupPosition >= groupCount) {
            throw IndexOutOfBoundsException("groupPosition = $groupPosition")
        }

        return mData[groupPosition].first
    }

    override fun getChildItem(groupPosition: Int, childPosition: Int): ConcreteChildData {
        if (groupPosition < 0 || groupPosition >= groupCount) {
            throw IndexOutOfBoundsException("groupPosition = $groupPosition")
        }

        val children = mData[groupPosition].second

        if (childPosition < 0 || childPosition >= children.size) {
            throw IndexOutOfBoundsException("childPosition = $childPosition")
        }

        return children[childPosition]
    }

    override fun moveGroupItem(fromGroupPosition: Int, toGroupPosition: Int) {
        if (fromGroupPosition == toGroupPosition) {
            return
        }

        val item = mData.removeAt(fromGroupPosition)
        mData.add(toGroupPosition, item)
    }

    override fun moveChildItem(fromGroupPosition: Int, fromChildPosition: Int, toGroupPosition: Int, toChildPosition: Int) {
        if (fromGroupPosition == toGroupPosition && fromChildPosition == toChildPosition) {
            return
        }

        val fromGroup = mData[fromGroupPosition]
        val toGroup = mData[toGroupPosition]

        val item = fromGroup.second.removeAt(fromChildPosition) as ConcreteChildData

        if (toGroupPosition != fromGroupPosition) {
            // assign a new ID
            val newId = (toGroup.first as ConcreteGroupData).generateNewChildId()
            item.childId = newId
        }

        toGroup.second.add(toChildPosition, item)
    }

    override fun removeGroupItem(groupPosition: Int) {
        mLastRemovedGroup = mData.removeAt(groupPosition)
        mLastRemovedGroupPosition = groupPosition

        mLastRemovedChild = null
        mLastRemovedChildParentGroupId = -1
        mLastRemovedChildPosition = -1
    }

    override fun removeChildItem(groupPosition: Int, childPosition: Int) {
        mLastRemovedChild = mData[groupPosition].second.removeAt(childPosition)
        mLastRemovedChildParentGroupId = mData[groupPosition].first.groupId
        mLastRemovedChildPosition = childPosition

        mLastRemovedGroup = null
        mLastRemovedGroupPosition = -1
    }


    override fun undoLastRemoval(): Long {
        return if (mLastRemovedGroup != null) {
            undoGroupRemoval()
        } else if (mLastRemovedChild != null) {
            undoChildRemoval()
        } else {
            RecyclerViewExpandableItemManager.NO_EXPANDABLE_POSITION
        }
    }

    private fun undoGroupRemoval(): Long {
        val insertedPosition: Int
        if (mLastRemovedGroupPosition >= 0 && mLastRemovedGroupPosition < mData.size) {
            insertedPosition = mLastRemovedGroupPosition
        } else {
            insertedPosition = mData.size
        }

        mData.add(insertedPosition, mLastRemovedGroup!!)

        mLastRemovedGroup = null
        mLastRemovedGroupPosition = -1

        return RecyclerViewExpandableItemManager.getPackedPositionForGroup(insertedPosition)
    }

    private fun undoChildRemoval(): Long {
        var group: Pair<AbstractExpandableDataProvider.GroupData, ArrayList<ConcreteChildData>>? = null
        var groupPosition = -1

        // find the group
        for (i in mData.indices) {
            if (mData[i].first.groupId == mLastRemovedChildParentGroupId) {
                group = mData[i]
                groupPosition = i
                break
            }
        }

        if (group == null) {
            return RecyclerViewExpandableItemManager.NO_EXPANDABLE_POSITION
        }

        val insertedPosition: Int
        if (mLastRemovedChildPosition >= 0 && mLastRemovedChildPosition < group.second.size) {
            insertedPosition = mLastRemovedChildPosition
        } else {
            insertedPosition = group.second.size
        }

        group.second.add(insertedPosition, mLastRemovedChild!!)

        mLastRemovedChildParentGroupId = -1
        mLastRemovedChildPosition = -1
        mLastRemovedChild = null

        return RecyclerViewExpandableItemManager.getPackedPositionForChild(groupPosition, insertedPosition)
    }

    class ConcreteGroupData internal constructor(override val groupId: Long, val text: String)
        : AbstractExpandableDataProvider.GroupData() {
        override var isPinned: Boolean = false
        private var mNextChildId: Long = 0

        override val isSectionHeader: Boolean
            get() = false

        init {
            mNextChildId = 0
        }

        fun generateNewChildId(): Long {
            val id = mNextChildId
            mNextChildId += 1
            return id
        }
    }

    class ConcreteChildData internal constructor(override var childId: Long)
        : AbstractExpandableDataProvider.ChildData() {

        var task: Task? = null

        constructor(childId: Long, text: String, taskInput: Task) : this(childId) {
            task = taskInput
        }

        override var isPinned: Boolean = false
    }
}
