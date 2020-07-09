package com.zky.myskin.activitys

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import com.zky.myskin.R
import com.zky.myskin.adapter.TestAdapter
import com.zky.myskin.fragment.TestFragment
import com.zky.skinlibrary.SkinActivity
import com.zky.skinlibrary.utils.PreferencesUtils
import kotlinx.android.synthetic.main.activity_neight.*
import java.io.File

class NeightActivity : SkinActivity() {
    private lateinit var type: String
    private lateinit var skinPath: String
    private lateinit var testAdapter:TestAdapter
    private var isDefa = true

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_neight)
        //设置adapter
        var list = arrayListOf<String>()
        for (i in 1..100) {
            list.add("test $i")
        }
         testAdapter = TestAdapter(list)
        rView.layoutManager = LinearLayoutManager(this)
        rView.adapter = testAdapter


        type = intent.getStringExtra("type") //1 日间 夜间切换
        if (type == "1") {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            val isNight = PreferencesUtils.getBoolean(this, "isNight")
            if (isNight) {
                setDayNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                setDayNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        } else {
            skinPath = (
                    "${File.separator}mnt${File.separator}sdcard${File.separator}mySkin${File.separator}mySkin.skin.apk")
            // 运行时权限申请（6.0+）
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val perms = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                if (checkSelfPermission(perms[0]) === PackageManager.PERMISSION_DENIED) {
                    requestPermissions(perms, 200)
                }
            }
            isDefa = if ("mySkin" == PreferencesUtils.getString(this, "currentSkin")) {
                skinDynamic(skinPath, R.color.skin_item_color)
                abt_sure.text = "还原加载皮肤包"
                false
            } else {
                defaultSkin(R.color.colorPrimary)
                abt_sure.text = "加载皮肤包"
                true
            }
        }
    }


    fun fragmentCommit(view: View?) {
        fl.visibility = View.VISIBLE
        supportFragmentManager.beginTransaction().add(R.id.fl, TestFragment()).commit()
    }


    // 点击事件
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun dayOrNight(view: View?) {

        when (type) {
            "1" -> {
                dayOrNeight()
            }
            "2" -> {
                isDefa = !isDefa
                if (isDefa) {
                    skinDefault()
                    abt_sure.text = "加载皮肤包"
                } else {
                    skinDynamic()
                    abt_sure.text = "还原加载皮肤包"
                }


            }
        }

    }

    // 换肤按钮（api限制：5.0版本）
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    fun skinDynamic() { // 真实项目中：需要先判断当前皮肤，避免重复操作！
        if ("mySkin" != PreferencesUtils.getString(this, "currentSkin")) {
            Log.e("skin >>> ", "-------------start-------------")
            val start = System.currentTimeMillis()
            skinDynamic(skinPath, R.color.skin_item_color)
            PreferencesUtils.putString(this, "currentSkin", "mySkin")
            val end = System.currentTimeMillis() - start
            Log.e("skin >>> ", "换肤耗时（毫秒）：$end")
            Log.e("skin >>> ", "-------------end---------------")
        }
    }

    // （api限制：5.0版本）
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    fun skinDefault() {
        if ("default" != PreferencesUtils.getString(this, "currentSkin")) {
            Log.e("skin >>> ", "-------------start-------------")
            val start = System.currentTimeMillis()
            defaultSkin(R.color.colorPrimary)
            PreferencesUtils.putString(this, "currentSkin", "default")
            val end = System.currentTimeMillis() - start
            Log.e("skin >>> ", "还原耗时（毫秒）：$end")
            Log.e("skin >>> ", "-------------end---------------")
        }
    }


    /**
     * 日间 夜间切换
     */

    private fun dayOrNeight() {
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

    /**
     * 重写dayOrNeight 同意换肤
     */
    override fun openChangeSkin(): Boolean {
        return true
    }



}
