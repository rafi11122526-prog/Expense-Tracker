package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.domain.model.ThemeMode
import com.example.domain.model.TransactionType
import com.example.ui.components.AddTransactionBottomSheet
import com.example.ui.dashboard.DashboardScreen
import com.example.ui.modules.CreateInvoiceBottomSheet
import com.example.ui.modules.CustomerDetailBottomSheet
import com.example.ui.modules.CustomerFormBottomSheet
import com.example.ui.modules.CustomersScreen
import com.example.ui.modules.InvoiceDetailBottomSheet
import com.example.ui.modules.InvoicesScreen
import com.example.ui.modules.ReportsScreen
import com.example.ui.modules.TransactionsScreen
import com.example.ui.navigation.CustomersRoute
import com.example.ui.navigation.DashboardRoute
import com.example.ui.navigation.InvoicesRoute
import com.example.ui.navigation.ReportsRoute
import com.example.ui.navigation.SettingsRoute
import com.example.ui.navigation.TransactionsRoute
import com.example.ui.navigation.primaryDestinations
import com.example.ui.settings.SettingsScreen
import com.example.ui.theme.LocalPocketBizColors
import com.example.ui.theme.PocketBizTheme
import com.example.ui.viewmodel.MainViewModel
import com.example.util.InvoicePdfGenerator

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val app = application as PocketBizApplication
        setContent {
            val mainViewModel: MainViewModel = viewModel(
                factory = MainViewModel.Factory(app.container.businessRepository)
            )
            val uiState by mainViewModel.uiState.collectAsStateWithLifecycle()
            val systemDark = isSystemInDarkTheme()
            val useDarkTheme = when (uiState.settings.themeMode) {
                ThemeMode.SYSTEM -> systemDark
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
            }

            LaunchedEffect(useDarkTheme) {
                enableEdgeToEdge(
                    statusBarStyle = if (useDarkTheme) {
                        SystemBarStyle.dark(android.graphics.Color.TRANSPARENT)
                    } else {
                        SystemBarStyle.light(
                            android.graphics.Color.TRANSPARENT,
                            android.graphics.Color.TRANSPARENT
                        )
                    }
                )
            }

            PocketBizTheme(darkTheme = useDarkTheme) {
                PocketBizAppRoot(viewModel = mainViewModel)
            }
        }
    }
}

@Composable
private fun rememberScrollAwareFabVisibility(
    listState: LazyListState,
    activeRouteKey: Any?
): Boolean {
    var isVisible by remember(activeRouteKey) { mutableStateOf(true) }
    var previousIndex by remember(activeRouteKey) { mutableIntStateOf(listState.firstVisibleItemIndex) }
    var previousOffset by remember(activeRouteKey) { mutableIntStateOf(listState.firstVisibleItemScrollOffset) }

    LaunchedEffect(listState, activeRouteKey) {
        snapshotFlow {
            listState.firstVisibleItemIndex to listState.firstVisibleItemScrollOffset
        }.collect { (index, offset) ->
            isVisible = when {
                index == 0 && offset < 60 -> true
                index > previousIndex -> false
                index < previousIndex -> true
                offset > previousOffset + 12 -> false
                offset < previousOffset - 12 -> true
                else -> isVisible
            }
            previousIndex = index
            previousOffset = offset
        }
    }
    return isVisible
}

