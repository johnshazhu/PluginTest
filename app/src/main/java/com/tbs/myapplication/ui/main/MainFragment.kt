package com.tbs.myapplication.ui.main

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tbs.myapplication.BaseApplication
import com.tbs.myapplication.databinding.MainFragmentBinding
import com.tbs.myapplication.loader.PluginClassLoader

class MainFragment : Fragment() {
    private lateinit var binding: MainFragmentBinding
    private lateinit var pluginLoader: PluginClassLoader
    companion object {
        const val TAG = "MainFragment"
        const val PLUGIN_NAME = "plugin1-debug.apk"
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = MainFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        pluginLoader = (activity?.application as BaseApplication).getPluginLoader()
        binding.testMethod.setOnClickListener {
            test()
        }

        binding.testActivity.setOnClickListener {
            activity()
        }
    }

    private fun test() {
        val cls = pluginLoader.loadClass("com.tbs.plugin1.util.Util")
        cls ?: return

        Log.i(TAG, "find class : ${cls.name}")

        val instanceField = cls.getField("INSTANCE")
        instanceField ?: return
        instanceField.isAccessible = true

        val utilObj = instanceField.get(cls)

        val method = cls.getDeclaredMethod("test", Context::class.java)
        method.isAccessible = true
        method.invoke(utilObj, activity?.applicationContext)

        pluginLoader.clearAssertionStatus()
    }

    private fun activity() {
        val cls = pluginLoader.loadClass("com.tbs.plugin1.MainActivity")
        cls ?: return
        Log.i(TAG, "find class : ${cls.name}")
        val intent = Intent()
        intent.component = ComponentName("com.tbs.plugin1", cls.name)
        startActivity(intent)
    }
}