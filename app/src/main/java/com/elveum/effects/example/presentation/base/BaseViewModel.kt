package com.elveum.effects.example.presentation.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

abstract class BaseViewModel : ViewModel() {

    fun <T> liveData(value: T? = null): LiveData<T> = MutableLiveData(value)

    fun <T> LiveData<T>.updateWith(newValue: T) {
        (this as? MutableLiveData<T>)?.value = newValue
    }

}