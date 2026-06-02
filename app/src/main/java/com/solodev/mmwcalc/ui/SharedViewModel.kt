package com.solodev.mmwcalc.ui

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor() : ViewModel() {

    private val _pendingInputs = MutableStateFlow<Triple<String, Map<String, String>, Long>?>(null)
    val pendingInputs: StateFlow<Triple<String, Map<String, String>, Long>?> = _pendingInputs.asStateFlow()

    fun setPendingInputs(topicId: String, inputs: Map<String, String>) {
        // Use timestamp as unique trigger key so LaunchedEffect always fires
        _pendingInputs.value = Triple(topicId, inputs, System.currentTimeMillis())
    }

    fun consumePendingInputs(): Pair<String, Map<String, String>>? {
        val v = _pendingInputs.value ?: return null
        _pendingInputs.value = null
        return v.first to v.second
    }
}