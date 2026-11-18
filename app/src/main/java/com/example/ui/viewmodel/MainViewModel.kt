package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.CustomerEntity
import com.example.data.local.InvoiceEntity
import com.example.data.local.InvoiceItemEntity
import com.example.data.local.PaymentEntity
import com.example.data.local.TransactionEntity
import com.example.data.repository.BusinessRepository
import com.example.domain.calculator.FinancialCalculator
import com.example.domain.model.AnalyticsChartType
import com.example.domain.model.AnalyticsPeriod
import com.example.domain.model.BusinessSettings
import com.example.domain.model.DashboardMetrics
import com.example.domain.model.InvoiceFilterStatus
import com.example.domain.model.InvoiceLineInput
import com.example.domain.model.InvoiceStatus
import com.example.domain.model.ThemeMode
import com.example.domain.model.TransactionFilterType
import com.example.domain.model.TransactionType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale

data class MainUiState(
    val isLoading: Boolean = true,
    val settings: BusinessSettings = BusinessSettings(),
    val metrics: DashboardMetrics = DashboardMetrics(),
    val allTransactions: List<TransactionEntity> = emptyList(),
    val filteredTransactions: List<TransactionEntity> = emptyList(),
    val recentTransactions: List<TransactionEntity> = emptyList(),
    val selectedChartType: AnalyticsChartType = AnalyticsChartType.DONUT,
    val breakdownType: TransactionType = TransactionType.EXPENSE,
    val breakdownPeriod: AnalyticsPeriod = AnalyticsPeriod.THIS_MONTH,
    val searchQuery: String = "",
    val filterType: TransactionFilterType = TransactionFilterType.ALL,
    val filterPeriod: AnalyticsPeriod = AnalyticsPeriod.ALL_TIME,
    val isAddTransactionSheetVisible: Boolean = false,
    val initialTransactionType: TransactionType = TransactionType.INCOME,
    val isSavingTransaction: Boolean = false,
    val addTransactionError: String? = null,
    val settingsMessage: String? = null,
    // Customer management
    val customers: List<CustomerEntity> = emptyList(),
    val filteredCustomers: List<CustomerEntity> = emptyList(),
    val customerSearchQuery: String = "",
    val isCustomerSheetVisible: Boolean = false,
    val editingCustomer: CustomerEntity? = null,
    val isSavingCustomer: Boolean = false,
    val customerError: String? = null,
    val selectedCustomerId: Long? = null,
    // Invoice management
    val invoices: List<InvoiceEntity> = emptyList(),
    val filteredInvoices: List<InvoiceEntity> = emptyList(),
    val invoiceItemsByInvoiceId: Map<Long, List<InvoiceItemEntity>> = emptyMap(),
    val paymentsByInvoiceId: Map<Long, List<PaymentEntity>> = emptyMap(),
    val invoiceSearchQuery: String = "",
    val invoiceStatusFilter: InvoiceFilterStatus = InvoiceFilterStatus.ALL,
    val isCreateInvoiceSheetVisible: Boolean = false,
    val preselectedCustomerId: Long? = null,
    val isSavingInvoice: Boolean = false,
    val invoiceError: String? = null,
    val selectedInvoiceId: Long? = null,
    val isRecordingPayment: Boolean = false,
    val paymentError: String? = null,
    val actionFeedbackMessage: String? = null
)

