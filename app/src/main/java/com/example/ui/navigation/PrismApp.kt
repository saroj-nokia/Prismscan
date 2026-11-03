package com.example.ui.navigation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.example.AppContainer
import com.example.domain.model.ParsedBarcode
import com.example.ui.history.HistoryScreen
import com.example.ui.history.HistoryViewModel
import com.example.ui.result.ResultScreen
import com.example.ui.scanner.ScannerScreen
import com.example.ui.scanner.ScannerViewModel
import com.example.ui.settings.SettingsSheet

enum class AppDestination(val label: String) {
    SCANNER("Scan"),
    HISTORY("History")
}

@Composable
fun PrismApp(
    container: AppContainer,
    modifier: Modifier = Modifier
) {
    var currentDestination by remember { mutableStateOf(AppDestination.SCANNER) }
    var activeResult by remember { mutableStateOf<ParsedBarcode?>(null) }
    var showSettingsSheet by remember { mutableStateOf(false) }

    val scannerViewModel: ScannerViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
        factory = ScannerViewModel.provideFactory(
            container.historyRepository,
            container.preferencesRepository
        )
    )

    val historyViewModel: HistoryViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
        factory = HistoryViewModel.provideFactory(
            container.historyRepository,
            container.preferencesRepository
        )
    )

    if (activeResult != null) {
        ResultScreen(
            barcode = activeResult!!,
            onNavigateBack = {
                activeResult = null
                scannerViewModel.resumeScanning()
            }
        )
    } else {
        NavigationSuiteScaffold(
            navigationSuiteItems = {
                item(
                    selected = currentDestination == AppDestination.SCANNER,
                    onClick = { currentDestination = AppDestination.SCANNER },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.QrCodeScanner,
                            contentDescription = "Scanner Tab"
                        )
                    },
                    label = { Text("Scan") },
                    modifier = Modifier.testTag("nav_scanner_tab")
                )
                item(
                    selected = currentDestination == AppDestination.HISTORY,
                    onClick = { currentDestination = AppDestination.HISTORY },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = "History Tab"
                        )
                    },
                    label = { Text("History") },
                    modifier = Modifier.testTag("nav_history_tab")
                )
            },
            modifier = modifier.fillMaxSize()
        ) {
            AnimatedContent(
                targetState = currentDestination,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "navigation_destination"
            ) { destination ->
                when (destination) {
                    AppDestination.SCANNER -> {
                        ScannerScreen(
                            viewModel = scannerViewModel,
                            onNavigateToResult = { scan ->
                                activeResult = scan
                            },
                            onNavigateToHistory = {
                                currentDestination = AppDestination.HISTORY
                            },
                            onOpenSettings = {
                                showSettingsSheet = true
                            }
                        )
                    }

                    AppDestination.HISTORY -> {
                        HistoryScreen(
                            viewModel = historyViewModel,
                            onNavigateBack = {
                                currentDestination = AppDestination.SCANNER
                            }
                        )
                    }
                }
            }
        }
    }

    if (showSettingsSheet) {
        SettingsSheet(
            preferencesRepository = container.preferencesRepository,
            onDismiss = { showSettingsSheet = false }
        )
    }
}
