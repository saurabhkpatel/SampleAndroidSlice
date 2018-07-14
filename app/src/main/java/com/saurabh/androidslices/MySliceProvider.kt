package com.saurabh.androidslices

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.net.wifi.WifiManager
import android.provider.Settings
import android.util.Log
import androidx.core.graphics.drawable.IconCompat
import androidx.slice.Slice
import androidx.slice.SliceProvider
import androidx.slice.builders.*
import androidx.slice.builders.ListBuilder.INFINITY
import androidx.slice.core.SliceHints
import java.util.*
import kotlin.concurrent.schedule


class MySliceProvider : SliceProvider() {

    companion object {
        var contentLoaded = false
        val actualFare = "45 miles | 45 mins | $45.23"
        val delayContentSliceUri = Uri.parse("content://com.saurabh.androidslices/delayContentSlice")
    }

    override fun onMapIntentToUri(intent: Intent?): Uri {
        Log.d("MySliceProvider", "onMapIntentToUri");
        return super.onMapIntentToUri(intent)
    }

    override fun onCreateSliceProvider(): Boolean {
        return true
    }

    override fun onBindSlice(sliceUri: Uri): Slice? {
        return when {
            sliceUri.path == "/basicRowSlice" -> createBasicRowSlice(sliceUri)
            sliceUri.path == "/basicHeaderSlice" -> createBasicHeaderSlice(sliceUri)
            sliceUri.path == "/basicActionClickSlice" -> createBasicInteractiveSlice(sliceUri)
            sliceUri.path == "/basicActionClickSliceWithKTX" -> createBasicInteractiveSliceWithKTX(sliceUri)
            sliceUri.path == "/wifiToggleAction" -> createWifiToggleActionSlice(sliceUri)
            sliceUri.path == "/dynamicCountSlice" -> createDynamicSlice(sliceUri)
            sliceUri.path == "/inputRangeSlice" -> createInputRangeSlice(sliceUri)
            sliceUri.path == "/rangeSlice" -> createRangeSlice(sliceUri)
            sliceUri.path == "/headerSlice" -> createHeaderSlice(sliceUri)
            sliceUri.path == "/headerSliceWithMoreActions" -> createHeaderSliceWithMoreActions(sliceUri)
            sliceUri.path == "/headerSliceWithHeaderAndRow" -> createSliceWithHeaderAndRow(sliceUri)
            sliceUri.path == "/gridRowSlice" -> createSliceWithGridRow(sliceUri)
            sliceUri.path == "/delayContentSlice" -> createSliceShowingLoading(sliceUri)
            sliceUri.path == "/seeMoreRowSlice" -> createSliceWithSeeMoreAction(sliceUri)
            sliceUri.path == "/combineSlices" -> createCombineSlices(sliceUri)
            else -> createErrorSlice(sliceUri)
        }
    }

    //region Basic Slices

    private fun createBasicRowSlice(sliceUri: Uri): Slice {
        return ListBuilder(context, sliceUri, ListBuilder.INFINITY)
                .addRow {
                    it.setTitle("Hello Slice.")
                    // uncomment the following line to remove the exception.
                    // it.primaryAction = createPrimaryOpenMainActivityAction()
                }
                .build()
    }

    private fun createBasicHeaderSlice(sliceUri: Uri): Slice {
        return ListBuilder(context, sliceUri, ListBuilder.INFINITY)
                .setHeader {
                    it.setTitle("Hello Slice: Header")
                }
                .build()
    }

    private fun createBasicInteractiveSlice(sliceUri: Uri): Slice {
        val listBuilder = ListBuilder(context, sliceUri, INFINITY)
        val rowBuilder = ListBuilder.RowBuilder(listBuilder)
                .setTitle("Perform action in app")
                .setSubtitle("Click Me", true)
                .setPrimaryAction(createPrimaryOpenMainActivityAction())
        listBuilder.addRow(rowBuilder)
        return listBuilder.build()
    }

