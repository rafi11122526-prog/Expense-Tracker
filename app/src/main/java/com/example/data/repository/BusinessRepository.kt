package com.example.data.repository

import android.database.sqlite.SQLiteConstraintException
import com.example.data.local.CustomerDao
import com.example.data.local.CustomerEntity
import com.example.data.local.InvoiceDao
import com.example.data.local.InvoiceEntity
import com.example.data.local.InvoiceItemEntity
import com.example.data.local.PaymentEntity
import com.example.data.local.ReceiptDao
import com.example.data.local.SettingsDataStore
import com.example.data.local.TransactionDao
import com.example.data.local.TransactionEntity
import com.example.domain.calculator.FinancialCalculator
import com.example.domain.model.BusinessSettings
import com.example.domain.model.FeatureEntitlement
import com.example.domain.model.InvoiceLineInput
import com.example.domain.model.InvoiceStatus
import com.example.domain.model.ThemeMode
import com.example.domain.model.TransactionType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.util.UUID

class BusinessRepository(
    private val transactionDao: TransactionDao,
    private val customerDao: CustomerDao,
    private val invoiceDao: InvoiceDao,
    private val receiptDao: ReceiptDao,
    private val settingsDataStore: SettingsDataStore
) {
    val allTransactions: Flow<List<TransactionEntity>> = transactionDao.observeAllTransactions()
    val recentTransactions: Flow<List<TransactionEntity>> = transactionDao.observeRecentTransactions(limit = 10)
    val transactionCount: Flow<Int> = transactionDao.observeTransactionCount()
    val allCustomers: Flow<List<CustomerEntity>> = customerDao.observeAllCustomers()
    val customerCount: Flow<Int> = customerDao.observeCustomerCount()
    val allInvoices: Flow<List<InvoiceEntity>> = invoiceDao.observeAllInvoices()
    val allInvoiceItems: Flow<List<InvoiceItemEntity>> = invoiceDao.observeAllInvoiceItems()
    val allPayments: Flow<List<PaymentEntity>> = invoiceDao.observeAllPayments()
    val invoiceCount: Flow<Int> = invoiceDao.observeInvoiceCount()
    val outstandingBalanceMinor: Flow<Long> = invoiceDao.observeOutstandingBalanceMinor()
    val receiptCount: Flow<Int> = receiptDao.observeReceiptCount()
    val businessSettings: Flow<BusinessSettings> = settingsDataStore.settingsFlow

    suspend fun recordTransaction(
        type: TransactionType,
        category: String,
        amountInput: String,
        description: String,
        currencyCode: String? = null,
        transactionDateEpochMs: Long = System.currentTimeMillis(),
        clientUuid: String = UUID.randomUUID().toString()
    ): Result<Long> {
        val amountMinor = FinancialCalculator.parsePositiveAmountToMinorUnits(amountInput)
            ?: return Result.failure(IllegalArgumentException("Enter a valid positive amount (max 2 decimals)."))

        val cleanCategory = category.trim()
        if (cleanCategory.isEmpty()) {
            return Result.failure(IllegalArgumentException("Select a transaction category."))
        }

        val resolvedCurrency = currencyCode?.takeIf { it.isNotBlank() }
            ?: settingsDataStore.settingsFlow.first().currencyCode
        val now = System.currentTimeMillis()
        val entity = TransactionEntity(
            clientUuid = clientUuid,
            type = type,
            category = cleanCategory,
            amountMinor = amountMinor,
            currencyCode = resolvedCurrency,
            description = description.trim(),
            transactionDateEpochMs = transactionDateEpochMs,
            createdAtEpochMs = now,
            updatedAtEpochMs = now
        )
        return runCatching { transactionDao.insertTransaction(entity) }
    }

    suspend fun deleteTransaction(id: Long): Result<Unit> =
        runCatching { transactionDao.deleteTransactionById(id) }

    suspend fun saveCustomer(
        existingCustomer: CustomerEntity? = null,
        name: String,
        phone: String,
        email: String,
        address: String,
        notes: String
    ): Result<Long> {
        val cleanName = name.trim()
        if (cleanName.isEmpty()) {
            return Result.failure(IllegalArgumentException("Customer name is required."))
        }
        val now = System.currentTimeMillis()
        return runCatching {
            if (existingCustomer == null) {
                customerDao.insertCustomer(
                    CustomerEntity(
                        name = cleanName,
                        phone = phone.trim(),
                        email = email.trim(),
                        address = address.trim(),
                        notes = notes.trim(),
                        createdAtEpochMs = now,
                        updatedAtEpochMs = now
                    )
                )
            } else {
                customerDao.updateCustomer(
                    existingCustomer.copy(
                        name = cleanName,
                        phone = phone.trim(),
                        email = email.trim(),
                        address = address.trim(),
                        notes = notes.trim(),
                        updatedAtEpochMs = now
                    )
                )
                existingCustomer.id
            }
        }
    }

    suspend fun deleteCustomer(customerId: Long): Result<Unit> {
        return try {
            customerDao.deleteCustomerById(customerId)
            Result.success(Unit)
        } catch (_: SQLiteConstraintException) {
            Result.failure(
                IllegalStateException("Cannot delete this customer because they have linked invoices.")
            )
        } catch (t: Throwable) {
            Result.failure(t)
        }
    }

    suspend fun createInvoice(
        customerId: Long,
        lineInputs: List<InvoiceLineInput>,
        discountInput: String,
        taxPercentInput: String,
        dueDaysFromNow: Int = 7,
        issueImmediately: Boolean = true,
        notes: String = ""
    ): Result<Long> {
        if (customerId <= 0L) {
            return Result.failure(IllegalArgumentException("Select a customer for this invoice."))
        }
        if (lineInputs.isEmpty()) {
            return Result.failure(IllegalArgumentException("Add at least one product or service item."))
        }
        val cleanLines = lineInputs.map { line ->
            val desc = line.description.trim()
            if (desc.isEmpty()) {
                return Result.failure(IllegalArgumentException("Every item needs a description."))
            }
            if (line.quantityMilli <= 0L) {
                return Result.failure(IllegalArgumentException("Quantity must be greater than zero."))
            }
            if (line.unitPriceMinor <= 0L) {
                return Result.failure(IllegalArgumentException("Unit price must be greater than zero."))
            }
            val lineTotal = FinancialCalculator.calculateLineTotalMinor(
                quantityMilli = line.quantityMilli,
                unitPriceMinor = line.unitPriceMinor
            )
            Triple(desc, line, lineTotal)
        }

        val discountMinor = FinancialCalculator.parseNonNegativeAmountToMinorUnits(discountInput)
            ?: return Result.failure(IllegalArgumentException("Enter a valid non-negative discount."))
        val taxBasisPoints = FinancialCalculator.parseTaxPercentToBasisPoints(taxPercentInput)
            ?: return Result.failure(IllegalArgumentException("Tax rate must be between 0% and 100%."))

        val totals = FinancialCalculator.calculateInvoiceTotals(
            lineTotalsMinor = cleanLines.map { it.third },
            discountMinor = discountMinor,
            taxRateBasisPoints = taxBasisPoints
        )

        return runCatching {
            val currentSettings = settingsDataStore.settingsFlow.first()
            val existingNumbers = invoiceDao.observeAllInvoices().first().map { it.invoiceNumber }.toSet()
            var candidateNum = currentSettings.nextInvoiceNumber.coerceAtLeast(1)
            var invoiceNumber = "${currentSettings.invoicePrefix}$candidateNum"
            while (invoiceNumber in existingNumbers) {
                candidateNum += 1
                invoiceNumber = "${currentSettings.invoicePrefix}$candidateNum"
            }

            val now = System.currentTimeMillis()
            val dueEpochMs = now + dueDaysFromNow.coerceAtLeast(0) * 86_400_000L
            val invoiceId = invoiceDao.insertInvoice(
                InvoiceEntity(
                    invoiceNumber = invoiceNumber,
                    customerId = customerId,
                    issueDateEpochMs = now,
                    dueDateEpochMs = dueEpochMs,
                    subtotalMinor = totals.subtotalMinor,
                    discountMinor = totals.discountMinor,
                    taxRateBasisPoints = taxBasisPoints,
                    taxAmountMinor = totals.taxAmountMinor,
                    totalMinor = totals.totalMinor,
                    paidAmountMinor = 0L,
                    status = if (issueImmediately) InvoiceStatus.SENT else InvoiceStatus.DRAFT,
                    notes = notes.trim(),
                    createdAtEpochMs = now,
                    updatedAtEpochMs = now
                )
            )
            invoiceDao.insertInvoiceItems(
                cleanLines.map { (desc, input, lineTotal) ->
                    InvoiceItemEntity(
                        invoiceId = invoiceId,
                        description = desc,
                        quantityMilli = input.quantityMilli,
                        unitPriceMinor = input.unitPriceMinor,
                        lineTotalMinor = lineTotal
                    )
                }
            )
            settingsDataStore.updateBusinessSettings(
                currentSettings.copy(nextInvoiceNumber = candidateNum + 1)
            )
            invoiceId
        }
    }

    suspend fun markInvoiceSent(invoiceId: Long): Result<Unit> = runCatching {
        val invoice = invoiceDao.getInvoiceById(invoiceId)
            ?: throw IllegalArgumentException("Invoice not found.")
        if (invoice.status == InvoiceStatus.DRAFT) {
            invoiceDao.updateInvoice(
                invoice.copy(
                    status = InvoiceStatus.SENT,
                    updatedAtEpochMs = System.currentTimeMillis()
                )
            )
        }
    }

    suspend fun recordInvoicePayment(
        invoiceId: Long,
        amountInput: String,
        method: String,
        reference: String
    ): Result<Unit> {
        val paymentMinor = FinancialCalculator.parsePositiveAmountToMinorUnits(amountInput)
            ?: return Result.failure(IllegalArgumentException("Enter a valid positive payment amount."))

        return runCatching {
            val invoice = invoiceDao.getInvoiceById(invoiceId)
                ?: throw IllegalArgumentException("Invoice not found.")
            val remaining = (invoice.totalMinor - invoice.paidAmountMinor).coerceAtLeast(0L)
            if (remaining <= 0L) {
                throw IllegalStateException("This invoice is already paid in full.")
            }
            if (paymentMinor > remaining) {
                val settings = settingsDataStore.settingsFlow.first()
                val remainingStr = FinancialCalculator.formatCurrency(remaining, settings.currencyCode)
                throw IllegalArgumentException("Payment exceeds remaining balance ($remainingStr).")
            }

            val now = System.currentTimeMillis()
            val newPaid = invoice.paidAmountMinor + paymentMinor
            val newStatus = if (newPaid >= invoice.totalMinor) InvoiceStatus.PAID else InvoiceStatus.PARTIAL

            invoiceDao.insertPayment(
                PaymentEntity(
                    invoiceId = invoice.id,
                    customerId = invoice.customerId,
                    amountMinor = paymentMinor,
                    paymentDateEpochMs = now,
                    method = method.ifBlank { "Cash" },
                    reference = reference.trim(),
                    createdAtEpochMs = now
                )
            )
            invoiceDao.updateInvoice(
                invoice.copy(
                    paidAmountMinor = newPaid,
                    status = newStatus,
                    updatedAtEpochMs = now
                )
            )

            val settings = settingsDataStore.settingsFlow.first()
            transactionDao.insertTransaction(
                TransactionEntity(
                    type = TransactionType.INCOME,
                    category = "Invoice Payment",
                    amountMinor = paymentMinor,
                    currencyCode = settings.currencyCode,
                    description = "Payment for ${invoice.invoiceNumber}",
                    transactionDateEpochMs = now,
                    customerId = invoice.customerId,
                    invoiceId = invoice.id,
                    createdAtEpochMs = now,
                    updatedAtEpochMs = now
                )
            )
        }
    }

    suspend fun deleteInvoice(invoiceId: Long): Result<Unit> = runCatching {
        invoiceDao.deleteInvoiceById(invoiceId)
    }

    suspend fun saveBusinessSettings(settings: BusinessSettings): Result<Unit> {
        val taxVal = settings.defaultTaxRatePercent.trim().toBigDecimalOrNull()
        if (taxVal == null || taxVal.signum() < 0 || taxVal.toDouble() > 100.0) {
            return Result.failure(IllegalArgumentException("Tax rate must be between 0 and 100."))
        }
        return runCatching { settingsDataStore.updateBusinessSettings(settings) }
    }

    suspend fun setThemeMode(themeMode: ThemeMode): Result<Unit> =
        runCatching { settingsDataStore.updateThemeMode(themeMode) }

    fun hasEntitlement(settings: BusinessSettings, feature: FeatureEntitlement): Boolean {
        return settings.subscriptionTier.ordinal >= feature.requiredTier.ordinal
    }
}
