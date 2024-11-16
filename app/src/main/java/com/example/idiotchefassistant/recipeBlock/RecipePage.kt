package com.example.idiotchefassistant.recipeBlock

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.example.idiotchefassistant.R
import com.example.idiotchefassistant.databinding.ActivityRecipePageBinding
import java.util.Locale

class RecipePage : AppCompatActivity() {
    private lateinit var binding: ActivityRecipePageBinding
    private lateinit var recipeViewModel: RecipeViewModel
    private lateinit var recipeFactory: RecipeFactory
    private lateinit var recipeRepository: RecipeRepository


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRecipePageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        recipeRepository = RecipeRepository()
        recipeFactory = RecipeFactory(recipeRepository)
        recipeViewModel = ViewModelProvider(this, recipeFactory)[RecipeViewModel::class.java]

        // get recipe content
        val rid = intent.getIntExtra("rid", -1)
        recipeViewModel.getData(rid)

        recipeViewModel.callBack().observe(this) { data ->
            Log.d("SearchPage", "Received items: $data")
            binding.recipeName.text = data?.title ?: "Unknown"
            val imageLink = recipeViewModel.link2Image(data?.video)
            binding.recipeImage.load(imageLink)
            imageLink.let {
                binding.recipeImage.load(it) {
                    placeholder(R.drawable.downloading_24)  // 可選的佔位符
                    error(R.drawable.broken_image_24)       // 可選的錯誤圖片
                }
            }
            binding.recipeImage.setOnClickListener{
                openYoutube(this, data.video.toString())
            }
            val floatValue: Float = (data?.score ?: 0).toFloat()
            binding.recipeReviewNum.text = floatValue.toString()
            binding.recipeReviewStar.rating = floatValue
            // create ingredients adapter
            recipeViewModel.getIngredients(data.iids).observe(this) { ingredients ->
                val adapter = RecipeIngredientsAdapter(ingredients)
                binding.ingredients.layoutManager = LinearLayoutManager(this)
                binding.ingredients.adapter = adapter
            }
        }
    }

    private fun openYoutube(context: Context, url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        // 检查是否有应用可以处理这个意图
        val resolveInfo = context.packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)
        if (resolveInfo?.activityInfo?.packageName?.lowercase(Locale.ROOT)?.contains("youtube") == true) {
            context.startActivity(intent)
        } else {
            // 没有 YouTube 应用，使用浏览器打开
            val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            webIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(webIntent)
        }
    }

}

