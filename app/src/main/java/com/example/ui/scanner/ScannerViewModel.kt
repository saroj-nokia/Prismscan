package com.example.ui.scanner

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.HistoryRepository
import com.example.data.local.PreferencesRepository
import com.example.domain.model.ParsedBarcode
import com.example.domain.parser.BarcodeParser
import com.example.scanner.BarcodeAnalyzer
import com.example.scanner.GalleryImageScanner
import com.example.scanner.HapticFeedbackHelper
import com.google.mlkit.vision.barcode.common.Barcode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ScannerUiState(
    val torchEnabled: Boolean = false,
    val zoomRatio: Float = 1f,
    val isScanning: Boolean = true,
    val isProcessingGallery: Boolean = false,
    val lastScan: ParsedBarcode? = null,
    val galleryErrorMessage: String? = null
)

class ScannerViewModel(
    private val historyRepository: HistoryRepository,
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ScannerUiState())
    val uiState: StateFlow<ScannerUiState> = _uiState.asStateFlow()

    val analyzer = BarcodeAnalyzer { barcode ->
        onBarcodeDetected(barcode)
    }

    private var appContext: Context? = null

    fun setContext(context: Context) {
        appContext = context.applicationContext
    }

    private fun onBarcodeDetected(barcode: Barcode) {
        val parsed = BarcodeParser.parse(barcode)
        appContext?.let { ctx ->
            HapticFeedbackHelper.performScanSuccess(ctx, preferencesRepository.vibrateEnabled.value)
        }
        viewModelScope.launch {
            historyRepository.saveScan(parsed)
            _uiState.update {
                it.copy(
                    isScanning = false,
                    lastScan = parsed
                )
            }
        }
    }

    fun scanGallery(context: Context, uri: Uri) {
        viewModelScope.launch {
            _uiState.update { it.copy(isProcessingGallery = true, galleryErrorMessage = null) }
            val parsed = GalleryImageScanner.scanUri(context, uri)
            if (parsed != null) {
                HapticFeedbackHelper.performScanSuccess(context, preferencesRepository.vibrateEnabled.value)
                historyRepository.saveScan(parsed)
                _uiState.update {
                    it.copy(
                        isProcessingGallery = false,
                        isScanning = false,
                        lastScan = parsed
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        isProcessingGallery = false,
                        galleryErrorMessage = "No recognizable barcode or QR code found in the selected image."
                    )
                }
            }
        }
    }

    fun toggleTorch() {
        _uiState.update { it.copy(torchEnabled = !it.torchEnabled) }
    }

    fun setZoom(ratio: Float) {
        _uiState.update { it.copy(zoomRatio = ratio) }
    }

    fun clearGalleryError() {
        _uiState.update { it.copy(galleryErrorMessage = null) }
    }

    fun resumeScanning() {
        analyzer.resume()
        _uiState.update {
            it.copy(
                isScanning = true,
                lastScan = null,
                galleryErrorMessage = null
            )
        }
    }

    companion object {
        fun provideFactory(
            historyRepository: HistoryRepository,
            preferencesRepository: PreferencesRepository
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ScannerViewModel(historyRepository, preferencesRepository) as T
            }
        }
    }
}
