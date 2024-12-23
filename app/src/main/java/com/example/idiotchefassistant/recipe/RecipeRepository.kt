package com.example.idiotchefassistant.recipe

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.idiotchefassistant.ingredientService
import com.example.idiotchefassistant.ingredients.IngredientItem
import com.example.idiotchefassistant.recipeAddTokenService
import com.example.idiotchefassistant.recipeService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.Executors

class RecipeRepository {
    private var liveData: MutableLiveData<RecipeData> = MutableLiveData(RecipeData())

    fun loadData(task: OnTaskFinish): MutableLiveData<RecipeData>{
        Executors.newSingleThreadExecutor().submit{
            val data = RecipeData()
            task.onFinish(data)
        }
        return liveData
    }

    fun getRecipeContent(rid: Int): LiveData<RecipeData>{
        val liveData = MutableLiveData<RecipeData>()
        recipeService.getRecipeContent(rid).enqueue(object: Callback<RecipeData> {
            override fun onResponse(
                call: Call<RecipeData>,
                response: Response<RecipeData>
            ){
                if(response.isSuccessful){
                    liveData.postValue(response.body())
                    Log.i("getRecipeContent", "Success:$rid ${response.body()?.iids} ${response.body()?.score}")
                }
                else{
                    liveData.postValue(RecipeData())
                    Log.e("getRecipeContent", "Failed:${response.code()} ${response.message()}")
                }
            }

            override fun onFailure(call: Call<RecipeData>, t: Throwable){
                Log.e("getRecipeContent", "API call failed: ${t.message}")
                liveData.postValue(RecipeData())
            }
        })
        return liveData
    }

    fun getRecipeContentIsLogin(rid: Int): LiveData<RecipeData>{
        val liveData = MutableLiveData<RecipeData>()
        recipeAddTokenService.getRecipeContent(rid).enqueue(object: Callback<RecipeData> {
            override fun onResponse(
                call: Call<RecipeData>,
                response: Response<RecipeData>
            ){
                if(response.isSuccessful){
                    val result = response.body()
                    liveData.postValue(response.body())
                    Log.i("getRecipeContent", "Success:$rid ${result?.iids} ${result?.score} ${result?.author} ${result?.comments}")
                }
                else{
                    liveData.postValue(RecipeData())
                    Log.e("getRecipeContent", "Failed:${response.code()} ${response.message()}")
                }
            }

            override fun onFailure(call: Call<RecipeData>, t: Throwable){
                Log.e("getRecipeContent", "API call failed: ${t.message}")
                liveData.postValue(RecipeData())
            }
        })
        return liveData
    }

    fun getIngredients(): LiveData<ArrayList<IngredientItem>> {
        val liveData = MutableLiveData<ArrayList<IngredientItem>>()
        // get ingredient list
        ingredientService.getList().enqueue(object : Callback<ArrayList<IngredientItem>> {
            override fun onResponse(
                call: Call<ArrayList<IngredientItem>>,
                response: Response<ArrayList<IngredientItem>>
            ) {
                if (response.isSuccessful) {
                    liveData.value = response.body()
                    Log.i("get ingredient", response.body()?.size.toString())
                }
                else{
                    liveData.value = arrayListOf()
                    Log.i("get ingredient", response.body()?.size.toString())
                }
            }

            override fun onFailure(call: Call<ArrayList<IngredientItem>>, t: Throwable) {
                liveData.value = arrayListOf()
                Log.i("get ingredient", t.toString())
            }
        })
        return liveData
    }


    interface OnTaskFinish{
        fun onFinish(data: RecipeData)
    }
}