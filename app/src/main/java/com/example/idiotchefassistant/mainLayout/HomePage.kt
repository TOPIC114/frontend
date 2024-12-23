package com.example.idiotchefassistant.mainLayout

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.idiotchefassistant.databinding.FragmentHomePageBinding
import com.example.idiotchefassistant.recipe.RecipeItem
import com.example.idiotchefassistant.recipe.RecipeItemAdapter
import com.example.idiotchefassistant.recipe.RecipePage
import com.example.idiotchefassistant.userDataService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomePage : Fragment(), RecipeItemAdapter.OnItemClickListener {
    private var _binding: FragmentHomePageBinding? = null
    private val binding get() = _binding!!
    private val _recommendItems = MutableLiveData<List<RecipeItem>>()
    private var recommendItems: LiveData<List<RecipeItem>> = _recommendItems

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomePageBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.CameraBtn.setOnClickListener {
            val intent = Intent(activity, CameraPage::class.java)
            startActivity(intent)
        }

        val items = arrayListOf<RecipeItem>()
        val recycleView = binding.RecipeRecycleView
        recycleView.layoutManager = LinearLayoutManager(requireContext())

        val adapter = RecipeItemAdapter(items)
        adapter.setOnItemClickListener(this)
        recycleView.adapter = adapter

        recommendItems.observe(viewLifecycleOwner){ data ->
            items.clear()
            items.addAll(data)
            adapter.notifyDataSetChanged()
        }

        recommend()

        return view
    }

    private fun recommend(){
        userDataService.recommend().enqueue(
            object : Callback<List<RecipeItem>> {
                override fun onResponse(
                    call: Call<List<RecipeItem>>,
                    response: Response<List<RecipeItem>>
                ) {
                    if (response.isSuccessful) {
                        _recommendItems.postValue(response.body())
                        Log.i("UserRecommend","Success:${response.body().toString()}")

                    } else{
                        _recommendItems.postValue(emptyList())
                        Log.i("UserRecommend", "API Failed:${response.code()} ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<List<RecipeItem>>, t: Throwable) {
                    _recommendItems.postValue(emptyList())
                    Log.e("UserRecommend", "API call failed: ${t.message}")
                }
            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemClick(item: RecipeItem){
        val intent = Intent(activity, RecipePage::class.java)
            .putExtra("rid", item.rid)
        Log.i("rid","rid is: ${item.rid}")
        startActivity(intent)
    }

}