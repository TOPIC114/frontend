package com.example.idiotchefassistant.resultBlock

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.idiotchefassistant.itemBlock.IngredientItem
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import com.example.idiotchefassistant.resultBlock.ResultRepository.OnTaskFinish

class ResultViewModel(private var resultRepository: ResultRepository): ViewModel() {
    private var userLiveData = MutableLiveData<ResultData>()

    fun callBack():LiveData<ResultData>{
        resultRepository.loadData(object: OnTaskFinish{
            override fun onFinish(data: ResultData){
                userLiveData.postValue(data)
            }
        })
        return userLiveData
    }

    fun addData(title: String, image: String) {
        val currentMap = resultRepository.getDatas()?.toMutableMap() ?: mutableMapOf()
        currentMap[title] = image
        resultRepository.uploadData(currentMap)
    }

    fun editData(oldTitle: String, newTitle: String) {
        val currentMap = resultRepository.getDatas()?.toMutableMap() ?: mutableMapOf()
        val images = currentMap.remove(oldTitle)
        if (images != null) {
            currentMap[newTitle] = images
            resultRepository.uploadData(currentMap)
        }
    }

    fun deleteData(title: String, image: String? = null) {
        val currentMap = resultRepository.getDatas()?.toMutableMap() ?: return

        if (image == null || currentMap[title] == image) {
            currentMap.remove(title)
        }
        resultRepository.uploadData(currentMap)
    }

    fun findData(item: String):Boolean {
        val currentMap = resultRepository.getDatas()
        return currentMap?.containsKey(item) == true
    }

    fun uploadVideo(video: String?){
        // upload video
        val videoFile = File(video.toString())
        val requestFile = RequestBody.create(MultipartBody.FORM, videoFile)
        val fbody = MultipartBody.Part.createFormData("video", videoFile.name, requestFile)
        detectService.detect(fbody).enqueue(object : Callback<HashMap<String, ArrayList<String>>> {
            override fun onResponse(call: Call<HashMap<String, ArrayList<String>>>, response: Response<HashMap<String, ArrayList<String>>>) {
                if(response.isSuccessful) {
                    val map = response.body()
                    Log.i("onResponse2","OK")
                    val resultMap = map?.mapValues { entry ->
                        entry.value.lastOrNull()?:""
                    }?: emptyMap()
                    // get ingredient list
                    ingredientService.getList().enqueue(object : Callback<ArrayList<IngredientItem>> {
                        override fun onResponse(
                            call: Call<ArrayList<IngredientItem>>,
                            response: Response<ArrayList<IngredientItem>>
                        ) {
                            if (response.isSuccessful) {
                                val list = response.body()
                                val names: Array<String>? = list?.map { it.name }?.toTypedArray()
                                val mandarins: Array<String>? = list?.map { it.mandarin }?.toTypedArray()
                                Log.i("onResponse3", "OK")
                                val updateMap = resultMap.mapKeys { entry ->
                                    val newKey = entry.key.replace("_", " ")
                                    val index = names?.indexOfFirst { it.equals(newKey, ignoreCase = true) }
                                    if (index != -1) {
                                        "${index?.let { mandarins?.get(it) }} ${index?.let { names[it] }}"
                                    }
                                    else {
                                        newKey
                                    }
                                }
                                resultRepository.uploadData(updateMap)
                                callBack()
                            }
                        }
                        override fun onFailure(call: Call<ArrayList<IngredientItem>>, t: Throwable) {
                            Log.i("onFailure3",t.toString())
                        }
                    })
                }
            }
            override fun onFailure(call: Call<HashMap<String, ArrayList<String>>>, t: Throwable) {
                Log.i("onFailure2",t.toString())
            }
        })
    }
}