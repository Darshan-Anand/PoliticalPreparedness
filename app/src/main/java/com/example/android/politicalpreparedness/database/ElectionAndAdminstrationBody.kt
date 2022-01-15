package com.example.android.politicalpreparedness.database

import androidx.room.Embedded
import androidx.room.Relation
import com.example.android.politicalpreparedness.network.models.AdministrationBody
import com.example.android.politicalpreparedness.network.models.Election

data class ElectionAndAdministrationBody(
    @Embedded val election: Election,
    @Relation(
        parentColumn = "id",
        entityColumn = "adminId"
    )
    val administrationBody: AdministrationBody
)