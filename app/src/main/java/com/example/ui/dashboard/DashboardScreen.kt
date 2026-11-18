package com.example.ui.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.calculator.FinancialCalculator
import com.example.domain.model.AnalyticsChartType
import com.example.domain.model.AnalyticsPeriod
import com.example.domain.model.TransactionType
import com.example.ui.components.AdaptiveAmountText
import com.example.ui.components.InteractiveAnalyticsStudioCard
import com.example.ui.components.PocketBizCard
import com.example.ui.components.QuickActionTileGrid
import com.example.ui.components.TransactionRowCard
import com.example.ui.theme.LocalPocketBizColors
import com.example.ui.theme.MonoFinancialLarge
import com.example.ui.theme.MonoFinancialMedium
import com.example.ui.theme.MonoFinancialSmall
import com.example.ui.viewmodel.MainUiState

@Composable
fun DashboardScreen(
    uiState: MainUiState,
    onToggleTheme: () -> Unit,
    onOpenSettings: () -> Unit,
    onViewAllTransactions: () -> Unit,
    onQuickAddIncome: () -> Unit,
    onQuickAddExpense: () -> Unit,
    onOpenInvoices: () -> Unit,
    onOpenCustomers: () -> Unit,
    onChartTypeChange: (AnalyticsChartType) -> Unit,
    onBreakdownTypeChange: (TransactionType) -> Unit,
    onBreakdownPeriodChange: (AnalyticsPeriod) -> Unit,
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState()
) {
    val extendedColors = LocalPocketBizColors.current
    val currency = uiState.settings.currencyCode
    val metrics = uiState.metrics

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(210.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            extendedColors.canvasTopVeil,
                            MaterialTheme.colorScheme.background
                        )
                    )
                )
        )

        val isWide = maxWidth >= 600.dp
        val maxContentWidth = if (isWide) 880.dp else 600.dp

        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 112.dp)
        ) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .windowInsetsPadding(WindowInsets.statusBars)
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Column(
                        modifier = Modifier.widthIn(max = maxContentWidth),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        DashboardTopBar(
                            businessName = uiState.settings.businessName,
                            tierName = uiState.settings.subscriptionTier.displayName,
                            isDark = extendedColors.isDark,
                            onToggleTheme = onToggleTheme,
                            onOpenSettings = onOpenSettings
                        )

                        HeroBalanceCard(
                            monthlyNetProfitMinor = metrics.monthlyNetProfitMinor,
                            totalAllTimeBalanceMinor = metrics.totalAllTimeBalanceMinor,
                            todaySalesMinor = metrics.todaySalesMinor,
                            todayExpensesMinor = metrics.todayExpensesMinor,
                            profitMarginPercent = metrics.profitMarginPercent,
                            transactionsCount = metrics.totalTransactionsCount,
                            currencyCode = currency
                        )

                        QuickActionTileGrid(
                            onAddIncome = onQuickAddIncome,
                            onAddExpense = onQuickAddExpense,
                            onOpenInvoices = onOpenInvoices,
                            onOpenCustomers = onOpenCustomers
                        )

                        CashFlowOverviewSection(
                            monthlyRevenueMinor = metrics.monthlyRevenueMinor,
                            monthlyExpensesMinor = metrics.monthlyExpensesMinor,
                            todaySalesMinor = metrics.todaySalesMinor,
                            todayExpensesMinor = metrics.todayExpensesMinor,
                            monthlyNetProfitMinor = metrics.monthlyNetProfitMinor,
                            outstandingCustomerPaymentsMinor = metrics.outstandingCustomerPaymentsMinor,
                            expenseRatioPercent = metrics.expenseRatioPercent,
                            currencyCode = currency,
                            isWide = isWide
                        )

                        InteractiveAnalyticsStudioCard(
                            selectedChartType = uiState.selectedChartType,
                            breakdownType = uiState.breakdownType,
                            breakdownPeriod = uiState.breakdownPeriod,
                            slices = metrics.categoryBreakdown,
                            breakdownTotalMinor = metrics.categoryBreakdownTotalMinor,
                            monthlySeries = metrics.monthlySeries,
                            monthlyNetProfitMinor = metrics.monthlyNetProfitMinor,
                            profitMarginPercent = metrics.profitMarginPercent,
                            topCategoryName = metrics.topCategoryName,
                            currencyCode = currency,
                            onChartTypeChange = onChartTypeChange,
                            onBreakdownTypeChange = onBreakdownTypeChange,
                            onBreakdownPeriodChange = onBreakdownPeriodChange
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 2.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Recent Transactions",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            if (uiState.allTransactions.isNotEmpty()) {
                                TextButton(
                                    onClick = onViewAllTransactions,
                                    modifier = Modifier
                                        .minimumInteractiveComponentSize()
                                        .testTag("see_all_transactions_button")
                                ) {
                                    Text("See All (${uiState.allTransactions.size})")
                                }
                            }
                        }
                    }
                }
            }

            if (uiState.recentTransactions.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        EmptyTransactionsCard(
                            modifier = Modifier.widthIn(max = maxContentWidth)
                        )
                    }
                }
            } else {
                items(
                    items = uiState.recentTransactions,
                    key = { it.id }
                ) { tx ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 5.dp),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        TransactionRowCard(
                            transaction = tx,
                            currencyCode = currency,
                            dateFormatPattern = uiState.settings.dateFormatPattern,
                            modifier = Modifier.widthIn(max = maxContentWidth)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DashboardTopBar(
    businessName: String,
    tierName: String,
    isDark: Boolean,
    onToggleTheme: () -> Unit,
    onOpenSettings: () -> Unit
) {
    val extendedColors = LocalPocketBizColors.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(13.dp))
                    .background(extendedColors.heroStart),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = businessName.trim().firstOrNull()?.uppercase() ?: "P",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = businessName,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "Smart Business Manager",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    Surface(
                        shape = RoundedCornerShape(999.dp),
                        color = extendedColors.positiveContainer,
                        border = BorderStroke(1.dp, extendedColors.positiveBorder)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.CloudOff,
                                contentDescription = null,
                                tint = extendedColors.positive,
                                modifier = Modifier.size(11.dp)
                            )
                            Text(
                                text = "Offline • $tierName",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                color = extendedColors.positive,
                                maxLines = 1,
                                softWrap = false
                            )
                        }
                    }
                }
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            PocketBizCard(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(13.dp))
            ) {
                IconButton(
                    onClick = onToggleTheme,
                    modifier = Modifier
                        .fillMaxSize()
                        .minimumInteractiveComponentSize()
                        .testTag("theme_toggle_button")
                ) {
                    Icon(
                        imageVector = if (isDark) Icons.Filled.LightMode else Icons.Filled.DarkMode,
                        contentDescription = if (isDark) "Switch to Light Theme" else "Switch to Dark Theme",
                        tint = if (isDark) extendedColors.amberBright else extendedColors.indigo,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            PocketBizCard(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(13.dp))
            ) {
                IconButton(
                    onClick = onOpenSettings,
                    modifier = Modifier
                        .fillMaxSize()
                        .minimumInteractiveComponentSize()
                        .testTag("settings_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.Settings,
                        contentDescription = "Business Settings",
                        tint = extendedColors.indigo,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun HeroBalanceCard(
    monthlyNetProfitMinor: Long,
    totalAllTimeBalanceMinor: Long,
    todaySalesMinor: Long,
    todayExpensesMinor: Long,
    profitMarginPercent: String,
    transactionsCount: Int,
    currencyCode: String
) {
    val extendedColors = LocalPocketBizColors.current
    val gradient = Brush.linearGradient(
        colors = listOf(
            extendedColors.heroStart,
            extendedColors.heroMid
        )
    )

    Card(
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.14f),
                shape = MaterialTheme.shapes.large
            )
            .testTag("monthly_net_profit_card")
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(gradient)
                .padding(18.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.weight(1f).padding(end = 8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF34D399))
                        )
                        Text(
                            text = "Monthly Net Profit",
                            style = MaterialTheme.typography.labelLarge,
                            color = Color.White.copy(alpha = 0.90f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(999.dp),
                        color = Color.White.copy(alpha = 0.14f),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.20f))
                    ) {
                        Text(
                            text = "$profitMarginPercent margin",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                            color = Color(0xFF6EE7B7),
                            maxLines = 1,
                            softWrap = false,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }

                AdaptiveAmountText(
                    text = FinancialCalculator.formatCurrency(
                        minorUnits = monthlyNetProfitMinor,
                        currencyCode = currencyCode,
                        includePlusSign = monthlyNetProfitMinor > 0L
                    ),
                    style = MonoFinancialLarge,
                    color = if (monthlyNetProfitMinor < 0L) Color(0xFFFCA5A5) else Color.White,
                    maxFontSize = 30.sp,
                    minFontSize = 18.sp,
                    modifier = Modifier.fillMaxWidth(),
                    textModifier = Modifier.testTag("monthly_net_profit_value")
                )

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color.White.copy(alpha = 0.10f),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.14f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            HeroMiniMetric(
                                label = "Today Sales",
                                value = FinancialCalculator.formatCurrency(todaySalesMinor, currencyCode),
                                valueColor = Color(0xFF6EE7B7),
                                modifier = Modifier.weight(1f)
                            )
                            HeroMiniMetric(
                                label = "Today Expenses",
                                value = FinancialCalculator.formatCurrency(todayExpensesMinor, currencyCode),
                                valueColor = Color(0xFFFCA5A5),
                                modifier = Modifier.weight(1f)
                            )
                        }
                        HorizontalDivider(color = Color.White.copy(alpha = 0.14f))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "All-Time Balance",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White.copy(alpha = 0.78f),
                                maxLines = 1,
                                softWrap = false,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            AdaptiveAmountText(
                                text = FinancialCalculator.formatCurrency(totalAllTimeBalanceMinor, currencyCode),
                                style = MonoFinancialSmall.copy(fontWeight = FontWeight.Bold),
                                color = Color.White,
                                maxFontSize = 14.sp,
                                minFontSize = 11.sp,
                                textAlign = TextAlign.End,
                                modifier = Modifier.weight(1f),
                                textModifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                Text(
                    text = "$transactionsCount verified ledger entries stored locally",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.72f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun HeroMiniMetric(
    label: String,
    value: String,
    valueColor: Color,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = Color.White.copy(alpha = 0.74f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        AdaptiveAmountText(
            text = value,
            style = MonoFinancialSmall.copy(fontWeight = FontWeight.Bold),
            color = valueColor,
            maxFontSize = 14.sp,
            minFontSize = 10.sp,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun CashFlowOverviewSection(
    monthlyRevenueMinor: Long,
    monthlyExpensesMinor: Long,
    todaySalesMinor: Long,
    todayExpensesMinor: Long,
    monthlyNetProfitMinor: Long,
    outstandingCustomerPaymentsMinor: Long,
    expenseRatioPercent: String,
    currencyCode: String,
    isWide: Boolean
) {
    val colors = LocalPocketBizColors.current

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = "Cash Flow Overview",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Card(
                modifier = Modifier.weight(1f),
                shape = MaterialTheme.shapes.medium,
                colors = CardDefaults.cardColors(containerColor = colors.positiveContainer),
                border = BorderStroke(1.dp, colors.positiveBorder)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Monthly Income",
                            style = MaterialTheme.typography.labelMedium,
                            color = colors.positive,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f).padding(end = 6.dp)
                        )
                        Box(
                            modifier = Modifier
                                .size(26.dp)
                                .clip(CircleShape)
                                .background(colors.positive.copy(alpha = 0.14f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                                contentDescription = "Income",
                                tint = colors.positive,
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    }
                    AdaptiveAmountText(
                        text = FinancialCalculator.formatCurrency(monthlyRevenueMinor, currencyCode),
                        style = MonoFinancialMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxFontSize = 17.sp,
                        minFontSize = 11.sp,
                        modifier = Modifier.fillMaxWidth()
                    )
                    AdaptiveAmountText(
                        text = "Today: ${FinancialCalculator.formatCurrency(todaySalesMinor, currencyCode)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = colors.positive,
                        maxFontSize = 11.sp,
                        minFontSize = 9.sp,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Card(
                modifier = Modifier.weight(1f),
                shape = MaterialTheme.shapes.medium,
                colors = CardDefaults.cardColors(containerColor = colors.negativeContainer),
                border = BorderStroke(1.dp, colors.negativeBorder)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Monthly Expense",
                            style = MaterialTheme.typography.labelMedium,
                            color = colors.negative,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f).padding(end = 6.dp)
                        )
                        Box(
                            modifier = Modifier
                                .size(26.dp)
                                .clip(CircleShape)
                                .background(colors.negative.copy(alpha = 0.14f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.TrendingDown,
                                contentDescription = "Expense",
                                tint = colors.negative,
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    }
                    AdaptiveAmountText(
                        text = FinancialCalculator.formatCurrency(monthlyExpensesMinor, currencyCode),
                        style = MonoFinancialMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxFontSize = 17.sp,
                        minFontSize = 11.sp,
                        modifier = Modifier.fillMaxWidth()
                    )
                    AdaptiveAmountText(
                        text = "Today: ${FinancialCalculator.formatCurrency(todayExpensesMinor, currencyCode)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = colors.negative,
                        maxFontSize = 11.sp,
                        minFontSize = 9.sp,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        val netBg = when {
            monthlyNetProfitMinor > 0L -> colors.positiveContainer
            monthlyNetProfitMinor < 0L -> colors.negativeContainer
            else -> colors.skyContainer
        }
        val netBorder = when {
            monthlyNetProfitMinor > 0L -> colors.positiveBorder
            monthlyNetProfitMinor < 0L -> colors.negativeBorder
            else -> colors.sky.copy(alpha = 0.30f)
        }
        val statusLabel = when {
            monthlyNetProfitMinor > 0L -> "Healthy Cash Position"
            monthlyNetProfitMinor < 0L -> "Expense Alert ($expenseRatioPercent)"
            else -> "Ready for Entries"
        }
        val statusColor = when {
            monthlyNetProfitMinor > 0L -> colors.positive
            monthlyNetProfitMinor < 0L -> colors.negative
            else -> colors.sky
        }

        if (isWide) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                NetCashStatusCard(
                    netBg = netBg,
                    netBorder = netBorder,
                    statusLabel = statusLabel,
                    statusColor = statusColor,
                    netProfitMinor = monthlyNetProfitMinor,
                    currencyCode = currencyCode,
                    modifier = Modifier.weight(1f)
                )
                OutstandingPaymentsCard(
                    outstandingMinor = outstandingCustomerPaymentsMinor,
                    currencyCode = currencyCode,
                    modifier = Modifier.weight(1f)
                )
            }
        } else {
            NetCashStatusCard(
                netBg = netBg,
                netBorder = netBorder,
                statusLabel = statusLabel,
                statusColor = statusColor,
                netProfitMinor = monthlyNetProfitMinor,
                currencyCode = currencyCode,
                modifier = Modifier.fillMaxWidth()
            )
            OutstandingPaymentsCard(
                outstandingMinor = outstandingCustomerPaymentsMinor,
                currencyCode = currencyCode,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun NetCashStatusCard(
    netBg: Color,
    netBorder: Color,
    statusLabel: String,
    statusColor: Color,
    netProfitMinor: Long,
    currencyCode: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = netBg),
        border = BorderStroke(1.dp, netBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Net Cash Flow",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f).padding(end = 8.dp)
                )
                Surface(
                    shape = RoundedCornerShape(999.dp),
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Text(
                        text = statusLabel,
                        style = MaterialTheme.typography.labelSmall,
                        color = statusColor,
                        maxLines = 1,
                        softWrap = false,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }
            AdaptiveAmountText(
                text = FinancialCalculator.formatCurrency(
                    netProfitMinor,
                    currencyCode,
                    includePlusSign = netProfitMinor > 0L
                ),
                style = MonoFinancialMedium.copy(fontWeight = FontWeight.Bold),
                color = statusColor,
                maxFontSize = 18.sp,
                minFontSize = 13.sp,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun OutstandingPaymentsCard(
    outstandingMinor: Long,
    currencyCode: String,
    modifier: Modifier = Modifier
) {
    val colors = LocalPocketBizColors.current
    PocketBizCard(
        modifier = modifier,
        containerColor = colors.amberContainer.copy(alpha = 0.55f),
        borderColor = colors.amberBright.copy(alpha = 0.30f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(colors.amberContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Schedule,
                    contentDescription = null,
                    tint = colors.amber,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Outstanding Receivables",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Unpaid customer invoice balance",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = FinancialCalculator.formatCurrency(outstandingMinor, currencyCode),
                style = MonoFinancialSmall.copy(fontWeight = FontWeight.Bold),
                color = colors.amber,
                maxLines = 1,
                softWrap = false
            )
        }
    }
}

@Composable
private fun EmptyTransactionsCard(modifier: Modifier = Modifier) {
    val colors = LocalPocketBizColors.current
    PocketBizCard(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(22.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(colors.skyContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.ReceiptLong,
                    contentDescription = null,
                    tint = colors.sky
                )
            }
            Text(
                text = "Your Ledger Is Ready",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Tap Income, Expense, or the + button to record real business entries.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}