    private fun createBasicInteractiveSliceWithKTX(sliceUri: Uri): Slice {
        return list(context, sliceUri, INFINITY) {
            row {
                title = "Perform action :KTX"
                primaryAction = createPrimaryOpenMainActivityAction()
                subtitle = "Click Me"
            }
        }
    }

    //endregion

    //region Interactive Slice (eg. Wifi Toggle)
    private fun createWifiToggleActionSlice(sliceUri: Uri): Slice? {
        val subTitle: String
        val wifiManager = context.getSystemService(Context.WIFI_SERVICE)
        val isWifiEnabled = wifiManager is WifiManager && wifiManager.isWifiEnabled
        if (isWifiEnabled) {
            subTitle = "Enabled";
        } else {
            subTitle = "Not Enabled";
        }

        return list(context, sliceUri, INFINITY) {
            row {
                title = "Wifi"
                subtitle = subTitle
                primaryAction = createWiFiToggleAction(isWifiEnabled)
            }
        }
    }

    private fun createWiFiToggleAction(wifiEnabled: Boolean): SliceAction {
        val intent = Intent(context, MyBroadcastReceiver::class.java).setAction(MyBroadcastReceiver.TOGGLE_WIFI).putExtra(MyBroadcastReceiver.EXTRA_VALUE_KEY, wifiEnabled);
        return SliceAction(PendingIntent.getBroadcast(context, 0, intent, 0), "Toggle Wi-Fi", wifiEnabled)
    }
    //endregion

    //region Dynamic Slice

    private fun createDynamicSlice(sliceUri: Uri): Slice? {
        val incrementPendingIntent = PendingIntent.getBroadcast(context,
                0,
                Intent(context, MyBroadcastReceiver::class.java).setAction(MyBroadcastReceiver.INCREMENT_COUNTER_ACTION).putExtra(MyBroadcastReceiver.EXTRA_VALUE_KEY,
                        MyBroadcastReceiver.currentValue + 1), PendingIntent.FLAG_UPDATE_CURRENT)

        val decrementPendingIntent = PendingIntent.getBroadcast(context,
                0,
                Intent(context, MyBroadcastReceiver::class.java).setAction(MyBroadcastReceiver.DECREMENT_COUNTER_ACTION).putExtra(MyBroadcastReceiver.EXTRA_VALUE_KEY,
                        MyBroadcastReceiver.currentValue - 1), PendingIntent.FLAG_UPDATE_CURRENT)

        val incrementAction = SliceAction.create(incrementPendingIntent, IconCompat.createWithResource(context, R.drawable.ic_plus), ListBuilder.ICON_IMAGE, "Increment Counter.")
        val decrementAction = SliceAction.create(decrementPendingIntent, IconCompat.createWithResource(context, R.drawable.ic_minus), ListBuilder.ICON_IMAGE, "Decrement Counter.")

        return list(context, sliceUri, INFINITY) {
            row {
                primaryAction = createPrimaryOpenMainActivityAction()
                title = "Count: ${MyBroadcastReceiver.currentValue}"
                subtitle = "Click me"
                addEndItem(incrementAction)
                addEndItem(decrementAction)
            }
        }
    }

    //endregion

    //region Delayed Content Slice example
    private fun createSliceShowingLoading(sliceUri: Uri): Slice {
        // We’re waiting to load the time to work so indicate that on the slice by
        // setting the subtitle with the overloaded method and indicate true.
        Timer("SettingUp", false).schedule(2000) { loadSliceContents() }
        return list(context, sliceUri, ListBuilder.INFINITY) {
            row {
                title = "Ride to work"
                if (contentLoaded) {
                    setSubtitle(actualFare, false)
                } else {
                    setSubtitle(null, true)
                }
                primaryAction = createPrimaryOpenMainActivityAction()
            }
        }
    }

    private fun loadSliceContents() {
        contentLoaded = true
        context.contentResolver.notifyChange(delayContentSliceUri, null)
    }
    //endregion

    //region Range/InputRange Slices
    private fun createRangeSlice(sliceUri: Uri): Slice? {
        return list(context, sliceUri, INFINITY) {
            header {
                title = "Current brightness level"
                primaryAction = createPrimaryOpenMainActivityAction()
            }
            range {
                max = 100
                value = 45
            }
        }
    }

