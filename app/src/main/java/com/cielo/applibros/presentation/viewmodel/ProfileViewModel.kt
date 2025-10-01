package com.cielo.applibros.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cielo.applibros.domain.model.UserStats
import com.cielo.applibros.domain.usecase.GetUserStatsUseCase
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val getUserStatsUseCase: GetUserStatsUseCase
) : ViewModel() {

    private val _userStats = MutableLiveData<UserStats>()
    val userStats: LiveData<UserStats> = _userStats

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun loadUserStats() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                val stats = getUserStatsUseCase()
                _userStats.value = stats
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
}