package com.tbs.myapplication

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.tbs.myapplication.hook.AMSHookHelper
import com.tbs.myapplication.ui.main.MainFragment

class MainActivity : AppCompatActivity() {

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase)
        try {
            AMSHookHelper.execStartActivityEntry()
            AMSHookHelper.activityThreadHandler()
        } catch (r : Throwable) {
            Log.e(AMSHookHelper.TAG, "hook failed")
            r.printStackTrace()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, MainFragment.newInstance())
                .commitNow()
        }
    }
}