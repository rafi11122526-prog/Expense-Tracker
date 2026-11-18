package com.example

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.CustomerEntity
import com.example.data.local.InvoiceEntity
import com.example.data.local.PocketBizDatabase
import com.example.data.local.SettingsDataStore
import com.example.data.local.TransactionEntity
import com.example.data.repository.BusinessRepository
import com.example.domain.model.BusinessSettings
import com.example.domain.model.InvoiceStatus
import com.example.domain.model.ThemeMode
import com.example.domain.model.TransactionType
import com.example.ui.theme.PocketBizTheme
import com.example.ui.viewmodel.MainViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36], qualifiers = "w412dp-h915dp")
class ExampleRobolectricTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var context: Context
    private lateinit var database: PocketBizDatabase
    private lateinit var repository: BusinessRepository

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        val dataStore = SettingsDataStore(context)
        runBlocking {
            dataStore.updateBusinessSettings(BusinessSettings())
        }
        database = Room.inMemoryDatabaseBuilder(context, PocketBizDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = BusinessRepository(
            transactionDao = database.transactionDao(),
            customerDao = database.customerDao(),
            invoiceDao = database.invoiceDao(),
            receiptDao = database.receiptDao(),
            settingsDataStore = dataStore
        )
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `app name matches PocketBiz`() {
        assertEquals("PocketBiz", context.getString(R.string.app_name))
    }

    @Test
    fun `room database inserts transactions and computes outstanding invoice balance`() = runBlocking {
        val customerId = database.customerDao().insertCustomer(
            CustomerEntity(name = "Al-Noor Traders", phone = "+923001234567")
        )
        database.invoiceDao().insertInvoice(
            InvoiceEntity(
                invoiceNumber = "INV-1001",
                customerId = customerId,
                issueDateEpochMs = System.currentTimeMillis(),
                dueDateEpochMs = System.currentTimeMillis(),
                subtotalMinor = 100000L,
                totalMinor = 100000L,
                paidAmountMinor = 35000L,
                status = InvoiceStatus.PARTIAL
            )
        )
        database.transactionDao().insertTransaction(
            TransactionEntity(
                type = TransactionType.INCOME,
                category = "Sales",
                amountMinor = 50000L,
                currencyCode = "PKR",
                description = "Walk-in sale"
            )
        )

        val outstanding = database.invoiceDao().observeOutstandingBalanceMinor().first()
        val transactions = database.transactionDao().observeAllTransactions().first()

        assertEquals(65000L, outstanding)
        assertEquals(1, transactions.size)
        assertEquals("Sales", transactions.first().category)
    }

    @Test
    fun `navigation across all 5 tabs and settings works cleanly`() {
        val viewModel = MainViewModel(repository)
        composeTestRule.setContent {
            PocketBizTheme {
                PocketBizAppRoot(viewModel = viewModel)
            }
        }
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("monthly_net_profit_card").assertIsDisplayed()

        composeTestRule.onNodeWithTag("nav_transactions").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("transactions_screen").assertIsDisplayed()

        composeTestRule.onNodeWithTag("nav_invoices").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("invoices_screen").assertIsDisplayed()

        composeTestRule.onNodeWithTag("nav_customers").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("customers_screen").assertIsDisplayed()

        composeTestRule.onNodeWithTag("nav_reports").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("reports_screen").assertIsDisplayed()

        composeTestRule.onNodeWithTag("nav_dashboard").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("settings_button").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("settings_screen").assertIsDisplayed()
    }

    @Test
    fun `recording a transaction via FAB updates dashboard net profit`() {
        val viewModel = MainViewModel(repository)
        composeTestRule.setContent {
            PocketBizTheme {
                PocketBizAppRoot(viewModel = viewModel)
            }
        }
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("add_transaction_fab").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("transaction_amount_input").performTextInput("2500.50")
        composeTestRule.onNodeWithTag("transaction_description_input").performTextInput("Retail counter sale")
        composeTestRule.onNodeWithTag("save_transaction_button").performScrollTo().performClick()

        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule.onAllNodesWithText("+PKR 2,500.50").fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("monthly_net_profit_value")
            .assertIsDisplayed()
            .assertTextEquals("+PKR 2,500.50")
    }

    @Test
    fun `updating business name in settings persists and updates dashboard`() {
        val viewModel = MainViewModel(repository)
        composeTestRule.setContent {
            PocketBizTheme {
                PocketBizAppRoot(viewModel = viewModel)
            }
        }
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("settings_button").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("settings_business_name_input").performTextClearance()
        composeTestRule.onNodeWithTag("settings_business_name_input").performTextInput("Lahore Goods Co")
        composeTestRule.onNodeWithTag("save_settings_button").performScrollTo().performClick()

        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule.onAllNodesWithText("Settings saved locally").fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithTag("settings_back_button").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Lahore Goods Co").assertIsDisplayed()
    }

    @Test
    fun `quick action expense records entry and transactions search filters results`() {
        val viewModel = MainViewModel(repository)
        composeTestRule.setContent {
            PocketBizTheme {
                PocketBizAppRoot(viewModel = viewModel)
            }
        }
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("quick_action_expense").performScrollTo().performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("transaction_amount_input").performTextInput("450.00")
        composeTestRule.onNodeWithTag("transaction_description_input").performTextInput("Packaging boxes")
        composeTestRule.onNodeWithTag("save_transaction_button").performScrollTo().performClick()

        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule.onAllNodesWithText("-PKR 450.00").fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithTag("nav_transactions").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("transactions_search_input").performTextInput("Packaging")
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Packaging boxes • ${java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault()).format(java.util.Date())}").assertIsDisplayed()
    }

    @Test
    fun `analytics chart selector switches between Donut, Pie, Bars, and Trend views`() {
        val viewModel = MainViewModel(repository)
        composeTestRule.setContent {
            PocketBizTheme {
                PocketBizAppRoot(viewModel = viewModel)
            }
        }
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("chart_mode_pie").performScrollTo().performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("chart_view_pie").performScrollTo().assertIsDisplayed()

        composeTestRule.onNodeWithTag("chart_mode_bars").performScrollTo().performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("chart_view_bars").performScrollTo().assertIsDisplayed()

        composeTestRule.onNodeWithTag("chart_mode_trend").performScrollTo().performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("chart_view_trend").performScrollTo().assertIsDisplayed()

        composeTestRule.onNodeWithTag("chart_mode_donut").performScrollTo().performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("chart_view_donut").performScrollTo().assertIsDisplayed()
    }

    @Test
    fun `theme toggle switches theme mode and persists in settings`() {
        val viewModel = MainViewModel(repository)
        composeTestRule.setContent {
            val state by viewModel.uiState.collectAsStateWithLifecycle()
            val isDark = state.settings.themeMode == ThemeMode.DARK
            PocketBizTheme(darkTheme = isDark) {
                PocketBizAppRoot(viewModel = viewModel)
            }
        }
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("theme_toggle_button").performClick()
        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule.onAllNodesWithContentDescription("Switch to Light Theme")
                .fetchSemanticsNodes().isNotEmpty()
        }
        assertEquals(ThemeMode.DARK, viewModel.uiState.value.settings.themeMode)

        composeTestRule.onNodeWithTag("theme_toggle_button").performClick()
        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule.onAllNodesWithContentDescription("Switch to Dark Theme")
                .fetchSemanticsNodes().isNotEmpty()
        }
        assertEquals(ThemeMode.LIGHT, viewModel.uiState.value.settings.themeMode)
    }

    @Test
    @Config(qualifiers = "w840dp-h600dp")
    fun `tablet layout renders navigation rail and dashboard cleanly`() {
        val viewModel = MainViewModel(repository)
        composeTestRule.setContent {
            PocketBizTheme {
                PocketBizAppRoot(viewModel = viewModel)
            }
        }
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("monthly_net_profit_card").assertIsDisplayed()
        composeTestRule.onNodeWithTag("nav_transactions").assertIsDisplayed()
    }

    @Test
    @Config(qualifiers = "w360dp-h780dp")
    fun `narrow 360dp phone renders dashboard, large PKR amounts, and all 5 bottom nav tabs without clipping`() = runBlocking<Unit> {
        database.transactionDao().insertTransaction(
            TransactionEntity(
                type = TransactionType.INCOME,
                category = "Sales",
                amountMinor = 440238400L,
                currencyCode = "PKR",
                description = "Wholesale bulk order"
            )
        )
        database.transactionDao().insertTransaction(
            TransactionEntity(
                type = TransactionType.EXPENSE,
                category = "Inventory",
                amountMinor = 53193000L,
                currencyCode = "PKR",
                description = "Stock restock"
            )
        )

        val viewModel = MainViewModel(repository)
        composeTestRule.setContent {
            PocketBizTheme {
                PocketBizAppRoot(viewModel = viewModel)
            }
        }
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("monthly_net_profit_value")
            .assertIsDisplayed()
            .assertTextEquals("+PKR 3,870,454.00")
        composeTestRule.onNodeWithText("Dashboard").assertIsDisplayed()
        composeTestRule.onNodeWithText("Transactions").assertIsDisplayed()
        composeTestRule.onAllNodesWithText("Invoices")[1].assertIsDisplayed()
        composeTestRule.onAllNodesWithText("Customers")[1].assertIsDisplayed()
        composeTestRule.onNodeWithText("Reports").assertIsDisplayed()
    }

    @Test
    @Config(qualifiers = "w393dp-h851dp")
    fun `reports screen at 393dp displays full PKR revenue, expenses, and analytics without stage jargon`() = runBlocking<Unit> {
        database.transactionDao().insertTransaction(
            TransactionEntity(
                type = TransactionType.INCOME,
                category = "Sales",
                amountMinor = 440238400L,
                currencyCode = "PKR",
                description = "Monthly wholesale"
            )
        )
        database.transactionDao().insertTransaction(
            TransactionEntity(
                type = TransactionType.EXPENSE,
                category = "Inventory",
                amountMinor = 53193000L,
                currencyCode = "PKR",
                description = "Supplier payment"
            )
        )

        val viewModel = MainViewModel(repository)
        composeTestRule.setContent {
            PocketBizTheme {
                PocketBizAppRoot(viewModel = viewModel)
            }
        }
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("nav_reports").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("reports_screen").assertIsDisplayed()
        composeTestRule.onNodeWithText("PKR 4,402,384.00").assertIsDisplayed()
        composeTestRule.onAllNodesWithText("PKR 531,930.00")[0].assertIsDisplayed()
        assertEquals(
            0,
            composeTestRule.onAllNodesWithText("Scheduled for Stage 4").fetchSemanticsNodes().size
        )
    }

    @Test
    @Config(qualifiers = "w432dp-h936dp")
    fun `customers and invoices screens at 432dp show polished commercial empty states`() {
        val viewModel = MainViewModel(repository)
        composeTestRule.setContent {
            PocketBizTheme {
                PocketBizAppRoot(viewModel = viewModel)
            }
        }
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("nav_customers").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("No Customers Added Yet").assertIsDisplayed()
        assertEquals(
            0,
            composeTestRule.onAllNodesWithText("Customer Ledger Schema Ready").fetchSemanticsNodes().size
        )

        composeTestRule.onNodeWithTag("nav_invoices").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("No Invoices Created Yet").assertIsDisplayed()
        assertEquals(
            0,
            composeTestRule.onAllNodesWithText("Invoice Engine Schema Ready").fetchSemanticsNodes().size
        )
    }

    @Test
    fun `analytics period filter shows Week label instead of 7D`() {
        val viewModel = MainViewModel(repository)
        composeTestRule.setContent {
            PocketBizTheme {
                PocketBizAppRoot(viewModel = viewModel)
            }
        }
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("donut_period_this_week").performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithText("Week").assertIsDisplayed()
        assertEquals(0, composeTestRule.onAllNodesWithText("7D").fetchSemanticsNodes().size)
    }

    @Test
    fun `adding and searching customers via Customers tab works cleanly`() {
        val viewModel = MainViewModel(repository)
        composeTestRule.setContent {
            PocketBizTheme {
                PocketBizAppRoot(viewModel = viewModel)
            }
        }
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("nav_customers").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("add_customer_button").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("customer_name_input").performTextInput("Crescent Textiles")
        composeTestRule.onNodeWithTag("customer_phone_input").performTextInput("+923214567890")
        composeTestRule.onNodeWithTag("customer_email_input").performTextInput("orders@crescent.pk")
        composeTestRule.onNodeWithTag("save_customer_button").performScrollTo().performClick()

        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule.onAllNodesWithText("Crescent Textiles").fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithText("Crescent Textiles").assertIsDisplayed()

        composeTestRule.onNodeWithTag("customer_search_input").performTextInput("Crescent")
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Crescent Textiles").assertIsDisplayed()
    }

    @Test
    fun `creating invoice and recording payment updates invoice status and dashboard profit`() {
        runBlocking {
            database.customerDao().insertCustomer(
                CustomerEntity(name = "Indus Hardware", phone = "+923009876543")
            )
        }
        val viewModel = MainViewModel(repository)
        composeTestRule.setContent {
            PocketBizTheme {
                PocketBizAppRoot(viewModel = viewModel)
            }
        }
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("nav_invoices").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("How Invoices Work").performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithTag("add_invoice_button").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("invoice_item_desc_0").performScrollTo().performTextInput("Power Drill Set")
        composeTestRule.onNodeWithTag("invoice_item_qty_0").performScrollTo().performTextClearance()
        composeTestRule.onNodeWithTag("invoice_item_qty_0").performTextInput("2")
        composeTestRule.onNodeWithTag("invoice_item_price_0").performScrollTo().performTextInput("1500.00")
        composeTestRule.onNodeWithTag("save_invoice_button").performScrollTo().performClick()

        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule.onAllNodesWithTag("toggle_record_payment_button").fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithTag("toggle_record_payment_button").performScrollTo().performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("save_payment_button").performScrollTo().performClick()

        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule.onAllNodesWithText("Payment History").fetchSemanticsNodes().isNotEmpty()
        }
        assertEquals(null, viewModel.uiState.value.paymentError)
        assertEquals(InvoiceStatus.PAID, viewModel.uiState.value.invoices.first().status)
        composeTestRule.onNodeWithText("Payment History").performScrollTo().assertIsDisplayed()

        // Dismiss the detail sheet, verify invoice card is listed, then check Dashboard profit
        viewModel.dismissInvoiceDetail()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("invoice_card_INV-1001").performScrollTo().assertIsDisplayed()

        composeTestRule.onNodeWithTag("nav_dashboard").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("monthly_net_profit_value")
            .assertIsDisplayed()
            .assertTextEquals("+PKR 3,000.00")
    }
}
