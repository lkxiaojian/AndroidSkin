package com.zky.myskin.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.zky.myskin.R
import com.zky.skinlibrary.utils.PreferencesUtils

/**
 * Created by lk
 * Date 2020/7/7
 * Time 16:49
 * Detail:
 */
class TestFragment:Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        return inflater.inflate(R.layout.fragment_root, container, false)
    }

}