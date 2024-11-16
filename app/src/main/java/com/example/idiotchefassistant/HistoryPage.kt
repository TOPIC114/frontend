package com.example.idiotchefassistant

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.idiotchefassistant.databinding.ActivitySearchPageBinding
import com.example.idiotchefassistant.recipeBlock.RecipeItem
import com.example.idiotchefassistant.recipeBlock.RecipeItemAdapter
import com.example.idiotchefassistant.recipeBlock.RecipePage

class HistoryPage : AppCompatActivity(), RecipeItemAdapter.OnItemClickListener {
    private lateinit var binding: ActivitySearchPageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
//        setContentView(R.layout.activity_search_page)
        binding = ActivitySearchPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.SearchBtn.setOnClickListener {
            val intent = Intent(this, HistoryPage::class.java)
            startActivity(intent)
        }
        binding.EditText
        val item = mutableListOf<RecipeItem>()
        val recycleView = binding.RecipeRecycleView
        recycleView.layoutManager = LinearLayoutManager(this)

        val adapter = RecipeItemAdapter(item)
        adapter.setOnItemClickListener(this)
        recycleView.adapter = adapter
    }

    override fun onItemClick(item: RecipeItem) {
        val intent = Intent(this, RecipePage::class.java)
        startActivity(intent)
    }
}