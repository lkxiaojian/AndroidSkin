# AndroidSkin

1引用

 1.1 Add it in your root build.gradle at the end of repositories:
 
 
    
   allprojects {
   
   
        repositories {
        
        
            ...
            maven { url 'https://jitpack.io' }
            
            
        }
        
        
    }
    
    
    


 1.2 Add the dependency
 
   dependencies {
       implementation 'com.github.lkxiaojian:AndroidSkin:1.0.2'
     }
     
     
2使用

  1.如果使用静态皮肤包 就别集成框架了 直接设置
  
delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_YES. //日间

delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_NO//夜间

即可 （查看3.1静态换肤）

2.2  
  在Application 中调用 SkinManager.init(this)
    Activity 
    Activity 继承 SkinActivity  
     加载动态皮肤包 skinDynamic(skinPath, R.color.skin_item_color) //1外部路径 2主题颜色
     还原皮肤包 defaultSkin(R.color.colorPrimary)

 自定义VIew  
 需要 继承 ViewsMatch  查看domo 中的用法（可以封装一个BaseView ，看项目需求）
 com.zky.myskin.views.CustomCircleView

Fragment 


调用 SkinManager.instance?.applyViews(view)

RecycleView  
两种方式 1.把recycleView 复用关闭 holder.setIsRecyclable(true) (不建议)
在viewHodle 中调用 SkinManager.instance?.applyViews(view)

public class BaseHolder extends RecyclerView.ViewHolder{
    public BaseHolder(@NonNull View itemView) {
        super(itemView);
        SkinManager.Companion.getInstance().applyViews(itemView);
    }  
}


3原理

3.1静态换肤
    

//Kotlin code  

delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_YES. //日间

delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_NO//夜间

在对应的src 中新建 drawable-night values-night 即可  




3.2 动态换肤 
  在Acvitity 创建布局都需要 setContentView()  

@Override
public void setContentView(int resId) {
    ensureSubDecor();
    //android 总布局 是个FrameLayout 我们所有的布局都加载这个上面 R.id.content 是系统的ID
    ViewGroup contentParent = mSubDecor.findViewById(android.R.id.content);
    contentParent.removeAllViews();
    LayoutInflater.from(mContext).inflate(resId, contentParent);
    mAppCompatWindowCallback.getWrapped().onContentChanged();
}
 
   LayoutInflater.from(mContext).inflate(resId, contentParent);  这个才是加载布局实际操作的

   public View inflate(@LayoutRes int resource, @Nullable ViewGroup root, boolean attachToRoot) {
    final Resources res = getContext().getResources();
    if (DEBUG) {
        Log.d(TAG, "INFLATING from resource: \"" + res.getResourceName(resource) + "\" ("
              + Integer.toHexString(resource) + ")");
    }

    //如果创建过这个view ,内部通过反射 得到 
    View view = tryInflatePrecompiled(resource, res, root, attachToRoot);
    if (view != null) {
        return view;
    }
   //得到xml 解析器
    XmlResourceParser parser = res.getLayout(resource);
    try {
        return inflate(parser, root, attachToRoot);
    } finally {
        parser.close();
    }
}

inflate(parser, root, attachToRoot) 

由于代码过多 简略下
public View inflate(XmlPullParser parser, @Nullable ViewGroup root, boolean attachToRoot) {
    synchronized (mConstructorArgs) {
        Trace.traceBegin(Trace.TRACE_TAG_VIEW, "inflate");


        final Context inflaterContext = mContext;
         // 资源管理器
        final AttributeSet attrs = Xml.asAttributeSet(parser);
        Context lastContext = (Context) mConstructorArgs[0];
        mConstructorArgs[0] = inflaterContext;
        View result = root;
 ...
       
      rInflate(parser, root, inflaterContext, attrs, false);

 ...
        return result;
    }
}


 //解析xml 布局 用的是 Pull解析xml(while 循环  把view 添加到viewGroup)  

