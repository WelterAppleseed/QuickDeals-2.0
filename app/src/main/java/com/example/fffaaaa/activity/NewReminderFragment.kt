package com.example.fffaaaa.activity

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
import com.example.fffaaaa.presenter.FragmentPresenter
import com.example.fffaaaa.presenter.NavigationPresenter
import com.example.fffaaaa.presenter.NewReminderPresenter
import com.example.fffaaaa.room.SDao
import com.example.fffaaaa.room.SectorEntity
import com.example.fffaaaa.room.TDao
import com.example.fffaaaa.room.TaskEntity
import kotlinx.android.synthetic.main.create_reminer_one.category_img
import kotlinx.android.synthetic.main.create_reminer_one.category_tv
import kotlinx.android.synthetic.main.create_reminer_one.date_tv
import kotlinx.android.synthetic.main.create_reminer_one.view.category_tv
import kotlinx.android.synthetic.main.create_reminer_one.view.createRemButton
import kotlinx.android.synthetic.main.create_reminer_one.view.date_tv
import kotlinx.android.synthetic.main.create_reminer_one.view.new_rem_toolbar
import kotlinx.android.synthetic.main.create_reminer_two.*
import kotlinx.android.synthetic.main.create_reminer_two.view.*
import java.time.LocalDateTime

class NewReminderFragment(private var sDao: SDao, private var tDao: TDao) : Fragment(),
    ReminderContract.View,
    NavigationContract.View, KeyboardInterface {
    private val SELECT_CATEGORY_CODE = 5
    var newReminderPresenter: NewReminderPresenter? = null
    var fragmentPresenter: FragmentPresenter? = null
    private lateinit var vibrator: Vibrator
    private var hasFocus: Boolean = false
    private val vibrationEffect: VibrationEffect =
        VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE)
    protected lateinit var navigationPresenter: FragmentInterface.Presenter
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.create_reminer_two, container, false)

        view.viewTreeObserver.addOnGlobalLayoutListener {
            view.viewTreeObserver.addOnGlobalLayoutListener {
                var r = Rect()
                view.getWindowVisibleDisplayFrame(r)
                var screenH = view.rootView.height
                var keypadH = screenH - r.bottom

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
        vibrator = context?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        view.new_rem_toolbar.menu.findItem(R.id.close_it).setOnMenuItemClickListener {
            over()
            hideSoftKeyboard(context!!)
            true
        }
        setupUI(this, view.new_rem_toolbar, context!!)
        setupUI(this, view.createRemButton, context!!)
        newReminderPresenter = NewReminderPresenter(this, sDao, tDao)
        view.category_tv.setOnClickListener(View.OnClickListener {
            val categoryDialog = CategoryDialog()
            categoryDialog.setTargetFragment(this, SELECT_CATEGORY_CODE)
            fragmentManager?.let { it1 -> categoryDialog.show(it1, categoryDialog.javaClass.name) }
        })
        view.date_tv.setOnClickListener {
            newReminderPresenter!!.onDateTextViewClicked(
                context!!,
                it as TextView
            )
        }
        view.createRemButton.setOnClickListener(View.OnClickListener {
            if (date_tv.text.equals(resources.getString(R.string.date_tv_text)) || view.task_et.length() == 0) {
                if (view.task_et.length() == 0) view.task_et_title.invalidValue()
                if (date_tv.text.equals(resources.getString(R.string.date_tv_text))) view.date_tv.invalidValue()
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

        })
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

    override fun onSectorSaved(sector: SectorEntity, position: Int, taskEntity: TaskEntity) {
        Log.i("Function", "onSectorSaved")
        if (position < 0) {
            fragmentPresenter?.insertSectorToStartView(sector, position)
        } else {
            fragmentPresenter?.changeSectorFromStartView(sector, position, taskEntity, true)
        }
        over()
    }

    override fun openSectorScreen(sectorId: Long) {
        TODO("Not yet implemented")
    }

    override fun transit(up: Boolean) {
        Log.i("Function", "transit")
        if (!up) {
            println(up)
            view?.scroll_v?.scrollTo(0, 100)
        } else {
            println(up)
            view?.scroll_v?.fullScroll(FOCUS_DOWN)
        }
    }

    override fun onNotificationCreating(taskEntity: TaskEntity, icon: Int) {
        fragmentPresenter?.onNotificationEventReceived(taskEntity, icon)
    }

    override fun onSortList(list: ArrayList<SectorEntity>) {
        Log.i("Function", "onSortList")
        newReminderPresenter?.updateSectorsStringList(list)
    }

    override fun attachNavigationPresenter(presenter: NavigationPresenter) {
        Log.i("Function", "attachNavigationPresenter")
        navigationPresenter = presenter;
    }

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
            data!!.getIntExtra("icon", R.drawable.file_def_dr).let {
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
            h.postDelayed(Runnable {
                view?.task_et?.text?.clear()
                context?.getColor(R.color.black)?.let {
                    view?.date_tv?.setTextColor(it)
                    view?.task_et_title?.setTextColor(it)
                }
                view?.date_tv?.text = context?.getString(R.string.date_tv_text)
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
        view?.category_img?.setImageResource(R.drawable.file_def_dr)
        view?.category_img?.tag = null
        view?.category_tv?.text = resources.getString(R.string.category_tv_text)
        view?.act_im?.setImageResource(R.drawable.file_def_dr)
        view?.act_tv?.text = resources.getString(R.string.category_tv_text)
        view?.act_tv?.setTextColor(context!!.getColor(R.color.mainColor))
        view?.category_tv?.text = resources.getString(R.string.category_tv_text)
    }
}