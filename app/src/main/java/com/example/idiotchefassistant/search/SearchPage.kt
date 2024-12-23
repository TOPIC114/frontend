package com.example.idiotchefassistant.search

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.idiotchefassistant.mainLayout.MainActivity
import com.example.idiotchefassistant.databinding.ActivitySearchPageBinding
import com.example.idiotchefassistant.recipe.RecipeItem
import com.example.idiotchefassistant.recipe.RecipeItemAdapter
import com.example.idiotchefassistant.recipe.RecipePage

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

        val recipeList = binding.RecipeRecycleView
        val home = binding.HomeBtn
        val keyword = binding.EditText
        val search = binding.SearchBtn
        val back = binding.backPage
        val next = binding.nextPage
        val offset = binding.offset

        val adapter = RecipeItemAdapter(emptyList())
        adapter.setOnItemClickListener(this)
        recipeList.layoutManager = LinearLayoutManager(this)
        recipeList.adapter = adapter

//      Observe live data
        searchViewModel.callBack().observe(this){data ->
            adapter.updateItems(data.list ?: emptyList())
        }

//      upload recycleView
        val iids = intent.getIntegerArrayListExtra("iids")?: emptyList<Int>()
        searchViewModel.setIids(iids.toList())
        searchViewModel.iidsSearch()

        home.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }

        keyword.apply {
            afterTextChanged {
                searchViewModel.setKeyword(keyword.text.toString())
            }
            setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE ->
                        searchViewModel.keywordSearch()
                }
                false
            }
            search.setOnClickListener{
                searchViewModel.keywordSearch()
            }
        }

        back.setOnClickListener{
            searchViewModel.backPage().observe(this){ isLastPage ->
                if(!isLastPage){
                    Toast.makeText(this, "已經是第一頁了", Toast.LENGTH_LONG).show()
                }
            }
        }
        next.setOnClickListener{
            searchViewModel.nextPage().observe(this) { isLastPage ->
                if (isLastPage) {
                    searchViewModel.backPage()
                    Toast.makeText(this, "已經是最後一頁了", Toast.LENGTH_LONG).show()
                }
            }
        }
        searchViewModel.nowOffset.observe(this) { n ->
            val s = n + 1
            offset.text = s.toString()
        }
    }

    override fun onItemClick(item: RecipeItem) {
        val intent = Intent(this, RecipePage::class.java)
            .putExtra("rid", item.rid)
        Log.i("rid","rid is: ${item.rid}")
        startActivity(intent)
    }
}

fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}



