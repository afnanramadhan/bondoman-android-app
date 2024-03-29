package com.example.android_hit.utils

import android.icu.text.DecimalFormat
import com.github.mikephil.charting.formatter.ValueFormatter


class MyValueFormatter : ValueFormatter() {
    private val mFormat: DecimalFormat

    init {
        mFormat = DecimalFormat("###,###,##0.0") // use one decimal
    }

    override fun getFormattedValue(value: Float): String {
        return mFormat.format(value) + " %" // e.g. append percentage sign
    }
}