    private fun createInputRangeSlice(sliceUri: Uri): Slice? {
        val toggleAction = createToggleAction()

        return list(context, sliceUri, INFINITY) {
            row {
                title = "Adaptive brightness"
                subtitle = "Optimizes brightness for available light"
                primaryAction = toggleAction
            }
            inputRange {
                min = 0
                max = 100
                value = 45
                inputAction = createSettingsPendingIntent()
            }
        }
    }
    //endregion

    //region Header/Row Builder Examples
    private fun createHeaderSlice(sliceUri: Uri): Slice? {
        return list(context, sliceUri, INFINITY) {
            header {
                title = "Header Title"
                subtitle = "Header Sub Title"
                summary = "Header Summary"
            }
        }
    }

    private fun createHeaderSliceWithMoreActions(sliceUri: Uri): Slice? {
        return list(context, sliceUri, INFINITY) {
            header {
                title = "Header 2 actions"
                subtitle = "Choose any action"
                summary = "Choose any action from two actions"
                primaryAction = createPrimaryOpenMainActivityAction()
            }
            addAction(createToggleAction())
            addAction(createPrimaryOpenMainActivityAction())
        }
    }

    private fun createSliceWithHeaderAndRow(sliceUri: Uri): Slice? {
        val intent = Intent(context, MainActivity::class.java)
        val sliceAction = SliceAction.createDeeplink(PendingIntent.getActivity(context, 0, intent, 0),
                IconCompat.createWithResource(context, R.drawable.ic_launcher_background),
                ListBuilder.ICON_IMAGE,
                "Open MainActivity.")

        return list(context, sliceUri, ListBuilder.INFINITY) {
            header {
                title = "Get a ride."
                subtitle = "Ride in 4 min."
                summary = "Work in 45 min | Home in 15 min."
            }
            row {
                primaryAction = sliceAction
                title = "Home"
                subtitle = "15 miles | 15 min | $15.23"
                addEndItem(IconCompat.createWithResource(context, R.drawable.ic_plus), SliceHints.ICON_IMAGE)

            }
            row {
                primaryAction = sliceAction
                title = "Time Stamp"
                subtitle = "contains start and end items"
                addEndItem(System.currentTimeMillis())
                setTitleItem(IconCompat.createWithResource(context, R.drawable.ic_car_24), SliceHints.ICON_IMAGE)
            }
            setAccentColor(R.color.colorAccent)
        }
    }
    //endregion

    //region GridRowBuilder examples
    private fun createSliceWithGridRow(sliceUri: Uri): Slice {

        return list(context, sliceUri, ListBuilder.INFINITY) {
            header {
                title = "Famous restaurants"
                primaryAction = createPrimaryOpenMainActivityAction()
            }
            gridRow {
                cell {
                    addImage(IconCompat.createWithResource(context!!, R.drawable.r1), SliceHints.LARGE_IMAGE)
                    addTitleText("Top Restaurant")
                    addText("0.3 mil")
                    contentIntent = createSettingsPendingIntent()
                }
                cell {
                    addImage(IconCompat.createWithResource(context!!, R.drawable.r2), SliceHints.LARGE_IMAGE)
                    addTitleText("Fast and Casual")
                    addText("0.5 mil")
                    contentIntent = createSettingsPendingIntent()
                }
                cell {
                    addImage(IconCompat.createWithResource(context!!, R.drawable.r3), SliceHints.LARGE_IMAGE)
                    addTitleText("Casual Diner")
                    addText("0.9 mi")
                    contentIntent = createSettingsPendingIntent()
                }
                cell {
                    addImage(IconCompat.createWithResource(context!!, R.drawable.r4), SliceHints.LARGE_IMAGE)
                    addTitleText("Ramen Spot")
                    addText("1.2 mi")
                    contentIntent = createSettingsPendingIntent()
                }
                setSeeMoreAction(createSettingsPendingIntent())
                // uncomment the below line to use the custom see more cell.
                // seeMoreCell = GridRowBuilder.CellBuilder().addTitleText("Custom Title").addText("Custom Text").addImage(IconCompat.createWithResource(context!!, R.drawable.ic_android_24), SliceHints.SMALL_IMAGE).setContentIntent(createSettingsPendingIntent())
            }
        }
    }
    //endregion

