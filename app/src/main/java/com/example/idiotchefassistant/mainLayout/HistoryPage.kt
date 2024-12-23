package com.example.idiotchefassistant.mainLayout

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.idiotchefassistant.MyApp
import com.example.idiotchefassistant.databinding.FragmentHistoryPageBinding
import com.example.idiotchefassistant.login.LoginActivity
import com.example.idiotchefassistant.recipe.RecipeItem
import com.example.idiotchefassistant.recipe.RecipeItemAdapter
import com.example.idiotchefassistant.recipe.RecipePage
import com.example.idiotchefassistant.userDataService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HistoryPage : Fragment(), RecipeItemAdapter.OnItemClickListener {
    private var _binding: FragmentHistoryPageBinding? = null
    private val binding get() = _binding!!
    private val _historyItems = MutableLiveData<List<RecipeItem>>()
    private var historyItems: LiveData<List<RecipeItem>> = _historyItems

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryPageBinding.inflate(inflater, container, false)
        val view = binding.root

        val items = arrayListOf<RecipeItem>()
        val recycleView = binding.RecipeRecycleView
        recycleView.layoutManager = LinearLayoutManager(requireContext())

        val adapter = RecipeItemAdapter(items)
        adapter.setOnItemClickListener(this)
        recycleView.adapter = adapter

        historyItems.observe(viewLifecycleOwner){ data ->
            items.clear()
            items.addAll(data)
            adapter.notifyDataSetChanged()
        }
        val remind = binding.remindBlock
        binding.login.setOnClickListener{
            val intent = Intent(activity, LoginActivity::class.java)
            startActivity(intent)
        }
        MyApp.isLogin.observe(viewLifecycleOwner) { isLoggedIn ->
            recycleView.isVisible = isLoggedIn
            if (isLoggedIn)
                history()
            remind.isVisible = !isLoggedIn
        }
        return view
    }

    private fun history(){
        userDataService.history().enqueue(object :
            Callback<List<RecipeItem>> {
            override fun onResponse(
                call: Call<List<RecipeItem>>,
                response: Response<List<RecipeItem>>
            ) {
                if (response.isSuccessful) {
                    _historyItems.value = response.body()
                    Log.i("userHistory","Success:${response.body().toString()}")
                } else {
                    Log.i("userHistory","Failed:${response.code()} ${response.message()}")
                    _historyItems.value = emptyList()
                }
            }

            override fun onFailure(call: Call<List<RecipeItem>>, t: Throwable) {
                Log.e("userHistory", "API call failed: ${t.message}")
                _historyItems.value = emptyList()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemClick(item: RecipeItem) {
        val intent = Intent(activity, RecipePage::class.java)
            .putExtra("rid", item.rid)
        Log.i("rid","rid is: ${item.rid}")
        startActivity(intent)
    }
}