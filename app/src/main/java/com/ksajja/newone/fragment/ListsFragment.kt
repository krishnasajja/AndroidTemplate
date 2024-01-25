package com.ksajja.newone.fragment

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import androidx.appcompat.widget.PopupMenu
import android.text.InputType
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import com.ksajja.newone.R
import com.ksajja.newone.activity.MainActivity
import com.ksajja.newone.adapter.ListsAdapter
import com.ksajja.newone.base.BaseFragment
import com.ksajja.newone.interfaces.ListsActionListener
import com.ksajja.newone.model.List
import com.ksajja.newone.network.APICallback
import com.ksajja.newone.network.APIService
import kotlinx.android.synthetic.main.fragment_list.*
import java.util.*


/**
 * Created by ksajja on 2/16/18.
 */

class ListsFragment : BaseFragment(R.layout.fragment_list), ListsActionListener {

    private val mArrayList = ArrayList<List?>()
    private var mAdapter: ListsAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val actionBar = (activity as MainActivity).supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(false)
        actionBar?.setHomeButtonEnabled(false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //For the add item
        mArrayList.add(null)
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        listRecyclerView!!.layoutManager = GridLayoutManager(context, 2)
        mAdapter = ListsAdapter(context!!, this, mArrayList)
        listRecyclerView!!.adapter = mAdapter
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        APIService.instance.lists.enqueue(object : APICallback<kotlin.collections.List<List>>() {
            override fun onSuccess(value: kotlin.collections.List<List>) {
                mArrayList.clear()
                val list = List()
                list.name = context!!.getString(R.string.all)
                mArrayList.add(list)
                mArrayList.addAll(value)
                mArrayList.add(null)
                mAdapter!!.notifyDataSetChanged()
            }
        })
    }

    override fun onListClick(list: List) {
        if (list.listId == null) {
            (activity as MainActivity).supportFragmentManager?.popBackStack()
        } else if (activity is MainActivity) {
            (activity as MainActivity).replaceFragment(TasksFragment.newInstance(list))
        }
    }

    override fun onAddListClick() {
        val builder = AlertDialog.Builder(context!!)
        builder.setTitle(getString(R.string.enter_name_for_new_list))

        // Set up the input
        val input = EditText(context)
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(input)

        // Set up the buttons
        builder.setPositiveButton(context!!.getString(R.string.ok)) { _, _ -> createList(input.text.toString()) }
        builder.setNegativeButton(context!!.getString(R.string.cancel)) { dialog, _ -> dialog.cancel() }

        builder.show()
    }

    override fun onOverFlowMenuClick(position: Int, list: List, popupMenuButton: ImageView) {
        //Creating the instance of PopupMenu
        val popup = PopupMenu(context!!, popupMenuButton)
        //Inflating the Popup using xml file
        popup.menuInflater
                .inflate(R.menu.pop_up_menu_delete, popup.menu)

        //registering popup with OnMenuItemClickListener
        popup.setOnMenuItemClickListener { item ->
            when {
                item?.itemId == R.id.popUpMenuDelete -> {
                    deleteTaskList(position, list)
                    true
                }
                else -> false
            }
        }
        popup.show() //showing popup menu
    }

    private fun deleteTaskList(position: Int, list: List) {
        APIService.instance.deleteList(list.listId!!).enqueue(object : APICallback<Boolean>() {
            override fun onSuccess(value: Boolean) {
                mArrayList.removeAt(position)
                mAdapter?.notifyItemRemoved(position)
            }
        })
    }

    private fun createList(title: String) {
        if (title.isEmpty()) return
        val list = List()
        list.name = title
        APIService.instance.createList(list).enqueue(object : APICallback<List>() {
            override fun onSuccess(value: List) {
                mArrayList.add(mArrayList.size - 1, value)
                mAdapter!!.notifyDataSetChanged()
            }
        })
    }

    override fun shouldShowHomeButton(): Boolean {
        return false
    }

    override fun getToolBarTitle(): CharSequence? {
        return getString(R.string.all_lists)
    }
}
