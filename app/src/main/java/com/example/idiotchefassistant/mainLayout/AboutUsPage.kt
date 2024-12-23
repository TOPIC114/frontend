package com.example.idiotchefassistant.mainLayout

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.idiotchefassistant.databinding.FragmentAboutUsPageBinding
import com.example.idiotchefassistant.databinding.FragmentInformationPageBinding
import java.util.Locale

class AboutUsPage : Fragment() {
    private var _binding: FragmentAboutUsPageBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAboutUsPageBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.yt1.setOnClickListener{
            openYoutube(context = requireContext(), "https://www.youtube.com/@christable0202")
        }
        binding.yt2.setOnClickListener{
            openYoutube(context = requireContext(), "https://www.youtube.com/@RosalinaKitchen")
        }
        binding.yt3.setOnClickListener{
            openYoutube(context = requireContext(), "https://www.youtube.com/@menustudy")
        }

        return view
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun openYoutube(context: Context, url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        // 检查是否有应用可以处理这个意图
        val resolveInfo = context.packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)
        if (resolveInfo?.activityInfo?.packageName?.lowercase(Locale.ROOT)?.contains("youtube") == true) {
            context.startActivity(intent)
        } else {
            // 没有 YouTube 应用，使用浏览器打开
            val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            webIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(webIntent)
        }
    }
}