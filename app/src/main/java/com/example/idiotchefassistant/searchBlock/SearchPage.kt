package com.example.idiotchefassistant.searchBlock

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.idiotchefassistant.MainActivity
import com.example.idiotchefassistant.databinding.ActivitySearchPageBinding
import com.example.idiotchefassistant.recipeBlock.RecipeItem
import com.example.idiotchefassistant.recipeBlock.RecipeItemAdapter
import com.example.idiotchefassistant.recipeBlock.RecipePage

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

        val adapter = RecipeItemAdapter(emptyList())
        adapter.setOnItemClickListener(this)
        binding.RecipeRecycleView.layoutManager = LinearLayoutManager(this)
        binding.RecipeRecycleView.adapter = adapter

//      Observe live data
        searchViewModel.callBack().observe(this){data ->
            adapter.updateItems(data.list ?: emptyList())
        }

//      upload recycleView
        val rItems = intent.getParcelableArrayListExtra<RecipeItem>("rItems")
        val item = mutableListOf<RecipeItem>()
        rItems?.forEach { r ->
            item.add(RecipeItem(r.rid, r.title, r.link, r.score))
        }
        val iids = intent.getIntegerArrayListExtra("iids")?: emptyList<Int>()
        searchViewModel.setIids(iids.toList())

        val searchData = SearchData()
        searchData.list = ArrayList(item)
        searchViewModel.uploadData(searchData)

        binding.HomeBtn.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        binding.EditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val newKeyword = s.toString()
                searchViewModel.setKeyword(newKeyword)
            }

            override fun afterTextChanged(s: Editable?) {}
        })
        binding.SearchBtn.setOnClickListener{
            searchViewModel.keywordSearch()
        }
        binding.backPage.setOnClickListener{
            searchViewModel.backPage().observe(this){ isLastPage ->
                if(!isLastPage){
                    Toast.makeText(this, "已經是第一頁了", Toast.LENGTH_LONG).show()
                }
            }
        }
        binding.nextPage.setOnClickListener{
            searchViewModel.nextPage().observe(this) { isLastPage ->
                if (isLastPage) {
                    searchViewModel.backPage()
                    Toast.makeText(this, "已經是最後一頁了", Toast.LENGTH_LONG).show()
                }
            }
        }
        searchViewModel.nowOffset.observe(this) { offset ->
            val s = offset + 1
            binding.offset.text = s.toString()
        }
    }

    override fun onItemClick(item: RecipeItem) {
        val intent = Intent(this, RecipePage::class.java)
            .putExtra("rid", item.rid)
        Log.i("rid","rid is: ${item.rid}")
        startActivity(intent)
    }
}



