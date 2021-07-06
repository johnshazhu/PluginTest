package com.tbs.myapplication.hook

import android.content.ComponentName
import android.content.Intent
import android.util.Log
import com.tbs.myapplication.ui.activity.StubActivity
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method

class IActivityManagerHandler(private val base: Any?) : InvocationHandler {
    override fun invoke(proxy: Any?, method: Method?, args: Array<Any>?): Any? {
        Log.i(AMSHookHelper.TAG, "method : ${method?.name}, args size ：${args?.size}")
        if ("startActivity" == method?.name) {
            // 只拦截这个方法
            // 替换参数, 任你所为;甚至替换原始Activity启动别的Activity偷梁换柱
            // API 23:
            // public final Activity startActivityNow(Activity parent, String id,
            // Intent intent, ActivityInfo activityInfo, IBinder token, Bundle state,
            // Activity.NonConfigurationInstances lastNonConfigurationInstances) {

            // 找到参数里面的第一个Intent 对象
            val raw: Intent
            var index = -1
            args?.let {
                for (i in it.indices) {
                    if (it[i] is Intent) {
                        index = i
                        break
                    }
                }
                if (index >= 0) {
                    raw = it[index] as Intent
                    Log.e(AMSHookHelper.TAG, "target intent : $raw")
                    val newIntent = Intent()

                    // 替身Activity的包名, 也就是我们自己的包名
                    val stubPackage = "com.tbs.myapplication"

                    // 这里我们把启动的Activity临时替换为 StubActivity
                    val componentName = ComponentName(stubPackage, StubActivity::class.java.name)
                    newIntent.component = componentName

                    // 把我们原始要启动的TargetActivity先存起来
                    newIntent.putExtra(AMSHookHelper.EXTRA_TARGET_INTENT, raw)

                    // 替换掉Intent, 达到欺骗AMS的目的
                    it[index] = newIntent

                    Log.d(AMSHookHelper.TAG, "hook success")
                } else {
                    Log.e(AMSHookHelper.TAG, "args don not contain intent")
                }
            }
        }
        if (args == null) {
            return method?.invoke(base)
        }
        return method?.invoke(base, *args)
    }
}