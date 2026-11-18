package com.example.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.domain.model.BusinessSettings
import com.example.domain.model.ThemeMode
import com.example.ui.components.PocketBizCard
import com.example.ui.theme.LocalPocketBizColors

private val supportedCurrencies = listOf("PKR", "USD", "EUR", "GBP", "AED", "SAR")
private val supportedDateFormats = listOf("dd MMM yyyy", "yyyy-MM-dd", "MM/dd/yyyy")

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SettingsScreen(
    settings: BusinessSettings,
    statusMessage: String?,
    onClearStatusMessage: () -> Unit,
    onSaveSettings: (BusinessSettings) -> Unit,
    onThemeModeChange: (ThemeMode) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val extendedColors = LocalPocketBizColors.current
    val snackbarHostState = remember { SnackbarHostState() }

    var businessName by rememberSaveable(settings.businessName) { mutableStateOf(settings.businessName) }
    var phone by rememberSaveable(settings.businessPhone) { mutableStateOf(settings.businessPhone) }
    var email by rememberSaveable(settings.businessEmail) { mutableStateOf(settings.businessEmail) }
    var address by rememberSaveable(settings.businessAddress) { mutableStateOf(settings.businessAddress) }
    var currencyCode by rememberSaveable(settings.currencyCode) { mutableStateOf(settings.currencyCode) }
    var dateFormat by rememberSaveable(settings.dateFormatPattern) { mutableStateOf(settings.dateFormatPattern) }
    var invoicePrefix by rememberSaveable(settings.invoicePrefix) { mutableStateOf(settings.invoicePrefix) }
    var nextInvoiceNumber by rememberSaveable(settings.nextInvoiceNumber) {
        mutableStateOf(settings.nextInvoiceNumber.toString())
    }
    var taxRate by rememberSaveable(settings.defaultTaxRatePercent) {
        mutableStateOf(settings.defaultTaxRatePercent)
    }

    LaunchedEffect(statusMessage) {
        if (!statusMessage.isNullOrBlank()) {
            snackbarHostState.showSnackbar(statusMessage)
            onClearStatusMessage()
        }
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("settings_screen"),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        contentWindowInsets = WindowInsets.navigationBars
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
        ) {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .windowInsetsPadding(WindowInsets.statusBars)
                            .padding(horizontal = 8.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        IconButton(
                            onClick = onNavigateBack,
                            modifier = Modifier
                                .minimumInteractiveComponentSize()
                                .testTag("settings_back_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Business Settings",
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Theme, currency, tax & invoicing preferences",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    androidx.compose.material3.HorizontalDivider(color = extendedColors.cardBorder)
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                Column(
                    modifier = Modifier.widthIn(max = 760.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    PocketBizCard(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = "Theme Appearance",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "Switch between Light, Dark, or follow system setting",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                                ThemeMode.entries.forEachIndexed { index, mode ->
                                    SegmentedButton(
                                        selected = settings.themeMode == mode,
                                        onClick = { onThemeModeChange(mode) },
                                        shape = SegmentedButtonDefaults.itemShape(
                                            index = index,
                                            count = ThemeMode.entries.size
                                        ),
                                        modifier = Modifier.testTag("theme_mode_${mode.name.lowercase()}")
                                    ) {
                                        Text(mode.label)
                                    }
                                }
                            }
                        }
                    }

                    PocketBizCard(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "Business Identity",
                                style = MaterialTheme.typography.titleMedium
                            )
                            OutlinedTextField(
                                value = businessName,
                                onValueChange = { businessName = it },
                                label = { Text("Business Name") },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("settings_business_name_input")
                            )
                            OutlinedTextField(
                                value = phone,
                                onValueChange = { phone = it },
                                label = { Text("Contact Phone") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = email,
                                onValueChange = { email = it },
                                label = { Text("Business Email") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = address,
                                onValueChange = { address = it },
                                label = { Text("Business Address") },
                                maxLines = 2,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    PocketBizCard(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "Currency & Formatting",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "Default Currency",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                supportedCurrencies.forEach { code ->
                                    val selected = currencyCode == code
                                    FilterChip(
                                        selected = selected,
                                        onClick = { currencyCode = code },
                                        label = { Text(code) },
                                        leadingIcon = if (selected) {
                                            { Icon(Icons.Filled.Check, contentDescription = null) }
                                        } else null,
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                                            selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimary
                                        )
                                    )
                                }
                            }

                            Text(
                                text = "Date Format",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                supportedDateFormats.forEach { pattern ->
                                    val selected = dateFormat == pattern
                                    FilterChip(
                                        selected = selected,
                                        onClick = { dateFormat = pattern },
                                        label = { Text(pattern) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                        )
                                    )
                                }
                            }
                        }
                    }

                    PocketBizCard(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "Invoicing & Tax",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                OutlinedTextField(
                                    value = invoicePrefix,
                                    onValueChange = { invoicePrefix = it },
                                    label = { Text("Invoice Prefix") },
                                    singleLine = true,
                                    modifier = Modifier.weight(1f)
                                )
                                OutlinedTextField(
                                    value = nextInvoiceNumber,
                                    onValueChange = { nextInvoiceNumber = it },
                                    label = { Text("Next Number") },
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            OutlinedTextField(
                                value = taxRate,
                                onValueChange = { taxRate = it },
                                label = { Text("Default Tax Rate (%)") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.fillMaxWidth()
                            )
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant
                            ) {
                                Text(
                                    text = "Plan Tier: ${settings.subscriptionTier.displayName} • Cloud backup disabled (local-only mode)",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                        }
                    }

                    Button(
                        onClick = {
                            onSaveSettings(
                                settings.copy(
                                    businessName = businessName,
                                    businessPhone = phone,
                                    businessEmail = email,
                                    businessAddress = address,
                                    currencyCode = currencyCode,
                                    dateFormatPattern = dateFormat,
                                    invoicePrefix = invoicePrefix,
                                    nextInvoiceNumber = nextInvoiceNumber.toIntOrNull() ?: 1001,
                                    defaultTaxRatePercent = taxRate
                                )
                            )
                        },
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("save_settings_button")
                    ) {
                        Text("Save Settings")
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}
