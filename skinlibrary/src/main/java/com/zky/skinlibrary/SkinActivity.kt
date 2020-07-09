package com.zky.skinlibrary

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate.NightMode
import androidx.core.view.LayoutInflaterCompat
import com.zky.skinlibrary.core.CustomAppCompatViewInflater
import com.zky.skinlibrary.utils.ActionBarUtils.forActionBar
import com.zky.skinlibrary.utils.NavigationUtils
import com.zky.skinlibrary.utils.StatusBarUtils

/**
 * 换肤Activity父类
 *
 * 用法：
 * 1、继承此类
 * 2、重写openChangeSkin()方法
 */
open class SkinActivity : AppCompatActivity() {
     var viewInflater: CustomAppCompatViewInflater? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        val layoutInflater = LayoutInflater.from(this)
        LayoutInflaterCompat.setFactory2(layoutInflater, this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(parent: View?, name: String, context: Context, attrs: AttributeSet): View? {
        if (openChangeSkin()) {
            if (viewInflater == null) {
                viewInflater = CustomAppCompatViewInflater(context)
            }
            viewInflater!!.setName(name)
            viewInflater!!.setAttrs(attrs)
            return viewInflater!!.autoMatch()
        }
        return super.onCreateView(parent, name, context, attrs)
    }

    /**
     * @return 是否开启换肤，增加此开关是为了避免开发者误继承此父类，导致未知bug
     */
    protected open fun openChangeSkin(): Boolean {
        return false
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    protected open fun defaultSkin(themeColorId: Int) {
        skinDynamic(null, themeColorId)
    }

    /**
     * 动态换肤（api限制：5.0版本）
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    protected open fun skinDynamic(skinPath: String?, themeColorId: Int) {
        if(  SkinManager.instance==null){
            throw Exception("SkinManager has not initialized,please use SkinManager.init(application) in Application")
        }
        SkinManager.instance?.loaderSkinResources(skinPath)
        if (themeColorId != 0) {
            val themeColor: Int = SkinManager.instance!!.getColor(themeColorId)
            StatusBarUtils.forStatusBar(this, themeColor)
            NavigationUtils.forNavigation(this, themeColor)
            forActionBar(this, themeColor)
        }
        SkinManager.instance?.applyViews(window.decorView)
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    protected fun setDayNightMode(@NightMode nightMode: Int) {
        val isPost21 = Build.VERSION.SDK_INT >= 21
        delegate.localNightMode = nightMode
        if (isPost21) { // 换状态栏
            StatusBarUtils.forStatusBar(this)
            // 换标题栏
            forActionBar(this@SkinActivity)
            // 换底部导航栏
            NavigationUtils.forNavigation(this)
        }
        val decorView = window.decorView
        SkinManager.instance?. applyViews(decorView)
    }


}