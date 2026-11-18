package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.DonutLarge
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.HomeWork
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.RemoveCircle
import androidx.compose.material.icons.filled.RequestQuote
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.WorkOutline
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.TransactionEntity
import com.example.domain.calculator.FinancialCalculator
import com.example.domain.model.AnalyticsChartType
import com.example.domain.model.AnalyticsPeriod
import com.example.domain.model.CategoryBreakdownSlice
import com.example.domain.model.MonthlyCashFlowPoint
import com.example.domain.model.TransactionCategories
import com.example.domain.model.TransactionType
import com.example.ui.theme.LocalPocketBizColors
import com.example.ui.theme.MonoFinancialMedium
import com.example.ui.theme.MonoFinancialSmall
import com.example.ui.theme.PocketBizExtendedColors
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.atan2
import kotlin.math.hypot

data class CategoryVisualStyle(
    val icon: ImageVector,
    val tint: Color,
    val containerColor: Color
)

@Composable
fun rememberCategoryStyle(category: String, type: TransactionType): CategoryVisualStyle {
    val colors = LocalPocketBizColors.current
    return remember(category, type, colors) {
        categoryStyleFor(category, type, colors)
    }
}

private fun categoryStyleFor(
    category: String,
    type: TransactionType,
    colors: PocketBizExtendedColors
): CategoryVisualStyle {
    return when (category) {
        "Sales" -> CategoryVisualStyle(Icons.Filled.ShoppingBag, colors.sky, colors.skyContainer)
        "Services" -> CategoryVisualStyle(Icons.Filled.WorkOutline, colors.positive, colors.positiveContainer)
        "Consulting" -> CategoryVisualStyle(Icons.AutoMirrored.Filled.TrendingUp, colors.violet, colors.violetContainer)
        "Invoice Payment" -> CategoryVisualStyle(Icons.Filled.Paid, colors.teal, colors.tealContainer)
        "Other Income" -> CategoryVisualStyle(Icons.Filled.AccountBalanceWallet, colors.lime, colors.limeContainer)
        "Inventory" -> CategoryVisualStyle(Icons.Filled.Inventory2, colors.sky, colors.skyContainer)
        "Rent & Utilities" -> CategoryVisualStyle(Icons.Filled.HomeWork, colors.amber, colors.amberContainer)
        "Salaries" -> CategoryVisualStyle(Icons.Filled.Groups, colors.violet, colors.violetContainer)
        "Marketing" -> CategoryVisualStyle(Icons.Filled.Campaign, colors.peach, colors.peachContainer)
        "Transport" -> CategoryVisualStyle(Icons.Filled.DirectionsCar, colors.teal, colors.tealContainer)
        "Equipment" -> CategoryVisualStyle(Icons.Filled.Build, colors.amber, colors.amberContainer)
        "Taxes" -> CategoryVisualStyle(Icons.Filled.RequestQuote, colors.negative, colors.negativeContainer)
        else -> if (type == TransactionType.INCOME) {
            CategoryVisualStyle(Icons.Filled.Category, colors.positive, colors.positiveContainer)
        } else {
            CategoryVisualStyle(Icons.Filled.Category, colors.negative, colors.negativeContainer)
        }
    }
}

/**
 * Single-line adaptive tabular currency text that scales smoothly to fit available width
 * so exact monetary values never wrap awkwardly or silently truncate on narrow screens.
 */
@Composable
fun AdaptiveAmountText(
    text: String,
    style: TextStyle,
    color: Color,
    modifier: Modifier = Modifier,
    maxFontSize: TextUnit = style.fontSize,
    minFontSize: TextUnit = 10.sp,
    textAlign: TextAlign = TextAlign.Start,
    textModifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    BoxWithConstraints(modifier = modifier) {
        val maxWidthDp = maxWidth.value.takeIf { it.isFinite() && it > 0f } ?: 320f
        val fontScale = density.fontScale.coerceAtLeast(1f)
        val estimatedFitSp = (maxWidthDp / (text.length.coerceAtLeast(1) * 0.64f * fontScale))
        val initialSp = minOf(maxFontSize.value, estimatedFitSp).coerceAtLeast(minFontSize.value)
        var currentSp by remember(text, maxWidth, maxFontSize, minFontSize) {
            mutableFloatStateOf(initialSp)
        }

        Text(
            text = text,
            style = style.copy(
                fontSize = currentSp.sp,
                lineHeight = (currentSp * 1.25f).sp
            ),
            color = color,
            maxLines = 1,
            softWrap = false,
            overflow = TextOverflow.Ellipsis,
            textAlign = textAlign,
            onTextLayout = { result ->
                if (result.didOverflowWidth && currentSp > minFontSize.value) {
                    currentSp = (currentSp - 0.8f).coerceAtLeast(minFontSize.value)
                }
            },
            modifier = textModifier
        )
    }
}

