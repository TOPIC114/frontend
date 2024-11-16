package com.example.idiotchefassistant.resultBlock

class ResultData {
    var result: Map<Int, IngItem>? = null
}

data class IngItem (
    val name: String,
    val images: ArrayList<String>
)