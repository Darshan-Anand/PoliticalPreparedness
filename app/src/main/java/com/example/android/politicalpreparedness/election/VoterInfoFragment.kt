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
import com.example.android.politicalpreparedness.PoliticalPreparedness
import com.example.android.politicalpreparedness.databinding.FragmentVoterInfoBinding

class VoterInfoFragment : Fragment() {

    private lateinit var binding: FragmentVoterInfoBinding
    private lateinit var voterInfoViewModel: VoterInfoViewModel
    private val args: VoterInfoFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentVoterInfoBinding.inflate(inflater)

        val appContainer = (requireActivity().application as PoliticalPreparedness).appContainer
        val voterInfoViewModelFactory =
            VoterInfoViewModelFactory(appContainer.repository)

        voterInfoViewModel = ViewModelProvider(
            this,
            voterInfoViewModelFactory
        ).get(VoterInfoViewModel::class.java)

        binding.apply {
            lifecycleOwner = this@VoterInfoFragment
            viewModel = voterInfoViewModel
        }

        val address = voterInfoViewModel.getAddress(args.argDivision)

        voterInfoViewModel.loadElectionInfo(address, args.argElectionId, args.argLoadFromDb)

        voterInfoViewModel.election.observe(requireActivity()) {
            binding.election = it
        }

        voterInfoViewModel.stateAdministrationBody.observe(viewLifecycleOwner) {
            binding.stateAdministrationBody = it
        }


        binding.stateBallot.setOnClickListener {
            val adminBody = voterInfoViewModel.stateAdministrationBody.value
            loadElectionInfoUrl(adminBody?.ballotInfoUrl)
        }

        binding.stateLocations.setOnClickListener {
            val adminBody = voterInfoViewModel.stateAdministrationBody.value
            loadElectionInfoUrl(adminBody?.electionInfoUrl)
        }


        voterInfoViewModel.followElectionButtonText.observe(viewLifecycleOwner) {
            binding.followElectionButton.text = it
        }

        binding.followElectionButton.setOnClickListener {
            voterInfoViewModel.followOrUnfollowElection(args.argElectionId)
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