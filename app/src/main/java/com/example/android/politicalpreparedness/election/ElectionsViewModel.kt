package com.example.android.politicalpreparedness.election

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.Repository
import com.example.android.politicalpreparedness.network.models.Election
import kotlinx.coroutines.launch

class ElectionsViewModel(applicationContext: Context) : ViewModel() {

    private val repository = Repository(applicationContext)

    private val _upcomingElections = MutableLiveData<List<Election>>()
    val upcomingElections: LiveData<List<Election>>
        get() = _upcomingElections

    private val _savedElections = MutableLiveData<List<Election>>()
    val savedElections: LiveData<List<Election>>
        get() = _savedElections

    private fun getUpcomingElections() {
        viewModelScope.launch() {
            _upcomingElections.value = repository.getUpcomingElectionsList()
        }
    }

    private fun getSavedElections() {
        viewModelScope.launch {
            _savedElections.value = repository.getSavedElectionsList()
        }
    }

    fun loadElections() {
        getSavedElections()
        getUpcomingElections()
    }

}