package com.ksajja.newone.fragment

import android.graphics.Rect
import android.graphics.drawable.NinePatchDrawable
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.appcompat.widget.SearchView
import android.text.TextUtils
import android.view.*
import android.view.MotionEvent.ACTION_DOWN
import com.h6ah4i.android.widget.advrecyclerview.animator.DraggableItemAnimator
import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager
import com.h6ah4i.android.widget.advrecyclerview.expandable.RecyclerViewExpandableItemManager
import com.h6ah4i.android.widget.advrecyclerview.utils.WrapperAdapterUtils
import com.ksajja.newone.Constants
import com.ksajja.newone.R
import com.ksajja.newone.TestDatePickerView
import com.ksajja.newone.TestNoteAdd
import com.ksajja.newone.activity.MainActivity
import com.ksajja.newone.adapter.TasksAdapter
import com.ksajja.newone.base.BaseFragment
import com.ksajja.newone.data_provider.TasksExpandableDataProvider
import com.ksajja.newone.databinding.FragmentRecyclerListViewBinding
import com.ksajja.newone.helper.AppHelper
import com.ksajja.newone.interfaces.TaskActionListener
import com.ksajja.newone.interfaces.TaskLoadingListener
import com.ksajja.newone.model.Task
import com.ksajja.newone.model.TemplateItem
import com.ksajja.newone.model.enums.TaskStatus
import com.ksajja.newone.network.APICallback
import com.ksajja.newone.network.APIService
import com.ksajja.newone.util.CustomAlertDialog
import com.ksajja.newone.widgets.reminder_composer.DefaultTabMenuBtnView
import com.ksajja.newone.widgets.reminder_composer.ReminderComposer
import com.ksajja.newone.widgets.reminder_composer.listener.ComposerClickerActionListener
import com.ksajja.newone.widgets.reminder_composer.listener.ComposerEditListener
import com.ksajja.newone.widgets.reminder_composer.listener.ComposerStateChangeListener
import kotlinx.android.synthetic.main.fragment_recycler_list_view.*
import org.parceler.Parcels
import java.util.*


/**
 * Created by ksajja on 2/28/18.
 *
 * This Fragment will display either all Tasks or Tasks of specific Task list.
 */

