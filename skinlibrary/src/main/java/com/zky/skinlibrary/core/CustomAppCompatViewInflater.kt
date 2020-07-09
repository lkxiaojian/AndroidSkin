package com.zky.skinlibrary.core

import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.zky.skinlibrary.view.*

/**
 * Created by lk
 * Date 2020/7/4
 * Time 11:25
 * Detail:
 */
class CustomAppCompatViewInflater(mContext: Context) {
    private var name // 控件名
            : String? = null
    private var context = mContext
    private var attrs // 某控件对应所有属性
            : AttributeSet? = null

    fun setName(name: String?) {
        this.name = name
    }

    fun setAttrs(attrs: AttributeSet?) {
        this.attrs = attrs
    }

    /**
     * @return 自动匹配控件名，并初始化控件对象
     */
    fun autoMatch(): View? {
        var view: View? = null
        if (name.isNullOrBlank()) {
            return null
        }

        when {
            name!!.contains("LinearLayout") -> {
                view = context?.let { SkinnableLinearLayout(it, attrs) }
                verifyNotNull(view, name!!)
            }
            name!!.contains("RelativeLayout") -> {
                view = SkinnableRelativeLayout(context, attrs)
                verifyNotNull(view, name!!)
            }
            name!!.contains("AppCompatTextView") ||
                    "TextView" == name -> {
                view = SkinnableTextView(context, attrs)
                verifyNotNull(view, name!!)
            }
            name!!.contains("ImageView") -> {
                view = SkinnableImageView(context, attrs)
                verifyNotNull(view, name!!)
            }
            name!!.contains("Button") -> {
                view = SkinnableButton(context, attrs)
                verifyNotNull(view, name!!)
            }
            name!!.contains("ConstraintLayout") -> {
                view = SkinnableConstraintLayout(context, attrs)
                verifyNotNull(view, name!!)
            }


        }
        return view
    }

    /**
     * 校验控件不为空（源码方法，由于private修饰，只能复制过来了。为了代码健壮，可有可无）
     *
     * @param view 被校验控件，如：AppCompatTextView extends TextView（v7兼容包，兼容是重点！！！）
     * @param name 控件名，如："ImageView"
     */
    private fun verifyNotNull(view: View?, name: String) {
        if (view == null) {
            throw IllegalStateException(context?.javaClass?.name + " asked to inflate view for <" + name + ">, but returned null")
        }
    }


}