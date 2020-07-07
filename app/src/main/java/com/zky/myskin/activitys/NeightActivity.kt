package com.zky.myskin.activitys

import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import com.zky.myskin.R
import com.zky.myskin.adapter.TestAdapter
import com.zky.myskin.fragment.TestFragment
import com.zky.skinlibrary.SkinActivity
import com.zky.skinlibrary.utils.PreferencesUtils
import kotlinx.android.synthetic.main.activity_neight.*

class NeightActivity : SkinActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_neight)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        val isNight = PreferencesUtils.getBoolean(this, "isNight")
        if (isNight) {
            setDayNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            setDayNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        //设置adapter
        var list= arrayListOf<String>()
        for (i in 1..10){
            list.add("test $i")
        }
        val testAdapter = TestAdapter(list)
        rView.layoutManager= LinearLayoutManager(this)
        rView.adapter = testAdapter




    }


    fun fragmentCommit(view: View?){
        fl.visibility= View.VISIBLE

        supportFragmentManager.beginTransaction().add(R.id.fl, TestFragment()).commit()
    }


    // 点击事件
    fun dayOrNight(view: View?) {
        val uiMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        when (uiMode) {
            Configuration.UI_MODE_NIGHT_NO -> {
                setDayNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                PreferencesUtils.putBoolean(this, "isNight", true)
            }
            Configuration.UI_MODE_NIGHT_YES -> {
                setDayNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                PreferencesUtils.putBoolean(this, "isNight", false)
            }
        }
    }


    override fun openChangeSkin(): Boolean {
        return true
    }


}
