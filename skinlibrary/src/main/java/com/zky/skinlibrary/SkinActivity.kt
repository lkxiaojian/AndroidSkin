package com.zky.skinlibrary

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate.NightMode
import androidx.core.view.LayoutInflaterCompat
import com.zky.skinlibrary.core.CustomAppCompatViewInflater
import com.zky.skinlibrary.core.ViewsMatch
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
    private var viewInflater: CustomAppCompatViewInflater? = null
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
        applyDayNightForView(decorView)
    }

    /**
     * 回调接口 给具体控件换肤操作
     */
     fun applyDayNightForView(view: View?) {
        if (view is ViewsMatch) {
            val viewsMatch = view as ViewsMatch
            viewsMatch.skinnableView()
        }
        if (view is ViewGroup) {
            val parent = view
            val childCount = parent.childCount
            for (i in 0 until childCount) {
                applyDayNightForView(parent.getChildAt(i))
            }
        }
    }
}