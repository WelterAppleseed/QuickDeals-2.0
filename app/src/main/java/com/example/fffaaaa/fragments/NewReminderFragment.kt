package com.example.fffaaaa.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.*
import android.util.Log
import android.view.*
import android.widget.TextView
import android.widget.TextView.FOCUS_DOWN
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.example.fffaaaa.*
import com.example.fffaaaa.contract.FragmentInterface
import com.example.fffaaaa.contract.KeyboardInterface
import com.example.fffaaaa.contract.NavigationContract
import com.example.fffaaaa.contract.ReminderContract
import com.example.fffaaaa.custom.CategoryDialog
import com.example.fffaaaa.observer.ScrollPositionObserver
import com.example.fffaaaa.presenter.FragmentPresenter
import com.example.fffaaaa.presenter.NavigationPresenter
import com.example.fffaaaa.presenter.NewReminderPresenter
import com.example.fffaaaa.room.daos.SDao
import com.example.fffaaaa.room.enitites.SectorEntity
import com.example.fffaaaa.room.daos.TDao
import com.example.fffaaaa.room.enitites.TaskEntity
import kotlinx.android.synthetic.main.create_reminer_one.category_img
import kotlinx.android.synthetic.main.create_reminer_one.category_tv
import kotlinx.android.synthetic.main.create_reminer_one.date_tv
import kotlinx.android.synthetic.main.create_reminer_one.view.category_tv
import kotlinx.android.synthetic.main.create_reminer_one.view.createRemButton
import kotlinx.android.synthetic.main.create_reminer_one.view.date_tv
import kotlinx.android.synthetic.main.create_reminer_one.view.new_rem_toolbar
import kotlinx.android.synthetic.main.create_reminer_two.*
import kotlinx.android.synthetic.main.create_reminer_two.view.*
import kotlinx.coroutines.DelicateCoroutinesApi

