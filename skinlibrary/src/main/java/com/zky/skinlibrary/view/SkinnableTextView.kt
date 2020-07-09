package com.zky.skinlibrary.view

import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
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
class SkinnableTextView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = android.R.attr.textViewStyle) : AppCompatTextView(context, attrs, defStyleAttr), ViewsMatch {
    private val attrsBean: AttrsBean = AttrsBean()

    override fun skinnableView() {
        // 根据自定义属性，获取styleable中的background属性
        var key = R.styleable.SkinnableTextView[R.styleable.SkinnableTextView_android_background]
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
        key = R.styleable.SkinnableTextView[R.styleable.SkinnableTextView_android_textColor]
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


//
//        // 根据自定义属性，获取styleable中的 drawableBottom top left right属性
        var dbkey = R.styleable.SkinnableTextView[R.styleable.SkinnableTextView_android_drawableBottom]
        var dtkey = R.styleable.SkinnableTextView[R.styleable.SkinnableTextView_android_drawableTop]
        var dlkey = R.styleable.SkinnableTextView[R.styleable.SkinnableTextView_android_drawableLeft]
        var drkey = R.styleable.SkinnableTextView[R.styleable.SkinnableTextView_android_drawableRight]

        // 根据styleable获取控件某属性的resourceId
        val dbResourceId = attrsBean.getViewResource(dbkey)
        val dtResourceId = attrsBean.getViewResource(dtkey)
        val dlResourceId = attrsBean.getViewResource(dlkey)
        val drResourceId = attrsBean.getViewResource(drkey)
        //bottom
        var drawableDb: Drawable? = null
        if (dbResourceId > 0) {
            drawableDb = if(instance!!.isDefaultSkin){
                ContextCompat.getDrawable(context, dbResourceId)
            }else{
                val backgroundOrSrc = instance!!.getBackgroundOrSrc(dbResourceId)

                if (backgroundOrSrc is Int ) {
                    ContextCompat.getDrawable(context, backgroundOrSrc)
                } else {
                    backgroundOrSrc as Drawable
                }
            }

            drawableDb?.setBounds(0, 0, drawableDb?.intrinsicWidth, drawableDb?.intrinsicHeight)
        }
        //top
        var drawableDt: Drawable? = null
        if (dtResourceId > 0) {
            drawableDt = if(instance!!.isDefaultSkin){
                ContextCompat.getDrawable(context, dtResourceId)
            }else{
                val backgroundOrSrc = instance!!.getBackgroundOrSrc(dtResourceId)
                if (backgroundOrSrc is Int ) {
                    ContextCompat.getDrawable(context, backgroundOrSrc)
                } else {
                    backgroundOrSrc as Drawable
                }
            }


            drawableDt?.setBounds(0, 0, drawableDt?.intrinsicWidth, drawableDt?.intrinsicHeight)
        }
        //left
        var drawableDl: Drawable? = null
        if (dlResourceId > 0) {
            drawableDl = if(instance!!.isDefaultSkin){
                ContextCompat.getDrawable(context, dlResourceId)
            }else{
                val backgroundOrSrc = instance!!.getBackgroundOrSrc(dlResourceId)
                if (backgroundOrSrc is Int ) {
                    ContextCompat.getDrawable(context, backgroundOrSrc)
                } else {
                    backgroundOrSrc as Drawable
                }

            }


            drawableDl?.setBounds(0, 0, drawableDl?.intrinsicWidth, drawableDl?.intrinsicHeight)
        }

        //right
        var drawableDr: Drawable? = null
        if (drResourceId > 0) {
            drawableDr=  if(instance!!.isDefaultSkin){
                ContextCompat.getDrawable(context, drResourceId)
            }else{
                val backgroundOrSrc = instance!!.getBackgroundOrSrc(drResourceId)
                if (backgroundOrSrc is Int ) {
                    ContextCompat.getDrawable(context, backgroundOrSrc)
                } else {
                    backgroundOrSrc as Drawable
                }
            }


            drawableDr?.setBounds(0, 0, drawableDr?.intrinsicWidth, drawableDr?.intrinsicHeight)
        }
        //left, top, right, bottom
        setCompoundDrawables(drawableDl, drawableDt, drawableDr, drawableDb)
    }

    init {
        // 根据自定义属性，匹配控件属性的类型集合，如：background + textColor
        val typedArray = context.obtainStyledAttributes(attrs,
                R.styleable.SkinnableTextView,
                defStyleAttr, 0)
        // 存储到临时JavaBean对象
        attrsBean.saveViewResource(typedArray, R.styleable.SkinnableTextView)
        // 这一句回收非常重要！obtainStyledAttributes()有语法提示！！
        typedArray.recycle()
    }
}