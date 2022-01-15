package com.example.android.politicalpreparedness.election

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.Repository
import com.example.android.politicalpreparedness.database.ElectionAndAdministrationBody
import com.example.android.politicalpreparedness.network.models.AdministrationBody
import com.example.android.politicalpreparedness.network.models.Division
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse
import kotlinx.coroutines.launch
import timber.log.Timber


class VoterInfoViewModel(applicationContext: Context) :
    ViewModel() {
    private val repository = Repository(applicationContext)

    private val _election = MutableLiveData<Election>()
    val election: LiveData<Election>
        get() = _election

    private val _stateAdministrationBody = MutableLiveData<AdministrationBody>()
    val stateAdministrationBody: LiveData<AdministrationBody>
        get() = _stateAdministrationBody

    private val _followElectionButtonText = MutableLiveData<String>()
    val followElectionButtonText: LiveData<String>
        get() = _followElectionButtonText


    fun getAddress(division: Division): String {
        if (division.state.isNotEmpty()) {
            return "${division.country},${division.state}"
        }
        return "USA,WA"
    }

    fun loadElectionInfo(address: String, electionId: Int, loadFromDB: Boolean) {
        Timber.d("loadFromDb= $loadFromDB")
        if (loadFromDB) {
            getElectionInfoFromDb(electionId)
        } else {
            getElectionInfoFromNetwork(address, electionId)
        }
        updateFollowButtonText(electionId)
    }

    private fun getElectionInfoFromNetwork(address: String, electionId: Int) {
        viewModelScope.launch {
            val voterInfoResponse: VoterInfoResponse? =
                repository.getElectionInfo(address, electionId)
            if (voterInfoResponse != null) {
                _election.value = voterInfoResponse.election
                _stateAdministrationBody.value =
                    voterInfoResponse.state?.first()?.electionAdministrationBody
            }
        }
    }

    private fun getElectionInfoFromDb(electionId: Int) {
        viewModelScope.launch {
            val electionAndAdministrationBody: ElectionAndAdministrationBody =
                repository.getElectionAndAdministrationBody(electionId)!!
            _election.value = electionAndAdministrationBody.election
            _stateAdministrationBody.value = electionAndAdministrationBody.administrationBody
        }
    }

    fun followOrUnfollowElection(electionId: Int) {
        viewModelScope.launch {
            val electionFromDB = repository.getElectionFromDb(electionId)
            if (electionFromDB == electionId) {
                repository.deleteElectionAndAdministrationBody(
                    _election.value!!,
                    _stateAdministrationBody.value!!
                )
            } else {
                repository.insertElectionAndAdministrationBody(
                    election.value!!,
                    _stateAdministrationBody.value!!
                )
            }
            updateFollowButtonText(electionId)
        }
    }

    private fun updateFollowButtonText(electionId: Int) {
        viewModelScope.launch {
            val electionFromDB = repository.getElectionFromDb(electionId)
            Timber.d("electionID= $electionId, electionIdFromDb= $electionFromDB")
            if (electionFromDB == electionId) {
                _followElectionButtonText.value = "Unfollow"
            } else {
                _followElectionButtonText.value = "Follow"
            }
        }
    }


}