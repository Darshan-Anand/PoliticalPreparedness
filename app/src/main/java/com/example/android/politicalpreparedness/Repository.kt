package com.example.android.politicalpreparedness

import android.content.Context
import com.example.android.politicalpreparedness.database.ElectionAndAdministrationBody
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.AdministrationBody
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.RepresentativeResponse
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class Repository(applicationContext: Context) {

    private val database = ElectionDatabase.getDatabase(applicationContext)

    suspend fun getUpcomingElectionsList(): List<Election> {
        val upcomingElection: List<Election>
        withContext(Dispatchers.IO) {
            upcomingElection = CivicsApi.retrofitService.getElectionsList().elections
        }
        return upcomingElection
    }

    suspend fun getRepresentativeInfoByAddress(address: String): RepresentativeResponse {
        val representativeResponse: RepresentativeResponse
        withContext(Dispatchers.IO) {
            representativeResponse =
                CivicsApi.retrofitService.getRepresentativesInfoByAddress(address)
        }
        return representativeResponse
    }

    suspend fun getSavedElectionsList(): List<Election> {
        val savedElections: List<Election>
        withContext(Dispatchers.IO) {
            savedElections = database.electionDao.getAllElections()
        }
        return savedElections
    }

    suspend fun getElectionInfo(address: String, electionId: Int): VoterInfoResponse? {
        val voterInfoResponse: VoterInfoResponse?
        withContext(Dispatchers.IO) {
            voterInfoResponse = CivicsApi.retrofitService.getVoterInfo(address, electionId)
        }
        return voterInfoResponse
    }

    suspend fun getElectionFromDb(electionId: Int): Int {
        val electionIdReturned: Int
        withContext(Dispatchers.IO) {
            electionIdReturned = database.electionDao.checkElectionSaved(electionId)
        }
        return electionIdReturned
    }


    suspend fun insertElectionAndAdministrationBody(
        election: Election,
        administrationBody: AdministrationBody
    ) {
        withContext(Dispatchers.IO) {
            administrationBody.adminId = election.id
            database.electionDao.insertElectionAndAdministrationBody(election, administrationBody)
        }
    }

    suspend fun deleteElectionAndAdministrationBody(
        election: Election,
        administrationBody: AdministrationBody
    ) {
        withContext(Dispatchers.IO) {
            administrationBody.adminId = election.id
            database.electionDao.deleteElectionAndAdminstrationBody(election, administrationBody)
        }
    }

    suspend fun getElectionAndAdministrationBody(electionId: Int): ElectionAndAdministrationBody? {
        val electionAndAdministrationBody: ElectionAndAdministrationBody
        withContext((Dispatchers.IO)) {
            electionAndAdministrationBody =
                database.electionDao.getElectionAndAdministrationBody(electionId)
        }
        return electionAndAdministrationBody
    }
}