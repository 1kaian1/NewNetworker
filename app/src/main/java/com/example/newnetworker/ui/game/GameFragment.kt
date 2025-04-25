package com.example.newnetworker.ui.game

import android.app.ActivityOptions
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.newnetworker.MainActivity
import com.example.newnetworker.SecondActivity
import com.example.newnetworker.databinding.FragmentGameBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter

class GameFragment : Fragment() {

    private var _binding: FragmentGameBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentGameBinding.inflate(inflater, container, false)
        val root: View = binding.root

        generateQrCode()

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mainActivity = activity as MainActivity
        mainActivity.playerCountLiveData.observe(viewLifecycleOwner) { count ->
            binding.gameInfo.text = "Welcome to the game! Players: $count/10.\nYou invited: ${0}. Please, invite more people."
        }
    }

    private fun generateQrCode() {

        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser

        if (user != null) {
            val uid = user.uid

            try {
                val qrCodeWriter = QRCodeWriter()
                val bitMatrix = qrCodeWriter.encode(uid, BarcodeFormat.QR_CODE, 512, 512)
                val qrCodeBitmap = Bitmap.createBitmap(512, 512, Bitmap.Config.RGB_565)

                for (x in 0 until 512) {
                    for (y in 0 until 512) {
                        qrCodeBitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
                    }
                }
                binding.imageViewQrCode.setImageBitmap(qrCodeBitmap)
                binding.textViewCode.text = uid
                binding.copyIcon.setOnClickListener {
                    val intent = Intent(requireContext(), SecondActivity::class.java)
                    startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(requireActivity()).toBundle())

                    //val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    //val clip = ClipData.newPlainText("label", uid)
                    //clipboard.setPrimaryClip(clip)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            Toast.makeText(requireContext(), "User not authenticated", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}