package com.example.newnetworker.ui.profile

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.newnetworker.databinding.FragmentProfileBinding
import com.google.firebase.Firebase
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storage

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            binding.image.setImageURI(uri)
            Glide.with(this)
                .load(uri)
                .circleCrop()
                .into(binding.image)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.image.setOnClickListener {
            uploadProfileImage()
        }


        return root
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun uploadProfileImage() {
        pickImageLauncher.launch("image/*")

        //val storage = FirebaseStorage.getInstance("gs://newnetworker-56eb7.appspot.com")
        //val storageRef = storage.getReference("profile_images/")
        //val uploadTask = storageRef.putBytes(byteArray)
        //uploadTask.addOnSuccessListener {

        //}.addOnFailureListener {

        //}

    }
}