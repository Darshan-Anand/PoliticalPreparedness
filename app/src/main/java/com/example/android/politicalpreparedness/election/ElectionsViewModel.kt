package com.example.android.politicalpreparedness.election

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.Repository
import com.example.android.politicalpreparedness.network.models.Election
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

//TODO: Construct ViewModel and provide election datasource
class ElectionsViewModel : ViewModel() {

    private val repository = Repository()

    private val _navigateToVoterInfo = MutableLiveData<Election>()
    val navigateToVoterInfo: LiveData<Election>
        get() = _navigateToVoterInfo

    // live data val for upcoming elections
    private val _upcomingElections = MutableLiveData<List<Election>>()
    val upcomingElections: LiveData<List<Election>>
        get() = _upcomingElections

    private fun getUpcomingElections() {
        viewModelScope.launch() {
            _upcomingElections.value = repository.getUpcomingElectionsList()
        }
    }

    //TODO: Create live data val for saved elections

    //TODO: Create val and functions to populate live data for upcoming elections from the API and saved elections from local database

    //TODO: Create functions to navigate to saved or upcoming election voter info

    fun startNavigationToVoterInfo(election: Election) {
        _navigateToVoterInfo.value = election
    }

    fun doneNavigationToVoterInfo() {
        _navigateToVoterInfo.value = null
    }

    init {
        getUpcomingElections()
    }

}