@Composable
fun PocketBizAppRoot(
    viewModel: MainViewModel,
    navController: NavHostController = rememberNavController()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val systemDark = isSystemInDarkTheme()
    val resolvedDarkTheme = when (uiState.settings.themeMode) {
        ThemeMode.SYSTEM -> systemDark
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination
    val isSettingsRoute = currentDestination?.hasRoute(SettingsRoute::class) == true
    val isDashboardRoute = currentDestination?.hasRoute(DashboardRoute::class) == true
    val isTransactionsRoute = currentDestination?.hasRoute(TransactionsRoute::class) == true
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.actionFeedbackMessage) {
        val message = uiState.actionFeedbackMessage
        if (!message.isNullOrBlank()) {
            snackbarHostState.showSnackbar(message)
            viewModel.clearActionFeedback()
        }
    }

    val dashboardListState = rememberLazyListState()
    val transactionsListState = rememberLazyListState()

    val activeListState = if (isTransactionsRoute) transactionsListState else dashboardListState
    val fabScrollVisible = rememberScrollAwareFabVisibility(
        listState = activeListState,
        activeRouteKey = currentDestination?.route
    )
    val showFab = (isDashboardRoute || isTransactionsRoute) && fabScrollVisible

    fun navigateToPrimaryTab(route: Any) {
        navController.navigate(route) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val useNavigationRail = maxWidth >= 600.dp

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            bottomBar = {
                if (!isSettingsRoute && !useNavigationRail) {
                    PocketBizBottomBar(
                        currentDestination = currentDestination,
                        onNavigate = ::navigateToPrimaryTab
                    )
                }
            },
            floatingActionButton = {
                AnimatedVisibility(
                    visible = showFab,
                    enter = fadeIn() + scaleIn(initialScale = 0.85f),
                    exit = fadeOut() + scaleOut(targetScale = 0.85f)
                ) {
                    FloatingActionButton(
                        onClick = { viewModel.openAddTransactionSheet(TransactionType.INCOME) },
                        shape = RoundedCornerShape(16.dp),
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        elevation = FloatingActionButtonDefaults.elevation(
                            defaultElevation = 4.dp,
                            pressedElevation = 6.dp
                        ),
                        modifier = Modifier
                            .size(52.dp)
                            .testTag("add_transaction_fab")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "Add Transaction"
                        )
                    }
                }
            }
        ) { innerPadding ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                if (!isSettingsRoute && useNavigationRail) {
                    NavigationRail(
                        containerColor = MaterialTheme.colorScheme.surface,
                        modifier = Modifier.fillMaxHeight()
                    ) {
                        primaryDestinations.forEach { dest ->
                            val selected = currentDestination?.hasRoute(dest.route::class) == true
                            NavigationRailItem(
                                selected = selected,
                                onClick = { navigateToPrimaryTab(dest.route) },
                                icon = {
                                    Icon(
                                        imageVector = if (selected) dest.selectedIcon else dest.unselectedIcon,
                                        contentDescription = dest.label
                                    )
                                },
                                label = { Text(dest.label, maxLines = 1) },
                                modifier = Modifier.testTag(dest.testTag)
                            )
                        }
                    }
                }

                NavHost(
                    navController = navController,
                    startDestination = DashboardRoute,
                    modifier = Modifier.weight(1f)
                ) {
                    composable<DashboardRoute> {
                        DashboardScreen(
                            uiState = uiState,
                            onToggleTheme = { viewModel.toggleThemeMode(resolvedDarkTheme) },
                            onOpenSettings = { navController.navigate(SettingsRoute) },
                            onViewAllTransactions = { navigateToPrimaryTab(TransactionsRoute) },
                            onQuickAddIncome = { viewModel.openAddTransactionSheet(TransactionType.INCOME) },
                            onQuickAddExpense = { viewModel.openAddTransactionSheet(TransactionType.EXPENSE) },
                            onOpenInvoices = { navigateToPrimaryTab(InvoicesRoute) },
                            onOpenCustomers = { navigateToPrimaryTab(CustomersRoute) },
                            onChartTypeChange = { viewModel.setChartType(it) },
                            onBreakdownTypeChange = { viewModel.setBreakdownType(it) },
                            onBreakdownPeriodChange = { viewModel.setBreakdownPeriod(it) },
                            listState = dashboardListState
                        )
                    }
                    composable<TransactionsRoute> {
                        TransactionsScreen(
                            uiState = uiState,
                            onSearchQueryChange = { viewModel.updateSearchQuery(it) },
                            onFilterTypeChange = { viewModel.setFilterType(it) },
                            onFilterPeriodChange = { viewModel.setFilterPeriod(it) },
                            onClearFilters = { viewModel.clearTransactionFilters() },
                            listState = transactionsListState
                        )
                    }
                    composable<InvoicesRoute> {
                        InvoicesScreen(
                            uiState = uiState,
                            onSearchQueryChange = { viewModel.updateInvoiceSearchQuery(it) },
                            onStatusFilterChange = { viewModel.setInvoiceStatusFilter(it) },
                            onClearFilters = { viewModel.clearInvoiceFilters() },
                            onCreateInvoiceClick = { viewModel.openCreateInvoiceSheet() },
                            onInvoiceClick = { invoiceId -> viewModel.openInvoiceDetail(invoiceId) }
                        )
                    }
                    composable<CustomersRoute> {
                        CustomersScreen(
                            uiState = uiState,
                            onSearchQueryChange = { viewModel.updateCustomerSearchQuery(it) },
                            onAddCustomerClick = { viewModel.openAddCustomerSheet() },
                            onCustomerClick = { customerId -> viewModel.openCustomerDetail(customerId) }
                        )
                    }
                    composable<ReportsRoute> {
                        ReportsScreen(
                            uiState = uiState,
                            onChartTypeChange = { viewModel.setChartType(it) },
                            onBreakdownTypeChange = { viewModel.setBreakdownType(it) },
                            onBreakdownPeriodChange = { viewModel.setBreakdownPeriod(it) }
                        )
                    }
                    composable<SettingsRoute> {
                        SettingsScreen(
                            settings = uiState.settings,
                            statusMessage = uiState.settingsMessage,
                            onClearStatusMessage = { viewModel.clearSettingsMessage() },
                            onSaveSettings = { updated -> viewModel.saveSettings(updated) },
                            onThemeModeChange = { mode -> viewModel.setThemeMode(mode) },
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }

    if (uiState.isAddTransactionSheetVisible) {
        AddTransactionBottomSheet(
            currencyCode = uiState.settings.currencyCode,
            initialType = uiState.initialTransactionType,
            isSaving = uiState.isSavingTransaction,
            errorMessage = uiState.addTransactionError,
            onDismiss = { viewModel.dismissAddTransactionSheet() },
            onSave = { type, category, amount, description ->
                viewModel.recordTransaction(
                    type = type,
                    category = category,
                    amountInput = amount,
                    description = description
                )
            }
        )
    }

    if (uiState.isCustomerSheetVisible) {
        CustomerFormBottomSheet(
            editingCustomer = uiState.editingCustomer,
            isSaving = uiState.isSavingCustomer,
            errorMessage = uiState.customerError,
            onDismiss = { viewModel.dismissCustomerSheet() },
            onSave = { name, phone, email, address, notes ->
                viewModel.saveCustomer(
                    name = name,
                    phone = phone,
                    email = email,
                    address = address,
                    notes = notes
                )
            }
        )
    }

    val selectedCustomer = uiState.selectedCustomerId?.let { cid ->
        uiState.customers.firstOrNull { it.id == cid }
    }
    if (selectedCustomer != null) {
        val customerInvoices = uiState.invoices.filter { it.customerId == selectedCustomer.id }
        CustomerDetailBottomSheet(
            customer = selectedCustomer,
            customerInvoices = customerInvoices,
            currencyCode = uiState.settings.currencyCode,
            dateFormatPattern = uiState.settings.dateFormatPattern,
            onDismiss = { viewModel.dismissCustomerDetail() },
            onCreateInvoice = {
                viewModel.dismissCustomerDetail()
                viewModel.openCreateInvoiceSheet(preselectedCustomerId = selectedCustomer.id)
            },
            onEditCustomer = {
                viewModel.dismissCustomerDetail()
                viewModel.openEditCustomerSheet(selectedCustomer)
            },
            onDeleteCustomer = { viewModel.deleteCustomer(selectedCustomer.id) },
            onOpenInvoice = { invoiceId ->
                viewModel.dismissCustomerDetail()
                viewModel.openInvoiceDetail(invoiceId)
            }
        )
    }

    if (uiState.isCreateInvoiceSheetVisible) {
        CreateInvoiceBottomSheet(
            settings = uiState.settings,
            customers = uiState.customers,
            preselectedCustomerId = uiState.preselectedCustomerId,
            isSavingCustomer = uiState.isSavingCustomer,
            isSavingInvoice = uiState.isSavingInvoice,
            errorMessage = uiState.invoiceError,
            onDismiss = { viewModel.dismissCreateInvoiceSheet() },
            onQuickAddCustomer = { name, phone, email, onCreated ->
                viewModel.quickAddCustomerForInvoice(
                    name = name,
                    phone = phone,
                    email = email,
                    onCreated = onCreated
                )
            },
            onCreateInvoice = { customerId, lines, discount, tax, dueDays, issueImmediately, notes ->
                viewModel.createInvoice(
                    customerId = customerId,
                    lineInputs = lines,
                    discountInput = discount,
                    taxPercentInput = tax,
                    dueDaysFromNow = dueDays,
                    issueImmediately = issueImmediately,
                    notes = notes
                )
            }
        )
    }

    val selectedInvoice = uiState.selectedInvoiceId?.let { iid ->
        uiState.invoices.firstOrNull { it.id == iid }
    }
    if (selectedInvoice != null) {
        val invoiceCustomer = uiState.customers.firstOrNull { it.id == selectedInvoice.customerId }
        val invoiceItems = uiState.invoiceItemsByInvoiceId[selectedInvoice.id].orEmpty()
        val invoicePayments = uiState.paymentsByInvoiceId[selectedInvoice.id].orEmpty()
        InvoiceDetailBottomSheet(
            invoice = selectedInvoice,
            customer = invoiceCustomer,
            items = invoiceItems,
            payments = invoicePayments,
            settings = uiState.settings,
            isRecordingPayment = uiState.isRecordingPayment,
            paymentError = uiState.paymentError,
            onDismiss = { viewModel.dismissInvoiceDetail() },
            onMarkSent = { viewModel.markInvoiceSent(selectedInvoice.id) },
            onRecordPayment = { amountInput, method, reference ->
                viewModel.recordInvoicePayment(
                    invoiceId = selectedInvoice.id,
                    amountInput = amountInput,
                    method = method,
                    reference = reference
                )
            },
            onSharePdf = {
                InvoicePdfGenerator.generateInvoicePdf(
                    context = context,
                    invoice = selectedInvoice,
                    customer = invoiceCustomer,
                    items = invoiceItems,
                    settings = uiState.settings
                ).fold(
                    onSuccess = { file ->
                        InvoicePdfGenerator.shareInvoicePdf(
                            context = context,
                            pdfFile = file,
                            invoiceNumber = selectedInvoice.invoiceNumber
                        ).onFailure { err ->
                            viewModel.showActionFeedback(err.message ?: "Unable to share PDF")
                        }
                    },
                    onFailure = { err ->
                        viewModel.showActionFeedback(err.message ?: "Unable to generate PDF")
                    }
                )
            },
            onPrintPdf = {
                InvoicePdfGenerator.generateInvoicePdf(
                    context = context,
                    invoice = selectedInvoice,
                    customer = invoiceCustomer,
                    items = invoiceItems,
                    settings = uiState.settings
                ).fold(
                    onSuccess = { file ->
                        InvoicePdfGenerator.printInvoicePdf(
                            context = context,
                            pdfFile = file,
                            invoiceNumber = selectedInvoice.invoiceNumber
                        ).onFailure { err ->
                            viewModel.showActionFeedback(err.message ?: "Unable to print PDF")
                        }
                    },
                    onFailure = { err ->
                        viewModel.showActionFeedback(err.message ?: "Unable to generate PDF")
                    }
                )
            },
            onDeleteInvoice = { viewModel.deleteInvoice(selectedInvoice.id) }
        )
    }
}

@Composable
private fun PocketBizBottomBar(
    currentDestination: NavDestination?,
    onNavigate: (Any) -> Unit
) {
    val extendedColors = LocalPocketBizColors.current
    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.navigationBars)
        ) {
            HorizontalDivider(color = extendedColors.cardBorder)
            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                val labelFontSize = if (maxWidth < 380.dp) 9.5.sp else 10.5.sp
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(62.dp)
                        .padding(horizontal = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    primaryDestinations.forEach { dest ->
                        val selected = currentDestination?.hasRoute(dest.route::class) == true
                        val activeTint = MaterialTheme.colorScheme.primary
                        val inactiveTint = MaterialTheme.colorScheme.onSurfaceVariant
                        val tint = if (selected) activeTint else inactiveTint
                        val interactionSource = remember { MutableInteractionSource() }

                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .defaultMinSize(minHeight = 48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .selectable(
                                    selected = selected,
                                    onClick = { onNavigate(dest.route) },
                                    role = Role.Tab,
                                    interactionSource = interactionSource,
                                    indication = ripple(bounded = true)
                                )
                                .padding(vertical = 6.dp, horizontal = 2.dp)
                                .testTag(dest.testTag),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(46.dp)
                                    .height(26.dp)
                                    .clip(RoundedCornerShape(999.dp))
                                    .background(
                                        if (selected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (selected) dest.selectedIcon else dest.unselectedIcon,
                                    contentDescription = dest.label,
                                    tint = tint,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Text(
                                text = dest.label,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = labelFontSize,
                                    lineHeight = 12.sp,
                                    letterSpacing = (-0.25).sp,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                                ),
                                color = tint,
                                maxLines = 1,
                                softWrap = false,
                                overflow = TextOverflow.Clip,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}
