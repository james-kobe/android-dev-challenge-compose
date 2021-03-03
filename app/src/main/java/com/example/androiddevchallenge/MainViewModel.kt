/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.androiddevchallenge

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androiddevchallenge.logic.Repository
import com.example.androiddevchallenge.model.Cat
import com.example.androiddevchallenge.util.Resource
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

/**
 * MainViewModel is the bridge to communicate between data layer and ui layer.
 *
 * @author james
 * @since 2022/3/3
 */
class MainViewModel(private val repository: Repository) : ViewModel() {

    /**
     * The LiveData variable to observe Contact object list.
     */
    val catsLiveData: LiveData<Resource<List<Cat>>>
        get() = _catsLiveData

    private val _catsLiveData = MutableLiveData<Resource<List<Cat>>>()

    private val cats = mutableListOf<Cat>()

    /**
     * The handler to handle coroutine exceptions and notify to the observer.
     */
    private val handler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
        _catsLiveData.value = Resource.error(throwable.message ?: "Uncaught exception happens")
    }

    /**
     * Start to read and parse the cats and notify the result to the observer.
     */
    fun readAndParseData() = viewModelScope.launch(handler) {
        _catsLiveData.value = Resource.loading()
        cats.addAll(repository.getCats())
        _catsLiveData.value = Resource.success(cats)
    }

    /**
     * Set the specific cat as adopted.
     */
    fun setCatAdopted(position: Int) {
        cats[position].adopted = true
        _catsLiveData.value = _catsLiveData.value
    }
}
