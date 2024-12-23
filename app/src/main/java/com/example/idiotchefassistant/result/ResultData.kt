package com.example.idiotchefassistant.result

class ResultData {
    var result: Map<Int, IngItem>? = null
}

data class IngItem (
    val name: String,
    val images: ArrayList<String>
)