package com.example

import com.example.data.local.TransactionEntity
import com.example.domain.calculator.FinancialCalculator
import com.example.domain.model.TransactionType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.util.Calendar
import java.util.TimeZone

class FinancialCalculatorTest {

    @Test
    fun `parsePositiveAmountToMinorUnits validates decimal precision strictly`() {
        assertEquals(125050L, FinancialCalculator.parsePositiveAmountToMinorUnits("1250.50"))
        assertEquals(100000L, FinancialCalculator.parsePositiveAmountToMinorUnits("1,000"))
        assertEquals(99L, FinancialCalculator.parsePositiveAmountToMinorUnits("0.99"))
        assertNull(FinancialCalculator.parsePositiveAmountToMinorUnits("0"))
        assertNull(FinancialCalculator.parsePositiveAmountToMinorUnits("-50.00"))
        assertNull(FinancialCalculator.parsePositiveAmountToMinorUnits("12.345"))
        assertNull(FinancialCalculator.parsePositiveAmountToMinorUnits("abc"))
    }

    @Test
    fun `calculateNetProfit and calculateOutstandingBalance remain exact without float error`() {
        // Classic float trap: 0.10 + 0.20 vs 0.30
        val income = FinancialCalculator.parsePositiveAmountToMinorUnits("1000.10")!!
        val expense = FinancialCalculator.parsePositiveAmountToMinorUnits("300.20")!!
        assertEquals(69990L, FinancialCalculator.calculateNetProfit(income, expense))
        assertEquals("PKR 699.90", FinancialCalculator.formatCurrency(69990L, "PKR"))

        val loss = FinancialCalculator.calculateNetProfit(10000L, 25000L)
        assertEquals(-15000L, loss)
        assertEquals("-PKR 150.00", FinancialCalculator.formatCurrency(loss, "PKR"))

        assertEquals(45000L, FinancialCalculator.calculateOutstandingBalance(100000L, 55000L))
        assertEquals(0L, FinancialCalculator.calculateOutstandingBalance(100000L, 120000L))
    }

