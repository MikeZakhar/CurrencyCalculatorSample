package com.zakorchook.currencytest.ui.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zakorchook.currencytest.data.db.HistoryEntity
import com.zakorchook.currencytest.data.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(private val repository: Repository) : ViewModel() {

    private val _history = MutableLiveData<List<HistoryEntity>>()
    val history: LiveData<List<HistoryEntity>> = _history

    fun requestHistory() {
        viewModelScope.launch {
            _history.postValue(repository.getHistory())
        }
    }
}