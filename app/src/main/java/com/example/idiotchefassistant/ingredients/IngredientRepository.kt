package com.example.idiotchefassistant.ingredients

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
    private var seasoningList = arrayOf("醬油 soy sauce","牛奶 milk","番茄醬 ketchup","松露醬 truffle sauce","味噌 miso","美乃滋 mayonnaise","番茄糊 tomato paste","胡麻醬 sesame sauce","甜麵醬 sweet bean sauce","咖哩 curry","優格 yogurt","高湯 stock","豆瓣醬 chili bean sauce","抹茶 matcha","鮮奶油 cream whip","紅椒粉 paprika","八角 star anise","伍斯特醬 worcestershire sauce","花椒 sichuan pepper","芝麻油 sesame oil","味醂 mirin","清酒 sake","蠔油 oyster sauce","紹興酒 shaoxing wine","白酒 Chinese spirits","紅葡萄酒 red wine","魚露 fish sauce","沙茶醬 shacha sauce","義大利麵醬 pasta sauce","韓式辣醬 gochujang sauce","米酒 rice wine","威士忌 whisky","優酪乳 drinking yogurt","酒 wine","泰式醬 sauce thai","辣椒粉 chili powder","醋 vinegar","蜂蜜芥末醬 honey mustard","五香粉 five spice powder","玉米粉 corn powder","燒肉醬 sauce bbq","啤酒 beer","七味粉 shichimi powder","薑黃粉 turmeric sauce","冬蔭功醬 tom yum paste","紅油 chili oil","苦茶油 camellia oil","煉乳 condensed milk","咖哩粉 curry powder","紅椒粉 pepper red powder","花生醬 peanut butter")
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