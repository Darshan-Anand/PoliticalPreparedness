package com.example.android.politicalpreparedness.network.jsonadapter

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import java.text.SimpleDateFormat
import java.util.*

class DateAdapter {

    private val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
    @FromJson
    fun electionDayFromJson(electionDay: String) : Date {
        return simpleDateFormat.parse(electionDay)
    }

    @ToJson
    fun electionDayToJson(electionDay: Date) : String {
        return simpleDateFormat.format(electionDay)
    }
}