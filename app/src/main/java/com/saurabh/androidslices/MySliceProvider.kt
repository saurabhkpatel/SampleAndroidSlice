package com.saurabh.androidslices

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.slice.Slice
import androidx.slice.SliceProvider
import androidx.slice.builders.ListBuilder

class MySliceProvider : SliceProvider() {

    override fun onMapIntentToUri(intent: Intent?): Uri {
        Log.d("MySliceProvider", "onMapIntentToUri");
        return super.onMapIntentToUri(intent)
    }

    override fun onCreateSliceProvider(): Boolean {
        return true
    }

    override fun onBindSlice(sliceUri: Uri): Slice? {

        val slice: Slice;
        if (sliceUri.path == "/hello") {
            slice = ListBuilder(context, sliceUri, ListBuilder.INFINITY)
                    .addRow { it.setTitle("URI found.") }
                    .build()
        } else {
            slice = ListBuilder(context, sliceUri, ListBuilder.INFINITY)
                    .addRow {
                        it.setTitle("URI not found.")
                    }
                    .build()
        }
        return slice;
    }

}