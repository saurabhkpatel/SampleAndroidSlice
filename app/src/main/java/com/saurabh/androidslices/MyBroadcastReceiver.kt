package com.saurabh.androidslices

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.net.wifi.WifiManager

class MyBroadcastReceiver : BroadcastReceiver() {

    companion object {
        const val EXTRA_VALUE_KEY = "extra_value"
        const val TOGGLE_WIFI = "com.saurabh.androidslices.MyBroadcastReceiver.TOGGLE_WIFI"
        const val DECREMENT_COUNTER_ACTION = "com.saurabh.androidslices.MyBroadcastReceiver.ACTION_DECREMENT_COUNTER"
        const val INCREMENT_COUNTER_ACTION = "com.saurabh.androidslices.MyBroadcastReceiver.ACTION_INCREMENT_COUNTER"
        var currentValue = 0
        val dynamicSliceUri = Uri.parse("content://com.saurabh.androidslices/dynamicCountSlice")
        val wifiToggleUri = Uri.parse("content://com.saurabh.androidslices/wifiToggleAction")
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action?.equals(TOGGLE_WIFI) == true) {
            val wifiManager = context?.applicationContext?.getSystemService(Context.WIFI_SERVICE);
            if (wifiManager is WifiManager) {
                val wifiState = intent.getBooleanExtra(EXTRA_VALUE_KEY, wifiManager.isWifiEnabled)
                wifiManager.setWifiEnabled(!wifiState)
                // Actually, wifiManager.setWifiEnabled is an async call, so you can wait or listen to the wifi state change with help of any broadcast and update the slice later for the accurate results.
                context.contentResolver?.notifyChange(wifiToggleUri, null)
            }

        } else if (intent?.action?.equals(DECREMENT_COUNTER_ACTION) == true && intent.hasExtra(EXTRA_VALUE_KEY)) {
            currentValue = intent.getIntExtra(EXTRA_VALUE_KEY, 0);
            context?.contentResolver?.notifyChange(dynamicSliceUri, null)
        } else if (intent?.action?.equals(INCREMENT_COUNTER_ACTION) == true && intent.hasExtra(EXTRA_VALUE_KEY)) {
            currentValue = intent.getIntExtra(EXTRA_VALUE_KEY, 0);
            context?.contentResolver?.notifyChange(dynamicSliceUri, null)
        }
    }
}
