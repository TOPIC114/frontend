package com.example.idiotchefassistant.result

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.viewpager2.widget.ViewPager2
import com.example.idiotchefassistant.databinding.FragmentImageDetailBinding

class ImageDetailFragment : DialogFragment() {
    private var _binding: FragmentImageDetailBinding? = null
    private val binding get() = _binding!!
    private var imageUrls: ArrayList<String>? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        imageUrls = arguments?.getStringArrayList("IMAGE_URL")

        imageUrls?.let { urls ->
            binding.viewPager.adapter = ImagePagerAdapter(urls)
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                updatePositionText(position + 1, urls.size)
            }
        })
        updatePositionText(1, urls.size)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentImageDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun updatePositionText(currentPosition: Int, total: Int) {
        val positionText = "$currentPosition/$total"
        binding.imagePositionTextView.text = positionText
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(imageUrls: ArrayList<String>): ImageDetailFragment {
            val fragment = ImageDetailFragment()
            val args = Bundle()
            args.putStringArrayList("IMAGE_URL", imageUrls)
            fragment.arguments = args
            return fragment
        }
    }
}