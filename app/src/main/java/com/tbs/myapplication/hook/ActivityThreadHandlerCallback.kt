package com.tbs.myapplication.hook

import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.Message
import android.util.Log
import java.lang.reflect.Field

class ActivityThreadHandlerCallback(private val handler: Handler, private val msgId: Int) : Handler.Callback {
    override fun handleMessage(msg: Message): Boolean {
        Log.i(AMSHookHelper.TAG, "msgId : ${msg.what}, msg : $msg")
        val obj = msg.obj

        when (msg.what) {
            msgId -> {
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
                    try {
                        val callbacksField: Field =
                            obj.javaClass.getDeclaredField("mActivityCallbacks")
                        callbacksField.isAccessible = true
                        val callbacks = callbacksField[obj]
                        if (callbacks is List<*>) {
                            for (item in callbacks) {
                                if (item?.javaClass?.name == "android.app.servertransaction.LaunchActivityItem") {
                                    val intentField = item.javaClass.getDeclaredField("mIntent")
                                    intentField.isAccessible = true
                                    val raw = intentField.get(item) as Intent
                                    val target =
                                        raw.getParcelableExtra<Intent>(AMSHookHelper.EXTRA_TARGET_INTENT)
                                    if (target != null) {
                                        raw.component = target.component
                                    }
                                }
                                Log.i(AMSHookHelper.TAG, "item : " + item?.javaClass)
                            }
                        }
                    } catch (r : Throwable) {
                        r.printStackTrace()
                    }
                } else {
                    try {
                        // 把替身恢复成真身
                        val intent: Field = obj.javaClass.getDeclaredField("intent")
                        intent.isAccessible = true
                        val raw = intent.get(obj) as Intent

                        val target = raw.getParcelableExtra<Intent>(AMSHookHelper.EXTRA_TARGET_INTENT)
                        raw.component = target!!.component
                    } catch (r : Throwable) {
                        r.printStackTrace()
                    }
                }
            }
        }
        handler.handleMessage(msg)
        return true
    }
}