package com.example.idiotchefassistant.recipeBlock

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.idiotchefassistant.MainActivity
import com.example.idiotchefassistant.databinding.ActivitySearchPageBinding

class SearchPage : AppCompatActivity(), RecipeItemAdapter.OnItemClickListener {
    private lateinit var binding: ActivitySearchPageBinding
    private lateinit var searchViewModel: SearchViewModel
    private lateinit var searchFactory: SearchFactory
    private lateinit var searchRepository: SearchRepository
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
//        setContentView(R.layout.activity_search_page)
        binding = ActivitySearchPageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        searchRepository = SearchRepository()
        searchFactory = SearchFactory(searchRepository)
        searchViewModel = ViewModelProvider(this, searchFactory)[SearchViewModel::class.java]

        searchViewModel.callBack().observe(this){
        }
        val rItems = intent.getParcelableArrayListExtra<RecipeItem>("rItems")
        binding.HomeBtn.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        binding.EditText
        binding.SearchBtn
        binding.backPage.setOnClickListener{

        }
        binding.nextPage
        // upload recycleView
        val item = mutableListOf<RecipeItem>()
        rItems?.forEach { r ->
            item.add(RecipeItem(r.rid, r.title, r.author, r.description, r.rType))
        }
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



