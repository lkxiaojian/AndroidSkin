package com.zky.skinlibrary.view

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import com.netease.skin.library.model.AttrsBean
import com.zky.skinlibrary.R
import com.zky.skinlibrary.SkinManager.Companion.instance
import com.zky.skinlibrary.core.ViewsMatch

/**
 * 继承TextView兼容包，9.0源码中也是如此
 * 参考：AppCompatViewInflater.java
 * 86行 + 138行 + 206行
 */
class SkinnableImageView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : AppCompatImageView(context, attrs, defStyleAttr), ViewsMatch {
    private val attrsBean: AttrsBean
    override fun skinnableView() { // 根据自定义属性，获取styleable中的src属性
        val key = R.styleable.SkinnableImageView[R.styleable.SkinnableImageView_android_src]
        // 根据styleable获取控件某属性的resourceId
        val backgroundResourceId = attrsBean.getViewResource(key)
        if (backgroundResourceId > 0) { // 是否默认皮肤
            if (instance!!.isDefaultSkin) { // 兼容包转换
                val drawable = ContextCompat.getDrawable(context, backgroundResourceId)
                setImageDrawable(drawable)
            } else { // 获取皮肤包资源
                val skinResourceId = instance!!.getBackgroundOrSrc(backgroundResourceId)
                // 兼容包转换
                if (skinResourceId is Int) {
                    setImageResource(skinResourceId)
                    // setImageBitmap(); // Bitmap未添加
                } else {
                    val drawable = skinResourceId as Drawable?
                    setImageDrawable(drawable)
                }
            }
        }
    }

    init {
        attrsBean = AttrsBean()
        // 根据自定义属性，匹配控件属性的类型集合，如：src
        val typedArray = context.obtainStyledAttributes(attrs,
                R.styleable.SkinnableImageView,
                defStyleAttr, 0)
        // 存储到临时JavaBean对象
        attrsBean.saveViewResource(typedArray, R.styleable.SkinnableImageView)
        // 这一句回收非常重要！obtainStyledAttributes()有语法提示！！
        typedArray.recycle()
    }
}