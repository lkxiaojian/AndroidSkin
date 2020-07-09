package com.zky.skinlibrary.view

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.netease.skin.library.model.AttrsBean
import com.zky.skinlibrary.R
import com.zky.skinlibrary.core.ViewsMatch

class SkinnableConstraintLayout
@JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
        ConstraintLayout(context, attrs, defStyleAttr), ViewsMatch {
    private val attrsBean: AttrsBean = AttrsBean()
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun skinnableView() {
        // 根据自定义属性，获取styleable中的background属性
        val key = R.styleable.SkinnableConstraintLayout[R.styleable.SkinnableConstraintLayout_android_background]
        // 根据styleable获取控件某属性的resourceId
        val backgroundResourceId = attrsBean.getViewResource(key)
        if (backgroundResourceId > 0) { // 兼容包转换
            val drawable = ContextCompat.getDrawable(context, backgroundResourceId)
            // 控件自带api，这里不用setBackgroundColor()因为在9.0测试不通过
            // setBackgroundDrawable在这里是过时了
            background = drawable
        }
    }

    init {
        // 根据自定义属性，匹配控件属性的类型集合，如：background
        val typedArray = context?.obtainStyledAttributes(attrs,
                R.styleable.SkinnableConstraintLayout,
                defStyleAttr, 0)
        // 存储到临时JavaBean对象
        attrsBean.saveViewResource(typedArray, R.styleable.SkinnableConstraintLayout)
        // 这一句回收非常重要！obtainStyledAttributes()有语法提示！！
        typedArray?.recycle()
    }
}