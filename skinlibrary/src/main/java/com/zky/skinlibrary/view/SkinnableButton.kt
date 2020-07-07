package com.zky.skinlibrary.view

import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatButton
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
class SkinnableButton @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = R.attr.buttonStyle) : AppCompatButton(context, attrs, defStyleAttr), ViewsMatch {
    private val attrsBean: AttrsBean
    override fun skinnableView() { // 根据自定义属性，获取styleable中的background属性
        var key = R.styleable.SkinnableButton[R.styleable.SkinnableButton_android_background]
        // 根据styleable获取控件某属性的resourceId
        val backgroundResourceId = attrsBean.getViewResource(key)
        if (backgroundResourceId > 0) { // 是否默认皮肤
            if (instance!!.isDefaultSkin) { // 兼容包转换
                val drawable = ContextCompat.getDrawable(context, backgroundResourceId)
                // 控件自带api，这里不用setBackgroundColor()因为在9.0测试不通过
// setBackgroundDrawable本来过时了，但是兼容包重写了方法
                setBackgroundDrawable(drawable)
            } else { // 获取皮肤包资源
                val skinResourceId = instance!!.getBackgroundOrSrc(backgroundResourceId)
                // 兼容包转换
                if (skinResourceId is Int) {
                    setBackgroundColor(skinResourceId)
                    // setBackgroundResource(color); // 未做兼容测试
                } else {
                    val drawable = skinResourceId as Drawable?
                    setBackgroundDrawable(drawable)
                }
            }
        }
        // 根据自定义属性，获取styleable中的textColor属性
        key = R.styleable.SkinnableButton[R.styleable.SkinnableButton_android_textColor]
        val textColorResourceId = attrsBean.getViewResource(key)
        if (textColorResourceId > 0) {
            if (instance!!.isDefaultSkin) {
                val color = ContextCompat.getColorStateList(context, textColorResourceId)
                setTextColor(color)
            } else {
                val color = instance!!.getColorStateList(textColorResourceId)
                setTextColor(color)
            }
        }
        // 根据自定义属性，获取styleable中的字体 custom_typeface 属性
        key = R.styleable.SkinnableTextView[R.styleable.SkinnableTextView_custom_typeface]
        val textTypefaceResourceId = attrsBean.getViewResource(key)
        if (textTypefaceResourceId > 0) {
            typeface = if (instance!!.isDefaultSkin) {
                Typeface.DEFAULT
            } else {
                instance!!.getTypeface(textTypefaceResourceId)
            }
        }
    }

    init {
        attrsBean = AttrsBean()
        // 根据自定义属性，匹配控件属性的类型集合，如：background + textColor
        val typedArray = context.obtainStyledAttributes(attrs,
                R.styleable.SkinnableButton,
                defStyleAttr, 0)
        // 存储到临时JavaBean对象
        attrsBean.saveViewResource(typedArray, R.styleable.SkinnableButton)
        // 这一句回收非常重要！obtainStyledAttributes()有语法提示！！
        typedArray.recycle()
    }
}