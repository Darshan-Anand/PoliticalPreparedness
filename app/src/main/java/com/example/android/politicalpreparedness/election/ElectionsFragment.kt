package com.example.android.politicalpreparedness.election

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.android.politicalpreparedness.databinding.FragmentElectionBinding
import com.example.android.politicalpreparedness.election.adapter.ElectionListAdapter
import com.example.android.politicalpreparedness.network.models.Division
import com.example.android.politicalpreparedness.network.models.Election
import java.text.DateFormat
import java.util.*

class ElectionsFragment : Fragment() {

    //TODO: Declare ViewModel

    private lateinit var binding: FragmentElectionBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentElectionBinding.inflate(inflater)
        //TODO: Add ViewModel values and create ViewModel

        //TODO: Add binding values

        //TODO: Link elections to voter info

        //TODO: Initiate recycler adapters
        val upcommingElectionListAdapter =
            ElectionListAdapter(ElectionListAdapter.ElectionListener { electionId ->
                Toast.makeText(requireContext(), "$electionId id clicked", Toast.LENGTH_SHORT).show()
            })
        binding.upcomingElectionsContainer.adapter = upcommingElectionListAdapter

        //TODO: Populate recycler adapters

        val division = Division("ocd-division/country:us", "USA", "la")
        val date = Date(2025,7, 6)
        val electionsList = listOf<Election>(Election(2000,"VIP Test Election",date, division),
        Election(7162, "Louisiana Election", date, division)
        )
        upcommingElectionListAdapter.submitList(electionsList)

            return binding.root
    }

    //TODO: Refresh adapters when fragment loads

}