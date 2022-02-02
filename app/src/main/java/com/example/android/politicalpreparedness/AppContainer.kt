package com.example.android.politicalpreparedness

import android.content.Context
import com.example.android.politicalpreparedness.database.ElectionDatabase

class AppContainer(private val applicationContext: Context) {
    private val database = ElectionDatabase.getDatabase(applicationContext)

    val repository = Repository(database)
}