package com.example.domain.model

enum class TransactionType {
    INCOME,
    EXPENSE
}

enum class InvoiceStatus {
    DRAFT,
    SENT,
    PARTIAL,
    PAID,
    OVERDUE
}

enum class SyncStatus {
    LOCAL_ONLY,
    PENDING_SYNC,
    SYNCED
}

enum class ThemeMode(val label: String) {
    SYSTEM("System"),
    LIGHT("Light"),
    DARK("Dark")
}

enum class AnalyticsChartType(val label: String) {
    DONUT("Donut"),
    PIE("Pie"),
    BARS("Bars"),
    TREND("Trend")
}

enum class SubscriptionTier(val displayName: String) {
    FREE("Free"),
    PREMIUM("Premium"),
    PRO("Pro")
}

enum class FeatureEntitlement(val requiredTier: SubscriptionTier) {
    BASIC_LEDGER(SubscriptionTier.FREE),
    BASIC_INVOICING(SubscriptionTier.FREE),
    ADVANCED_REPORTS(SubscriptionTier.PREMIUM),
    UNLIMITED_HISTORY(SubscriptionTier.PREMIUM),
    PDF_CSV_EXPORTS(SubscriptionTier.PREMIUM),
    RECEIPT_MANAGEMENT(SubscriptionTier.PREMIUM),
    CLOUD_BACKUP(SubscriptionTier.PRO),
    MULTI_DEVICE_SYNC(SubscriptionTier.PRO)
}

enum class AnalyticsPeriod(val label: String) {
    THIS_WEEK("This Week"),
    THIS_MONTH("This Month"),
    ALL_TIME("All Time")
}

enum class TransactionFilterType(val label: String) {
    ALL("All"),
    INCOME("Income"),
    EXPENSE("Expenses")
}

enum class InvoiceFilterStatus(val label: String) {
    ALL("All"),
    SENT("Sent"),
    PARTIAL("Partial"),
    PAID("Paid"),
    DRAFT("Draft")
}

data class InvoiceLineInput(
    val description: String,
    val quantityMilli: Long,
    val unitPriceMinor: Long
)

object TransactionCategories {
    val incomeCategories = listOf(
        "Sales",
        "Services",
        "Consulting",
        "Invoice Payment",
        "Other Income"
    )

    val expenseCategories = listOf(
        "Inventory",
        "Rent & Utilities",
        "Salaries",
        "Marketing",
        "Transport",
        "Equipment",
        "Taxes",
        "Other Expense"
    )

    fun forType(type: TransactionType): List<String> = when (type) {
        TransactionType.INCOME -> incomeCategories
        TransactionType.EXPENSE -> expenseCategories
    }
}

data class BusinessSettings(
    val businessName: String = "PocketBiz",
    val businessPhone: String = "",
    val businessEmail: String = "",
    val businessAddress: String = "",
    val currencyCode: String = "PKR",
    val dateFormatPattern: String = "dd MMM yyyy",
    val invoicePrefix: String = "INV-",
    val nextInvoiceNumber: Int = 1001,
    val defaultTaxRatePercent: String = "0.00",
    val cloudBackupEnabled: Boolean = false,
    val subscriptionTier: SubscriptionTier = SubscriptionTier.FREE,
    val themeMode: ThemeMode = ThemeMode.LIGHT
)

data class MonthlyCashFlowPoint(
    val monthLabel: String,
    val revenueMinor: Long,
    val expenseMinor: Long
) {
    val netMinor: Long get() = revenueMinor - expenseMinor
}

data class CategoryBreakdownSlice(
    val category: String,
    val amountMinor: Long,
    val percentageLabel: String,
    val shareFraction: Float,
    val colorIndex: Int
)

data class DashboardMetrics(
    val todaySalesMinor: Long = 0L,
    val todayExpensesMinor: Long = 0L,
    val monthlyRevenueMinor: Long = 0L,
    val monthlyExpensesMinor: Long = 0L,
    val monthlyNetProfitMinor: Long = 0L,
    val totalAllTimeBalanceMinor: Long = 0L,
    val outstandingCustomerPaymentsMinor: Long = 0L,
    val profitMarginPercent: String = "0.0%",
    val expenseRatioPercent: String = "0.0%",
    val monthlySeries: List<MonthlyCashFlowPoint> = emptyList(),
    val categoryBreakdown: List<CategoryBreakdownSlice> = emptyList(),
    val categoryBreakdownTotalMinor: Long = 0L,
    val topCategoryName: String? = null,
    val totalTransactionsCount: Int = 0,
    val totalCustomersCount: Int = 0,
    val totalInvoicesCount: Int = 0
)
