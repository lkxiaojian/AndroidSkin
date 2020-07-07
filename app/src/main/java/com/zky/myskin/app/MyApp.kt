package com.zky.myskin.app

import android.app.Application
import com.zky.skinlibrary.SkinManager

/**
 * Created by lk
 * Date 2020/7/7
 * Time 15:23
 * Detail:
 */
class MyApp :Application() {
    override fun onCreate() {
        super.onCreate()
        SkinManager.init(this)
    }
}