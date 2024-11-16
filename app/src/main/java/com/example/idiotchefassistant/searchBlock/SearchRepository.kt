package com.example.idiotchefassistant.searchBlock

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.idiotchefassistant.recipeBlock.RecipeItem
import com.example.idiotchefassistant.resultSearchService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.Executors

class SearchRepository {
    private var liveData: MutableLiveData<SearchData> = MutableLiveData(SearchData())

    fun loadData(task: OnTaskFinish): MutableLiveData<SearchData>{
        Executors.newSingleThreadExecutor().submit{
            val data = SearchData()
            data.list = liveData.value?.list
            task.onFinish(data)
        }
        return liveData
    }

    fun searchByIids(offset: Int, iids: List<Int>): LiveData<List<RecipeItem>> {
        val liveData = MutableLiveData<List<RecipeItem>>()
        resultSearchService.searchByIid(offset, iids).enqueue(object: Callback<List<RecipeItem>> {
            override fun onResponse(
                call: Call<List<RecipeItem>>,
                response: Response<List<RecipeItem>>
            ){
                if(response.isSuccessful){
                    liveData.postValue(response.body())
                    Log.i("searchByIds","SearchRepository Successful:${response.body().toString()}")
                }
                else{
                    Log.i("searchByIds","SearchRepository Failed:${response.code()} ${response.message()}")
                    liveData.postValue(emptyList())
                }
            }

            override fun onFailure(call: Call<List<RecipeItem>>, t: Throwable) {
                Log.e("searchByIds", "API call failed: ${t.message}")
                liveData.postValue(emptyList())
            }
        })
        return liveData
    }

    fun searchByKeyword(offset: Int, keyword: String): LiveData<List<RecipeItem>>{
        val liveData = MutableLiveData<List<RecipeItem>>()
        resultSearchService.searchByKeyword(offset, keyword).enqueue(object:
            Callback<List<RecipeItem>> {
            override fun onResponse(
                call: Call<List<RecipeItem>>,
                response: Response<List<RecipeItem>>
            ){
                if(response.isSuccessful){
                    liveData.postValue(response.body())
                    Log.i("searchByKeyword","SearchRepository Successful:${response.body().toString()}")
                }
                else{
                    Log.i("searchByKeyword","SearchRepository Failed:${response.code()} ${response.message()}")
                    liveData.postValue(emptyList())
                }
            }

            override fun onFailure(call: Call<List<RecipeItem>>, t: Throwable) {
                Log.e("searchByKeyword", "API call failed: ${t.message}")
                liveData.postValue(emptyList())
            }
        })
        return liveData
    }

    interface OnTaskFinish{
        fun onFinish(data: SearchData)
    }
}