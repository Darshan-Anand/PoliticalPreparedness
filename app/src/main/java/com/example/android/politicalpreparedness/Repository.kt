package com.example.android.politicalpreparedness

import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.State
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

class Repository {

    suspend fun getUpcomingElectionsList(): List<Election> {
        val upcomingElection: List<Election>
        withContext(Dispatchers.IO) {
            upcomingElection = CivicsApi.retrofitService.getElectionsList().elections
        }
        return upcomingElection
    }

    suspend fun getVoterInfo(address: String, electionId: Int): State? {
        val state: State?
        withContext(Dispatchers.IO) {
            state = CivicsApi.retrofitService.getVoterInfo(address, electionId).state?.first()
        }
        return state
    }
}