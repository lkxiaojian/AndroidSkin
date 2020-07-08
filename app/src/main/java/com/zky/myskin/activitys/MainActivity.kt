package com.zky.myskin.activitys

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import com.zky.myskin.R
import com.zky.skinlibrary.SkinActivity
import kotlinx.android.synthetic.main.activity_main.*

/**
 * 日间夜间模式  切换
 */
class MainActivity : SkinActivity() {
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //日间/夜间模式 切换
        bt_night.setOnClickListener {
            start(NeightActivity::class.java,"1")
        }

        bt_dynamic.setOnClickListener {
            start(NeightActivity::class.java,"2")
        }

    }

    private fun start(java: Class<*>, type: String) {
        val intent = Intent(this, java)
        intent.putExtra("type", type)
        startActivity(intent)
    }




}