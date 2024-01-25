package com.ksajja.newone.interfaces

import android.widget.ImageView
import com.ksajja.newone.model.List

/**
 * Created by ksajja on 2/16/18.
 */

interface ListsActionListener {
    fun onListClick(list: List)
    fun onAddListClick()
    fun onOverFlowMenuClick(position: Int, list: List, popupMenuButton: ImageView)
}
