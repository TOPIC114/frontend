package com.example.idiotchefassistant.ingredientsBlock

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.idiotchefassistant.ingredientService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.Executors

class IngredientRepository {
    private var ingredientList = emptyArray<String>()
    private var seasoningList = arrayOf("醬油 soy oil", "番茄醬 ketchup")
    private var nowData = ingredientList
    private val listeners = mutableListOf<OnTaskFinish>()

    fun loadData(task: OnTaskFinish) {
        listeners.add(task)
        Executors.newSingleThreadExecutor().submit {
            val ingredients = IngredientData()
            ingredients.ingredientNames = nowData
            task.onFinish(ingredients)
        }
    }

    fun getData(): Array<String>{
        return nowData
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

    fun setData(newData: Array<String>, isSeason: Boolean = false) {
        if (isSeason)
            seasoningList = newData
        else
            ingredientList = newData
        nowData = if (isSeason) seasoningList else ingredientList
        notifyListeners()
    }

    fun switchData(isSeason: Boolean) {
        nowData = if (isSeason) seasoningList else ingredientList
        notifyListeners()
    }

    private fun notifyListeners() {
        val ingredients = IngredientData()
        ingredients.ingredientNames = nowData
        listeners.forEach { it.onFinish(ingredients) }
    }
}

interface OnTaskFinish {
    fun onFinish(data: IngredientData)
}