package com.zky.myskin.activitys

import android.content.Intent
import android.os.Bundle
import com.zky.myskin.R
import com.zky.skinlibrary.SkinActivity
import kotlinx.android.synthetic.main.activity_main.*

/**
 * 日间夜间模式  切换
 */
class MainActivity : SkinActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //日间/夜间模式 切换
        bt_night.setOnClickListener {
            start(NeightActivity::class.java)
        }
    }

    private fun start(java: Class<*>) {
        startActivity(Intent(this, java))
    }

    override fun onResume() {
        super.onResume()
    }

    override fun openChangeSkin()=true


}