    //region seeMoreAction/Row example, This isn't working, need to check why
    private fun createSliceWithSeeMoreAction(sliceUri: Uri): Slice {

        return list(context, sliceUri, ListBuilder.INFINITY) {
            header {
                title = "Near by restaurants"
                primaryAction = createToggleAction()
            }
            row {
                primaryAction = createPrimaryOpenMainActivityAction()
                title = "Bakora, Mediterranean food"
                setTitleItem(IconCompat.createWithResource(context, R.drawable.r1), SliceHints.SMALL_IMAGE)
            }
            row {
                primaryAction = createPrimaryOpenMainActivityAction()
                title = "Taj, Indian food"
                setTitleItem(IconCompat.createWithResource(context, R.drawable.r2), SliceHints.SMALL_IMAGE)
            }
            row {
                primaryAction = createPrimaryOpenMainActivityAction()
                title = "Primer Pizza, Italian food"
                setTitleItem(IconCompat.createWithResource(context, R.drawable.r3), SliceHints.SMALL_IMAGE)
            }
            setSeeMoreAction(createSettingsPendingIntent())
        }
    }
    //endregion

    //region Combine Row/Item tempaltes
    private fun createCombineSlices(sliceUri: Uri): Slice {

        return list(context, sliceUri, ListBuilder.INFINITY) {
            row {
                title = "Upcoming Trip: Seattle"
                subtitle = "Aug 15-20 • 5 Guests"
                addEndItem(createActivityAction(R.drawable.ic_flight_24, SliceHints.ICON_IMAGE))
                addEndItem(createActivityAction(R.drawable.ic_email_24, SliceHints.ICON_IMAGE))
                primaryAction = createPrimaryOpenMainActivityAction()
            }
            gridRow {
                cell {
                    addImage(IconCompat.createWithResource(context, R.drawable.r1), SliceHints.LARGE_IMAGE)
                }
            }
            gridRow {
                cell {
                    addTitleText("Check In")
                    addText("2:00 PM, Aug 15")
                }
                cell {
                    addTitleText("Check In")
                    addText("11:00 AM, Aug 20")
                }
            }
        }
    }
    //endregion

    //region Helper methods
    private fun createSettingsPendingIntent(): PendingIntent {
        return PendingIntent.getActivity(
                context,
                0,
                Intent(Settings.ACTION_SETTINGS),
                PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun createPrimaryOpenMainActivityAction(): SliceAction {
        val intent = Intent(context, MainActivity::class.java)
        return SliceAction(
                PendingIntent.getActivity(context, 0, intent, 0),
                IconCompat.createWithResource(context, R.drawable.ic_android_24),
                ListBuilder.ICON_IMAGE,
                "Open MainActivity."
        )
    }

    private fun createActivityAction(drawableInt: Int, imageMode: Int): SliceAction {
        val intent = Intent(context, MainActivity::class.java)
        return SliceAction(
                PendingIntent.getActivity(context, 0, intent, 0),
                IconCompat.createWithResource(context, drawableInt),
                imageMode,
                "Open MainActivity."
        )
    }

    private fun createToggleAction(): SliceAction {
        val intent = Intent(context, MyBroadcastReceiver::class.java)
        return SliceAction(PendingIntent.getBroadcast(context, 0, intent, 0),
                "Toggle adaptive brightness",
                true)
    }

    private fun createErrorSlice(sliceUri: Uri): Slice {
        return ListBuilder(context, sliceUri, ListBuilder.INFINITY)
                .addRow {
                    it.title = "URI not found, Error."
                    it.primaryAction = createPrimaryOpenMainActivityAction()
                }
                .build()
    }
    //endregion
}