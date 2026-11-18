package com.example.ui.modules

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.SwapHoriz
import androidx.compose.material.icons.outlined.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.CustomerEntity
import com.example.data.local.InvoiceEntity
import com.example.domain.calculator.FinancialCalculator
import com.example.domain.model.AnalyticsChartType
import com.example.domain.model.AnalyticsPeriod
import com.example.domain.model.InvoiceFilterStatus
import com.example.domain.model.TransactionFilterType
import com.example.domain.model.TransactionType
import com.example.ui.components.AdaptiveAmountText
import com.example.ui.components.InteractiveAnalyticsStudioCard
import com.example.ui.components.PocketBizCard
import com.example.ui.components.TransactionRowCard
import com.example.ui.theme.LocalPocketBizColors
import com.example.ui.theme.MonoFinancialLarge
import com.example.ui.theme.MonoFinancialMedium
import com.example.ui.theme.MonoFinancialSmall
import com.example.ui.viewmodel.MainUiState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
private fun ModuleHeader(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconTint: Color,
    iconBg: Color,
    badgeText: String? = null,
    actionLabel: String? = null,
    actionTag: String? = null,
    onActionClick: (() -> Unit)? = null
) {
    val extendedColors = LocalPocketBizColors.current
    Surface(
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
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
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(21.dp)
                    )
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                if (!actionLabel.isNullOrBlank() && onActionClick != null) {
                    Button(
                        onClick = onActionClick,
                        shape = RoundedCornerShape(999.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                        modifier = Modifier
                            .defaultMinSize(minHeight = 40.dp)
                            .then(if (actionTag != null) Modifier.testTag(actionTag) else Modifier)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = actionLabel,
                            style = MaterialTheme.typography.labelLarge,
                            maxLines = 1,
                            softWrap = false
                        )
                    }
                } else if (!badgeText.isNullOrBlank()) {
                    Surface(
                        shape = RoundedCornerShape(999.dp),
                        color = MaterialTheme.colorScheme.primaryContainer,
                        border = BorderStroke(1.dp, extendedColors.cardBorder)
                    ) {
                        Text(
                            text = badgeText,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            maxLines = 1,
                            softWrap = false,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }
                }
            }
            HorizontalDivider(color = extendedColors.cardBorder)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TransactionsScreen(
    uiState: MainUiState,
    onSearchQueryChange: (String) -> Unit,
    onFilterTypeChange: (TransactionFilterType) -> Unit,
    onFilterPeriodChange: (AnalyticsPeriod) -> Unit,
    onClearFilters: () -> Unit,
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState()
) {
    val colors = LocalPocketBizColors.current
    val hasActiveFilters = uiState.searchQuery.isNotBlank() ||
        uiState.filterType != TransactionFilterType.ALL ||
        uiState.filterPeriod != AnalyticsPeriod.ALL_TIME

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("transactions_screen")
    ) {
        ModuleHeader(
            title = "Transactions",
            subtitle = "${uiState.filteredTransactions.size} of ${uiState.allTransactions.size} entries shown",
            icon = Icons.Outlined.SwapHoriz,
            iconTint = colors.sky,
            iconBg = colors.skyContainer,
            badgeText = uiState.settings.currencyCode
        )

        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 112.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                PocketBizCard(
                    modifier = Modifier
                        .widthIn(max = 760.dp)
                        .fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = uiState.searchQuery,
                            onValueChange = onSearchQueryChange,
                            placeholder = { Text("Search category or notes...") },
                            leadingIcon = {
                                Icon(Icons.Filled.Search, contentDescription = "Search")
                            },
                            trailingIcon = {
                                if (uiState.searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { onSearchQueryChange("") }) {
                                        Icon(Icons.Filled.Clear, contentDescription = "Clear search")
                                    }
                                }
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("transactions_search_input")
                        )

                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            TransactionFilterType.entries.forEach { filter ->
                                val selected = uiState.filterType == filter
                                val chipColor = when (filter) {
                                    TransactionFilterType.ALL -> MaterialTheme.colorScheme.primary
                                    TransactionFilterType.INCOME -> colors.positive
                                    TransactionFilterType.EXPENSE -> colors.negative
                                }
                                FilterChip(
                                    selected = selected,
                                    onClick = { onFilterTypeChange(filter) },
                                    label = { Text(filter.label) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = chipColor,
                                        selectedLabelColor = Color.White
                                    ),
                                    modifier = Modifier.testTag("filter_type_${filter.name.lowercase()}")
                                )
                            }

                            AnalyticsPeriod.entries.forEach { period ->
                                val selected = uiState.filterPeriod == period
                                FilterChip(
                                    selected = selected,
                                    onClick = { onFilterPeriodChange(period) },
                                    label = { Text(period.label) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = colors.violet,
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }

                            if (hasActiveFilters) {
                                TextButton(onClick = onClearFilters) {
                                    Text("Clear all")
                                }
                            }
                        }
                    }
                }
            }

            if (uiState.filteredTransactions.isEmpty()) {
                item {
                    ProfessionalEmptyStateCard(
                        title = if (uiState.allTransactions.isEmpty()) {
                            "No Transactions Recorded"
                        } else {
                            "No Matching Transactions"
                        },
                        description = if (uiState.allTransactions.isEmpty()) {
                            "Tap the + button to record your first business income or expense entry."
                        } else {
                            "Try clearing your search query or adjusting the active category and date filters."
                        },
                        iconTint = colors.sky,
                        iconContainer = colors.skyContainer,
                        icon = Icons.Outlined.SwapHoriz
                    )
                }
            } else {
                items(items = uiState.filteredTransactions, key = { it.id }) { tx ->
                    TransactionRowCard(
                        transaction = tx,
                        currencyCode = uiState.settings.currencyCode,
                        dateFormatPattern = uiState.settings.dateFormatPattern,
                        modifier = Modifier.widthIn(max = 760.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun InvoicesScreen(
    uiState: MainUiState,
    onSearchQueryChange: (String) -> Unit,
    onStatusFilterChange: (InvoiceFilterStatus) -> Unit,
    onClearFilters: () -> Unit,
    onCreateInvoiceClick: () -> Unit,
    onInvoiceClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalPocketBizColors.current
    val nextInvoiceLabel = "${uiState.settings.invoicePrefix}${uiState.settings.nextInvoiceNumber}"
    val customersById = remember(uiState.customers) { uiState.customers.associateBy { it.id } }
    val hasActiveFilters = uiState.invoiceSearchQuery.isNotBlank() ||
        uiState.invoiceStatusFilter != InvoiceFilterStatus.ALL

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("invoices_screen")
    ) {
        ModuleHeader(
            title = "Invoices",
            subtitle = "${uiState.filteredInvoices.size} of ${uiState.invoices.size} invoices • ${uiState.settings.currencyCode}",
            icon = Icons.AutoMirrored.Outlined.ReceiptLong,
            iconTint = colors.sky,
            iconBg = colors.skyContainer,
            actionLabel = "New",
            actionTag = "add_invoice_button",
            onActionClick = onCreateInvoiceClick
        )
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Row(
                    modifier = Modifier
                        .widthIn(max = 760.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    TintedSummaryTile(
                        title = "Total Invoices",
                        value = uiState.metrics.totalInvoicesCount.toString(),
                        tint = colors.sky,
                        container = colors.skyContainer,
                        modifier = Modifier.weight(1f)
                    )
                    TintedSummaryTile(
                        title = "Next Number",
                        value = nextInvoiceLabel,
                        tint = colors.violet,
                        container = colors.violetContainer,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            item {
                TintedSummaryTile(
                    title = "Outstanding Invoice Balance",
                    value = FinancialCalculator.formatCurrency(
                        uiState.metrics.outstandingCustomerPaymentsMinor,
                        uiState.settings.currencyCode
                    ),
                    tint = colors.amber,
                    container = colors.amberContainer,
                    modifier = Modifier
                        .widthIn(max = 760.dp)
                        .fillMaxWidth()
                )
            }

            if (uiState.invoices.isNotEmpty()) {
                item {
                    PocketBizCard(
                        modifier = Modifier
                            .widthIn(max = 760.dp)
                            .fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedTextField(
                                value = uiState.invoiceSearchQuery,
                                onValueChange = onSearchQueryChange,
                                placeholder = { Text("Search invoice # or customer...") },
                                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Search invoices") },
                                trailingIcon = {
                                    if (uiState.invoiceSearchQuery.isNotEmpty()) {
                                        IconButton(onClick = { onSearchQueryChange("") }) {
                                            Icon(Icons.Filled.Clear, contentDescription = "Clear search")
                                        }
                                    }
                                },
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("invoice_search_input")
                            )

                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                InvoiceFilterStatus.entries.forEach { statusFilter ->
                                    val selected = uiState.invoiceStatusFilter == statusFilter
                                    FilterChip(
                                        selected = selected,
                                        onClick = { onStatusFilterChange(statusFilter) },
                                        label = { Text(statusFilter.label) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                        ),
                                        modifier = Modifier.testTag("invoice_filter_${statusFilter.name.lowercase()}")
                                    )
                                }
                                if (hasActiveFilters) {
                                    TextButton(onClick = onClearFilters) {
                                        Text("Clear all")
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (uiState.filteredInvoices.isEmpty()) {
                item {
                    ProfessionalEmptyStateCard(
                        title = if (uiState.invoices.isEmpty()) {
                            "No Invoices Created Yet"
                        } else {
                            "No Matching Invoices"
                        },
                        description = if (uiState.invoices.isEmpty()) {
                            "Create customer invoices starting at $nextInvoiceLabel with automatic tax (${uiState.settings.defaultTaxRatePercent}%), PDF sharing, and payment tracking."
                        } else {
                            "Try clearing your search query or selecting a different invoice status filter."
                        },
                        iconTint = colors.amber,
                        iconContainer = colors.amberContainer,
                        icon = Icons.AutoMirrored.Outlined.ReceiptLong,
                        actionLabel = if (uiState.invoices.isEmpty()) "Create First Invoice" else "Clear Filters",
                        actionTag = "empty_create_invoice_button",
                        onActionClick = if (uiState.invoices.isEmpty()) onCreateInvoiceClick else onClearFilters
                    )
                }
                if (uiState.invoices.isEmpty()) {
                    item {
                        HowInvoicesWorkCard(nextInvoiceLabel = nextInvoiceLabel)
                    }
                }
            } else {
                items(items = uiState.filteredInvoices, key = { it.id }) { invoice ->
                    val customer = customersById[invoice.customerId]
                    InvoiceRowCard(
                        invoice = invoice,
                        customerName = customer?.name ?: "Customer",
                        currencyCode = uiState.settings.currencyCode,
                        dateFormatPattern = uiState.settings.dateFormatPattern,
                        onClick = { onInvoiceClick(invoice.id) },
                        modifier = Modifier.widthIn(max = 760.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun HowInvoicesWorkCard(nextInvoiceLabel: String) {
    val colors = LocalPocketBizColors.current
    PocketBizCard(
        modifier = Modifier
            .widthIn(max = 760.dp)
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "How Invoices Work",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            GuideStepRow(
                step = "1",
                title = "Select or Quick-Add a Customer",
                detail = "Pick an existing client or create one inline without leaving the invoice form."
            )
            GuideStepRow(
                step = "2",
                title = "Add Items, Discount & Tax ($nextInvoiceLabel)",
                detail = "Enter quantities and unit prices; subtotals and taxes compute automatically."
            )
            GuideStepRow(
                step = "3",
                title = "Share PDF & Record Payments",
                detail = "Export or print a PDF invoice, and record payments to auto-update Dashboard revenue."
            )
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = colors.skyContainer
            ) {
                Text(
                    text = "Tip: Recording a payment on an invoice automatically adds an Income entry to your ledger.",
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.sky,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                )
            }
        }
    }
}

@Composable
private fun GuideStepRow(
    step: String,
    title: String,
    detail: String
) {
    Row(
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(26.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = step,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = detail,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun InvoiceRowCard(
    invoice: InvoiceEntity,
    customerName: String,
    currencyCode: String,
    dateFormatPattern: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalPocketBizColors.current
    val remaining = (invoice.totalMinor - invoice.paidAmountMinor).coerceAtLeast(0L)
    val formatter = remember(dateFormatPattern) {
        runCatching { SimpleDateFormat(dateFormatPattern, Locale.getDefault()) }
            .getOrElse { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }
    }

    PocketBizCard(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("invoice_card_${invoice.invoiceNumber}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(colors.skyContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ReceiptLong,
                    contentDescription = null,
                    tint = colors.sky,
                    modifier = Modifier.size(20.dp)
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = invoice.invoiceNumber,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    InvoiceStatusPill(status = invoice.status)
                }
                Text(
                    text = customerName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Due ${formatter.format(Date(invoice.dueDateEpochMs))}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(2.dp),
                modifier = Modifier.widthIn(min = 96.dp, max = 148.dp)
            ) {
                AdaptiveAmountText(
                    text = FinancialCalculator.formatCurrency(invoice.totalMinor, currencyCode),
                    style = MonoFinancialSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxFontSize = 15.sp,
                    minFontSize = 10.sp,
                    textAlign = TextAlign.End,
                    modifier = Modifier.fillMaxWidth(),
                    textModifier = Modifier.fillMaxWidth()
                )
                if (remaining > 0L) {
                    Text(
                        text = "Due ${FinancialCalculator.formatCurrency(remaining, currencyCode)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = colors.amber,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                } else {
                    Text(
                        text = "Paid in full",
                        style = MaterialTheme.typography.labelSmall,
                        color = colors.positive,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

@Composable
fun CustomersScreen(
    uiState: MainUiState,
    onSearchQueryChange: (String) -> Unit,
    onAddCustomerClick: () -> Unit,
    onCustomerClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalPocketBizColors.current
    val invoicesByCustomer = remember(uiState.invoices) { uiState.invoices.groupBy { it.customerId } }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("customers_screen")
    ) {
        ModuleHeader(
            title = "Customers",
            subtitle = "${uiState.filteredCustomers.size} of ${uiState.customers.size} profiles • ${uiState.settings.currencyCode}",
            icon = Icons.Outlined.People,
            iconTint = colors.violet,
            iconBg = colors.violetContainer,
            actionLabel = "Add",
            actionTag = "add_customer_button",
            onActionClick = onAddCustomerClick
        )
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Row(
                    modifier = Modifier
                        .widthIn(max = 760.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    TintedSummaryTile(
                        title = "Total Customers",
                        value = uiState.metrics.totalCustomersCount.toString(),
                        tint = colors.violet,
                        container = colors.violetContainer,
                        modifier = Modifier.weight(1f)
                    )
                    TintedSummaryTile(
                        title = "Receivables",
                        value = FinancialCalculator.formatCurrency(
                            uiState.metrics.outstandingCustomerPaymentsMinor,
                            uiState.settings.currencyCode
                        ),
                        tint = colors.positive,
                        container = colors.positiveContainer,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            if (uiState.customers.isNotEmpty()) {
                item {
                    PocketBizCard(
                        modifier = Modifier
                            .widthIn(max = 760.dp)
                            .fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = uiState.customerSearchQuery,
                            onValueChange = onSearchQueryChange,
                            placeholder = { Text("Search name, phone, or email...") },
                            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Search customers") },
                            trailingIcon = {
                                if (uiState.customerSearchQuery.isNotEmpty()) {
                                    IconButton(onClick = { onSearchQueryChange("") }) {
                                        Icon(Icons.Filled.Clear, contentDescription = "Clear search")
                                    }
                                }
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp)
                                .testTag("customer_search_input")
                        )
                    }
                }
            }

            if (uiState.filteredCustomers.isEmpty()) {
                item {
                    ProfessionalEmptyStateCard(
                        title = if (uiState.customers.isEmpty()) {
                            "No Customers Added Yet"
                        } else {
                            "No Matching Customers"
                        },
                        description = if (uiState.customers.isEmpty()) {
                            "Add customer profiles with phone, email, and billing details to issue invoices and track outstanding balances."
                        } else {
                            "No customer matched \"${uiState.customerSearchQuery}\". Try clearing the search box."
                        },
                        iconTint = colors.violet,
                        iconContainer = colors.violetContainer,
                        icon = Icons.Outlined.People,
                        actionLabel = if (uiState.customers.isEmpty()) "Add Customer" else "Clear Search",
                        actionTag = "empty_add_customer_button",
                        onActionClick = if (uiState.customers.isEmpty()) onAddCustomerClick else ({ onSearchQueryChange("") })
                    )
                }
            } else {
                items(items = uiState.filteredCustomers, key = { it.id }) { customer ->
                    val linkedInvoices = invoicesByCustomer[customer.id].orEmpty()
                    CustomerRowCard(
                        customer = customer,
                        linkedInvoices = linkedInvoices,
                        currencyCode = uiState.settings.currencyCode,
                        onClick = { onCustomerClick(customer.id) },
                        modifier = Modifier.widthIn(max = 760.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun CustomerRowCard(
    customer: CustomerEntity,
    linkedInvoices: List<InvoiceEntity>,
    currencyCode: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalPocketBizColors.current
    val totalBilled = linkedInvoices.sumOf { it.totalMinor }
    val balanceDue = linkedInvoices.sumOf { (it.totalMinor - it.paidAmountMinor).coerceAtLeast(0L) }
    val contactLine = listOf(customer.phone, customer.email)
        .filter { it.isNotBlank() }
        .joinToString(" • ")
        .ifBlank { "${linkedInvoices.size} invoice(s)" }

    PocketBizCard(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("customer_card_${customer.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(colors.violetContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = customer.name.take(2).uppercase(Locale.getDefault()),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = colors.violet
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Text(
                    text = customer.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = contactLine,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${linkedInvoices.size} invoice(s) • Billed ${FinancialCalculator.formatCurrency(totalBilled, currencyCode)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(2.dp),
                modifier = Modifier.widthIn(min = 84.dp, max = 140.dp)
            ) {
                AdaptiveAmountText(
                    text = FinancialCalculator.formatCurrency(balanceDue, currencyCode),
                    style = MonoFinancialSmall.copy(fontWeight = FontWeight.Bold),
                    color = if (balanceDue > 0L) colors.amber else colors.positive,
                    maxFontSize = 14.sp,
                    minFontSize = 10.sp,
                    textAlign = TextAlign.End,
                    modifier = Modifier.fillMaxWidth(),
                    textModifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = if (balanceDue > 0L) "Balance Due" else "Settled",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (balanceDue > 0L) colors.amber else colors.positive,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun ReportsScreen(
    uiState: MainUiState,
    onChartTypeChange: (AnalyticsChartType) -> Unit,
    onBreakdownTypeChange: (TransactionType) -> Unit,
    onBreakdownPeriodChange: (AnalyticsPeriod) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalPocketBizColors.current
    val currency = uiState.settings.currencyCode
    val metrics = uiState.metrics

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("reports_screen")
    ) {
        ModuleHeader(
            title = "Reports",
            subtitle = "Live Profit & Loss",
            icon = Icons.Outlined.BarChart,
            iconTint = colors.indigo,
            iconBg = MaterialTheme.colorScheme.primaryContainer,
            badgeText = currency
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 36.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                BoxWithConstraints(
                    modifier = Modifier
                        .widthIn(max = 760.dp)
                        .fillMaxWidth()
                ) {
                    val isWide = maxWidth >= 600.dp
                    if (isWide) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            TintedSummaryTile(
                                title = "Monthly Revenue",
                                value = FinancialCalculator.formatCurrency(metrics.monthlyRevenueMinor, currency),
                                tint = colors.positive,
                                container = colors.positiveContainer,
                                modifier = Modifier.weight(1f)
                            )
                            TintedSummaryTile(
                                title = "Monthly Expenses",
                                value = FinancialCalculator.formatCurrency(metrics.monthlyExpensesMinor, currency),
                                tint = colors.negative,
                                container = colors.negativeContainer,
                                modifier = Modifier.weight(1f)
                            )
                            TintedSummaryTile(
                                title = "Net Margin",
                                value = metrics.profitMarginPercent,
                                tint = colors.sky,
                                container = colors.skyContainer,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    } else {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                TintedSummaryTile(
                                    title = "Revenue",
                                    value = FinancialCalculator.formatCurrency(metrics.monthlyRevenueMinor, currency),
                                    tint = colors.positive,
                                    container = colors.positiveContainer,
                                    modifier = Modifier.weight(1f)
                                )
                                TintedSummaryTile(
                                    title = "Expenses",
                                    value = FinancialCalculator.formatCurrency(metrics.monthlyExpensesMinor, currency),
                                    tint = colors.negative,
                                    container = colors.negativeContainer,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                TintedSummaryTile(
                                    title = "Net Margin",
                                    value = metrics.profitMarginPercent,
                                    tint = colors.sky,
                                    container = colors.skyContainer,
                                    modifier = Modifier.weight(1f)
                                )
                                TintedSummaryTile(
                                    title = "Expense Ratio",
                                    value = metrics.expenseRatioPercent,
                                    tint = colors.violet,
                                    container = colors.violetContainer,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }

            item {
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
                    onBreakdownPeriodChange = onBreakdownPeriodChange,
                    modifier = Modifier.widthIn(max = 760.dp)
                )
            }

            item {
                ProfitAndLossStatementCard(
                    incomeFormatted = FinancialCalculator.formatCurrency(metrics.monthlyRevenueMinor, currency),
                    expensesFormatted = FinancialCalculator.formatCurrency(metrics.monthlyExpensesMinor, currency),
                    netProfitFormatted = FinancialCalculator.formatCurrency(
                        metrics.monthlyNetProfitMinor,
                        currency,
                        includePlusSign = metrics.monthlyNetProfitMinor > 0L
                    ),
                    isProfitable = metrics.monthlyNetProfitMinor >= 0L
                )
            }

            item {
                ReportDetailCard(
                    title = "Cash Flow Balance (All-Time)",
                    subtitle = "Cumulative inflows minus outflows across ${metrics.totalTransactionsCount} recorded entries.",
                    amount = FinancialCalculator.formatCurrency(metrics.totalAllTimeBalanceMinor, currency),
                    tint = colors.sky,
                    container = colors.skyContainer,
                    icon = Icons.Filled.AccountBalance
                )
            }

            item {
                ReportDetailCard(
                    title = "Today's Expense Outflow",
                    subtitle = "Total operating expenses recorded today in $currency.",
                    amount = FinancialCalculator.formatCurrency(metrics.todayExpensesMinor, currency),
                    tint = colors.negative,
                    container = colors.negativeContainer,
                    icon = Icons.AutoMirrored.Filled.TrendingDown
                )
            }

            item {
                PocketBizCard(
                    modifier = Modifier
                        .widthIn(max = 760.dp)
                        .fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(colors.positiveContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.VerifiedUser,
                                contentDescription = null,
                                tint = colors.positive,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Decimal-Exact Local Accounting",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "All totals are computed offline from ${metrics.totalTransactionsCount} verified ledger entries without rounding drift.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TintedSummaryTile(
    title: String,
    value: String,
    tint: Color,
    container: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = container),
        border = BorderStroke(1.dp, tint.copy(alpha = 0.25f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = tint,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            AdaptiveAmountText(
                text = value,
                style = MonoFinancialMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface,
                maxFontSize = 17.sp,
                minFontSize = 11.sp,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun ProfitAndLossStatementCard(
    incomeFormatted: String,
    expensesFormatted: String,
    netProfitFormatted: String,
    isProfitable: Boolean
) {
    val colors = LocalPocketBizColors.current
    val netTint = if (isProfitable) colors.positive else colors.negative

    PocketBizCard(
        modifier = Modifier
            .widthIn(max = 760.dp)
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f).padding(end = 8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(colors.positiveContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                            contentDescription = null,
                            tint = colors.positive,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "Profit & Loss",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "This Month Statement",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Surface(
                    shape = RoundedCornerShape(999.dp),
                    color = if (isProfitable) colors.positiveContainer else colors.negativeContainer
                ) {
                    Text(
                        text = if (isProfitable) "Net Surplus" else "Net Deficit",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = netTint,
                        maxLines = 1,
                        softWrap = false,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            HorizontalDivider(color = colors.cardBorder)

            StatementLineRow(
                label = "Monthly Income",
                amount = incomeFormatted,
                amountColor = colors.positive
            )
            StatementLineRow(
                label = "Monthly Expenses",
                amount = expensesFormatted,
                amountColor = colors.negative
            )

            HorizontalDivider(color = colors.cardBorder)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Net Profit",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(end = 12.dp)
                )
                AdaptiveAmountText(
                    text = netProfitFormatted,
                    style = MonoFinancialMedium.copy(fontWeight = FontWeight.Bold),
                    color = netTint,
                    maxFontSize = 18.sp,
                    minFontSize = 12.sp,
                    textAlign = TextAlign.End,
                    modifier = Modifier.weight(1f),
                    textModifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun StatementLineRow(
    label: String,
    amount: String,
    amountColor: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(end = 12.dp)
        )
        AdaptiveAmountText(
            text = amount,
            style = MonoFinancialSmall.copy(fontWeight = FontWeight.SemiBold),
            color = amountColor,
            maxFontSize = 14.sp,
            minFontSize = 11.sp,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1f),
            textModifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun ReportDetailCard(
    title: String,
    subtitle: String,
    amount: String,
    tint: Color,
    container: Color,
    icon: ImageVector
) {
    PocketBizCard(
        modifier = Modifier
            .widthIn(max = 760.dp)
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(container),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = tint,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
            }
            AdaptiveAmountText(
                text = amount,
                style = MonoFinancialLarge,
                color = tint,
                maxFontSize = 22.sp,
                minFontSize = 14.sp,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ProfessionalEmptyStateCard(
    title: String,
    description: String,
    iconTint: Color,
    iconContainer: Color,
    icon: ImageVector,
    actionLabel: String? = null,
    actionTag: String? = null,
    onActionClick: (() -> Unit)? = null
) {
    PocketBizCard(
        modifier = Modifier
            .widthIn(max = 760.dp)
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(iconContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            if (!actionLabel.isNullOrBlank() && onActionClick != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Button(
                    onClick = onActionClick,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .height(48.dp)
                        .then(if (actionTag != null) Modifier.testTag(actionTag) else Modifier)
                ) {
                    Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(actionLabel)
                }
            }
        }
    }
}
