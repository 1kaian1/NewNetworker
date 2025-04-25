package com.example.newnetworker.ui.settings

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.newnetworker.LoginActivity
import com.example.newnetworker.databinding.FragmentSettingsBinding
import com.google.firebase.auth.FirebaseAuth

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.signOut.setOnClickListener {
            val auth = FirebaseAuth.getInstance()
            auth.signOut()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}