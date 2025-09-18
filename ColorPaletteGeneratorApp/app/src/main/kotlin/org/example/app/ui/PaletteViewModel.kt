package org.example.app.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.example.app.model.Debouncer
import org.example.app.model.HslColor
import org.example.app.model.PaletteGenerator

/**
 * ViewModel coordinating palette generation and UI state exposure.
 * Uses MutableLiveData to keep compatibility with View-based UI and Espresso.
 */
class PaletteViewModel(
    private val generator: PaletteGenerator = PaletteGenerator(),
    private val debouncer: Debouncer = Debouncer(400)
) : ViewModel() {

    private val _palette = MutableLiveData<List<HslColor>>()
    val palette: LiveData<List<HslColor>> = _palette

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _lastDurationMs = MutableLiveData<Long>()
    val lastDurationMs: LiveData<Long> = _lastDurationMs

    private var runningJob: Job? = null

    // PUBLIC_INTERFACE
    fun generate(initial: Boolean = false, size: Int = 5) {
        if (!initial && !debouncer.shouldProceed()) {
            return
        }
        runningJob?.cancel()
        runningJob = viewModelScope.launch(Dispatchers.Default) {
            _isLoading.postValue(true)
            val start = System.currentTimeMillis()
            val result = generator.generateDistinctPalette(size = size)
            val duration = System.currentTimeMillis() - start
            _lastDurationMs.postValue(duration)
            // small delay to make crossfade visible but respect performance
            delay(100)
            _palette.postValue(result)
            _isLoading.postValue(false)
        }
    }
}
