package com.example.idiotchefassistant.recipeBlock

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class RecipeViewModel(private var recipeRepository: RecipeRepository):ViewModel() {
    private val userLiveData = MutableLiveData<RecipeData>()

    fun callBack(): LiveData<RecipeData> {
        recipeRepository.loadData(object : RecipeRepository.OnTaskFinish {
            override fun onFinish(data: RecipeData) {
                userLiveData.postValue(data)
            }
        })
        return userLiveData
    }

    fun getData(rid: Int) {
        recipeRepository.getRecipeContent(rid).observeForever { r ->
            userLiveData.postValue(r)
        }
    }

    fun getIngredients(ids: ArrayList<Int>): LiveData<List<String>>{
        val liveData = MutableLiveData<List<String>>()
        recipeRepository.getIngredients().observeForever{ list ->
            val iids: Array<Int> = list.map { it.id }.toTypedArray()
            val names: Array<String> = list.map { it.name }.toTypedArray()
            val mandarins: Array<String> = list.map { it.mandarin }.toTypedArray()
            val newNames = Array(names.size) { i ->
                "${mandarins[i]} ${names[i].replace("_"," ")}"
            }
            val ingredients = ids.mapNotNull { id ->
                newNames.getOrNull(iids.indexOf(id))
            }
            liveData.postValue(ingredients)
        }
        return liveData
    }

    fun link2Image(link: String?): String {
        val videoId = link.let {
            val regex = "https?://(?:www\\.)?youtube\\.com/watch\\?v=([\\w-]+)".toRegex()
            val matchResult = it?.let { it1 -> regex.find(it1) }
            matchResult?.groups?.get(1)?.value
        }
        var imageLink = ""
        if (videoId != null) {
            imageLink = "https://img.youtube.com/vi/${videoId}/hqdefault.jpg"
        }
        return imageLink
    }

}