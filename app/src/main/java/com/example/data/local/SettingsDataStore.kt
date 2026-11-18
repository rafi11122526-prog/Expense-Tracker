package com.example.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.domain.model.BusinessSettings
import com.example.domain.model.SubscriptionTier
import com.example.domain.model.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.pocketBizDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "pocketbiz_settings"
)

class SettingsDataStore(private val context: Context) {

    private object Keys {
        val BUSINESS_NAME = stringPreferencesKey("business_name")
        val BUSINESS_PHONE = stringPreferencesKey("business_phone")
        val BUSINESS_EMAIL = stringPreferencesKey("business_email")
        val BUSINESS_ADDRESS = stringPreferencesKey("business_address")
        val CURRENCY_CODE = stringPreferencesKey("currency_code")
        val DATE_FORMAT_PATTERN = stringPreferencesKey("date_format_pattern")
        val INVOICE_PREFIX = stringPreferencesKey("invoice_prefix")
        val NEXT_INVOICE_NUMBER = intPreferencesKey("next_invoice_number")
        val DEFAULT_TAX_RATE = stringPreferencesKey("default_tax_rate")
        val CLOUD_BACKUP_ENABLED = booleanPreferencesKey("cloud_backup_enabled")
        val SUBSCRIPTION_TIER = stringPreferencesKey("subscription_tier")
        val THEME_MODE = stringPreferencesKey("theme_mode")
    }

    val settingsFlow: Flow<BusinessSettings> = context.pocketBizDataStore.data.map { prefs ->
        val tierName = prefs[Keys.SUBSCRIPTION_TIER] ?: SubscriptionTier.FREE.name
        val tier = SubscriptionTier.entries.firstOrNull { it.name == tierName } ?: SubscriptionTier.FREE
        val themeName = prefs[Keys.THEME_MODE] ?: ThemeMode.LIGHT.name
        val theme = ThemeMode.entries.firstOrNull { it.name == themeName } ?: ThemeMode.LIGHT

        BusinessSettings(
            businessName = prefs[Keys.BUSINESS_NAME] ?: "PocketBiz",
            businessPhone = prefs[Keys.BUSINESS_PHONE] ?: "",
            businessEmail = prefs[Keys.BUSINESS_EMAIL] ?: "",
            businessAddress = prefs[Keys.BUSINESS_ADDRESS] ?: "",
            currencyCode = prefs[Keys.CURRENCY_CODE] ?: "PKR",
            dateFormatPattern = prefs[Keys.DATE_FORMAT_PATTERN] ?: "dd MMM yyyy",
            invoicePrefix = prefs[Keys.INVOICE_PREFIX] ?: "INV-",
            nextInvoiceNumber = prefs[Keys.NEXT_INVOICE_NUMBER] ?: 1001,
            defaultTaxRatePercent = prefs[Keys.DEFAULT_TAX_RATE] ?: "0.00",
            cloudBackupEnabled = prefs[Keys.CLOUD_BACKUP_ENABLED] ?: false,
            subscriptionTier = tier,
            themeMode = theme
        )
    }

    suspend fun updateBusinessSettings(settings: BusinessSettings) {
        context.pocketBizDataStore.edit { prefs ->
            prefs[Keys.BUSINESS_NAME] = settings.businessName.ifBlank { "PocketBiz" }
            prefs[Keys.BUSINESS_PHONE] = settings.businessPhone.trim()
            prefs[Keys.BUSINESS_EMAIL] = settings.businessEmail.trim()
            prefs[Keys.BUSINESS_ADDRESS] = settings.businessAddress.trim()
            prefs[Keys.CURRENCY_CODE] = settings.currencyCode.ifBlank { "PKR" }
            prefs[Keys.DATE_FORMAT_PATTERN] = settings.dateFormatPattern.ifBlank { "dd MMM yyyy" }
            prefs[Keys.INVOICE_PREFIX] = settings.invoicePrefix.ifBlank { "INV-" }
            prefs[Keys.NEXT_INVOICE_NUMBER] = settings.nextInvoiceNumber.coerceAtLeast(1)
            prefs[Keys.DEFAULT_TAX_RATE] = settings.defaultTaxRatePercent.ifBlank { "0.00" }
            prefs[Keys.CLOUD_BACKUP_ENABLED] = settings.cloudBackupEnabled
            prefs[Keys.SUBSCRIPTION_TIER] = settings.subscriptionTier.name
            prefs[Keys.THEME_MODE] = settings.themeMode.name
        }
    }

    suspend fun updateThemeMode(themeMode: ThemeMode) {
        context.pocketBizDataStore.edit { prefs ->
            prefs[Keys.THEME_MODE] = themeMode.name
        }
    }
}
