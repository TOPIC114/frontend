package com.example.idiotchefassistant.result
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.idiotchefassistant.recipe.RecipeItem
import com.example.idiotchefassistant.resultSearchService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.Executors

class ResultRepository {
    private var nowData: MutableLiveData<ResultData> = MutableLiveData(ResultData())
    private var dataIsEmpty: MutableLiveData<Boolean> = MutableLiveData(false)
    fun loadData(task: OnTaskFinish): MutableLiveData<ResultData> {
        Executors.newSingleThreadExecutor().submit {
            val results = ResultData()
            results.result = nowData.value?.result
            task.onFinish(results)
        }
        return nowData
    }

    fun uploadData(newData: ResultData) {
        nowData.value = newData
    }

    fun getData(): ResultData? {
        return nowData.value
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
                }
                else{
                    Log.i("searchByIds","Failed:${response.code()} ${response.message()}")
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

    interface OnTaskFinish{
        fun onFinish(data: ResultData)
    }
}
