package com.example.idiotchefassistant.ingredients

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
class IngredientViewModel(private var ingredientRepository: IngredientRepository): ViewModel() {
    private var userLiveData = MutableLiveData<IngredientData>()

    private val ingredientObserver = Observer<ArrayList<IngredientItem>> { list ->
        if (list.isNotEmpty()) {
            val names: Array<String> = list.map {
                it.name.replace("_", " ")
            }.toTypedArray()
            val mandarins: Array<String> = list.map { it.mandarin }.toTypedArray()
            val zipArray = names.zip(mandarins) { name, mandarin -> "$mandarin $name" }.toTypedArray()
            ingredientRepository.setData(zipArray)
            val ingredientData = IngredientData()
            ingredientData.ingredientNames = zipArray
            userLiveData.postValue(ingredientData)
        } else {
            Log.i("IngredientViewModel", "Received empty data")
        }
    }

    fun callBack(): LiveData<IngredientData> {
        ingredientRepository.loadData(object : OnTaskFinish {
            override fun onFinish(data: IngredientData) {
                if(data.ingredientNames?.isNotEmpty() == true){
                    userLiveData.postValue(data)
                }
                else{
                    Log.i("Ingredient ViewModel", "No data received or empty")
                }
            }
        })
        return userLiveData
    }

    fun switchData(isSeason: Boolean){
        ingredientRepository.switchData(isSeason)
    }

    fun filterItems(query: String) {
        val allItems = ingredientRepository.getData()
        val data = IngredientData()
        data.ingredientNames = allItems.filter { it.contains(query, ignoreCase = true) }.toTypedArray()
        userLiveData.postValue(data)
    }

    fun getData() {
        ingredientRepository.getIngredients().observeForever(ingredientObserver)
    }
}