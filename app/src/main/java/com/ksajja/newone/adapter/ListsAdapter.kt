package com.ksajja.newone.adapter

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.ksajja.newone.R
import com.ksajja.newone.extension.setTextWithVisibility
import com.ksajja.newone.interfaces.ListsActionListener
import com.ksajja.newone.model.List
import java.util.*

/**
 * Created by ksajja on 2/16/18.
 */

class ListsAdapter(private val mContext: Context, private val mListener: ListsActionListener, private val mArrayList: ArrayList<List?>)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val LIST_ITEM = 1
        private const val ADD_LIST_ITEM = 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder? {
        if (viewType == LIST_ITEM)
            return ListViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.lists_list_item, parent, false))
        else if (viewType == ADD_LIST_ITEM)
            return AddListViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.add_list_item, parent, false))
        return null
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ListViewHolder) {
            val list = mArrayList[position]
            holder.mTitleTextView.setTextWithVisibility(list?.name)
            holder.mSubTitleTextView.setTextWithVisibility(list?.taskCount)
        }
    }

    override fun getItemCount(): Int {
        return mArrayList.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (mArrayList[position] != null)
            LIST_ITEM
        else
            ADD_LIST_ITEM
    }

    internal inner class ListViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var mTitleTextView: TextView = view.findViewById(R.id.listTitleText)
        var mSubTitleTextView: TextView = view.findViewById(R.id.listSubTitleText)
        var mParentView: RelativeLayout = view.findViewById(R.id.listItemParent)
        var mOverFlowButton: ImageView = view.findViewById(R.id.listOverFlowButton)

        init {
            mParentView.setOnClickListener { mListener.onListClick(mArrayList[adapterPosition]!!) }
            mOverFlowButton.setOnClickListener { mListener.onOverFlowMenuClick(adapterPosition, mArrayList[adapterPosition]!!, mOverFlowButton) }
        }
    }

    internal inner class AddListViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var imageView: ImageView = view.findViewById(R.id.addListItemImage)

        init {
            imageView.setOnClickListener { mListener.onAddListClick() }
        }
    }
}
