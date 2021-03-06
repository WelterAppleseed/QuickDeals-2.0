package com.example.fffaaaa.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.fffaaaa.R
import com.example.fffaaaa.contract.FragmentInterface
import com.example.fffaaaa.contract.NavigationContract
import com.example.fffaaaa.contract.SSI
import com.example.fffaaaa.contract.SectorPrivateInterface
import com.example.fffaaaa.presenter.FragmentPresenter
import com.example.fffaaaa.presenter.NavigationPresenter
import com.example.fffaaaa.presenter.SectorInfoPresenter
import com.example.fffaaaa.room.enitites.SectorEntity
import com.example.fffaaaa.room.daos.TDao
import com.example.fffaaaa.room.enitites.TaskEntity
import kotlinx.android.synthetic.main.sector_info.*
import kotlinx.android.synthetic.main.sector_info.view.*
import kotlinx.coroutines.DelicateCoroutinesApi

class SectorInfoFragment(private var dao: TDao) : Fragment(), NavigationContract.View, SSI.View,
    SectorPrivateInterface {
    private lateinit var navigationPresenter: FragmentInterface.Presenter
    private lateinit var sectorInfoPresenter: SectorInfoPresenter
    @DelicateCoroutinesApi
    var fragmentPresenter: FragmentPresenter? = null

    @DelicateCoroutinesApi
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.sector_info, container, false)
        view.sector_toolbar.menu.findItem(R.id.delete_it).setOnMenuItemClickListener {
            sectorInfoPresenter.modifyToolbar(view.context, it)
            return@setOnMenuItemClickListener true
        }
        view.sector_toolbar.setNavigationOnClickListener {
           changeVisibilityOfFragment(true)
        }
        sectorInfoPresenter = SectorInfoPresenter(this, dao)
        return view
    }

    fun changeVisibilityOfFragment(hide: Boolean) {
        if (hide) {
            navigationPresenter.hideFragment(this)
        } else {
            navigationPresenter.showFragment(this)
        }
    }
    override fun attachNavigationPresenter(navigation: NavigationPresenter) {
        Log.i("SectorInfoFragment", "attachNavigationPresenter")
        navigationPresenter = navigation
    }

    @DelicateCoroutinesApi
    override fun attachFragmentPresenter(fragmentPresenter: FragmentPresenter) {
        Log.i("SectorInfoFragment", "attachFragmentPresenter")
        this.fragmentPresenter = fragmentPresenter
        Log.i("SectorInfoFragment", this.fragmentPresenter.toString())
    }

    @DelicateCoroutinesApi
    override fun update(
        sectorEntity: SectorEntity,
        fragmentPresenter: FragmentPresenter,
        position: Int
    ) {
        Log.i("SectorInfoFragment", "update")
        sectorInfoPresenter.replaceInfo(
            main_layout,
            sectorEntity,
            position,
            context!!,
            fragmentPresenter
        )
        changeVisibilityOfFragment(false)
    }

    override fun taskCountDynamicallyUpdate(count: Int) {
        Log.i("SectorInfoFragment", "taskCountDynamicallyUpdate")
        view?.sector_info_task_count_tv?.text = context?.getString(R.string.task_count_text, count)
    }

    @DelicateCoroutinesApi
    override fun taskListDynamicallyUpdate(taskEntity: TaskEntity) {
        Log.i("SectorInfoFragment", "taskListDynamicallyUpdate")
        view?.let { sectorInfoPresenter.updateRecyclers(taskEntity, it.parent_rec) }
    }

    override fun attachSectorInfoPresenter(sectorInfoPresenter: SectorInfoPresenter) {
        Log.i("SectorInfoFragment", "attachSectorInfoPresenter")
        this.sectorInfoPresenter = sectorInfoPresenter
    }

    @DelicateCoroutinesApi
    override fun changeSectorFromStartView(
        sectorEntity: SectorEntity,
        position: Int,
        taskEntity: TaskEntity,
        callsForAdding: Boolean
    ) {
        Log.i("SectorInfoFragment", "changeSectorFromStartView")
        fragmentPresenter?.changeSectorFromStartView(
            sectorEntity,
            position,
            taskEntity,
            callsForAdding
        )
    }

    override fun getParentRec(): RecyclerView {
        Log.i("SectorInfoFragment", "getParentRec")
        return this.parent_rec
    }

    @DelicateCoroutinesApi
    override fun requestSectorDeleting(sector: SectorEntity) {
        Log.i("SectorInfoFragment", "requestSectorDeleting")
        fragmentPresenter?.delete(sector)
    }
}