class TasksFragment : BaseFragment(R.layout.fragment_recycler_list_view),
        RecyclerViewExpandableItemManager.OnGroupCollapseListener,
        RecyclerViewExpandableItemManager.OnGroupExpandListener,
        TaskLoadingListener, TaskActionListener {

    private var mRecyclerView: RecyclerView? = null
    private var mLayoutManager: RecyclerView.LayoutManager? = null
    private var mAdapter: RecyclerView.Adapter<*>? = null
    private var mWrappedAdapter: RecyclerView.Adapter<*>? = null
    private var mRecyclerViewExpandableItemManager: RecyclerViewExpandableItemManager? = null
    private var mRecyclerViewDragDropManager: RecyclerViewDragDropManager? = null
    private var mTasks = ArrayList<Task>()
    private var dataProvider: TasksExpandableDataProvider? = null
    private var mList: com.ksajja.newone.model.List? = null

    companion object {

        private const val SAVED_STATE_EXPANDABLE_ITEM_MANAGER = "RecyclerViewExpandableItemManager"
        private const val ALLOW_ITEMS_MOVE_ACROSS_SECTIONS = false // Set this flag "true" to change draggable item range

        fun newInstance(list: com.ksajja.newone.model.List): TasksFragment {
            val fragment = TasksFragment()
            val bundle = Bundle()
            bundle.putParcelable(Constants.Keys.LIST, Parcels.wrap(list))
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initReminderComposer()
        mRecyclerView = getView()!!.findViewById(R.id.recycler_view)
        mLayoutManager = LinearLayoutManager(context)

        swipeRefreshLayout.setOnRefreshListener { onRefresh() }

        val eimSavedState = savedInstanceState?.getParcelable<Parcelable>(SAVED_STATE_EXPANDABLE_ITEM_MANAGER)
        mRecyclerViewExpandableItemManager = RecyclerViewExpandableItemManager(eimSavedState)
        mRecyclerViewExpandableItemManager!!.setOnGroupExpandListener(this)
        mRecyclerViewExpandableItemManager!!.setOnGroupCollapseListener(this)

        // drag & drop manager
        mRecyclerViewDragDropManager = RecyclerViewDragDropManager()
        mRecyclerViewDragDropManager!!.setDraggingItemShadowDrawable(
                ContextCompat.getDrawable(context!!, R.drawable.material_shadow_z3) as NinePatchDrawable?)

        dataProvider = TasksExpandableDataProvider(mTasks, context!!)
        //adapter
        val myItemAdapter = TasksAdapter(dataProvider!!, this)

        mAdapter = myItemAdapter

        mWrappedAdapter = mRecyclerViewExpandableItemManager!!.createWrappedAdapter(myItemAdapter)       // wrap for expanding
        mWrappedAdapter = mRecyclerViewDragDropManager!!.createWrappedAdapter(mWrappedAdapter!!)           // wrap for dragging

        mRecyclerViewDragDropManager!!.isCheckCanDropEnabled = ALLOW_ITEMS_MOVE_ACROSS_SECTIONS
        myItemAdapter.setAllowItemsMoveAcrossSections(ALLOW_ITEMS_MOVE_ACROSS_SECTIONS)

        val animator = DraggableItemAnimator()

        // Change animations are enabled by default since support-v7-recyclerview v22.
        // Need to disable them when using animation indicator.
        animator.supportsChangeAnimations = false

        mRecyclerView!!.layoutManager = mLayoutManager
        mRecyclerView!!.adapter = mWrappedAdapter  // requires *wrapped* adapter
        mRecyclerView!!.itemAnimator = animator
        mRecyclerView!!.setHasFixedSize(false)

        // NOTE:
        // The initialization order is very important! This order determines the priority of touch event handling.
        //
        // priority: DragAndDrop > ExpandableItem
        mRecyclerViewDragDropManager!!.attachRecyclerView(mRecyclerView!!)
        mRecyclerViewExpandableItemManager!!.attachRecyclerView(mRecyclerView!!)
    }

    private fun initReminderComposer() {
        (mBinding as FragmentRecyclerListViewBinding).reminderComposer.with(DefaultTabMenuBtnView(context!!
                , R.drawable.ic_ps_bell, R.drawable.ic_ps_bell, R.color.white, R.color.grey_300)
                , TestDatePickerView("Date", context!!)).with(DefaultTabMenuBtnView(context!!
                , R.drawable.ic_gifting_note_gray, R.drawable.ic_gifting_note_gray, R.color.white, R.color.grey_300)
                , TestNoteAdd("Note", context!!)).setOnClickListener(object : ComposerClickerActionListener {
            override fun onClicked(tag: String, value: Any) {
                if (tag == ReminderComposer.CLICK_EVENT_SUBMIT) {
                    (mBinding as FragmentRecyclerListViewBinding).reminderComposer.dismiss()
                    addNewTask(value as? HashMap<String, Any>)
                }
            }
        }).setOnEditListener(object : ComposerEditListener {
            override fun onEditStart() {
                if ((mBinding as FragmentRecyclerListViewBinding).reminderComposer.curState() == ReminderComposer.STATE_SHOW_ALL
                        && !(mBinding as FragmentRecyclerListViewBinding).reminderComposer.hasChildInEdit()) {
                    activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
                    return
                }
                activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
            }
        }).setStateChangeListener(object : ComposerStateChangeListener {
            override fun onComposerStateChange(curState: Int) {
                if (ReminderComposer.STATE_SHOW_ALL == curState) {
                    activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
                } else {
                    activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                }
            }
        }).build()

        val decorView = activity?.window?.decorView
        val oriWindowVisibleDisplayFrame = Rect()
        activity?.window?.decorView?.getWindowVisibleDisplayFrame(oriWindowVisibleDisplayFrame)
        decorView?.viewTreeObserver?.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            private val windowVisibleDisplayFrame = Rect()
            private var lastVisibleDecorViewHeight: Int = 0

            override fun onGlobalLayout() {
                // Retrieve visible rectangle inside window.
                decorView.getWindowVisibleDisplayFrame(windowVisibleDisplayFrame)
                val visibleDecorViewHeight = windowVisibleDisplayFrame.height()

                // Decide whether keyboard is visible from changing decor view height.
                if (lastVisibleDecorViewHeight != 0) {
                    if (lastVisibleDecorViewHeight > visibleDecorViewHeight) {
                        // Calculate current keyboard height (this includes also navigation bar height when in fullscreen mode).
                        val currentKeyboardHeight = oriWindowVisibleDisplayFrame.bottom - windowVisibleDisplayFrame.bottom
                        (mBinding as FragmentRecyclerListViewBinding)
                                .reminderComposer.setEditContentHeight(currentKeyboardHeight - AppHelper.dpToPx(18))
                    }
                }
                // Save current decor view height for the next call.
                lastVisibleDecorViewHeight = visibleDecorViewHeight
            }
        })
    }

    override fun onStart() {
        super.onStart()
        getValuesFromBundle()
        onRefresh()
    }

    private fun getValuesFromBundle() {
        if (arguments != null && arguments?.containsKey(Constants.Keys.LIST)!!) {
            mList = Parcels.unwrap(arguments?.getParcelable(Constants.Keys.LIST))
            if (!isAllTasks()) setupToolBar()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        // save current state to support screen rotation, etc...
        if (mRecyclerViewExpandableItemManager != null) {
            outState.putParcelable(
                    SAVED_STATE_EXPANDABLE_ITEM_MANAGER,
                    mRecyclerViewExpandableItemManager!!.savedState)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater!!.inflate(R.menu.tasks_menu, menu)

        val myActionMenuItem = menu?.findItem(R.id.menuActionSearch)
        val searchView = myActionMenuItem?.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                if (TextUtils.isEmpty(newText)) {
                    (mAdapter as TasksAdapter).filter()
                } else {
                    (mAdapter as TasksAdapter).filter(newText)
                }
                return true
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when {
            item?.itemId == R.id.menuActionSetting -> {
                (activity as MainActivity).replaceFragment(SettingsFragment())
                true
            }
            item?.itemId == R.id.menuActionSearch -> {


                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_UNSPECIFIED)
        super.onDestroy()
    }

    override fun onDestroyView() {
        if (mRecyclerViewDragDropManager != null) {
            mRecyclerViewDragDropManager!!.release()
            mRecyclerViewDragDropManager = null
        }

        if (mRecyclerViewExpandableItemManager != null) {
            mRecyclerViewExpandableItemManager!!.release()
            mRecyclerViewExpandableItemManager = null
        }

        if (mRecyclerView != null) {
            mRecyclerView!!.itemAnimator = null
            mRecyclerView!!.adapter = null
            mRecyclerView = null
        }

        if (mWrappedAdapter != null) {
            WrapperAdapterUtils.releaseAll(mWrappedAdapter)
            mWrappedAdapter = null
        }
        mAdapter = null
        mLayoutManager = null

        super.onDestroyView()
    }

    override fun onGroupCollapse(groupPosition: Int, fromUser: Boolean, payload: Any?) {}

    override fun onGroupExpand(groupPosition: Int, fromUser: Boolean, payload: Any?) {
        if (fromUser) {
            adjustScrollPositionOnGroupExpanded(groupPosition)
        }
    }

    private fun adjustScrollPositionOnGroupExpanded(groupPosition: Int) {
        val childItemHeight = activity!!.resources.getDimensionPixelSize(R.dimen.list_item_height)
        val topMargin = (activity!!.resources.displayMetrics.density * 16).toInt() // top-spacing: 16dp

        mRecyclerViewExpandableItemManager!!.scrollToGroup(groupPosition, childItemHeight, topMargin, topMargin)
    }

    private fun supportsViewElevation(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
    }

    override fun onTaskClick(task: Task) {
        if (activity is MainActivity) {
            (activity as MainActivity).replaceFragment(TaskDetailFragment.newInstance(task.taskId!!))
        }
    }

    override fun onTaskDone(task: Task) {
        task.taskStatus = TaskStatus.COMPLETED
        APIService.instance.updateTask(task?.taskId!!, task).enqueue(APICallback())
    }

    override fun onTaskUndone(task: Task) {
        task.taskStatus = TaskStatus.OPEN
        APIService.instance.updateTask(task?.taskId!!, task).enqueue(APICallback())
    }

    override fun onTaskDelete(task: Task, groupPosition: Int, childPosition: Int) {
        APIService.instance.deleteTask(task.taskId!!).enqueue(object : APICallback<Boolean>() {
            override fun onSuccess(value: Boolean) {
                dataProvider?.removeChildItem(groupPosition, childPosition)
                mAdapter?.notifyDataSetChanged()
            }
        })
    }

    override fun onCannedMessageClick(task: Task) {
        var item: TemplateItem? = task.cannedMessage?.items?.get(0)
        CustomAlertDialog.newInstance(context!!, item?.title, item?.description, "OK",
                object : CustomAlertDialog.DialogOnClickListener() {
                    override fun onPositiveButtonClick(dialog: CustomAlertDialog) {
                        dialog.dismiss()
                    }
                }).show()
    }

    private fun addNewTask(value: HashMap<String, Any>?) {
        if (value!!["Title"] == null || (value["Title"] as? String).isNullOrBlank())
            return
        val title = if (value["Title"] == null) "" else value["Title"] as String
        val task = Task(title)
        task.dueDate = value["Date"] as? Date
        task.note = value["Note"] as? String
        task.listId = mList?.listId

        val list = LinkedList<Task>()
        list.add(task)
        APIService.instance.addTasks(mList?.listId
                ?: "-", list).enqueue(object : APICallback<List<Task>>() {
            override fun onSuccess(value: List<Task>) {
                onRefresh()
                mAdapter!!.notifyDataSetChanged()
            }
        })
    }

    override fun onDataReady(taskList: List<Task>) {
        mAdapter?.notifyDataSetChanged()
    }

    private fun onRefresh() {
        makeBackendCall()
        swipeRefreshLayout.isRefreshing = false
    }

    override fun onTouch(ev: MotionEvent): Boolean {
        if (ev.action == ACTION_DOWN) {
            val viewRect = Rect()
            (mBinding as FragmentRecyclerListViewBinding).reminderComposer.getGlobalVisibleRect(viewRect)
            if (!viewRect.contains(ev.rawX.toInt(), ev.rawY.toInt())) {
                (mBinding as FragmentRecyclerListViewBinding).reminderComposer.hideEditPage()
            }
        }
        return true
    }

    private fun makeBackendCall() {
        showLoading(true)
        // Check if the call is for all tasks...
        if (isAllTasks()) {
            APIService.instance.getTasks().enqueue(object : APICallback<List<Task>>() {
                override fun onSuccess(value: List<Task>) {
                    mTasks = value as ArrayList<Task>
                    refreshTasks()
                    showLoading(false)
                }
            })
        } else {
            setToolBarTitle(mList?.name)
            APIService.instance.getTasks(mList?.listId!!).enqueue(object : APICallback<List<Task>>() {
                override fun onSuccess(value: List<Task>) {
                    mTasks = value as ArrayList<Task>
                    refreshTasks()
                    showLoading(false)
                }
            })
        }
    }

    private fun isAllTasks(): Boolean {
        return mList == null || mList?.listId == null
    }

    private fun refreshTasks() {
        dataProvider?.let {
            it.setData(mTasks)
            (mAdapter as? TasksAdapter)?.setProvider(it)
            (mAdapter as? TasksAdapter)?.notifyDataSetChanged()
            mRecyclerViewExpandableItemManager!!.expandGroup(0)
        }
    }

    override fun getToolBarTitle(): CharSequence? {
        return if (isAllTasks()) getString(R.string.all) else mList?.name
    }

    override fun getHomeButtonDrawableId(): Int {
        return if (isAllTasks()) R.drawable.ic_grid_white_24px else super.getHomeButtonDrawableId()
    }

    override fun shouldShowHomeButton(): Boolean {
        return true
    }

    override fun onHomeButtonPressed() {
        if (isAllTasks()) (activity as MainActivity).replaceFragment(ListsFragment())
        else super.onHomeButtonPressed()
    }
}