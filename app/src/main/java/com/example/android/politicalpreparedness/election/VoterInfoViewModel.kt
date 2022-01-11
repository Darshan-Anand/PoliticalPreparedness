package com.example.android.politicalpreparedness.election

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.Repository
import com.example.android.politicalpreparedness.network.models.AdministrationBody
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.State
import kotlinx.coroutines.launch
import timber.log.Timber

//class VoterInfoViewModel(private val dataSource: ElectionDao) : ViewModel() {
class VoterInfoViewModel(private val election: Election) : ViewModel() {
    private val repository = Repository()

    //TODO: Add live data to hold voter info
    private val _voterInfoState = MutableLiveData<State>()
    val voterInfoState: LiveData<State>
        get() = _voterInfoState


    //TODO: Add var and methods to populate voter info
    private val _electionSelected = MutableLiveData<Election>()
    val electionSelected: LiveData<Election>
        get() = _electionSelected

    //TODO: Add var and methods to support loading URLs

    private val _stateAdministrationBody = MutableLiveData<AdministrationBody>()
    val stateAdministrationBody: LiveData<AdministrationBody>
        get() = _stateAdministrationBody

    //TODO: Add var and methods to save and remove elections to local database
    //TODO: cont'd -- Populate initial state of save button to reflect proper action based on election saved status

    /**
     * Hint: The saved state can be accomplished in multiple ways. It is directly related to how elections are saved/removed from the database.
     */

    private fun getVoterInfo(address: String, electionId: Int) {
        viewModelScope.launch {
            _stateAdministrationBody.value =
                repository.getVoterInfo(address, electionId)?.electionAdministrationBody
        }
    }

    private fun getAddress(): String {
        if (election.division.state.isNotEmpty()) {
            return "${election.division.country},${election.division.state}"
        }
        return "USA,WA"
    }


    init {
        _electionSelected.value = election
        Timber.d("address: ${getAddress()}")
        getVoterInfo(getAddress(), election.id)
    }

}