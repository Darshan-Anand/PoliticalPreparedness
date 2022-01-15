package com.example.android.politicalpreparedness.database

import androidx.room.TypeConverter
import com.example.android.politicalpreparedness.network.models.Address
import java.util.*

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time?.toLong()
    }

    @TypeConverter
    fun addressToString(address: Address?): String? {
        return address?.let {
            address.line1.plus(",")
                .plus(address.line2).plus(",")
                .plus(address.city).plus(",")
                .plus(address.state).plus(',')
                .plus(address.zip)
        }

    }

    @TypeConverter
    fun stringToAddress(addressString: String?): Address? {
        val addressArray = addressString?.split(",")
        return addressArray?.let {
            Address(
                addressArray[0],
                addressArray[1],
                addressArray[2],
                addressArray[3],
                addressArray[4]
            )
        }
    }
}