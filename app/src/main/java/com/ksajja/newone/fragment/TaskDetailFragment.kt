package com.ksajja.newone.fragment


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import butterknife.BindView
import com.ksajja.newone.Constants
import com.ksajja.newone.R
import com.ksajja.newone.activity.MainActivity
import com.ksajja.newone.adapter.TaskTemplatesAdapter
import com.ksajja.newone.base.BaseFragment
import com.ksajja.newone.interfaces.TaskTemplateListener
import com.ksajja.newone.model.*
import com.ksajja.newone.model.enums.TaskStatus
import com.ksajja.newone.network.APICallback
import com.ksajja.newone.network.APIService
import kotlinx.android.synthetic.main.fragment_recommendation.*
import retrofit2.Call


/**
 * A simple [Fragment] subclass.
 */
class TaskDetailFragment : BaseFragment(R.layout.fragment_recommendation), TaskTemplateListener {

    private var mTask: Task? = null
    private var taskId: String? = null

    companion object {

        fun newInstance(taskId: String): TaskDetailFragment {
            val fragment = TaskDetailFragment()
            val bundle = Bundle()
            bundle.putString(Constants.Keys.TASK_ID, taskId)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        if (arguments != null) {
            taskId = arguments!!.getString(Constants.Keys.TASK_ID)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater!!.inflate(R.menu.generic_edit_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.action_edit -> {
                if (activity is MainActivity) {
                    (activity as MainActivity).replaceFragment(TaskEditFragment.newInstance(mTask?.taskId!!))
                }
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
            override fun onSuccess(task: Task) {
                mTask = task
                setData(task)
            }
        })
    }

    private fun setData(task: Task?) {
        if (task == null) return
        var list: ArrayList<Any?> = ArrayList()

        try {
            list.add(task)
            if (task.packages?.size == 1) {
                var packageValue: Package? = task?.packages?.get(0)

                //Todo convert to for each
                for (template in packageValue?.templates!!) {
                    for (templateItem in template.items!!) {
                        templateItem.templateType = template.templateType
                    }
                    if (Constants.TemplateTypes.WEB_ARTICLES == template.templateType) {
                        var header: HeaderModel = HeaderModel()
                        header.header = template.title
                        header.subHeader = template.commentary
                        list.add(header)
                        list.addAll(template.items!!)
                    } else {
                        list.add(template)
                    }
                }
            }
            setToolBarTitle(task.title)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        recommendationRecyclerView.layoutManager = LinearLayoutManager(context)
        var mAdapter: TaskTemplatesAdapter? = TaskTemplatesAdapter(list)
        mAdapter?.setListener(this)
        recommendationRecyclerView.adapter = mAdapter
    }


    override fun onTemplateItemClick(templateItem: TemplateItem?) {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(templateItem?.url)))

        /*if (activity is MainActivity) {
            (activity as MainActivity).replaceFragment(GenericWebViewFragment.newInstance(templateItem?.url,""))
        }*/
    }

    override fun onMarkAsDoneClicked(markAsDone: Boolean) {
        if(markAsDone){
            mTask?.taskStatus = TaskStatus.COMPLETED
        }else{
            mTask?.taskStatus = TaskStatus.OPEN
        }
        APIService.instance.updateTask(mTask?.taskId!!, mTask!!).enqueue(APICallback())
    }

}// Required empty public constructor
