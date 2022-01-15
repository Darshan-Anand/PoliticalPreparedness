package com.example.android.politicalpreparedness.election

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.android.politicalpreparedness.databinding.FragmentElectionBinding
import com.example.android.politicalpreparedness.election.adapter.ElectionListAdapter
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
        val electionsViewModelFactory =
            ElectionsViewModelFactory(requireActivity().applicationContext)

        /**
         * Getting instance of view model
         *  @param electionsViewModelFactory
         */
        viewModel =
            ViewModelProvider(this, electionsViewModelFactory).get(ElectionsViewModel::class.java)

        binding.viewModel = viewModel

        binding.lifecycleOwner = this

        val upcomingElectionListAdapter =
            ElectionListAdapter(ElectionListAdapter.ElectionListener { election ->
                //viewModel.startNavigationToVoterInfo(election)
                findNavController().navigate(
                    ElectionsFragmentDirections.actionElectionsFragmentToVoterInfoFragment(
                        election.id, election.division, false
                    )
                )
            })

        binding.upcomingElectionsContainer.adapter = upcomingElectionListAdapter

        viewModel.upcomingElections.observe(requireActivity(), { electionsList ->
            upcomingElectionListAdapter.submitList(electionsList)
        })

        val savedElectionListAdapter =
            ElectionListAdapter(ElectionListAdapter.ElectionListener { election ->
                findNavController().navigate(
                    ElectionsFragmentDirections.actionElectionsFragmentToVoterInfoFragment(
                        election.id, election.division, true
                    )
                )
            })

        binding.savedElectionsContainer.adapter = savedElectionListAdapter

        viewModel.savedElections.observe(requireActivity(), { electionList ->
            savedElectionListAdapter.submitList(electionList)
        })

        viewModel.loadElections()
        return binding.root
    }

}