package com.saurabh.androidslices

import android.app.PendingIntent
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.core.graphics.drawable.IconCompat
import androidx.slice.Slice
import androidx.slice.SliceProvider
import androidx.slice.builders.*
import androidx.slice.core.SliceHints

class SliceProblemProvider : SliceProvider() {

    override fun onCreateSliceProvider(): Boolean {
        return true
    }

    override fun onBindSlice(sliceUri: Uri): Slice? {
        return when {
            sliceUri.path == "/basicRowSliceKTX" -> createRowSliceWithStartItemKTX(sliceUri)
            sliceUri.path == "/inputRangePrimaryAction" -> createInputRangeSlice(sliceUri)
            else -> {
                return null
            }
        }
    }

    private fun createInputRangeSlice(sliceUri: Uri): Slice? {
        val toggleAction = createBrightnessAction()

        return list(context, sliceUri, ListBuilder.INFINITY) {
            inputRange {
                title = "Adaptive brightness"
                subtitle = "Optimizes brightness for available light"
                min = 0
                max = 100
                value = 45
                inputAction = createSettingsPendingIntent()
                // not working primary action.
                primaryAction = createPrimaryOpenMainActivityAction()
            }
        }
    }

    private fun createRowSliceWithStartItemKTX(sliceUri: Uri): Slice {
        return list(context, sliceUri, ListBuilder.INFINITY) {
            row {
                setTitleItem(createActivityAction(Intent(context, MainActivity::class.java), R.drawable.ic_pizza_slice_24, SliceHints.ICON_IMAGE))
                title = "Welcome Android Slice"
                subtitle = "It has Start Item"
            }
        }
    }

    private fun createActivityAction(actionIntent: Intent, drawableInt: Int, imageMode: Int): SliceAction {
        return SliceAction.create(
                PendingIntent.getActivity(context, 0, actionIntent, 0),
                IconCompat.createWithResource(context, drawableInt),
                imageMode,
                "Open MainActivity.")
    }

    private fun createBrightnessAction(): SliceAction {
        val intent = Intent(context, MyBroadcastReceiver::class.java)
        return SliceAction.create(PendingIntent.getBroadcast(context, 0, intent, 0),
                IconCompat.createWithResource(context, R.drawable.ic_brightness_auto_24),
                SliceHints.ICON_IMAGE,
                "Toggle adaptive brightness")
    }

    private fun createPrimaryOpenMainActivityAction(): SliceAction {
        val intent = Intent(context, MainActivity::class.java)
        return SliceAction.create(
                PendingIntent.getActivity(context, 0, intent, 0),
                IconCompat.createWithResource(context, R.drawable.ic_open_24),
                ListBuilder.ICON_IMAGE,
                "Open MainActivity."
        )
    }

    private fun createSettingsPendingIntent(): PendingIntent {
        return PendingIntent.getActivity(
                context,
                0,
                Intent(Settings.ACTION_SETTINGS),
                PendingIntent.FLAG_UPDATE_CURRENT)
    }
}