    @Test
    fun `computeDashboardMetrics aggregates today, monthly, and 6-month series accurately`() {
        val tz = TimeZone.getTimeZone("UTC")
        val nowCal = Calendar.getInstance(tz).apply {
            set(2026, Calendar.NOVEMBER, 18, 12, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val todayNoon = nowCal.timeInMillis

        val earlierThisMonth = Calendar.getInstance(tz).apply {
            set(2026, Calendar.NOVEMBER, 2, 9, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        val lastMonth = Calendar.getInstance(tz).apply {
            set(2026, Calendar.OCTOBER, 15, 10, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        val txs = listOf(
            TransactionEntity(
                type = TransactionType.INCOME,
                category = "Sales",
                amountMinor = 50000L,
                currencyCode = "PKR",
                transactionDateEpochMs = todayNoon
            ),
            TransactionEntity(
                type = TransactionType.EXPENSE,
                category = "Transport",
                amountMinor = 12000L,
                currencyCode = "PKR",
                transactionDateEpochMs = todayNoon
            ),
            TransactionEntity(
                type = TransactionType.INCOME,
                category = "Services",
                amountMinor = 100000L,
                currencyCode = "PKR",
                transactionDateEpochMs = earlierThisMonth
            ),
            TransactionEntity(
                type = TransactionType.EXPENSE,
                category = "Rent & Utilities",
                amountMinor = 40000L,
                currencyCode = "PKR",
                transactionDateEpochMs = lastMonth
            )
        )

        val metrics = FinancialCalculator.computeDashboardMetrics(
            transactions = txs,
            outstandingInvoicesMinor = 25000L,
            customersCount = 3,
            invoicesCount = 2,
            nowEpochMs = todayNoon,
            timeZone = tz
        )

        assertEquals(50000L, metrics.todaySalesMinor)
        assertEquals(12000L, metrics.todayExpensesMinor)
        assertEquals(150000L, metrics.monthlyRevenueMinor)
        assertEquals(12000L, metrics.monthlyExpensesMinor)
        assertEquals(138000L, metrics.monthlyNetProfitMinor)
        assertEquals(25000L, metrics.outstandingCustomerPaymentsMinor)
        assertEquals("92.0%", metrics.profitMarginPercent)
        assertEquals("8.0%", metrics.expenseRatioPercent)
        assertEquals(6, metrics.monthlySeries.size)
        assertEquals("Oct", metrics.monthlySeries[4].monthLabel)
        assertEquals(40000L, metrics.monthlySeries[4].expenseMinor)
        assertEquals("Nov", metrics.monthlySeries[5].monthLabel)
        assertEquals(150000L, metrics.monthlySeries[5].revenueMinor)
        assertEquals(1, metrics.categoryBreakdown.size)
        assertEquals("Transport", metrics.categoryBreakdown.first().category)
        assertEquals("100%", metrics.categoryBreakdown.first().percentageLabel)
    }

    @Test
    fun `filterTransactions filters by query, type, and period accurately`() {
        val tz = TimeZone.getTimeZone("UTC")
        val now = Calendar.getInstance(tz).apply {
            set(2026, Calendar.NOVEMBER, 18, 12, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        val txs = listOf(
            TransactionEntity(
                id = 1L,
                type = TransactionType.INCOME,
                category = "Sales",
                amountMinor = 50000L,
                currencyCode = "PKR",
                description = "Walk-in electronics",
                transactionDateEpochMs = now
            ),
            TransactionEntity(
                id = 2L,
                type = TransactionType.EXPENSE,
                category = "Transport",
                amountMinor = 12000L,
                currencyCode = "PKR",
                description = "Delivery van fuel",
                transactionDateEpochMs = now
            )
        )

        val filteredByQuery = FinancialCalculator.filterTransactions(
            transactions = txs,
            query = "fuel",
            typeFilter = com.example.domain.model.TransactionFilterType.ALL,
            period = com.example.domain.model.AnalyticsPeriod.THIS_MONTH,
            nowEpochMs = now,
            timeZone = tz
        )
        assertEquals(1, filteredByQuery.size)
        assertEquals(2L, filteredByQuery.first().id)

        val filteredByType = FinancialCalculator.filterTransactions(
            transactions = txs,
            query = "",
            typeFilter = com.example.domain.model.TransactionFilterType.INCOME,
            period = com.example.domain.model.AnalyticsPeriod.ALL_TIME,
            nowEpochMs = now,
            timeZone = tz
        )
        assertEquals(1, filteredByType.size)
        assertEquals("Sales", filteredByType.first().category)
    }

    @Test
    fun `invoice line items, discount, and tax calculate exact minor units`() {
        val qtyMilli = FinancialCalculator.parsePositiveQuantityMilli("2.5")!!
        val unitPriceMinor = FinancialCalculator.parsePositiveAmountToMinorUnits("400.00")!!
        assertEquals(2500L, qtyMilli)
        assertEquals("2.5", FinancialCalculator.formatQuantity(qtyMilli))

        val lineTotal = FinancialCalculator.calculateLineTotalMinor(qtyMilli, unitPriceMinor)
        assertEquals(100000L, lineTotal) // 2.5 * 400.00 = 1000.00

        val discountMinor = FinancialCalculator.parseNonNegativeAmountToMinorUnits("100.00")!!
        val taxBasisPoints = FinancialCalculator.parseTaxPercentToBasisPoints("15")!!
        assertEquals(1500, taxBasisPoints)

        val totals = FinancialCalculator.calculateInvoiceTotals(
            lineTotalsMinor = listOf(lineTotal),
            discountMinor = discountMinor,
            taxRateBasisPoints = taxBasisPoints
        )
        assertEquals(100000L, totals.subtotalMinor)
        assertEquals(10000L, totals.discountMinor)
        // Taxable = 900.00, 15% tax = 135.00 (13500 minor)
        assertEquals(13500L, totals.taxAmountMinor)
        assertEquals(103500L, totals.totalMinor)

        assertNull(FinancialCalculator.parsePositiveQuantityMilli("0"))
        assertNull(FinancialCalculator.parseTaxPercentToBasisPoints("120"))
    }
}
