package com.ksajja.newone.util

import android.view.View

/**
 * Created by ksajja on 2/28/18.
 */

object ViewUtils {
    // Used in onCheckCanExpandOrCollapseGroup, onGetGroupItemDraggableRange, onCheckGroupCanStartDrag
    fun hitTest(v: View, x: Int, y: Int): Boolean {
        val tx = (v.translationX + 0.5f).toInt()
        val ty = (v.translationY + 0.5f).toInt()
        val left = v.left + tx
        val right = v.right + tx
        val top = v.top + ty
        val bottom = v.bottom + ty

        return x >= left && x <= right && y >= top && y <= bottom
    }
}
