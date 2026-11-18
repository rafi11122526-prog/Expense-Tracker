package com.example.domain.calculator

import com.example.data.local.TransactionEntity
import com.example.domain.model.AnalyticsPeriod
import com.example.domain.model.CategoryBreakdownSlice
import com.example.domain.model.DashboardMetrics
import com.example.domain.model.MonthlyCashFlowPoint
import com.example.domain.model.TransactionFilterType
import com.example.domain.model.TransactionType
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

/**
 * Decimal-precise financial calculation engine.
 * All monetary amounts are stored as Long minor units (1/100th of currency unit, e.g., paisa/cents)
 * and converted to BigDecimal for arithmetic and formatting to avoid floating-point rounding drift.
 */
object FinancialCalculator {

    private val HUNDRED = BigDecimal(100)
    private val ZERO = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP)

    fun parsePositiveAmountToMinorUnits(rawInput: String): Long? {
        val cleaned = rawInput.trim().replace(",", "")
        if (cleaned.isEmpty()) return null
        val decimal = cleaned.toBigDecimalOrNull() ?: return null
        if (decimal <= BigDecimal.ZERO) return null
        if (decimal.scale() > 2) {
            val stripped = decimal.stripTrailingZeros()
            if (stripped.scale() > 2) return null
        }
        return try {
            decimal.multiply(HUNDRED).setScale(0, RoundingMode.UNNECESSARY).longValueExact()
        } catch (_: ArithmeticException) {
            null
        }
    }

    fun parseNonNegativeAmountToMinorUnits(rawInput: String): Long? {
        val cleaned = rawInput.trim().replace(",", "")
        if (cleaned.isEmpty()) return 0L
        val decimal = cleaned.toBigDecimalOrNull() ?: return null
        if (decimal < BigDecimal.ZERO) return null
        if (decimal.scale() > 2) {
            val stripped = decimal.stripTrailingZeros()
            if (stripped.scale() > 2) return null
        }
        return try {
            decimal.multiply(HUNDRED).setScale(0, RoundingMode.UNNECESSARY).longValueExact()
        } catch (_: ArithmeticException) {
            null
        }
    }

    fun parsePositiveQuantityMilli(rawInput: String): Long? {
        val cleaned = rawInput.trim().replace(",", "")
        if (cleaned.isEmpty()) return null
        val decimal = cleaned.toBigDecimalOrNull() ?: return null
        if (decimal <= BigDecimal.ZERO) return null
        if (decimal.scale() > 3) {
            val stripped = decimal.stripTrailingZeros()
            if (stripped.scale() > 3) return null
        }
        return try {
            decimal.multiply(BigDecimal(1000)).setScale(0, RoundingMode.UNNECESSARY).longValueExact()
        } catch (_: ArithmeticException) {
            null
        }
    }

    fun parseTaxPercentToBasisPoints(rawInput: String): Int? {
        val cleaned = rawInput.trim().replace(",", "")
        if (cleaned.isEmpty()) return 0
        val decimal = cleaned.toBigDecimalOrNull() ?: return null
        if (decimal < BigDecimal.ZERO || decimal > HUNDRED) return null
        return try {
            decimal.multiply(HUNDRED).setScale(0, RoundingMode.HALF_UP).intValueExact()
        } catch (_: ArithmeticException) {
            null
        }
    }

    fun calculateLineTotalMinor(quantityMilli: Long, unitPriceMinor: Long): Long {
        val qty = BigDecimal.valueOf(quantityMilli, 3)
        val price = minorUnitsToBigDecimal(unitPriceMinor)
        return qty.multiply(price)
            .multiply(HUNDRED)
            .setScale(0, RoundingMode.HALF_UP)
            .longValueExact()
    }

    data class InvoiceTotals(
        val subtotalMinor: Long,
        val discountMinor: Long,
        val taxAmountMinor: Long,
        val totalMinor: Long
    )

    fun calculateInvoiceTotals(
        lineTotalsMinor: List<Long>,
        discountMinor: Long,
        taxRateBasisPoints: Int
    ): InvoiceTotals {
        val subtotal = lineTotalsMinor.fold(0L) { acc, item -> acc + item.coerceAtLeast(0L) }
        val clampedDiscount = discountMinor.coerceIn(0L, subtotal)
        val taxableMinor = (subtotal - clampedDiscount).coerceAtLeast(0L)
        val taxableDecimal = minorUnitsToBigDecimal(taxableMinor)
        val taxRateFraction = BigDecimal(taxRateBasisPoints.coerceAtLeast(0))
            .divide(BigDecimal(10000), 6, RoundingMode.HALF_UP)
        val taxMinor = taxableDecimal.multiply(taxRateFraction)
            .multiply(HUNDRED)
            .setScale(0, RoundingMode.HALF_UP)
            .longValueExact()
        val totalMinor = taxableMinor + taxMinor
        return InvoiceTotals(
            subtotalMinor = subtotal,
            discountMinor = clampedDiscount,
            taxAmountMinor = taxMinor,
            totalMinor = totalMinor
        )
    }

    fun formatQuantity(quantityMilli: Long): String {
        val dec = BigDecimal.valueOf(quantityMilli, 3).stripTrailingZeros()
        return dec.toPlainString()
    }

    fun minorUnitsToBigDecimal(minorUnits: Long): BigDecimal =
        BigDecimal.valueOf(minorUnits, 2)

    fun calculateNetProfit(totalIncomeMinor: Long, totalExpensesMinor: Long): Long {
        val income = minorUnitsToBigDecimal(totalIncomeMinor)
        val expenses = minorUnitsToBigDecimal(totalExpensesMinor)
        return income.subtract(expenses)
            .multiply(HUNDRED)
            .setScale(0, RoundingMode.HALF_UP)
            .longValueExact()
    }

    fun calculateOutstandingBalance(totalInvoiceMinor: Long, totalPaymentsReceivedMinor: Long): Long {
        val total = minorUnitsToBigDecimal(totalInvoiceMinor)
        val paid = minorUnitsToBigDecimal(totalPaymentsReceivedMinor)
        val balance = total.subtract(paid).max(ZERO)
        return balance.multiply(HUNDRED)
            .setScale(0, RoundingMode.HALF_UP)
            .longValueExact()
    }

    fun calculateMarginPercent(netProfitMinor: Long, revenueMinor: Long): BigDecimal {
        if (revenueMinor <= 0L) return BigDecimal.ZERO.setScale(1, RoundingMode.HALF_UP)
        val net = minorUnitsToBigDecimal(netProfitMinor)
        val rev = minorUnitsToBigDecimal(revenueMinor)
        return net.multiply(HUNDRED)
            .divide(rev, 1, RoundingMode.HALF_UP)
    }

    fun calculateExpenseRatioPercent(expensesMinor: Long, revenueMinor: Long): BigDecimal {
        if (revenueMinor <= 0L) return BigDecimal.ZERO.setScale(1, RoundingMode.HALF_UP)
        val exp = minorUnitsToBigDecimal(expensesMinor)
        val rev = minorUnitsToBigDecimal(revenueMinor)
        return exp.multiply(HUNDRED)
            .divide(rev, 1, RoundingMode.HALF_UP)
    }

    fun formatCurrency(
        minorUnits: Long,
        currencyCode: String,
        includePlusSign: Boolean = false
    ): String {
        val amount = minorUnitsToBigDecimal(minorUnits)
        val absAmount = amount.abs()
        val symbols = DecimalFormatSymbols(Locale.US)
        val formatter = DecimalFormat("#,##0.00", symbols)
        val formattedNumber = formatter.format(absAmount)
        val sign = when {
            minorUnits < 0L -> "-"
            minorUnits > 0L && includePlusSign -> "+"
            else -> ""
        }
        val prefix = when (currencyCode.uppercase(Locale.ROOT)) {
            "PKR" -> "PKR "
            "USD" -> "$"
            "EUR" -> "€"
            "GBP" -> "£"
            "AED" -> "AED "
            "SAR" -> "SAR "
            else -> "$currencyCode "
        }
        return "$sign$prefix$formattedNumber"
    }

    fun filterTransactions(
        transactions: List<TransactionEntity>,
        query: String,
        typeFilter: TransactionFilterType,
        period: AnalyticsPeriod,
        nowEpochMs: Long = System.currentTimeMillis(),
        timeZone: TimeZone = TimeZone.getDefault()
    ): List<TransactionEntity> {
        val range = periodRangeEpochMs(period, nowEpochMs, timeZone)
        val cleanQuery = query.trim().lowercase(Locale.ROOT)

        return transactions.filter { tx ->
            val matchesType = when (typeFilter) {
                TransactionFilterType.ALL -> true
                TransactionFilterType.INCOME -> tx.type == TransactionType.INCOME
                TransactionFilterType.EXPENSE -> tx.type == TransactionType.EXPENSE
            }
            val matchesPeriod = range == null || tx.transactionDateEpochMs in range
            val matchesQuery = cleanQuery.isEmpty() ||
                tx.category.lowercase(Locale.ROOT).contains(cleanQuery) ||
                tx.description.lowercase(Locale.ROOT).contains(cleanQuery)
            matchesType && matchesPeriod && matchesQuery
        }
    }

    fun computeDashboardMetrics(
        transactions: List<TransactionEntity>,
        outstandingInvoicesMinor: Long,
        customersCount: Int,
        invoicesCount: Int,
        breakdownType: TransactionType = TransactionType.EXPENSE,
        breakdownPeriod: AnalyticsPeriod = AnalyticsPeriod.THIS_MONTH,
        nowEpochMs: Long = System.currentTimeMillis(),
        timeZone: TimeZone = TimeZone.getDefault()
    ): DashboardMetrics {
        val todayStart = startOfDayEpochMs(nowEpochMs, timeZone)
        val todayEnd = endOfDayEpochMs(nowEpochMs, timeZone)
        val currentMonthStart = startOfMonthEpochMs(nowEpochMs, timeZone, monthOffset = 0)
        val currentMonthEnd = startOfMonthEpochMs(nowEpochMs, timeZone, monthOffset = 1) - 1L

        var todaySales = 0L
        var todayExpenses = 0L
        var monthlyRevenue = 0L
        var monthlyExpenses = 0L
        var allTimeIncome = 0L
        var allTimeExpenses = 0L

        for (tx in transactions) {
            val ts = tx.transactionDateEpochMs
            when (tx.type) {
                TransactionType.INCOME -> allTimeIncome += tx.amountMinor
                TransactionType.EXPENSE -> allTimeExpenses += tx.amountMinor
            }
            if (ts in todayStart..todayEnd) {
                when (tx.type) {
                    TransactionType.INCOME -> todaySales += tx.amountMinor
                    TransactionType.EXPENSE -> todayExpenses += tx.amountMinor
                }
            }
            if (ts in currentMonthStart..currentMonthEnd) {
                when (tx.type) {
                    TransactionType.INCOME -> monthlyRevenue += tx.amountMinor
                    TransactionType.EXPENSE -> monthlyExpenses += tx.amountMinor
                }
            }
        }

        val monthlyNetProfit = calculateNetProfit(monthlyRevenue, monthlyExpenses)
        val totalAllTimeBalance = calculateNetProfit(allTimeIncome, allTimeExpenses)
        val marginPct = calculateMarginPercent(monthlyNetProfit, monthlyRevenue)
        val expenseRatioPct = calculateExpenseRatioPercent(monthlyExpenses, monthlyRevenue)

        val monthLabels = arrayOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
        val monthlySeries = (-5..0).map { offset ->
            val mStart = startOfMonthEpochMs(nowEpochMs, timeZone, monthOffset = offset)
            val mEnd = startOfMonthEpochMs(nowEpochMs, timeZone, monthOffset = offset + 1) - 1L
            val cal = Calendar.getInstance(timeZone).apply { timeInMillis = mStart }
            val label = monthLabels[cal.get(Calendar.MONTH)]
            var rev = 0L
            var exp = 0L
            for (tx in transactions) {
                if (tx.transactionDateEpochMs in mStart..mEnd) {
                    when (tx.type) {
                        TransactionType.INCOME -> rev += tx.amountMinor
                        TransactionType.EXPENSE -> exp += tx.amountMinor
                    }
                }
            }
            MonthlyCashFlowPoint(monthLabel = label, revenueMinor = rev, expenseMinor = exp)
        }

        // Category breakdown for the donut chart
        val breakdownRange = periodRangeEpochMs(breakdownPeriod, nowEpochMs, timeZone)
        val filteredForBreakdown = transactions.filter { tx ->
            tx.type == breakdownType && (breakdownRange == null || tx.transactionDateEpochMs in breakdownRange)
        }
        val groupedTotals = filteredForBreakdown
            .groupBy { it.category }
            .mapValues { (_, list) -> list.sumOf { it.amountMinor } }
            .entries
            .sortedByDescending { it.value }

        val breakdownTotalMinor = groupedTotals.sumOf { it.value }
        val breakdownSlices = if (breakdownTotalMinor <= 0L) {
            emptyList()
        } else {
            val totalDecimal = minorUnitsToBigDecimal(breakdownTotalMinor)
            groupedTotals.mapIndexed { index, entry ->
                val itemDecimal = minorUnitsToBigDecimal(entry.value)
                val pct = itemDecimal.multiply(HUNDRED)
                    .divide(totalDecimal, 0, RoundingMode.HALF_UP)
                val share = (entry.value.toFloat() / breakdownTotalMinor.toFloat()).coerceIn(0f, 1f)
                CategoryBreakdownSlice(
                    category = entry.key,
                    amountMinor = entry.value,
                    percentageLabel = "${pct.toPlainString()}%",
                    shareFraction = share,
                    colorIndex = index
                )
            }
        }

        return DashboardMetrics(
            todaySalesMinor = todaySales,
            todayExpensesMinor = todayExpenses,
            monthlyRevenueMinor = monthlyRevenue,
            monthlyExpensesMinor = monthlyExpenses,
            monthlyNetProfitMinor = monthlyNetProfit,
            totalAllTimeBalanceMinor = totalAllTimeBalance,
            outstandingCustomerPaymentsMinor = outstandingInvoicesMinor.coerceAtLeast(0L),
            profitMarginPercent = "${marginPct.toPlainString()}%",
            expenseRatioPercent = "${expenseRatioPct.toPlainString()}%",
            monthlySeries = monthlySeries,
            categoryBreakdown = breakdownSlices,
            categoryBreakdownTotalMinor = breakdownTotalMinor,
            topCategoryName = breakdownSlices.firstOrNull()?.category,
            totalTransactionsCount = transactions.size,
            totalCustomersCount = customersCount,
            totalInvoicesCount = invoicesCount
        )
    }

    private fun periodRangeEpochMs(
        period: AnalyticsPeriod,
        nowEpochMs: Long,
        timeZone: TimeZone
    ): LongRange? {
        return when (period) {
            AnalyticsPeriod.ALL_TIME -> null
            AnalyticsPeriod.THIS_MONTH -> {
                val start = startOfMonthEpochMs(nowEpochMs, timeZone, 0)
                val end = startOfMonthEpochMs(nowEpochMs, timeZone, 1) - 1L
                start..end
            }
            AnalyticsPeriod.THIS_WEEK -> {
                val cal = Calendar.getInstance(timeZone).apply {
                    timeInMillis = startOfDayEpochMs(nowEpochMs, timeZone)
                    add(Calendar.DAY_OF_YEAR, -6)
                }
                cal.timeInMillis..endOfDayEpochMs(nowEpochMs, timeZone)
            }
        }
    }

    private fun startOfDayEpochMs(epochMs: Long, timeZone: TimeZone): Long {
        return Calendar.getInstance(timeZone).apply {
            timeInMillis = epochMs
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    private fun endOfDayEpochMs(epochMs: Long, timeZone: TimeZone): Long {
        return Calendar.getInstance(timeZone).apply {
            timeInMillis = epochMs
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }.timeInMillis
    }

    private fun startOfMonthEpochMs(epochMs: Long, timeZone: TimeZone, monthOffset: Int): Long {
        return Calendar.getInstance(timeZone).apply {
            timeInMillis = epochMs
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            add(Calendar.MONTH, monthOffset)
        }.timeInMillis
    }
}
