package com.zky.skinlibrary

import android.app.Application
import android.content.pm.PackageManager
import android.content.res.AssetManager
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.util.Log
import com.zky.skinlibrary.model.SkinCache
import java.util.*

/**
 * 皮肤管理器
 * 加载应用资源（app内置：res/xxx） or 存储资源（下载皮肤包：net163.skin）
 */
class SkinManager private constructor(private val application: Application) {
    private val appResources // 用于加载app内置资源
            : Resources
    private var skinResources // 用于加载皮肤包资源
            : Resources? = null
    private var skinPackageName // 皮肤包资源所在包名（注：皮肤包不在app内，也不限包名）
            : String? = null
    var isDefaultSkin = true // 应用默认皮肤（app内置）
        private set
    private val cacheSkin: MutableMap<String?, SkinCache?>
    /**
     * 加载皮肤包资源
     *
     * @param skinPath 皮肤包路径，为空则加载app内置资源
     */
    fun loaderSkinResources(skinPath: String?) { // 优化：如果没有皮肤包或者没做换肤动作，方法不执行直接返回！
        if (TextUtils.isEmpty(skinPath)) {
            isDefaultSkin = true
            return
        }
        // 优化：app冷启动、热启动可以取缓存对象
        if (cacheSkin.containsKey(skinPath)) {
            isDefaultSkin = false
            val skinCache = cacheSkin[skinPath]
            if (null != skinCache) {
                skinResources = skinCache.skinResources
                skinPackageName = skinCache.skinPackageName
                return
            }
        }
        try { // 创建资源管理器（此处不能用：application.getAssets()）
            val assetManager = AssetManager::class.java.newInstance()
            // 由于AssetManager中的addAssetPath和setApkAssets方法都被@hide，目前只能通过反射去执行方法
            val addAssetPath = assetManager.javaClass.getDeclaredMethod(ADD_ASSET_PATH, String::class.java)
            // 设置私有方法可访问
            addAssetPath.isAccessible = true
            // 执行addAssetPath方法
            addAssetPath.invoke(assetManager, skinPath)
            //==============================================================================
// 如果还是担心@hide限制，可以反射addAssetPathInternal()方法，参考源码366行 + 387行
//==============================================================================
// 创建加载外部的皮肤包(net163.skin)文件Resources（注：依然是本应用加载）
            skinResources = Resources(assetManager,
                    appResources.displayMetrics, appResources.configuration)
            // 根据apk文件路径（皮肤包也是apk文件），获取该应用的包名。兼容5.0 - 9.0（亲测）
            skinPackageName = application.packageManager
                    .getPackageArchiveInfo(skinPath, PackageManager.GET_ACTIVITIES).packageName
            // 无法获取皮肤包应用的包名，则加载app内置资源
            isDefaultSkin = TextUtils.isEmpty(skinPackageName)
            if (!isDefaultSkin) {
                cacheSkin[skinPath] = SkinCache(skinResources, skinPackageName)
            }
            Log.e("skinPackageName >>> ", skinPackageName)
        } catch (e: Exception) {
            e.printStackTrace()
            // 发生异常，预判：通过skinPath获取skinPacakageName失败！
            isDefaultSkin = true
        }
    }

    /**
     * 参考：resources.arsc资源映射表
     * 通过ID值获取资源 Name 和 Type
     *
     * @param resourceId 资源ID值
     * @return 如果没有皮肤包则加载app内置资源ID，反之加载皮肤包指定资源ID
     */
    private fun getSkinResourceIds(resourceId: Int): Int { // 优化：如果没有皮肤包或者没做换肤动作，直接返回app内置资源！
        if (isDefaultSkin) return resourceId
        // 使用app内置资源加载，是因为内置资源与皮肤包资源一一对应（“netease_bg”, “drawable”）
        val resourceName = appResources.getResourceEntryName(resourceId)
        val resourceType = appResources.getResourceTypeName(resourceId)
        // 动态获取皮肤包内的指定资源ID
// getResources().getIdentifier(“netease_bg”, “drawable”, “com.netease.skin.packages”);
        val skinResourceId = skinResources!!.getIdentifier(resourceName, resourceType, skinPackageName)
        // 源码1924行：(0 is not a valid resource ID.)
        isDefaultSkin = skinResourceId == 0
        return if (skinResourceId == 0) resourceId else skinResourceId
    }

    //==============================================================================================
    fun getColor(resourceId: Int): Int {
        val ids = getSkinResourceIds(resourceId)
        return if (isDefaultSkin) appResources.getColor(ids) else skinResources!!.getColor(ids)
    }

    fun getColorStateList(resourceId: Int): ColorStateList {
        val ids = getSkinResourceIds(resourceId)
        return if (isDefaultSkin) appResources.getColorStateList(ids) else skinResources!!.getColorStateList(ids)
    }

    // mipmap和drawable统一用法（待测）
    fun getDrawableOrMipMap(resourceId: Int): Drawable {
        val ids = getSkinResourceIds(resourceId)
        return if (isDefaultSkin) appResources.getDrawable(ids) else skinResources!!.getDrawable(ids)
    }

    fun getString(resourceId: Int): String {
        val ids = getSkinResourceIds(resourceId)
        return if (isDefaultSkin) appResources.getString(ids) else skinResources!!.getString(ids)
    }

    // 返回值特殊情况：可能是color / drawable / mipmap
    fun getBackgroundOrSrc(resourceId: Int): Any? { // 需要获取当前属性的类型名Resources.getResourceTypeName(resourceId)再判断
        val resourceTypeName = appResources.getResourceTypeName(resourceId)
        when (resourceTypeName) {
            "color" -> return getColor(resourceId)
            "mipmap", "drawable" -> return getDrawableOrMipMap(resourceId)
        }
        return null
    }

    // 获得字体
    fun getTypeface(resourceId: Int): Typeface { // 通过资源ID获取资源path，参考：resources.arsc资源映射表
        val skinTypefacePath = getString(resourceId)
        // 路径为空，使用系统默认字体
        if (TextUtils.isEmpty(skinTypefacePath)) return Typeface.DEFAULT
        return if (isDefaultSkin) Typeface.createFromAsset(appResources.assets, skinTypefacePath) else Typeface.createFromAsset(skinResources!!.assets, skinTypefacePath)
    }

    companion object {
        var instance: SkinManager? = null
            private set
        private const val ADD_ASSET_PATH = "addAssetPath" // 方法名
        /**
         * 单例方法，目的是初始化app内置资源（越早越好，用户的操作可能是：换肤后的第2次冷启动）
         */
        fun init(application: Application) {
            if (instance == null) {
                synchronized(SkinManager::class.java) {
                    if (instance == null) {
                        instance = SkinManager(application)
                    }
                }
            }
        }

    }

    init {
        appResources = application.resources
        cacheSkin = HashMap()
    }
}