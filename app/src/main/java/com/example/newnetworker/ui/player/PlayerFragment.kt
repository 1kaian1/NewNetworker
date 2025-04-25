package com.example.newnetworker.ui.player

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.newnetworker.LoginResult
import com.example.newnetworker.MainActivity
import com.example.newnetworker.R
import com.example.newnetworker.databinding.FragmentPlayerBinding
import com.example.newnetworker.ui.creator.CreatorFragment
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage

class PlayerFragment : Fragment() {

    private var _binding: FragmentPlayerBinding? = null
    private val binding get() = _binding!!
    private val playerViewModel: PlayerViewModel by activityViewModels()

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                scanQrCodeFromUri(uri)
            }
        }
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                pickImageLauncher.launch("image/*")
            } else {
                Toast.makeText(requireContext(), "Please grant permission in app settings", Toast.LENGTH_SHORT).show()
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri: Uri = Uri.fromParts("package", requireContext().packageName, null)
                intent.data = uri
                startActivity(intent)
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View {
            _binding = FragmentPlayerBinding.inflate(inflater, container, false)
            val root: View = binding.root

            binding.joinBtn.setOnClickListener {
                val invitation = binding.invitation.text.toString()
                if (invitation.isEmpty()) {
                    Toast.makeText(
                        requireContext(),
                        "Please enter an invitation code",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                } else {
                    playerViewModel.login_player(invitation)

                    val inputMethodManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.hideSoftInputFromWindow(binding.joinBtn.windowToken, 0)
                }
            }
            binding.creatorBtn.setOnClickListener {
                val fragmentManager = parentFragmentManager
                val fragmentTransaction = fragmentManager.beginTransaction()
                fragmentTransaction.replace(R.id.nav_host_fragment_content_login, CreatorFragment())
                fragmentTransaction.commit()
            }
            binding.scanQrGallery.setOnClickListener {
                when {
                    ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED -> {
                        pickImageLauncher.launch("image/*")
                    }
                    shouldShowRequestPermissionRationale(Manifest.permission.READ_MEDIA_IMAGES) -> {
                        // In an educational UI, explain to the user why your app requires this permission for a specific feature to behave as expected.
                        requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES as String)
                    } else -> {
                        // You can directly ask for the permission.
                        requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES as String)
                    }
                }
            }
            playerViewModel.loginResult.observe(viewLifecycleOwner) { result ->
                when (result) {
                    is LoginResult.Loading -> {
                        // Show loading indicator
                        Log.d("PlayerFragment", "Loading...")
                        binding.loadingIndicator.visibility = View.VISIBLE
                    }
                    is LoginResult.Success -> {
                        // Navigate to MainActivity
                        Log.d("PlayerFragment", "Success!")
                        val intent = Intent(requireContext(), MainActivity::class.java)
                        startActivity(intent)
                        playerViewModel.resetLoginResult()
                    }
                    is LoginResult.Error -> {
                        // Show error message
                        Log.d("PlayerFragment", "Error: ${result.message}")
                        binding.loadingIndicator.visibility = View.INVISIBLE
                        Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()
                    }
                    is LoginResult.Idle -> {
                        // Do nothing
                        Log.d("PlayerFragment", "Idle")
                    }
                }
            }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun scanQrCodeFromUri(imageUri: Uri) {
        val image = InputImage.fromFilePath(requireContext(), imageUri)
        val scanner = BarcodeScanning.getClient()
        scanner.process(image)
            .addOnSuccessListener { barcodes ->
                for (barcode in barcodes) {
                    val rawValue = barcode.rawValue
                    binding.invitation.setText(rawValue)
                    playerViewModel.login_player(rawValue.toString())
                }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to scan QR code", Toast.LENGTH_SHORT).show()
            }
    }
}
