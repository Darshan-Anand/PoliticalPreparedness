package com.example.android.politicalpreparedness.election

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.example.android.politicalpreparedness.databinding.FragmentVoterInfoBinding

class VoterInfoFragment : Fragment() {

    private lateinit var binding: FragmentVoterInfoBinding
    private lateinit var viewModel: VoterInfoViewModel
    private val args: VoterInfoFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentVoterInfoBinding.inflate(inflater)

        val voterInfoViewModelFactory = VoterInfoViewModelFactory(args.election)
        viewModel = ViewModelProvider(
            requireActivity(),
            voterInfoViewModelFactory
        ).get(VoterInfoViewModel::class.java)


        //TODO: Add binding values

        binding.viewModel = viewModel

        viewModel.electionSelected.observe(requireActivity(), {
            binding.election = it
        })

        viewModel.stateAdministrationBody.observe(requireActivity(), {
            binding.stateAdministrationBody = it
        })

        //TODO: Populate voter info -- hide views without provided data.
        /**
        Hint: You will need to ensure proper data is provided from previous fragment.
         */


        //TODO: Handle loading of URLs
        binding.stateBallot.setOnClickListener {
            val adminBody = viewModel.stateAdministrationBody.value
            loadElectionInfoUrl(adminBody?.ballotInfoUrl)
        }
        binding.stateLocations.setOnClickListener {
            val adminBody = viewModel.stateAdministrationBody.value
            loadElectionInfoUrl(adminBody?.electionInfoUrl)
        }

        //TODO: Handle save button UI state
        //TODO: cont'd Handle save button clicks


        return binding.root
    }

    //TODO: Create method to load URL intents
    fun loadElectionInfoUrl(url: String?) {
        Intent(Intent.ACTION_VIEW).run {
            data = Uri.parse(url)
            startActivity(this)
        }
    }

}