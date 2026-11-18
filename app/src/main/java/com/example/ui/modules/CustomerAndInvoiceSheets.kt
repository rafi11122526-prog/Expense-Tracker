package com.example.ui.modules

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.CustomerEntity
import com.example.data.local.InvoiceEntity
import com.example.data.local.InvoiceItemEntity
import com.example.data.local.PaymentEntity
import com.example.domain.calculator.FinancialCalculator
import com.example.domain.model.BusinessSettings
import com.example.domain.model.InvoiceLineInput
import com.example.domain.model.InvoiceStatus
import com.example.ui.components.AdaptiveAmountText
import com.example.ui.components.PocketBizCard
import com.example.ui.theme.LocalPocketBizColors
import com.example.ui.theme.MonoFinancialMedium
import com.example.ui.theme.MonoFinancialSmall
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerFormBottomSheet(
    editingCustomer: CustomerEntity?,
    isSaving: Boolean,
    errorMessage: String?,
    onDismiss: () -> Unit,
    onSave: (name: String, phone: String, email: String, address: String, notes: String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val colors = LocalPocketBizColors.current
    var name by rememberSaveable(editingCustomer?.id) { mutableStateOf(editingCustomer?.name ?: "") }
    var phone by rememberSaveable(editingCustomer?.id) { mutableStateOf(editingCustomer?.phone ?: "") }
    var email by rememberSaveable(editingCustomer?.id) { mutableStateOf(editingCustomer?.email ?: "") }
    var address by rememberSaveable(editingCustomer?.id) { mutableStateOf(editingCustomer?.address ?: "") }
    var notes by rememberSaveable(editingCustomer?.id) { mutableStateOf(editingCustomer?.notes ?: "") }

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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = if (editingCustomer == null) "Add Customer" else "Edit Customer",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Save contact details to issue invoices and track customer balances.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Customer / Business Name *") },
                placeholder = { Text("e.g., Al-Noor Traders") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("customer_name_input")
            )

            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Phone Number") },
                placeholder = { Text("e.g., +92 300 1234567") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("customer_phone_input")
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email Address") },
                placeholder = { Text("billing@customer.com") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("customer_email_input")
            )

            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text("Billing Address") },
                placeholder = { Text("Street, City") },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("customer_address_input")
            )

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notes (Optional)") },
                placeholder = { Text("Payment terms or preferred items") },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("customer_notes_input")
            )

            if (!errorMessage.isNullOrBlank()) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = colors.negativeContainer,
                    border = BorderStroke(1.dp, colors.negativeBorder)
                ) {
                    Text(
                        text = errorMessage,
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.negative,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                    )
                }
            }

            Button(
                onClick = { onSave(name, phone, email, address, notes) },
                enabled = !isSaving,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("save_customer_button")
            ) {
                Text(
                    text = when {
                        isSaving -> "Saving..."
                        editingCustomer == null -> "Save Customer"
                        else -> "Update Customer"
                    },
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerDetailBottomSheet(
    customer: CustomerEntity,
    customerInvoices: List<InvoiceEntity>,
    currencyCode: String,
    dateFormatPattern: String,
    onDismiss: () -> Unit,
    onCreateInvoice: () -> Unit,
    onEditCustomer: () -> Unit,
    onDeleteCustomer: () -> Unit,
    onOpenInvoice: (Long) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val colors = LocalPocketBizColors.current
    val totalBilled = customerInvoices.sumOf { it.totalMinor }
    val totalPaid = customerInvoices.sumOf { it.paidAmountMinor }
    val outstanding = customerInvoices.sumOf { (it.totalMinor - it.paidAmountMinor).coerceAtLeast(0L) }

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
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(colors.violetContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = customer.name.take(2).uppercase(Locale.getDefault()),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = colors.violet
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = customer.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${customerInvoices.size} invoice(s) linked",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (customer.phone.isNotBlank() || customer.email.isNotBlank() || customer.address.isNotBlank() || customer.notes.isNotBlank()) {
                PocketBizCard {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (customer.phone.isNotBlank()) {
                            ContactInfoRow(Icons.Filled.Phone, customer.phone)
                        }
                        if (customer.email.isNotBlank()) {
                            ContactInfoRow(Icons.Filled.Email, customer.email)
                        }
                        if (customer.address.isNotBlank()) {
                            ContactInfoRow(Icons.Filled.LocationOn, customer.address)
                        }
                        if (customer.notes.isNotBlank()) {
                            Text(
                                text = customer.notes,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MiniMetricBox(
                    label = "Total Billed",
                    amount = FinancialCalculator.formatCurrency(totalBilled, currencyCode),
                    tint = colors.sky,
                    container = colors.skyContainer,
                    modifier = Modifier.weight(1f)
                )
                MiniMetricBox(
                    label = "Paid",
                    amount = FinancialCalculator.formatCurrency(totalPaid, currencyCode),
                    tint = colors.positive,
                    container = colors.positiveContainer,
                    modifier = Modifier.weight(1f)
                )
                MiniMetricBox(
                    label = "Balance Due",
                    amount = FinancialCalculator.formatCurrency(outstanding, currencyCode),
                    tint = if (outstanding > 0L) colors.amber else colors.positive,
                    container = if (outstanding > 0L) colors.amberContainer else colors.positiveContainer,
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onCreateInvoice,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("customer_new_invoice_button")
                ) {
                    Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("New Invoice", maxLines = 1)
                }
                OutlinedButton(
                    onClick = onEditCustomer,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .height(48.dp)
                        .testTag("edit_customer_button")
                ) {
                    Icon(Icons.Filled.Edit, contentDescription = "Edit Customer", modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Edit")
                }
                OutlinedButton(
                    onClick = onDeleteCustomer,
                    enabled = customerInvoices.isEmpty(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = colors.negative),
                    modifier = Modifier
                        .height(48.dp)
                        .testTag("delete_customer_button")
                ) {
                    Icon(Icons.Filled.DeleteOutline, contentDescription = "Delete Customer", modifier = Modifier.size(18.dp))
                }
            }

            if (customerInvoices.isNotEmpty()) {
                Text(
                    text = "Invoices for ${customer.name}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                customerInvoices.forEach { invoice ->
                    InvoiceSummaryMiniRow(
                        invoice = invoice,
                        currencyCode = currencyCode,
                        dateFormatPattern = dateFormatPattern,
                        onClick = { onOpenInvoice(invoice.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ContactInfoRow(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun MiniMetricBox(
    label: String,
    amount: String,
    tint: Color,
    container: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = container,
        border = BorderStroke(1.dp, tint.copy(alpha = 0.25f))
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = tint,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            AdaptiveAmountText(
                text = amount,
                style = MonoFinancialSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface,
                maxFontSize = 13.sp,
                minFontSize = 9.sp,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun InvoiceSummaryMiniRow(
    invoice: InvoiceEntity,
    currencyCode: String,
    dateFormatPattern: String,
    onClick: () -> Unit
) {
    val formatter = remember(dateFormatPattern) {
        runCatching { SimpleDateFormat(dateFormatPattern, Locale.getDefault()) }
            .getOrElse { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }
    }
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        border = BorderStroke(1.dp, LocalPocketBizColors.current.cardBorder),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = invoice.invoiceNumber,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${invoice.status.name} • ${formatter.format(Date(invoice.issueDateEpochMs))}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            AdaptiveAmountText(
                text = FinancialCalculator.formatCurrency(invoice.totalMinor, currencyCode),
                style = MonoFinancialSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface,
                maxFontSize = 14.sp,
                minFontSize = 10.sp,
                textAlign = TextAlign.End,
                modifier = Modifier.width(120.dp),
                textModifier = Modifier.fillMaxWidth()
            )
        }
    }
}

private class DraftLineItemState(
    val id: Int,
    initialDescription: String = "",
    initialQty: String = "1",
    initialUnitPrice: String = ""
) {
    var description by mutableStateOf(initialDescription)
    var quantity by mutableStateOf(initialQty)
    var unitPrice by mutableStateOf(initialUnitPrice)
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CreateInvoiceBottomSheet(
    settings: BusinessSettings,
    customers: List<CustomerEntity>,
    preselectedCustomerId: Long?,
    isSavingCustomer: Boolean,
    isSavingInvoice: Boolean,
    errorMessage: String?,
    onDismiss: () -> Unit,
    onQuickAddCustomer: (name: String, phone: String, email: String, onCreated: (Long) -> Unit) -> Unit,
    onCreateInvoice: (
        customerId: Long,
        lines: List<InvoiceLineInput>,
        discountInput: String,
        taxPercentInput: String,
        dueDaysFromNow: Int,
        issueImmediately: Boolean,
        notes: String
    ) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val colors = LocalPocketBizColors.current
    val nextNumberLabel = "${settings.invoicePrefix}${settings.nextInvoiceNumber}"

    var selectedCustomerId by rememberSaveable {
        mutableStateOf(preselectedCustomerId ?: customers.firstOrNull()?.id ?: 0L)
    }
    LaunchedEffect(preselectedCustomerId, customers) {
        if (preselectedCustomerId != null && preselectedCustomerId > 0L) {
            selectedCustomerId = preselectedCustomerId
        } else if (selectedCustomerId <= 0L && customers.isNotEmpty()) {
            selectedCustomerId = customers.first().id
        }
    }

    var showQuickAddCustomer by rememberSaveable { mutableStateOf(customers.isEmpty()) }
    var quickName by rememberSaveable { mutableStateOf("") }
    var quickPhone by rememberSaveable { mutableStateOf("") }
    var quickEmail by rememberSaveable { mutableStateOf("") }

    var issueImmediately by rememberSaveable { mutableStateOf(true) }
    var dueDays by rememberSaveable { mutableIntStateOf(7) }
    var discountInput by rememberSaveable { mutableStateOf("0") }
    var taxInput by rememberSaveable { mutableStateOf(settings.defaultTaxRatePercent) }
    var notes by rememberSaveable { mutableStateOf("") }
    var localValidationError by remember { mutableStateOf<String?>(null) }

    var nextLineId by remember { mutableIntStateOf(2) }
    val lineItems = remember {
        mutableStateListOf(DraftLineItemState(id = 1))
    }

    val liveLineTotals = lineItems.map { row ->
        val qty = FinancialCalculator.parsePositiveQuantityMilli(row.quantity) ?: 0L
        val price = FinancialCalculator.parsePositiveAmountToMinorUnits(row.unitPrice) ?: 0L
        if (qty > 0L && price > 0L) FinancialCalculator.calculateLineTotalMinor(qty, price) else 0L
    }
    val liveDiscount = FinancialCalculator.parseNonNegativeAmountToMinorUnits(discountInput) ?: 0L
    val liveTaxBp = FinancialCalculator.parseTaxPercentToBasisPoints(taxInput) ?: 0
    val liveTotals = FinancialCalculator.calculateInvoiceTotals(
        lineTotalsMinor = liveLineTotals,
        discountMinor = liveDiscount,
        taxRateBasisPoints = liveTaxBp
    )

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
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Create Invoice",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "From ${settings.businessName.ifBlank { "PocketBiz" }}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Surface(
                    shape = RoundedCornerShape(999.dp),
                    color = colors.violetContainer
                ) {
                    Text(
                        text = nextNumberLabel,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = colors.violet,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }

            // Customer picker + inline quick-add
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Bill To Customer *",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                TextButton(
                    onClick = { showQuickAddCustomer = !showQuickAddCustomer },
                    modifier = Modifier.testTag("create_invoice_quick_add_customer")
                ) {
                    Icon(Icons.Filled.PersonAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (showQuickAddCustomer) "Hide Quick Add" else "+ Quick Add Customer")
                }
            }

            if (customers.isNotEmpty()) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    customers.forEach { customer ->
                        val selected = selectedCustomerId == customer.id
                        FilterChip(
                            selected = selected,
                            onClick = { selectedCustomerId = customer.id },
                            label = { Text(customer.name, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            modifier = Modifier.testTag("invoice_customer_chip_${customer.id}")
                        )
                    }
                }
            }

            AnimatedVisibility(visible = showQuickAddCustomer) {
                PocketBizCard(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Quick Add Customer",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        OutlinedTextField(
                            value = quickName,
                            onValueChange = { quickName = it },
                            label = { Text("Customer Name *") },
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("quick_customer_name_input")
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = quickPhone,
                                onValueChange = { quickPhone = it },
                                label = { Text("Phone") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("quick_customer_phone_input")
                            )
                            OutlinedTextField(
                                value = quickEmail,
                                onValueChange = { quickEmail = it },
                                label = { Text("Email") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Button(
                            onClick = {
                                if (quickName.isBlank()) {
                                    localValidationError = "Enter a customer name before saving."
                                    return@Button
                                }
                                localValidationError = null
                                onQuickAddCustomer(quickName, quickPhone, quickEmail) { newId ->
                                    selectedCustomerId = newId
                                    quickName = ""
                                    quickPhone = ""
                                    quickEmail = ""
                                    showQuickAddCustomer = false
                                }
                            },
                            enabled = !isSavingCustomer,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("save_quick_customer_button")
                        ) {
                            Text(if (isSavingCustomer) "Adding..." else "Save & Select Customer")
                        }
                    }
                }
            }

            // Issue Status & Due Terms
            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                SegmentedButton(
                    selected = issueImmediately,
                    onClick = { issueImmediately = true },
                    shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
                    label = { Text("Issue Now (Sent)") }
                )
                SegmentedButton(
                    selected = !issueImmediately,
                    onClick = { issueImmediately = false },
                    shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                    label = { Text("Save Draft") }
                )
            }

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(0 to "Due Today", 7 to "7 Days", 14 to "14 Days", 30 to "30 Days").forEach { (days, label) ->
                    FilterChip(
                        selected = dueDays == days,
                        onClick = { dueDays = days },
                        label = { Text(label) }
                    )
                }
            }

            HorizontalDivider(color = colors.cardBorder)

            // Line Items
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Products / Services",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                TextButton(
                    onClick = {
                        lineItems.add(DraftLineItemState(id = nextLineId++))
                    },
                    modifier = Modifier.testTag("add_invoice_item_button")
                ) {
                    Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Item")
                }
            }

            lineItems.forEachIndexed { index, row ->
                val lineMinor = liveLineTotals.getOrElse(index) { 0L }
                PocketBizCard(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Item #${index + 1}",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = FinancialCalculator.formatCurrency(lineMinor, settings.currencyCode),
                                    style = MonoFinancialSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.primary
                                )
                                if (lineItems.size > 1) {
                                    IconButton(
                                        onClick = { lineItems.removeAt(index) },
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(
                                            Icons.Filled.DeleteOutline,
                                            contentDescription = "Remove item",
                                            tint = colors.negative,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }

                        OutlinedTextField(
                            value = row.description,
                            onValueChange = { row.description = it },
                            label = { Text("Item or Service Description *") },
                            placeholder = { Text("e.g., Website Setup or Wholesale Carton") },
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("invoice_item_desc_$index")
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = row.quantity,
                                onValueChange = { row.quantity = it },
                                label = { Text("Qty *") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .weight(0.9f)
                                    .testTag("invoice_item_qty_$index")
                            )
                            OutlinedTextField(
                                value = row.unitPrice,
                                onValueChange = { row.unitPrice = it },
                                label = { Text("Unit Price (${settings.currencyCode}) *") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .weight(1.4f)
                                    .testTag("invoice_item_price_$index")
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = discountInput,
                    onValueChange = { discountInput = it },
                    label = { Text("Discount (${settings.currencyCode})") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("invoice_discount_input")
                )
                OutlinedTextField(
                    value = taxInput,
                    onValueChange = { taxInput = it },
                    label = { Text("Tax (%)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("invoice_tax_input")
                )
            }

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notes / Payment Instructions (Optional)") },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("invoice_notes_input")
            )

            // Live Totals Card
            PocketBizCard(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    TotalsLine("Subtotal", FinancialCalculator.formatCurrency(liveTotals.subtotalMinor, settings.currencyCode))
                    if (liveTotals.discountMinor > 0L) {
                        TotalsLine("Discount", "-${FinancialCalculator.formatCurrency(liveTotals.discountMinor, settings.currencyCode)}")
                    }
                    if (liveTotals.taxAmountMinor > 0L) {
                        TotalsLine("Tax ($taxInput%)", FinancialCalculator.formatCurrency(liveTotals.taxAmountMinor, settings.currencyCode))
                    }
                    HorizontalDivider(color = colors.cardBorder)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Invoice Total",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        AdaptiveAmountText(
                            text = FinancialCalculator.formatCurrency(liveTotals.totalMinor, settings.currencyCode),
                            style = MonoFinancialMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary,
                            maxFontSize = 18.sp,
                            minFontSize = 12.sp,
                            textAlign = TextAlign.End,
                            modifier = Modifier.weight(1f),
                            textModifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            val combinedError = localValidationError ?: errorMessage
            if (!combinedError.isNullOrBlank()) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = colors.negativeContainer,
                    border = BorderStroke(1.dp, colors.negativeBorder)
                ) {
                    Text(
                        text = combinedError,
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.negative,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                    )
                }
            }

            Button(
                onClick = {
                    if (selectedCustomerId <= 0L) {
                        localValidationError = "Please select or quick-add a customer first."
                        return@Button
                    }
                    val parsedLines = mutableListOf<InvoiceLineInput>()
                    for ((idx, row) in lineItems.withIndex()) {
                        val desc = row.description.trim()
                        if (desc.isEmpty()) {
                            localValidationError = "Item #${idx + 1} needs a description."
                            return@Button
                        }
                        val qtyMilli = FinancialCalculator.parsePositiveQuantityMilli(row.quantity)
                        if (qtyMilli == null) {
                            localValidationError = "Enter a valid positive quantity for Item #${idx + 1}."
                            return@Button
                        }
                        val priceMinor = FinancialCalculator.parsePositiveAmountToMinorUnits(row.unitPrice)
                        if (priceMinor == null) {
                            localValidationError = "Enter a valid unit price for Item #${idx + 1}."
                            return@Button
                        }
                        parsedLines.add(
                            InvoiceLineInput(
                                description = desc,
                                quantityMilli = qtyMilli,
                                unitPriceMinor = priceMinor
                            )
                        )
                    }
                    localValidationError = null
                    onCreateInvoice(
                        selectedCustomerId,
                        parsedLines,
                        discountInput,
                        taxInput,
                        dueDays,
                        issueImmediately,
                        notes
                    )
                },
                enabled = !isSavingInvoice,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("save_invoice_button")
            ) {
                Text(
                    text = when {
                        isSavingInvoice -> "Creating Invoice..."
                        issueImmediately -> "Create & Issue Invoice"
                        else -> "Save Invoice Draft"
                    },
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun TotalsLine(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MonoFinancialSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun InvoiceDetailBottomSheet(
    invoice: InvoiceEntity,
    customer: CustomerEntity?,
    items: List<InvoiceItemEntity>,
    payments: List<PaymentEntity>,
    settings: BusinessSettings,
    isRecordingPayment: Boolean,
    paymentError: String?,
    onDismiss: () -> Unit,
    onMarkSent: () -> Unit,
    onRecordPayment: (amountInput: String, method: String, reference: String) -> Unit,
    onSharePdf: () -> Unit,
    onPrintPdf: () -> Unit,
    onDeleteInvoice: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val colors = LocalPocketBizColors.current
    val currency = settings.currencyCode
    val remainingMinor = (invoice.totalMinor - invoice.paidAmountMinor).coerceAtLeast(0L)

    val formatter = remember(settings.dateFormatPattern) {
        runCatching { SimpleDateFormat(settings.dateFormatPattern, Locale.getDefault()) }
            .getOrElse { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }
    }

    var showPaymentForm by rememberSaveable(invoice.id) { mutableStateOf(false) }
    var paymentAmountInput by rememberSaveable(invoice.id, remainingMinor) {
        mutableStateOf(
            if (remainingMinor > 0L) FinancialCalculator.minorUnitsToBigDecimal(remainingMinor).toPlainString() else ""
        )
    }
    var paymentMethod by rememberSaveable(invoice.id) { mutableStateOf("Cash") }
    var paymentReference by rememberSaveable(invoice.id) { mutableStateOf("") }

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
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = invoice.invoiceNumber,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = customer?.name ?: "Customer",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                InvoiceStatusPill(status = invoice.status)
            }

            PocketBizCard {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    TotalsLine("Issued", formatter.format(Date(invoice.issueDateEpochMs)))
                    TotalsLine("Due", formatter.format(Date(invoice.dueDateEpochMs)))
                    HorizontalDivider(color = colors.cardBorder, modifier = Modifier.padding(vertical = 4.dp))
                    items.forEach { item ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                                Text(
                                    text = item.description,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "${FinancialCalculator.formatQuantity(item.quantityMilli)} × ${FinancialCalculator.formatCurrency(item.unitPriceMinor, currency)}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Text(
                                text = FinancialCalculator.formatCurrency(item.lineTotalMinor, currency),
                                style = MonoFinancialSmall.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                    HorizontalDivider(color = colors.cardBorder, modifier = Modifier.padding(vertical = 4.dp))
                    TotalsLine("Subtotal", FinancialCalculator.formatCurrency(invoice.subtotalMinor, currency))
                    if (invoice.discountMinor > 0L) {
                        TotalsLine("Discount", "-${FinancialCalculator.formatCurrency(invoice.discountMinor, currency)}")
                    }
                    if (invoice.taxAmountMinor > 0L) {
                        TotalsLine("Tax", FinancialCalculator.formatCurrency(invoice.taxAmountMinor, currency))
                    }
                    TotalsLine("Total", FinancialCalculator.formatCurrency(invoice.totalMinor, currency))
                    TotalsLine("Paid", FinancialCalculator.formatCurrency(invoice.paidAmountMinor, currency))
                    HorizontalDivider(color = colors.cardBorder, modifier = Modifier.padding(vertical = 2.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Balance Due",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        AdaptiveAmountText(
                            text = FinancialCalculator.formatCurrency(remainingMinor, currency),
                            style = MonoFinancialMedium.copy(fontWeight = FontWeight.Bold),
                            color = if (remainingMinor == 0L) colors.positive else colors.amber,
                            maxFontSize = 18.sp,
                            minFontSize = 12.sp,
                            textAlign = TextAlign.End,
                            modifier = Modifier.weight(1f),
                            textModifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // Record Payment section
            if (remainingMinor > 0L) {
                Button(
                    onClick = { showPaymentForm = !showPaymentForm },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.positive,
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("toggle_record_payment_button")
                ) {
                    Icon(Icons.Filled.Payments, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (showPaymentForm) "Hide Payment Form" else "Record Payment")
                }

                AnimatedVisibility(visible = showPaymentForm) {
                    PocketBizCard(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedTextField(
                                value = paymentAmountInput,
                                onValueChange = { paymentAmountInput = it },
                                label = { Text("Payment Amount ($currency)") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("payment_amount_input")
                            )

                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                listOf("Cash", "Bank", "Card").forEach { method ->
                                    FilterChip(
                                        selected = paymentMethod == method,
                                        onClick = { paymentMethod = method },
                                        label = { Text(method) }
                                    )
                                }
                            }

                            OutlinedTextField(
                                value = paymentReference,
                                onValueChange = { paymentReference = it },
                                label = { Text("Reference / Receipt # (Optional)") },
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("payment_reference_input")
                            )

                            if (!paymentError.isNullOrBlank()) {
                                Text(
                                    text = paymentError,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = colors.negative
                                )
                            }

                            Button(
                                onClick = {
                                    onRecordPayment(paymentAmountInput, paymentMethod, paymentReference)
                                },
                                enabled = !isRecordingPayment,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("save_payment_button")
                            ) {
                                Text(if (isRecordingPayment) "Recording..." else "Confirm Payment")
                            }
                        }
                    }
                }
            }

            // Share / Print / Mark Sent / Delete Actions
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                if (invoice.status == InvoiceStatus.DRAFT) {
                    OutlinedButton(
                        onClick = onMarkSent,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .height(48.dp)
                            .testTag("mark_invoice_sent_button")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Mark Sent")
                    }
                }
                OutlinedButton(
                    onClick = onSharePdf,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .height(48.dp)
                        .testTag("share_invoice_pdf_button")
                ) {
                    Icon(Icons.Filled.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Share PDF")
                }
                OutlinedButton(
                    onClick = onPrintPdf,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .height(48.dp)
                        .testTag("print_invoice_pdf_button")
                ) {
                    Icon(Icons.Filled.Print, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Print")
                }
                OutlinedButton(
                    onClick = onDeleteInvoice,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = colors.negative),
                    modifier = Modifier
                        .height(48.dp)
                        .testTag("delete_invoice_button")
                ) {
                    Icon(Icons.Filled.DeleteOutline, contentDescription = "Delete Invoice", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Delete")
                }
            }

            if (payments.isNotEmpty()) {
                Text(
                    text = "Payment History",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                payments.forEach { pmt ->
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = colors.positiveContainer.copy(alpha = 0.6f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "${pmt.method}${if (pmt.reference.isNotBlank()) " • ${pmt.reference}" else ""}",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = formatter.format(Date(pmt.paymentDateEpochMs)),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Text(
                                text = "+${FinancialCalculator.formatCurrency(pmt.amountMinor, currency)}",
                                style = MonoFinancialSmall.copy(fontWeight = FontWeight.Bold),
                                color = colors.positive
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InvoiceStatusPill(status: InvoiceStatus) {
    val colors = LocalPocketBizColors.current
    val (bg, fg, label) = when (status) {
        InvoiceStatus.PAID -> Triple(colors.positiveContainer, colors.positive, "Paid")
        InvoiceStatus.PARTIAL -> Triple(colors.amberContainer, colors.amber, "Partial")
        InvoiceStatus.SENT -> Triple(colors.skyContainer, colors.sky, "Sent")
        InvoiceStatus.OVERDUE -> Triple(colors.negativeContainer, colors.negative, "Overdue")
        InvoiceStatus.DRAFT -> Triple(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.onSurfaceVariant, "Draft")
    }
    Surface(
        shape = RoundedCornerShape(999.dp),
        color = bg
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
            color = fg,
            maxLines = 1,
            softWrap = false,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}