class MainViewModel(
    private val repository: BusinessRepository
) : ViewModel() {

    private data class InteractiveState(
        val isSheetVisible: Boolean = false,
        val initialType: TransactionType = TransactionType.INCOME,
        val isSaving: Boolean = false,
        val error: String? = null,
        val settingsMessage: String? = null,
        val selectedChartType: AnalyticsChartType = AnalyticsChartType.DONUT,
        val breakdownType: TransactionType = TransactionType.EXPENSE,
        val breakdownPeriod: AnalyticsPeriod = AnalyticsPeriod.THIS_MONTH,
        val searchQuery: String = "",
        val filterType: TransactionFilterType = TransactionFilterType.ALL,
        val filterPeriod: AnalyticsPeriod = AnalyticsPeriod.ALL_TIME,
        // Customers & Invoices interactive state
        val customerSearchQuery: String = "",
        val isCustomerSheetVisible: Boolean = false,
        val editingCustomer: CustomerEntity? = null,
        val isSavingCustomer: Boolean = false,
        val customerError: String? = null,
        val selectedCustomerId: Long? = null,
        val invoiceSearchQuery: String = "",
        val invoiceStatusFilter: InvoiceFilterStatus = InvoiceFilterStatus.ALL,
        val isCreateInvoiceSheetVisible: Boolean = false,
        val preselectedCustomerId: Long? = null,
        val isSavingInvoice: Boolean = false,
        val invoiceError: String? = null,
        val selectedInvoiceId: Long? = null,
        val isRecordingPayment: Boolean = false,
        val paymentError: String? = null,
        val actionFeedbackMessage: String? = null
    )

    private data class LedgerSnapshot(
        val transactions: List<TransactionEntity>,
        val outstandingMinor: Long,
        val customers: List<CustomerEntity>,
        val invoices: List<InvoiceEntity>
    )

    private data class InvoiceDetailsSnapshot(
        val items: List<InvoiceItemEntity>,
        val payments: List<PaymentEntity>,
        val settings: BusinessSettings
    )

    private val interactiveState = MutableStateFlow(InteractiveState())

    private val ledgerSnapshotFlow = combine(
        repository.allTransactions,
        repository.outstandingBalanceMinor,
        repository.allCustomers,
        repository.allInvoices
    ) { transactions, outstanding, customers, invoices ->
        LedgerSnapshot(
            transactions = transactions,
            outstandingMinor = outstanding,
            customers = customers,
            invoices = invoices
        )
    }

    private val invoiceDetailsSnapshotFlow = combine(
        repository.allInvoiceItems,
        repository.allPayments,
        repository.businessSettings
    ) { items, payments, settings ->
        InvoiceDetailsSnapshot(
            items = items,
            payments = payments,
            settings = settings
        )
    }

    val uiState: StateFlow<MainUiState> = combine(
        ledgerSnapshotFlow,
        invoiceDetailsSnapshotFlow,
        interactiveState
    ) { ledger, details, ui ->
        val transactions = ledger.transactions
        val customers = ledger.customers
        val invoices = ledger.invoices
        val settings = details.settings

        val metrics = FinancialCalculator.computeDashboardMetrics(
            transactions = transactions,
            outstandingInvoicesMinor = ledger.outstandingMinor,
            customersCount = customers.size,
            invoicesCount = invoices.size,
            breakdownType = ui.breakdownType,
            breakdownPeriod = ui.breakdownPeriod
        )

        val filteredTx = FinancialCalculator.filterTransactions(
            transactions = transactions,
            query = ui.searchQuery,
            typeFilter = ui.filterType,
            period = ui.filterPeriod
        )

        val customerQuery = ui.customerSearchQuery.trim().lowercase(Locale.ROOT)
        val filteredCustomers = if (customerQuery.isEmpty()) {
            customers
        } else {
            customers.filter { c ->
                c.name.lowercase(Locale.ROOT).contains(customerQuery) ||
                    c.phone.lowercase(Locale.ROOT).contains(customerQuery) ||
                    c.email.lowercase(Locale.ROOT).contains(customerQuery)
            }
        }

        val customersById = customers.associateBy { it.id }
        val invoiceQuery = ui.invoiceSearchQuery.trim().lowercase(Locale.ROOT)
        val filteredInvoices = invoices.filter { inv ->
            val matchesStatus = when (ui.invoiceStatusFilter) {
                InvoiceFilterStatus.ALL -> true
                InvoiceFilterStatus.SENT -> inv.status == InvoiceStatus.SENT || inv.status == InvoiceStatus.OVERDUE
                InvoiceFilterStatus.PARTIAL -> inv.status == InvoiceStatus.PARTIAL
                InvoiceFilterStatus.PAID -> inv.status == InvoiceStatus.PAID
                InvoiceFilterStatus.DRAFT -> inv.status == InvoiceStatus.DRAFT
            }
            val customerName = customersById[inv.customerId]?.name?.lowercase(Locale.ROOT).orEmpty()
            val matchesQuery = invoiceQuery.isEmpty() ||
                inv.invoiceNumber.lowercase(Locale.ROOT).contains(invoiceQuery) ||
                customerName.contains(invoiceQuery) ||
                inv.notes.lowercase(Locale.ROOT).contains(invoiceQuery)
            matchesStatus && matchesQuery
        }

        MainUiState(
            isLoading = false,
            settings = settings,
            metrics = metrics,
            allTransactions = transactions,
            filteredTransactions = filteredTx,
            recentTransactions = transactions.take(5),
            selectedChartType = ui.selectedChartType,
            breakdownType = ui.breakdownType,
            breakdownPeriod = ui.breakdownPeriod,
            searchQuery = ui.searchQuery,
            filterType = ui.filterType,
            filterPeriod = ui.filterPeriod,
            isAddTransactionSheetVisible = ui.isSheetVisible,
            initialTransactionType = ui.initialType,
            isSavingTransaction = ui.isSaving,
            addTransactionError = ui.error,
            settingsMessage = ui.settingsMessage,
            customers = customers,
            filteredCustomers = filteredCustomers,
            customerSearchQuery = ui.customerSearchQuery,
            isCustomerSheetVisible = ui.isCustomerSheetVisible,
            editingCustomer = ui.editingCustomer,
            isSavingCustomer = ui.isSavingCustomer,
            customerError = ui.customerError,
            selectedCustomerId = ui.selectedCustomerId,
            invoices = invoices,
            filteredInvoices = filteredInvoices,
            invoiceItemsByInvoiceId = details.items.groupBy { it.invoiceId },
            paymentsByInvoiceId = details.payments.groupBy { it.invoiceId },
            invoiceSearchQuery = ui.invoiceSearchQuery,
            invoiceStatusFilter = ui.invoiceStatusFilter,
            isCreateInvoiceSheetVisible = ui.isCreateInvoiceSheetVisible,
            preselectedCustomerId = ui.preselectedCustomerId,
            isSavingInvoice = ui.isSavingInvoice,
            invoiceError = ui.invoiceError,
            selectedInvoiceId = ui.selectedInvoiceId,
            isRecordingPayment = ui.isRecordingPayment,
            paymentError = ui.paymentError,
            actionFeedbackMessage = ui.actionFeedbackMessage
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = MainUiState()
    )

    // --- Transactions & Dashboard ---

    fun openAddTransactionSheet(initialType: TransactionType = TransactionType.INCOME) {
        interactiveState.update {
            it.copy(
                isSheetVisible = true,
                initialType = initialType,
                error = null
            )
        }
    }

    fun dismissAddTransactionSheet() {
        interactiveState.update { it.copy(isSheetVisible = false, isSaving = false, error = null) }
    }

    fun setChartType(chartType: AnalyticsChartType) {
        interactiveState.update { it.copy(selectedChartType = chartType) }
    }

    fun setBreakdownType(type: TransactionType) {
        interactiveState.update { it.copy(breakdownType = type) }
    }

    fun setBreakdownPeriod(period: AnalyticsPeriod) {
        interactiveState.update { it.copy(breakdownPeriod = period) }
    }

    fun toggleThemeMode(currentlyDark: Boolean) {
        val next = if (currentlyDark) ThemeMode.LIGHT else ThemeMode.DARK
        setThemeMode(next)
    }

    fun setThemeMode(themeMode: ThemeMode) {
        viewModelScope.launch {
            repository.setThemeMode(themeMode)
        }
    }

    fun updateSearchQuery(query: String) {
        interactiveState.update { it.copy(searchQuery = query) }
    }

    fun setFilterType(filterType: TransactionFilterType) {
        interactiveState.update { it.copy(filterType = filterType) }
    }

    fun setFilterPeriod(period: AnalyticsPeriod) {
        interactiveState.update { it.copy(filterPeriod = period) }
    }

    fun clearTransactionFilters() {
        interactiveState.update {
            it.copy(
                searchQuery = "",
                filterType = TransactionFilterType.ALL,
                filterPeriod = AnalyticsPeriod.ALL_TIME
            )
        }
    }

    fun recordTransaction(
        type: TransactionType,
        category: String,
        amountInput: String,
        description: String
    ) {
        if (interactiveState.value.isSaving) return
        interactiveState.update { it.copy(isSaving = true, error = null) }

        viewModelScope.launch {
            val result = repository.recordTransaction(
                type = type,
                category = category,
                amountInput = amountInput,
                description = description,
                currencyCode = uiState.value.settings.currencyCode
            )
            result.fold(
                onSuccess = {
                    interactiveState.update {
                        it.copy(isSheetVisible = false, isSaving = false, error = null)
                    }
                },
                onFailure = { throwable ->
                    interactiveState.update {
                        it.copy(
                            isSaving = false,
                            error = throwable.message ?: "Unable to save transaction."
                        )
                    }
                }
            )
        }
    }

    // --- Customers ---

    fun updateCustomerSearchQuery(query: String) {
        interactiveState.update { it.copy(customerSearchQuery = query) }
    }

    fun openAddCustomerSheet() {
        interactiveState.update {
            it.copy(
                isCustomerSheetVisible = true,
                editingCustomer = null,
                customerError = null
            )
        }
    }

    fun openEditCustomerSheet(customer: CustomerEntity) {
        interactiveState.update {
            it.copy(
                isCustomerSheetVisible = true,
                editingCustomer = customer,
                customerError = null
            )
        }
    }

    fun dismissCustomerSheet() {
        interactiveState.update {
            it.copy(
                isCustomerSheetVisible = false,
                editingCustomer = null,
                isSavingCustomer = false,
                customerError = null
            )
        }
    }

    fun openCustomerDetail(customerId: Long) {
        interactiveState.update { it.copy(selectedCustomerId = customerId) }
    }

    fun dismissCustomerDetail() {
        interactiveState.update { it.copy(selectedCustomerId = null) }
    }

    fun saveCustomer(
        name: String,
        phone: String,
        email: String,
        address: String,
        notes: String
    ) {
        if (interactiveState.value.isSavingCustomer) return
        val editing = interactiveState.value.editingCustomer
        interactiveState.update { it.copy(isSavingCustomer = true, customerError = null) }

        viewModelScope.launch {
            val result = repository.saveCustomer(
                existingCustomer = editing,
                name = name,
                phone = phone,
                email = email,
                address = address,
                notes = notes
            )
            result.fold(
                onSuccess = { savedId ->
                    interactiveState.update { state ->
                        state.copy(
                            isCustomerSheetVisible = false,
                            editingCustomer = null,
                            isSavingCustomer = false,
                            customerError = null,
                            preselectedCustomerId = if (state.isCreateInvoiceSheetVisible) savedId else state.preselectedCustomerId,
                            actionFeedbackMessage = if (editing == null) "Customer added" else "Customer updated"
                        )
                    }
                },
                onFailure = { throwable ->
                    interactiveState.update {
                        it.copy(
                            isSavingCustomer = false,
                            customerError = throwable.message ?: "Unable to save customer."
                        )
                    }
                }
            )
        }
    }

    fun quickAddCustomerForInvoice(
        name: String,
        phone: String,
        email: String,
        onCreated: (Long) -> Unit
    ) {
        if (interactiveState.value.isSavingCustomer) return
        interactiveState.update { it.copy(isSavingCustomer = true, invoiceError = null) }

        viewModelScope.launch {
            val result = repository.saveCustomer(
                existingCustomer = null,
                name = name,
                phone = phone,
                email = email,
                address = "",
                notes = ""
            )
            result.fold(
                onSuccess = { savedId ->
                    interactiveState.update {
                        it.copy(
                            isSavingCustomer = false,
                            preselectedCustomerId = savedId,
                            actionFeedbackMessage = "Customer added & selected"
                        )
                    }
                    onCreated(savedId)
                },
                onFailure = { throwable ->
                    interactiveState.update {
                        it.copy(
                            isSavingCustomer = false,
                            invoiceError = throwable.message ?: "Unable to add customer."
                        )
                    }
                }
            )
        }
    }

    fun deleteCustomer(customerId: Long) {
        viewModelScope.launch {
            val result = repository.deleteCustomer(customerId)
            result.fold(
                onSuccess = {
                    interactiveState.update {
                        it.copy(
                            selectedCustomerId = null,
                            actionFeedbackMessage = "Customer removed"
                        )
                    }
                },
                onFailure = { err ->
                    interactiveState.update {
                        it.copy(actionFeedbackMessage = err.message ?: "Unable to delete customer.")
                    }
                }
            )
        }
    }

    // --- Invoices & Payments ---

    fun updateInvoiceSearchQuery(query: String) {
        interactiveState.update { it.copy(invoiceSearchQuery = query) }
    }

    fun setInvoiceStatusFilter(status: InvoiceFilterStatus) {
        interactiveState.update { it.copy(invoiceStatusFilter = status) }
    }

    fun clearInvoiceFilters() {
        interactiveState.update {
            it.copy(invoiceSearchQuery = "", invoiceStatusFilter = InvoiceFilterStatus.ALL)
        }
    }

    fun openCreateInvoiceSheet(preselectedCustomerId: Long? = null) {
        interactiveState.update {
            it.copy(
                isCreateInvoiceSheetVisible = true,
                preselectedCustomerId = preselectedCustomerId,
                invoiceError = null
            )
        }
    }

    fun dismissCreateInvoiceSheet() {
        interactiveState.update {
            it.copy(
                isCreateInvoiceSheetVisible = false,
                preselectedCustomerId = null,
                isSavingInvoice = false,
                invoiceError = null
            )
        }
    }

    fun openInvoiceDetail(invoiceId: Long) {
        interactiveState.update {
            it.copy(
                selectedInvoiceId = invoiceId,
                paymentError = null
            )
        }
    }

    fun dismissInvoiceDetail() {
        interactiveState.update {
            it.copy(
                selectedInvoiceId = null,
                isRecordingPayment = false,
                paymentError = null
            )
        }
    }

    fun createInvoice(
        customerId: Long,
        lineInputs: List<InvoiceLineInput>,
        discountInput: String,
        taxPercentInput: String,
        dueDaysFromNow: Int,
        issueImmediately: Boolean,
        notes: String
    ) {
        if (interactiveState.value.isSavingInvoice) return
        interactiveState.update { it.copy(isSavingInvoice = true, invoiceError = null) }

        viewModelScope.launch {
            val result = repository.createInvoice(
                customerId = customerId,
                lineInputs = lineInputs,
                discountInput = discountInput,
                taxPercentInput = taxPercentInput,
                dueDaysFromNow = dueDaysFromNow,
                issueImmediately = issueImmediately,
                notes = notes
            )
            result.fold(
                onSuccess = { invoiceId ->
                    interactiveState.update {
                        it.copy(
                            isCreateInvoiceSheetVisible = false,
                            preselectedCustomerId = null,
                            isSavingInvoice = false,
                            invoiceError = null,
                            selectedInvoiceId = invoiceId,
                            actionFeedbackMessage = if (issueImmediately) "Invoice created & issued" else "Invoice saved as draft"
                        )
                    }
                },
                onFailure = { err ->
                    interactiveState.update {
                        it.copy(
                            isSavingInvoice = false,
                            invoiceError = err.message ?: "Unable to create invoice."
                        )
                    }
                }
            )
        }
    }

    fun markInvoiceSent(invoiceId: Long) {
        viewModelScope.launch {
            repository.markInvoiceSent(invoiceId).fold(
                onSuccess = {
                    interactiveState.update {
                        it.copy(actionFeedbackMessage = "Invoice marked as Sent")
                    }
                },
                onFailure = { err ->
                    interactiveState.update {
                        it.copy(actionFeedbackMessage = err.message ?: "Unable to update invoice.")
                    }
                }
            )
        }
    }

    fun recordInvoicePayment(
        invoiceId: Long,
        amountInput: String,
        method: String,
        reference: String
    ) {
        if (interactiveState.value.isRecordingPayment) return
        interactiveState.update { it.copy(isRecordingPayment = true, paymentError = null) }

        viewModelScope.launch {
            repository.recordInvoicePayment(
                invoiceId = invoiceId,
                amountInput = amountInput,
                method = method,
                reference = reference
            ).fold(
                onSuccess = {
                    interactiveState.update {
                        it.copy(
                            isRecordingPayment = false,
                            paymentError = null,
                            actionFeedbackMessage = "Payment recorded & income updated"
                        )
                    }
                },
                onFailure = { err ->
                    interactiveState.update {
                        it.copy(
                            isRecordingPayment = false,
                            paymentError = err.message ?: "Unable to record payment."
                        )
                    }
                }
            )
        }
    }

    fun deleteInvoice(invoiceId: Long) {
        viewModelScope.launch {
            repository.deleteInvoice(invoiceId).fold(
                onSuccess = {
                    interactiveState.update {
                        it.copy(
                            selectedInvoiceId = null,
                            actionFeedbackMessage = "Invoice deleted"
                        )
                    }
                },
                onFailure = { err ->
                    interactiveState.update {
                        it.copy(actionFeedbackMessage = err.message ?: "Unable to delete invoice.")
                    }
                }
            )
        }
    }

    // --- Settings & Feedback ---

    fun saveSettings(updated: BusinessSettings) {
        viewModelScope.launch {
            val result = repository.saveBusinessSettings(updated)
            result.fold(
                onSuccess = {
                    interactiveState.update { it.copy(settingsMessage = "Settings saved locally") }
                },
                onFailure = { err ->
                    interactiveState.update {
                        it.copy(settingsMessage = err.message ?: "Invalid settings")
                    }
                }
            )
        }
    }

    fun clearSettingsMessage() {
        interactiveState.update { it.copy(settingsMessage = null) }
    }

    fun showActionFeedback(message: String) {
        interactiveState.update { it.copy(actionFeedbackMessage = message) }
    }

    fun clearActionFeedback() {
        interactiveState.update { it.copy(actionFeedbackMessage = null) }
    }

    class Factory(private val repository: BusinessRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                return MainViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
