package com.zakorchook.currencytest.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zakorchook.currencytest.data.model.ExchangeResult
import com.zakorchook.currencytest.data.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val repository: Repository) : ViewModel() {

    private val _allCurrenciesResult = MutableLiveData<List<String>>()
    val allCurrenciesResult: LiveData<List<String>> = _allCurrenciesResult

    private val _exchangeResult = MutableLiveData<ExchangeResult>()
    val exchangeResult: LiveData<ExchangeResult> = _exchangeResult

    private val _exception = MutableLiveData<String>()
    val exception: LiveData<String> = _exception

    private val _progress = MutableLiveData<Boolean>()
    val progress: LiveData<Boolean> = _progress

    init {
        requestAvailableCurrencies()
    }

    fun requestExchange(currencyCode: String, srcAmount: Float, date: Date) {
        viewModelScope.launch {
            _progress.postValue(true)
            try {
                _exchangeResult.postValue(repository.exchange(currencyCode, srcAmount, date))
                _progress.postValue(false)
            } catch (e: Exception) {
                _progress.postValue(false)
                _exception.postValue(e.message)
            }
        }
    }

    private fun requestAvailableCurrencies() {
        viewModelScope.launch {
            _progress.postValue(true)
            try {
                _allCurrenciesResult.postValue(repository.requestAllCurrencyCodes())
                _progress.postValue(false)
            } catch (e: Exception) {
                _progress.postValue(false)
                _exception.postValue(e.message)
            }
        }
    }
}