package com.ksajja.newone.data_provider

/**
 * Created by ksajja on 2/28/18.
 */

abstract class AbstractExpandableDataProvider {

    abstract val groupCount: Int

    abstract class BaseData {
        abstract var isPinned: Boolean
    }

    abstract class GroupData : BaseData() {
        abstract val isSectionHeader: Boolean
        abstract val groupId: Long
    }

    abstract class ChildData : BaseData() {
        abstract val childId: Long
    }

    abstract fun getChildCount(groupPosition: Int): Int

    abstract fun getGroupItem(groupPosition: Int): GroupData
    abstract fun getChildItem(groupPosition: Int, childPosition: Int): ChildData

    abstract fun moveGroupItem(fromGroupPosition: Int, toGroupPosition: Int)
    abstract fun moveChildItem(fromGroupPosition: Int, fromChildPosition: Int, toGroupPosition: Int, toChildPosition: Int)

    abstract fun removeGroupItem(groupPosition: Int)
    abstract fun removeChildItem(groupPosition: Int, childPosition: Int)

    abstract fun undoLastRemoval(): Long

}