void rInflate(XmlPullParser parser, View parent, Context context,
        AttributeSet attrs, boolean finishInflate) throws XmlPullParserException, IOException {


    final int depth = parser.getDepth();
    int type;
    boolean pendingRequestFocus = false;


    while (((type = parser.next()) != XmlPullParser.END_TAG ||
            parser.getDepth() > depth) && type != XmlPullParser.END_DOCUMENT) {


    ..

           final View view = createViewFromTag(parent, name, context, attrs);
            final ViewGroup viewGroup = (ViewGroup) parent;
            final ViewGroup.LayoutParams params = viewGroup.generateLayoutParams(attrs);
            rInflateChildren(parser, view, attrs, true);
            viewGroup.addView(view, params);
  ..


  
}




rInflate 其中调用 tryCreateView（） 方法

public final View tryCreateView(@Nullable View parent, @NonNull String name,
    @NonNull Context context,
    @NonNull AttributeSet attrs) {
    if (name.equals(TAG_1995)) {
        // Let's party like it's 1995!
        return new BlinkLayout(context, attrs);
    }


    View view;
     //这两个才是 创建view的方法  布局加载工厂
     //mFactory 是mFactory2 的父类
    if (mFactory2 != null) {

        view = mFactory2.onCreateView(parent, name, context, attrs);
    } else if (mFactory != null) {
        view = mFactory.onCreateView(name, context, attrs);
    } else {
        view = null;
    }
    if (view == null && mPrivateFactory != null) {
        view = mPrivateFactory.onCreateView(parent, name, context, attrs);
    }
    return view;
}

大致流程是 

通过布局ID (resID) —> XmlResourceParser —> 通过while 循环添加 
{
   createViewFromTag() 这个方法里面区分是否是自定义view（if (-1 == name.indexOf('.')) {}）。
   不是 android.view.* 这样的路径
   不过都通过反射实例化对象 （如果有缓冲，冲缓存中读取）
   然后 根布局.addView
}


在看 super.onCreate
override fun onCreate(savedInstanceState: Bundle?) {
   
    super.onCreate(savedInstanceState)
}


@Override
protected void onCreate(@Nullable Bundle savedInstanceState) {
    final AppCompatDelegate delegate = getDelegate();
    delegate.installViewFactory();
    delegate.onCreate(savedInstanceState);
    super.onCreate(savedInstanceState);
}

@Override
public void installViewFactory() {
    LayoutInflater layoutInflater = LayoutInflater.from(mContext);
    if (layoutInflater.getFactory() == null) {
        LayoutInflaterCompat.setFactory2(layoutInflater, this);
    } else {
        if (!(layoutInflater.getFactory2() instanceof AppCompatDelegateImpl)) {
            Log.i(TAG, "The Activity's LayoutInflater already has a Factory installed"
                    + " so we can not install AppCompat's");
        }
    }
}

这个方法设置 Factory 的 如果是null  抛出异常
所以我们在    super.onCreate(savedInstanceState) 添加
val layoutInflater = LayoutInflater.from(this)
LayoutInflaterCompat.setFactory2(layoutInflater, this)
这样就不会抛出异常了

然后在onCreateView 中收集资源就可以了
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

如TextView
<!-- TextView控件属性 -->
<declare-styleable name="SkinnableTextView">
    <attr name="android:background" />
    <attr name="android:textColor" />
    <attr name="android:drawableBottom" />
    <attr name="android:drawableLeft" />
    <attr name="android:drawableRight" />
    <attr name="android:drawableTop" />
    <!-- 字体属性 -->
    <attr name="custom_typeface" />
</declare-styleable>

设置textColor 属性
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
 

读取外部皮肤包
fun loaderSkinResources(skinPath: String?) {
    // 如果没有皮肤包或者没做换肤动作，方法不执行直接返回！
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


      // 创建加载外部的皮肤包(mySkin.skin.apk  名字随便去)文件Resources（注：依然是本应用加载）
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