class NewReminderFragment(private var sDao: SDao, private var tDao: TDao) : Fragment(),
    ReminderContract.View,
    NavigationContract.View, KeyboardInterface {
    private val selectCode = 5
    private var newReminderPresenter: NewReminderPresenter? = null
    @DelicateCoroutinesApi
    var fragmentPresenter: FragmentPresenter? = null
    private lateinit var vibrator: Vibrator
    private var hasFocus: Boolean = false
    private val vibrationEffect: VibrationEffect =
        VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE)
    private lateinit var navigationPresenter: FragmentInterface.Presenter
    /* companion object {
         const val SECTORS = "sectors"
         lateinit var dao : SDao
         fun buildIntent(activity: Activity, dao : SDao): Intent {
             this.dao = dao
             val intent = Intent(activity, NewReminderFragment::class.java)
             intent.putExtra(SECTORS, SectorEntity.getAllSectorTitles(dao))
             return intent
         }
     }*/

    @DelicateCoroutinesApi
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.create_reminer_two, container, false)

        view.viewTreeObserver.addOnGlobalLayoutListener {
            view.viewTreeObserver.addOnGlobalLayoutListener {
                val r = Rect()
                view.getWindowVisibleDisplayFrame(r)
                val screenH = view.rootView.height
                val keypadH = screenH - r.bottom

                if (keypadH > screenH * 0.15) {
                    if (!hasFocus) {
                        onKeyboardVisibilityChanged(true)
                    }
                } else {
                    if (hasFocus) {
                        onKeyboardVisibilityChanged(false)
                    }
                }
            }
        }
        view.scroll_v!!.viewTreeObserver.addOnScrollChangedListener(
            ScrollPositionObserver(
                view.scroll_v,
                view.act_im,
                resources.getDimensionPixelSize(R.dimen.act_im_size)
            )
        )
        view.note_tv.setOnTouchListener { v, _ ->
                val handler = Handler()
                handler.postDelayed({
                    view.note_tv.requestFocus()
                }, 200)
            v.performClick()
        }
        vibrator = context?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        view.new_rem_toolbar.menu.findItem(R.id.close_it).setOnMenuItemClickListener {
            over()
            hideSoftKeyboard(context!!)
            true
        }
        setupUI(this, view.new_rem_toolbar, context!!)
        setupUI(this, view.createRemButton, context!!)
        newReminderPresenter = NewReminderPresenter(this, sDao, tDao)
        view.category_tv.setOnClickListener {
            val categoryDialog = CategoryDialog()
            categoryDialog.setTargetFragment(this, selectCode)
            fragmentManager?.let { it1 -> categoryDialog.show(it1, categoryDialog.javaClass.name) }
        }
        view.date_tv.setOnClickListener {
            newReminderPresenter!!.onDateTextViewClicked(
                context!!,
                it as TextView
            )
        }
        view.createRemButton.setOnClickListener {
            if (date_tv.text.equals(resources.getString(R.string.date_tv_text)) || view.task_et.length() == 0) {
                if (view.task_et.length() == 0) view.task_et_title.invalidValue()
                if (date_tv.text.equals(resources.getString(R.string.date_tv_text))) view.date_tv.invalidValue()
                vibrator.vibrate(vibrationEffect)
            } else {
                hideSoftKeyboard(context!!)
                newReminderPresenter!!.onAddReminderButton(
                    getSectorId(category_tv.text.toString()),
                    if (category_img.tag == null) R.drawable.file_def_dr else category_img.tag as Int,
                    category_tv.text.toString(),
                    1,
                    view.task_et.text.toString(),
                    view.note_tv.text.toString(),
                    dateFromString(date_tv.text.toString())
                )
            }

        }
        return view
    }

    override fun updateView() {
    }

    override fun over() {
        Log.i("Function", " over")
        navigationPresenter.hideFragment(this)
    }

    override fun updateCategory() {
    }

    @DelicateCoroutinesApi
    override fun onSectorSaved(sector: SectorEntity, position: Int, taskEntity: TaskEntity) {
        Log.i("Function", "onSectorSaved")
        if (position < 0) {
            fragmentPresenter?.insertSectorToStartView(sector, position)
        } else {
            fragmentPresenter?.changeSectorFromStartView(sector, position, taskEntity, true)
        }
        over()
    }

    override fun transit(up: Boolean) {
        Log.i("Function", "transit")
        if (!up) {
            view?.scroll_v?.scrollTo(0, 100)
        } else {
            view?.scroll_v?.fullScroll(FOCUS_DOWN)
        }
    }

    @DelicateCoroutinesApi
    override fun onNotificationCreating(taskEntity: TaskEntity, icon: Int) {
        fragmentPresenter?.onNotificationEventReceived(taskEntity, icon)
    }

    override fun onSortList(list: ArrayList<SectorEntity>) {
        Log.i("Function", "onSortList")
        newReminderPresenter?.updateSectorsStringList(list)
    }

    override fun attachNavigationPresenter(navigation: NavigationPresenter) {
        Log.i("Function", "attachNavigationPresenter")
        navigationPresenter = navigation
    }

    @DelicateCoroutinesApi
    override fun attachFragmentPresenter(fragmentPresenter: FragmentPresenter) {
        Log.i("Function", "attachFragmentPresenter")
        this.fragmentPresenter = fragmentPresenter
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.i("Function", "onActivityResult")
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            category_tv.text = data?.getStringExtra("title")
            act_tv.text = data?.getStringExtra("title")
            act_tv.setTextColor(context!!.getColor(data!!.getIntExtra("color", R.color.mainColor)))
            data.getIntExtra("icon", R.drawable.file_def_dr).let {
                category_img.setImageResource(it)
                act_im.setImageResource(it)
                category_img.tag = it
            }
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        Log.i("Function", "onHiddenChanged")
        val h = Handler()
        if (hidden) {
            h.postDelayed( {
                clearSectorIdentifiers()
            }, 500)
        }
        super.onHiddenChanged(hidden)
    }

    override fun onKeyboardVisibilityChanged(opened: Boolean) {
        Log.i("Function", "onKeyboardVisibilityChanged")
        hasFocus = opened
        transit(opened)
        super.onKeyboardVisibilityChanged(opened)
    }

    private fun clearSectorIdentifiers() {
        view?.task_et?.text?.clear()
        context?.getColor(R.color.black)?.let {
            view?.date_tv?.setTextColor(it)
            view?.task_et_title?.setTextColor(it)
        }
        view?.date_tv?.text = context?.getString(R.string.date_tv_text)
        view?.category_img?.setImageResource(R.drawable.file_def_dr)
        view?.category_img?.tag = null
        view?.category_tv?.text = resources.getString(R.string.category_tv_text)
        view?.act_im?.setImageResource(R.drawable.file_def_dr)
        view?.act_tv?.text = resources.getString(R.string.category_tv_text)
        view?.act_tv?.setTextColor(context!!.getColor(R.color.mainColor))
        view?.category_tv?.text = resources.getString(R.string.category_tv_text)
        view?.note_tv?.text = null
    }
}