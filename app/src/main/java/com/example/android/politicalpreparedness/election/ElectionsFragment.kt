package com.example.android.politicalpreparedness.election

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.android.politicalpreparedness.databinding.FragmentElectionBinding
import com.example.android.politicalpreparedness.election.adapter.ElectionListAdapter
import com.example.android.politicalpreparedness.network.models.Division
import com.example.android.politicalpreparedness.network.models.Election
import timber.log.Timber
import java.util.*

class ElectionsFragment : Fragment() {

    private lateinit var binding: FragmentElectionBinding
    private lateinit var viewModel: ElectionsViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentElectionBinding.inflate(inflater)

        //Factory to get ElectionViewModel
        val electionsViewModelFactory = ElectionsViewModelFactory()

        /**
         * Getting instance of view model
         *  @param electionsViewModelFactory
         */
        viewModel =
            ViewModelProvider(this, electionsViewModelFactory).get(ElectionsViewModel::class.java)

        binding.viewModel = viewModel

        binding.lifecycleOwner = this

        /**
         * Observing
         */
        viewModel.navigateToVoterInfo.observe(requireActivity(), { election ->
            election?.let {
                findNavController().navigate(
                    ElectionsFragmentDirections.actionElectionsFragmentToVoterInfoFragment(
                        election.id,
                        election.division
                    )
                )
                viewModel.doneNavigationToVoterInfo()
            }

        })

        val upcomingElectionListAdapter =
            ElectionListAdapter(ElectionListAdapter.ElectionListener { election ->
                viewModel.startNavigationToVoterInfo(election)
            })

        binding.upcomingElectionsContainer.adapter = upcomingElectionListAdapter

        viewModel.upcomingElections.observe(requireActivity(), { electionsList ->
            upcomingElectionListAdapter.submitList(electionsList)
        })

        return binding.root
    }

    //TODO: Refresh adapters when fragment loads

}