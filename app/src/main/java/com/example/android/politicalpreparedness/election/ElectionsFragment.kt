package com.example.android.politicalpreparedness.election

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.android.politicalpreparedness.PoliticalPreparedness
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.FragmentElectionBinding
import com.example.android.politicalpreparedness.election.adapter.ElectionListAdapter
import com.example.android.politicalpreparedness.network.ElectionsNetworkManager
import timber.log.Timber

class ElectionsFragment : Fragment() {

    private lateinit var binding: FragmentElectionBinding
    private lateinit var electionsViewModel: ElectionsViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentElectionBinding.inflate(inflater)

        val appContainer = (requireActivity().application as PoliticalPreparedness).appContainer
        val electionsViewModelFactory =
            ElectionsViewModelFactory(appContainer.repository)

        electionsViewModel =
            ViewModelProvider(this, electionsViewModelFactory).get(ElectionsViewModel::class.java)


        binding.apply {
            lifecycleOwner = this@ElectionsFragment
            viewModel = electionsViewModel
        }

        val upcomingElectionListAdapter =
            ElectionListAdapter(ElectionListAdapter.ElectionListener { election ->
                findNavController().navigate(
                    ElectionsFragmentDirections.actionElectionsFragmentToVoterInfoFragment(
                        election.id, election.division, false
                    )
                )
            })

        binding.upcomingElectionsContainer.adapter = upcomingElectionListAdapter

        electionsViewModel.upcomingElections.observe(viewLifecycleOwner) { electionsList ->
            upcomingElectionListAdapter.submitList(electionsList)
        }

        val savedElectionListAdapter =
            ElectionListAdapter(ElectionListAdapter.ElectionListener { election ->
                findNavController().navigate(
                    ElectionsFragmentDirections.actionElectionsFragmentToVoterInfoFragment(
                        election.id, election.division, true
                    )
                )
            })

        binding.savedElectionsContainer.adapter = savedElectionListAdapter

        electionsViewModel.savedElections.observe(viewLifecycleOwner) { electionList ->
            savedElectionListAdapter.submitList(electionList)
        }

        refreshElections()

        binding.refreshUpcomingElections.setOnRefreshListener {
            Timber.d("on refresh upcoming called")
            refreshElections()
        }
        return binding.root
    }

    private fun refreshElections() {
        val netManager = ElectionsNetworkManager.getInstance(requireActivity().applicationContext)
        netManager.connectedToNetwork.observe(viewLifecycleOwner) { isNetworkAvailable ->
            Timber.d("isNetworkAvailable: $isNetworkAvailable")
            if (isNetworkAvailable) {
                showUpcomingElections()
            } else {
                showNoConnection()
            }
        }
        electionsViewModel.getSavedElections()
    }

    private fun showNoConnection() {
        binding.upcomingElectionsContainer.visibility = View.GONE
        binding.errorIcon.visibility = View.VISIBLE
        binding.errorIcon.setImageResource(R.drawable.no_connection)
        binding.refreshUpcomingElections.isRefreshing = false
    }

    private fun showUpcomingElections() {
        binding.errorIcon.visibility = View.GONE
        binding.upcomingElectionsContainer.visibility = View.VISIBLE
        electionsViewModel.refreshElections()
        binding.refreshUpcomingElections.isRefreshing = false
    }

}