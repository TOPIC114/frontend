package com.example.idiotchefassistant.searchBlock

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel

class SearchViewModel(private var searchRepository: SearchRepository): ViewModel() {
    private var userLiveData = MutableLiveData<SearchData>()
    private val _nowOffset = MutableLiveData(0)
    val nowOffset: LiveData<Int> get() = _nowOffset
    private var iids = emptyList<Int>()
    private val _keyword = MutableLiveData<String>()
    private val keyword: LiveData<String> get() = _keyword
    private var searchByIds = true
    private var recipeIsNull = false

    fun callBack(): LiveData<SearchData> {
        searchRepository.loadData(object : SearchRepository.OnTaskFinish {
            override fun onFinish(data: SearchData){
                userLiveData.postValue(data)
            }
        })
        return userLiveData
    }

    fun uploadData(newData: SearchData){
        userLiveData.postValue(newData)
    }

    fun nextPage(): LiveData<Boolean> {
        _nowOffset.value = (_nowOffset.value?: 0) + 1
        return search()
    }

    fun backPage(): LiveData<Boolean> {
        if(_nowOffset.value == 0) return MutableLiveData(false)
        _nowOffset.value = (_nowOffset.value?: 1) - 1
        return search()
    }

    fun setKeyword(newKeyword: String){
        _keyword.value = newKeyword
        Log.i("SVM", "Now keyword: ${_keyword.value}")
    }

    fun setIids(nowIids: List<Int>){
        iids = nowIids
    }

    fun keywordSearch(){
        searchByIds = false
        Log.i("SVM", "Search keyword by: (${_nowOffset.value}, ${_keyword.value})")
        search()
    }

    private fun search(): LiveData<Boolean>{
        val isLastPage = MutableLiveData<Boolean>()
        val liveData = if (searchByIds){
            searchRepository.searchByIids(_nowOffset.value!!, iids)
        }
        else{
            searchRepository.searchByKeyword(_nowOffset.value!!, _keyword.value!!)
        }
        liveData.observeForever { result ->
            recipeIsNull = result.isNullOrEmpty()
            userLiveData.postValue(SearchData().apply { list = ArrayList(result) })
            isLastPage.postValue(recipeIsNull)
        }
        return isLastPage
    }

}