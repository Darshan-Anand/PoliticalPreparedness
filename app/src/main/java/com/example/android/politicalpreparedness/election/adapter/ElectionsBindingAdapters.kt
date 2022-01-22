package com.example.android.politicalpreparedness.election.adapter

import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.Group
import androidx.databinding.BindingAdapter
import com.example.android.politicalpreparedness.network.models.Address
import java.text.SimpleDateFormat
import java.util.*

@BindingAdapter("electionDate")
fun TextView.getDate(date: Date?) {
    date?.let {
        val day = SimpleDateFormat("dd-MMM-yyyy").format(date)
        this.text = day
    }
}

@BindingAdapter("electionDay")
fun TextView.getDay(date: Date?) {
    date?.let {
        val dateString = SimpleDateFormat("EEEE").format(date)
        this.text = dateString
    }
}

@BindingAdapter("address")
fun TextView.getAddress(address: Address?) {
    address?.let {
        this.text = address.line1.plus(",\n")
            .plus(
                if (address.line2 != null) {
                    address.line2.plus(",\n")
                } else {
                    address.city.plus(",\n")
                        .plus(address.state).plus(", ")
                        .plus(address.zip)
                }
            )
    }
}

@BindingAdapter("groupVisibility")
fun Group.checkGroupVisibility(address: Address?) = if (address != null) {
    this.visibility = View.VISIBLE
} else {
    this.visibility = View.GONE
}
