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

        val voterInfoViewModelFactory =
            VoterInfoViewModelFactory(requireActivity().applicationContext)
        viewModel = ViewModelProvider(
            requireActivity(),
            voterInfoViewModelFactory
        ).get(VoterInfoViewModel::class.java)

        binding.viewModel = viewModel

        val address = viewModel.getAddress(args.argDivision)

        viewModel.loadElectionInfo(address, args.argElectionId, args.argLoadFromDb)

        viewModel.election.observe(requireActivity(), {
            binding.election = it
        })

        viewModel.stateAdministrationBody.observe(requireActivity(), {
            binding.stateAdministrationBody = it
        })


        binding.stateBallot.setOnClickListener {
            val adminBody = viewModel.stateAdministrationBody.value
            loadElectionInfoUrl(adminBody?.ballotInfoUrl)
        }

        binding.stateLocations.setOnClickListener {
            val adminBody = viewModel.stateAdministrationBody.value
            loadElectionInfoUrl(adminBody?.electionInfoUrl)
        }


        viewModel.followElectionButtonText.observe(requireActivity(), {
            binding.followElectionButton.text = it
        })

        binding.followElectionButton.setOnClickListener {
            viewModel.followOrUnfollowElection(args.argElectionId)
        }

        return binding.root
    }

    private fun loadElectionInfoUrl(url: String?) {
        Intent(Intent.ACTION_VIEW).run {
            data = Uri.parse(url)
            startActivity(this)
        }
    }

}