@Composable
fun PocketBizCard(
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    borderColor: Color? = null,
    elevation: Dp = 1.dp,
    content: @Composable () -> Unit
) {
    val extendedColors = LocalPocketBizColors.current
    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation),
        border = BorderStroke(1.dp, borderColor ?: extendedColors.cardBorder)
    ) {
        content()
    }
}

@Composable
fun QuickActionTileGrid(
    onAddIncome: () -> Unit,
    onAddExpense: () -> Unit,
    onOpenInvoices: () -> Unit,
    onOpenCustomers: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalPocketBizColors.current
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = "Quick Actions",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            QuickActionTile(
                label = "Income",
                icon = Icons.Filled.AddCircle,
                iconTint = colors.positive,
                iconBg = colors.positiveContainer,
                borderTint = colors.positiveBorder,
                testTag = "quick_action_income",
                onClick = onAddIncome,
                modifier = Modifier.weight(1f)
            )
            QuickActionTile(
                label = "Expense",
                icon = Icons.Filled.RemoveCircle,
                iconTint = colors.negative,
                iconBg = colors.negativeContainer,
                borderTint = colors.negativeBorder,
                testTag = "quick_action_expense",
                onClick = onAddExpense,
                modifier = Modifier.weight(1f)
            )
            QuickActionTile(
                label = "Invoices",
                icon = Icons.AutoMirrored.Filled.ReceiptLong,
                iconTint = colors.sky,
                iconBg = colors.skyContainer,
                borderTint = colors.cardBorder,
                testTag = "quick_action_invoices",
                onClick = onOpenInvoices,
                modifier = Modifier.weight(1f)
            )
            QuickActionTile(
                label = "Customers",
                icon = Icons.Filled.People,
                iconTint = colors.violet,
                iconBg = colors.violetContainer,
                borderTint = colors.cardBorder,
                testTag = "quick_action_customers",
                onClick = onOpenCustomers,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun QuickActionTile(
    label: String,
    icon: ImageVector,
    iconTint: Color,
    iconBg: Color,
    borderTint: Color,
    testTag: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
            .clickable(onClick = onClick)
            .minimumInteractiveComponentSize()
            .testTag(testTag),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        border = BorderStroke(1.dp, borderTint)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = iconTint,
                    modifier = Modifier.size(21.dp)
                )
            }
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 11.sp,
                    letterSpacing = (-0.2).sp,
                    fontWeight = FontWeight.SemiBold
                ),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Clip
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun InteractiveAnalyticsStudioCard(
    selectedChartType: AnalyticsChartType,
    breakdownType: TransactionType,
    breakdownPeriod: AnalyticsPeriod,
    slices: List<CategoryBreakdownSlice>,
    breakdownTotalMinor: Long,
    monthlySeries: List<MonthlyCashFlowPoint>,
    monthlyNetProfitMinor: Long,
    profitMarginPercent: String,
    topCategoryName: String?,
    currencyCode: String,
    onChartTypeChange: (AnalyticsChartType) -> Unit,
    onBreakdownTypeChange: (TransactionType) -> Unit,
    onBreakdownPeriodChange: (AnalyticsPeriod) -> Unit,
    modifier: Modifier = Modifier
) {
    val extendedColors = LocalPocketBizColors.current
    var selectedSliceIndex by remember(slices, selectedChartType) { mutableStateOf<Int?>(null) }
    var selectedMonthIndex by remember(monthlySeries, selectedChartType) { mutableStateOf<Int?>(null) }

    PocketBizCard(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header + non-wrapping margin badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = "Visual Analytics",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = if (topCategoryName != null) {
                            "Top: $topCategoryName • Tap chart to inspect"
                        } else {
                            "Choose Donut, Pie, Bars, or Trend"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Surface(
                    shape = RoundedCornerShape(999.dp),
                    color = extendedColors.violetContainer
                ) {
                    Text(
                        text = "$profitMarginPercent margin",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = extendedColors.violet,
                        maxLines = 1,
                        softWrap = false,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }

            // 4-Way Chart Type Switcher (Donut, Pie, Bars, Trend)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                AnalyticsChartType.entries.forEach { chartType ->
                    val isSelected = selectedChartType == chartType
                    val icon = when (chartType) {
                        AnalyticsChartType.DONUT -> Icons.Filled.DonutLarge
                        AnalyticsChartType.PIE -> Icons.Filled.PieChart
                        AnalyticsChartType.BARS -> Icons.Filled.BarChart
                        AnalyticsChartType.TREND -> Icons.AutoMirrored.Filled.ShowChart
                    }
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { onChartTypeChange(chartType) }
                            .testTag("chart_mode_${chartType.name.lowercase()}")
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 9.dp, horizontal = 4.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = chartType.label,
                                tint = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = chartType.label,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 11.sp,
                                    letterSpacing = (-0.15).sp,
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                softWrap = false
                            )
                        }
                    }
                }
            }

            // Income/Expense & Period Filters (cleanly wrapped on narrow screens)
            if (selectedChartType == AnalyticsChartType.DONUT || selectedChartType == AnalyticsChartType.PIE) {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        PillToggleChip(
                            label = "Expenses",
                            selected = breakdownType == TransactionType.EXPENSE,
                            selectedBg = extendedColors.negative,
                            selectedFg = Color.White,
                            testTag = "donut_toggle_expense",
                            onClick = { onBreakdownTypeChange(TransactionType.EXPENSE) }
                        )
                        PillToggleChip(
                            label = "Income",
                            selected = breakdownType == TransactionType.INCOME,
                            selectedBg = extendedColors.positive,
                            selectedFg = Color.White,
                            testTag = "donut_toggle_income",
                            onClick = { onBreakdownTypeChange(TransactionType.INCOME) }
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        AnalyticsPeriod.entries.forEach { period ->
                            val shortLabel = when (period) {
                                AnalyticsPeriod.THIS_WEEK -> "Week"
                                AnalyticsPeriod.THIS_MONTH -> "Month"
                                AnalyticsPeriod.ALL_TIME -> "All"
                            }
                            PillToggleChip(
                                label = shortLabel,
                                selected = breakdownPeriod == period,
                                selectedBg = extendedColors.skyContainer,
                                selectedFg = extendedColors.sky,
                                testTag = "donut_period_${period.name.lowercase()}",
                                onClick = { onBreakdownPeriodChange(period) }
                            )
                        }
                    }
                }
            }

            // Chart Render Stage
            when (selectedChartType) {
                AnalyticsChartType.DONUT -> {
                    DonutRingSection(
                        slices = slices,
                        totalMinor = breakdownTotalMinor,
                        breakdownType = breakdownType,
                        breakdownPeriod = breakdownPeriod,
                        currencyCode = currencyCode,
                        selectedSliceIndex = selectedSliceIndex,
                        onSelectSlice = { idx ->
                            selectedSliceIndex = if (selectedSliceIndex == idx) null else idx
                        }
                    )
                }

                AnalyticsChartType.PIE -> {
                    PieChartSection(
                        slices = slices,
                        totalMinor = breakdownTotalMinor,
                        breakdownType = breakdownType,
                        currencyCode = currencyCode,
                        selectedSliceIndex = selectedSliceIndex,
                        onSelectSlice = { idx ->
                            selectedSliceIndex = if (selectedSliceIndex == idx) null else idx
                        }
                    )
                }

                AnalyticsChartType.BARS -> {
                    DualBarChartSection(
                        series = monthlySeries,
                        currencyCode = currencyCode,
                        selectedMonthIndex = selectedMonthIndex,
                        onSelectMonth = { idx ->
                            selectedMonthIndex = if (selectedMonthIndex == idx) null else idx
                        }
                    )
                }

                AnalyticsChartType.TREND -> {
                    AreaTrendSection(
                        series = monthlySeries,
                        monthlyNetProfitMinor = monthlyNetProfitMinor,
                        currencyCode = currencyCode,
                        selectedMonthIndex = selectedMonthIndex,
                        onSelectMonth = { idx ->
                            selectedMonthIndex = if (selectedMonthIndex == idx) null else idx
                        }
                    )
                }
            }

            // Category Legend Chips for Donut / Pie
            if (selectedChartType == AnalyticsChartType.DONUT || selectedChartType == AnalyticsChartType.PIE) {
                if (slices.isEmpty()) {
                    Text(
                        text = "Add ${if (breakdownType == TransactionType.EXPENSE) "expenses" else "income"} to analyze category shares",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    val palette = extendedColors.chartPalette
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        slices.take(6).forEachIndexed { idx, slice ->
                            val color = palette[slice.colorIndex % palette.size]
                            val isSelected = selectedSliceIndex == idx
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) color else color.copy(alpha = 0.11f),
                                border = BorderStroke(
                                    width = if (isSelected) 1.5.dp else 1.dp,
                                    color = color.copy(alpha = 0.42f)
                                ),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable {
                                        selectedSliceIndex = if (selectedSliceIndex == idx) null else idx
                                    }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(if (isSelected) Color.White else color)
                                    )
                                    Text(
                                        text = "${slice.category} • ${slice.percentageLabel}",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun normalizedArcSweeps(
    slices: List<CategoryBreakdownSlice>,
    gapDegrees: Float,
    minSweepDegrees: Float
): List<Float> {
    if (slices.isEmpty()) return emptyList()
    if (slices.size == 1) return listOf(360f)

    val totalGap = gapDegrees * slices.size
    val availableDegrees = (360f - totalGap).coerceAtLeast(180f)
    val rawSweeps = slices.map { (it.shareFraction * availableDegrees).coerceAtLeast(minSweepDegrees) }
    val rawSum = rawSweeps.sum().takeIf { it > 0f } ?: availableDegrees
    val scale = availableDegrees / rawSum
    return rawSweeps.map { it * scale }
}

@Composable
private fun DonutRingSection(
    slices: List<CategoryBreakdownSlice>,
    totalMinor: Long,
    breakdownType: TransactionType,
    breakdownPeriod: AnalyticsPeriod,
    currencyCode: String,
    selectedSliceIndex: Int?,
    onSelectSlice: (Int) -> Unit
) {
    val extendedColors = LocalPocketBizColors.current
    val palette = extendedColors.chartPalette
    val activeSlice = selectedSliceIndex?.let { slices.getOrNull(it) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(210.dp)
            .testTag("chart_view_donut"),
        contentAlignment = Alignment.Center
    ) {
        val trackColor = MaterialTheme.colorScheme.surfaceVariant
        val gapDegrees = 2.5f
        val sweeps = remember(slices) { normalizedArcSweeps(slices, gapDegrees, minSweepDegrees = 4f) }

        Canvas(
            modifier = Modifier
                .size(192.dp)
                .pointerInput(slices, sweeps) {
                    if (slices.isEmpty()) return@pointerInput
                    detectTapGestures { offset ->
                        val center = Offset(size.width / 2f, size.height / 2f)
                        val dx = offset.x - center.x
                        val dy = offset.y - center.y
                        val dist = hypot(dx, dy)
                        if (dist < size.width * 0.22f) return@detectTapGestures
                        var angle = Math.toDegrees(atan2(dy.toDouble(), dx.toDouble())).toFloat() + 90f
                        if (angle < 0f) angle += 360f
                        val tappedIdx = findSliceIndexAtAngle(sweeps, gapDegrees, angle)
                        if (tappedIdx != null) onSelectSlice(tappedIdx)
                    }
                }
        ) {
            val strokePx = 22.dp.toPx()
            val inset = strokePx / 2f
            val arcSize = Size(size.width - strokePx, size.height - strokePx)
            val topLeft = Offset(inset, inset)

            if (slices.isEmpty()) {
                val previewColors = palette.take(5)
                val sweep = 360f / previewColors.size - 5f
                previewColors.forEachIndexed { i, color ->
                    drawArc(
                        color = color.copy(alpha = 0.24f),
                        startAngle = -90f + i * (sweep + 5f),
                        sweepAngle = sweep,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = strokePx, cap = StrokeCap.Butt)
                    )
                }
            } else if (slices.size == 1) {
                drawArc(
                    color = palette[0],
                    startAngle = -90f,
                    sweepAngle = 360f,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = strokePx, cap = StrokeCap.Butt)
                )
            } else {
                drawArc(
                    color = trackColor,
                    startAngle = 0f,
                    sweepAngle = 360f,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = strokePx)
                )
                var currentAngle = -90f
                slices.forEachIndexed { idx, slice ->
                    val sweep = sweeps[idx]
                    val color = palette[slice.colorIndex % palette.size]
                    val isDimmed = selectedSliceIndex != null && selectedSliceIndex != idx
                    drawArc(
                        color = if (isDimmed) color.copy(alpha = 0.30f) else color,
                        startAngle = currentAngle,
                        sweepAngle = sweep,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(
                            width = if (selectedSliceIndex == idx) strokePx + 4.dp.toPx() else strokePx,
                            cap = StrokeCap.Butt
                        )
                    )
                    currentAngle += sweep + gapDegrees
                }
            }
        }

        Column(
            modifier = Modifier.widthIn(max = 126.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.AccountBalanceWallet,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(15.dp)
                )
            }
            Text(
                text = activeSlice?.let { "${it.category} (${it.percentageLabel})" }
                    ?: if (breakdownType == TransactionType.EXPENSE) {
                        "Expenses • ${breakdownPeriod.label}"
                    } else {
                        "Income • ${breakdownPeriod.label}"
                    },
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
            AdaptiveAmountText(
                text = FinancialCalculator.formatCurrency(
                    activeSlice?.amountMinor ?: totalMinor,
                    currencyCode
                ),
                style = MonoFinancialMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface,
                maxFontSize = 15.sp,
                minFontSize = 10.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                textModifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun PieChartSection(
    slices: List<CategoryBreakdownSlice>,
    totalMinor: Long,
    breakdownType: TransactionType,
    currencyCode: String,
    selectedSliceIndex: Int?,
    onSelectSlice: (Int) -> Unit
) {
    val extendedColors = LocalPocketBizColors.current
    val palette = extendedColors.chartPalette
    val activeSlice = selectedSliceIndex?.let { slices.getOrNull(it) }
    val surfaceBg = MaterialTheme.colorScheme.surface
    val sweeps = remember(slices) { normalizedArcSweeps(slices, gapDegrees = 0f, minSweepDegrees = 3f) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("chart_view_pie"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(196.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(
                modifier = Modifier
                    .size(182.dp)
                    .pointerInput(slices, sweeps) {
                        if (slices.isEmpty()) return@pointerInput
                        detectTapGestures { offset ->
                            val center = Offset(size.width / 2f, size.height / 2f)
                            val dx = offset.x - center.x
                            val dy = offset.y - center.y
                            var angle = Math.toDegrees(atan2(dy.toDouble(), dx.toDouble())).toFloat() + 90f
                            if (angle < 0f) angle += 360f
                            val tappedIdx = findSliceIndexAtAngle(sweeps, gapDegrees = 0f, angle = angle)
                            if (tappedIdx != null) onSelectSlice(tappedIdx)
                        }
                    }
            ) {
                if (slices.isEmpty()) {
                    val previewColors = palette.take(5)
                    val sweep = 360f / previewColors.size
                    previewColors.forEachIndexed { idx, color ->
                        drawArc(
                            color = color.copy(alpha = 0.25f),
                            startAngle = -90f + idx * sweep,
                            sweepAngle = sweep,
                            useCenter = true
                        )
                    }
                    drawCircle(color = surfaceBg, radius = size.minDimension * 0.04f)
                } else {
                    var startAngle = -90f
                    slices.forEachIndexed { idx, slice ->
                        val sweep = sweeps[idx]
                        val color = palette[slice.colorIndex % palette.size]
                        val isSelected = selectedSliceIndex == idx
                        val isDimmed = selectedSliceIndex != null && !isSelected
                        val inset = if (isSelected) 0f else 6.dp.toPx()
                        val pieSize = Size(size.width - inset * 2, size.height - inset * 2)
                        drawArc(
                            color = if (isDimmed) color.copy(alpha = 0.45f) else color,
                            startAngle = startAngle,
                            sweepAngle = sweep,
                            useCenter = true,
                            topLeft = Offset(inset, inset),
                            size = pieSize
                        )
                        if (slices.size > 1) {
                            drawArc(
                                color = surfaceBg,
                                startAngle = startAngle,
                                sweepAngle = sweep,
                                useCenter = true,
                                topLeft = Offset(inset, inset),
                                size = pieSize,
                                style = Stroke(width = 2.dp.toPx())
                            )
                        }
                        startAngle += sweep
                    }
                }
            }
        }

        // Crisp inspector bar under Pie chart
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = activeSlice?.let { "${it.category} (${it.percentageLabel})" }
                        ?: if (breakdownType == TransactionType.EXPENSE) "Total Expenses" else "Total Income",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f).padding(end = 8.dp)
                )
                AdaptiveAmountText(
                    text = FinancialCalculator.formatCurrency(
                        activeSlice?.amountMinor ?: totalMinor,
                        currencyCode
                    ),
                    style = MonoFinancialSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary,
                    maxFontSize = 14.sp,
                    minFontSize = 11.sp,
                    textAlign = TextAlign.End
                )
            }
        }
    }
}

private fun findSliceIndexAtAngle(
    sweeps: List<Float>,
    gapDegrees: Float,
    angle: Float
): Int? {
    if (sweeps.isEmpty()) return null
    var cursor = 0f
    sweeps.forEachIndexed { idx, sweep ->
        val span = sweep + gapDegrees
        if (angle in cursor..(cursor + span)) {
            return idx
        }
        cursor += span
    }
    return sweeps.indices.lastOrNull()
}

@Composable
private fun DualBarChartSection(
    series: List<MonthlyCashFlowPoint>,
    currencyCode: String,
    selectedMonthIndex: Int?,
    onSelectMonth: (Int) -> Unit
) {
    val extendedColors = LocalPocketBizColors.current
    val hasData = remember(series) {
        series.any { it.revenueMinor > 0L || it.expenseMinor > 0L }
    }
    val selectedPoint = selectedMonthIndex?.let { series.getOrNull(it) } ?: series.lastOrNull()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("chart_view_bars"),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "6-Month Revenue vs Expenses",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f).padding(end = 8.dp)
            )
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                ChartLegendDot(color = extendedColors.positiveBright, label = "Income")
                ChartLegendDot(color = extendedColors.negativeBright, label = "Expense")
            }
        }

        if (!hasData) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(148.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.padding(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.BarChart,
                        contentDescription = null,
                        tint = extendedColors.sky,
                        modifier = Modifier.size(30.dp)
                    )
                    Text(
                        text = "No monthly bars yet",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Record income or expenses to compare monthly bars",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            val maxMinor = remember(series) {
                series.maxOfOrNull { maxOf(it.revenueMinor, it.expenseMinor) }?.coerceAtLeast(1L) ?: 1L
            }
            val gridColor = extendedColors.cardBorder
            val incomeColor = extendedColors.positiveBright
            val expenseColor = extendedColors.negativeBright

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(152.dp)
                    .pointerInput(series) {
                        detectTapGestures { offset ->
                            if (series.isEmpty()) return@detectTapGestures
                            val idx = ((offset.x / size.width) * series.size)
                                .toInt()
                                .coerceIn(0, series.lastIndex)
                            onSelectMonth(idx)
                        }
                    }
            ) {
                val chartHeight = size.height - 12.dp.toPx()
                val dashEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f), 0f)
                for (i in 0..2) {
                    val y = chartHeight * (i / 2f) + 4.dp.toPx()
                    drawLine(
                        color = gridColor,
                        start = Offset(0f, y),
                        end = Offset(size.width, y),
                        strokeWidth = 1.dp.toPx(),
                        pathEffect = if (i < 2) dashEffect else null
                    )
                }

                val groupWidth = size.width / series.size.coerceAtLeast(1)
                val barWidth = (groupWidth * 0.25f).coerceIn(8.dp.toPx(), 18.dp.toPx())
                val barGap = 4.dp.toPx()
                val cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())

                series.forEachIndexed { index, point ->
                    val groupCenterX = groupWidth * index + groupWidth / 2f
                    val isDimmed = selectedMonthIndex != null && selectedMonthIndex != index
                    val revRatio = (point.revenueMinor.toFloat() / maxMinor.toFloat()).coerceIn(0f, 1f)
                    val expRatio = (point.expenseMinor.toFloat() / maxMinor.toFloat()).coerceIn(0f, 1f)

                    val revHeight = (chartHeight * revRatio).let {
                        if (point.revenueMinor > 0L) it.coerceAtLeast(6.dp.toPx()) else 0f
                    }
                    val expHeight = (chartHeight * expRatio).let {
                        if (point.expenseMinor > 0L) it.coerceAtLeast(6.dp.toPx()) else 0f
                    }

                    val baselineY = chartHeight + 4.dp.toPx()
                    if (revHeight > 0f) {
                        drawRoundRect(
                            color = if (isDimmed) incomeColor.copy(alpha = 0.35f) else incomeColor,
                            topLeft = Offset(groupCenterX - barWidth - barGap / 2f, baselineY - revHeight),
                            size = Size(barWidth, revHeight),
                            cornerRadius = cornerRadius
                        )
                    }
                    if (expHeight > 0f) {
                        drawRoundRect(
                            color = if (isDimmed) expenseColor.copy(alpha = 0.35f) else expenseColor,
                            topLeft = Offset(groupCenterX + barGap / 2f, baselineY - expHeight),
                            size = Size(barWidth, expHeight),
                            cornerRadius = cornerRadius
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                series.forEachIndexed { idx, point ->
                    val isSelected = selectedMonthIndex == idx
                    Text(
                        text = point.monthLabel,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .clickable { onSelectMonth(idx) }
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            if (selectedPoint != null) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "${selectedPoint.monthLabel} Breakdown",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            AdaptiveAmountText(
                                text = "+${FinancialCalculator.formatCurrency(selectedPoint.revenueMinor, currencyCode)}",
                                style = MonoFinancialSmall.copy(fontWeight = FontWeight.SemiBold),
                                color = extendedColors.positive,
                                maxFontSize = 13.sp,
                                minFontSize = 10.sp,
                                modifier = Modifier.weight(1f)
                            )
                            AdaptiveAmountText(
                                text = "-${FinancialCalculator.formatCurrency(selectedPoint.expenseMinor, currencyCode)}",
                                style = MonoFinancialSmall.copy(fontWeight = FontWeight.SemiBold),
                                color = extendedColors.negative,
                                maxFontSize = 13.sp,
                                minFontSize = 10.sp,
                                textAlign = TextAlign.End,
                                modifier = Modifier.weight(1f),
                                textModifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AreaTrendSection(
    series: List<MonthlyCashFlowPoint>,
    monthlyNetProfitMinor: Long,
    currencyCode: String,
    selectedMonthIndex: Int?,
    onSelectMonth: (Int) -> Unit
) {
    val extendedColors = LocalPocketBizColors.current
    val violetLine = extendedColors.violet
    val hasSeriesData = remember(series) {
        series.any { it.revenueMinor > 0L || it.expenseMinor > 0L }
    }
    val inspectedPoint = selectedMonthIndex?.let { series.getOrNull(it) }

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = extendedColors.violetContainer.copy(alpha = 0.45f),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("chart_view_trend")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = inspectedPoint?.let { "${it.monthLabel} Net Flow" } ?: "6-Month Net Trajectory",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f).padding(end = 8.dp)
                )
                AdaptiveAmountText(
                    text = FinancialCalculator.formatCurrency(
                        inspectedPoint?.netMinor ?: monthlyNetProfitMinor,
                        currencyCode,
                        includePlusSign = (inspectedPoint?.netMinor ?: monthlyNetProfitMinor) > 0L
                    ),
                    style = MonoFinancialSmall.copy(fontWeight = FontWeight.Bold),
                    color = violetLine,
                    maxFontSize = 14.sp,
                    minFontSize = 11.sp,
                    textAlign = TextAlign.End
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(118.dp)
                    .pointerInput(series) {
                        detectTapGestures { offset ->
                            if (series.isEmpty()) return@detectTapGestures
                            val idx = ((offset.x / size.width) * series.size)
                                .toInt()
                                .coerceIn(0, series.lastIndex)
                            onSelectMonth(idx)
                        }
                    }
            ) {
                if (series.isEmpty()) return@Canvas
                val values = series.map { it.netMinor.toFloat() }
                val minVal = values.minOrNull() ?: 0f
                val maxVal = values.maxOrNull() ?: 0f
                val span = (maxVal - minVal).takeIf { it > 0f } ?: 1f

                val points = if (!hasSeriesData) {
                    series.indices.map { i ->
                        val x = if (series.size <= 1) size.width / 2f
                        else (i.toFloat() / (series.size - 1)) * size.width
                        Offset(x, size.height * 0.72f)
                    }
                } else {
                    values.mapIndexed { i, v ->
                        val x = if (values.size <= 1) size.width / 2f
                        else (i.toFloat() / (values.size - 1)) * size.width
                        val normalized = (v - minVal) / span
                        val y = size.height - 14.dp.toPx() - normalized * (size.height - 28.dp.toPx())
                        Offset(x, y)
                    }
                }

                val linePath = Path().apply {
                    moveTo(points.first().x, points.first().y)
                    for (i in 1 until points.size) {
                        val prev = points[i - 1]
                        val curr = points[i]
                        val midX = (prev.x + curr.x) / 2f
                        cubicTo(midX, prev.y, midX, curr.y, curr.x, curr.y)
                    }
                }

                val fillPath = Path().apply {
                    addPath(linePath)
                    lineTo(points.last().x, size.height)
                    lineTo(points.first().x, size.height)
                    close()
                }

                drawPath(
                    path = fillPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            violetLine.copy(alpha = 0.34f),
                            violetLine.copy(alpha = 0.02f)
                        )
                    )
                )
                drawPath(
                    path = linePath,
                    color = violetLine,
                    style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                )
                points.forEachIndexed { idx, pt ->
                    val isSelected = selectedMonthIndex == idx
                    drawCircle(
                        color = Color.White,
                        radius = if (isSelected) 6.5.dp.toPx() else 4.5.dp.toPx(),
                        center = pt
                    )
                    drawCircle(
                        color = violetLine,
                        radius = if (isSelected) 4.5.dp.toPx() else 3.dp.toPx(),
                        center = pt
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                series.forEachIndexed { idx, point ->
                    val isSelected = selectedMonthIndex == idx
                    Text(
                        text = point.monthLabel,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) violetLine else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .clickable { onSelectMonth(idx) }
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun PillToggleChip(
    label: String,
    selected: Boolean,
    selectedBg: Color,
    selectedFg: Color,
    testTag: String,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(999.dp),
        color = if (selected) selectedBg else MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .clickable(onClick = onClick)
            .testTag(testTag)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = if (selected) selectedFg else MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            softWrap = false,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
        )
    }
}

@Composable
private fun ChartLegendDot(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            softWrap = false
        )
    }
}

@Composable
fun TransactionRowCard(
    transaction: TransactionEntity,
    currencyCode: String,
    dateFormatPattern: String,
    modifier: Modifier = Modifier
) {
    val extendedColors = LocalPocketBizColors.current
    val isIncome = transaction.type == TransactionType.INCOME
    val style = rememberCategoryStyle(transaction.category, transaction.type)
    val amountColor = if (isIncome) extendedColors.positive else extendedColors.negative

    val formattedDate = remember(transaction.transactionDateEpochMs, dateFormatPattern) {
        runCatching {
            SimpleDateFormat(dateFormatPattern, Locale.getDefault())
                .format(Date(transaction.transactionDateEpochMs))
        }.getOrElse {
            SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                .format(Date(transaction.transactionDateEpochMs))
        }
    }

    val signedAmountMinor = if (isIncome) transaction.amountMinor else -transaction.amountMinor
    val formattedAmount = FinancialCalculator.formatCurrency(
        minorUnits = signedAmountMinor,
        currencyCode = transaction.currencyCode.ifBlank { currencyCode },
        includePlusSign = isIncome
    )

    PocketBizCard(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(style.containerColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = style.icon,
                    contentDescription = transaction.category,
                    tint = style.tint,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = transaction.category,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                val subtitle = if (transaction.description.isNotBlank()) {
                    "${transaction.description} • $formattedDate"
                } else {
                    formattedDate
                }
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = formattedAmount,
                    style = MonoFinancialSmall.copy(fontWeight = FontWeight.Bold),
                    color = amountColor,
                    maxLines = 1,
                    softWrap = false
                )
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (isIncome) extendedColors.positiveContainer else extendedColors.negativeContainer
                ) {
                    Text(
                        text = if (isIncome) "Income" else "Expense",
                        style = MaterialTheme.typography.labelSmall,
                        color = amountColor,
                        maxLines = 1,
                        softWrap = false,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddTransactionBottomSheet(
    currencyCode: String,
    initialType: TransactionType,
    isSaving: Boolean,
    errorMessage: String?,
    onDismiss: () -> Unit,
    onSave: (type: TransactionType, category: String, amount: String, description: String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var selectedType by rememberSaveable(initialType) { mutableStateOf(initialType) }
    val categories = remember(selectedType) { TransactionCategories.forType(selectedType) }
    var selectedCategory by rememberSaveable(selectedType) { mutableStateOf(categories.first()) }
    var amountInput by rememberSaveable { mutableStateOf("") }
    var descriptionInput by rememberSaveable { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Receipt,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(8.dp)
                    )
                }
                Column {
                    Text(
                        text = "Record Transaction",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Saved directly to your offline ledger",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                TransactionType.entries.forEachIndexed { index, type ->
                    SegmentedButton(
                        selected = selectedType == type,
                        onClick = {
                            selectedType = type
                            selectedCategory = TransactionCategories.forType(type).first()
                        },
                        shape = SegmentedButtonDefaults.itemShape(
                            index = index,
                            count = TransactionType.entries.size
                        ),
                        modifier = Modifier.testTag(
                            if (type == TransactionType.INCOME) "type_income_tab" else "type_expense_tab"
                        )
                    ) {
                        Text(if (type == TransactionType.INCOME) "Income (+)" else "Expense (-)")
                    }
                }
            }

            OutlinedTextField(
                value = amountInput,
                onValueChange = { amountInput = it },
                label = { Text("Amount ($currencyCode)") },
                placeholder = { Text("0.00") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                textStyle = MonoFinancialMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("transaction_amount_input")
            )

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Category",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    categories.forEach { category ->
                        val selected = selectedCategory == category
                        val catStyle = rememberCategoryStyle(category, selectedType)
                        FilterChip(
                            selected = selected,
                            onClick = { selectedCategory = category },
                            label = { Text(category) },
                            leadingIcon = if (selected) {
                                {
                                    Icon(
                                        imageVector = Icons.Filled.Check,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            } else {
                                {
                                    Icon(
                                        imageVector = catStyle.icon,
                                        contentDescription = null,
                                        tint = catStyle.tint,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = catStyle.tint,
                                selectedLabelColor = Color.White,
                                selectedLeadingIconColor = Color.White
                            )
                        )
                    }
                }
            }

            OutlinedTextField(
                value = descriptionInput,
                onValueChange = { descriptionInput = it },
                label = { Text("Description (optional)") },
                placeholder = { Text("e.g., Retail order #104 or Supplier restock") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("transaction_description_input")
            )

            if (!errorMessage.isNullOrBlank()) {
                Text(
                    text = errorMessage,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.testTag("transaction_error_text")
                )
            }

            Button(
                onClick = {
                    onSave(selectedType, selectedCategory, amountInput, descriptionInput)
                },
                enabled = !isSaving,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("save_transaction_button")
            ) {
                Text(
                    text = if (isSaving) "Saving..." else "Save Transaction",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}
