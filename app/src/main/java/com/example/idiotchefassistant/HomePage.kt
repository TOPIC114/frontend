package com.example.idiotchefassistant

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.idiotchefassistant.databinding.FragmentHomePageBinding
import com.example.idiotchefassistant.recipeBlock.RecipeItem
import com.example.idiotchefassistant.recipeBlock.RecipeItemAdapter
import com.example.idiotchefassistant.recipeBlock.RecipePage

class HomePage : Fragment(), RecipeItemAdapter.OnItemClickListener {
    private var _binding: FragmentHomePageBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomePageBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.CameraBtn.setOnClickListener {
            val intent = Intent(activity, CameraPage::class.java)
            startActivity(intent)
        }

        val items = generateFakeData()
        val recycleView = binding.RecipeRecycleView
        recycleView.layoutManager = LinearLayoutManager(requireContext())

        val adapter = RecipeItemAdapter(items)
        adapter.setOnItemClickListener(this)
        recycleView.adapter = adapter

        return view
    }
    private fun generateFakeData(): List<RecipeItem> {
        val fakeData = mutableListOf<RecipeItem>()
        for (i in 1..10) {
            fakeData.add(RecipeItem(182,"食譜 $i", "https://www.youtube.com/watch?v=OblT91aXQ5c", 5))
        }
        return fakeData
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemClick(item: RecipeItem){
        val intent = Intent(activity, RecipePage::class.java)
        startActivity(intent)
    }

}