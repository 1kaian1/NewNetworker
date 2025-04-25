package com.example.newnetworker.ui.creator

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.example.newnetworker.LoginResult
import com.example.newnetworker.MainActivity
import com.example.newnetworker.OnFragmentInteractionListener
import com.example.newnetworker.R
import com.example.newnetworker.databinding.FragmentCreatorBinding
import com.example.newnetworker.ui.player.PlayerFragment
import com.example.newnetworker.ui.player.PlayerViewModel
import com.google.android.material.snackbar.Snackbar

class CreatorFragment : Fragment() {

    private var _binding: FragmentCreatorBinding? = null
    private val binding get() = _binding!!
    private val creatorViewModel: CreatorViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentCreatorBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.loginBtn.setOnClickListener {
            val email = binding.email.text.toString()
            val password = binding.password.text.toString()
            creatorViewModel.login_creator(email, password)
        }
        binding.playerBtn.setOnClickListener {
            val fragmentManager = parentFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.nav_host_fragment_content_login, PlayerFragment())
            fragmentTransaction.commit()
        }

        creatorViewModel.loginResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is LoginResult.Loading -> {
                    // Show loading indicator
                    Log.d("CreatorFragment", "Loading...")
                    binding.loadingIndicator.visibility = View.VISIBLE
                }
                is LoginResult.Success -> {
                    // Navigate to MainActivity
                    Log.d("CreatorFragment", "Success!")
                    val intent = Intent(requireContext(), MainActivity::class.java)
                    startActivity(intent)
                    creatorViewModel.resetLoginResult()
                }
                is LoginResult.Error -> {
                    // Show error message
                    Log.d("CreatorFragment", "Error: ${result.message}")
                    binding.loadingIndicator.visibility = View.INVISIBLE
                    Snackbar.make(requireView(),result.message, Snackbar.LENGTH_SHORT).show()
                }
                is LoginResult.Idle -> {
                    // Do nothing
                    Log.d("CreatorFragment", "Idle")
                }
            }
        }

        return root
    }

    override fun onResume() {
        super.onResume()
        (activity as? OnFragmentInteractionListener)?.onFragmentResumed("Creator")
        binding.loadingIndicator.visibility = View.INVISIBLE
        binding.email.text?.clear()
        binding.password.text?.clear()
        binding.root.clearFocus()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}