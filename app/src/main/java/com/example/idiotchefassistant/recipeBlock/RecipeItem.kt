package com.example.idiotchefassistant.recipeBlock

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RecipeItem(
    val rid:Int,
    val title:String,
    val link:String,
    val score:Int
): Parcelable

//data class CommitItem(val userImg: Int, val userName:String, val context:String, val review:Int)
