package com.example.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.SwapHoriz
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.serialization.Serializable

@Serializable
object DashboardRoute

@Serializable
object TransactionsRoute

@Serializable
object InvoicesRoute

@Serializable
object CustomersRoute

@Serializable
object ReportsRoute

@Serializable
object SettingsRoute

data class BottomNavDestination(
    val route: Any,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
)

val primaryDestinations = listOf(
    BottomNavDestination(
        route = DashboardRoute,
        label = "Dashboard",
        selectedIcon = Icons.Filled.Dashboard,
        unselectedIcon = Icons.Outlined.Dashboard,
        testTag = "nav_dashboard"
    ),
    BottomNavDestination(
        route = TransactionsRoute,
        label = "Transactions",
        selectedIcon = Icons.Filled.SwapHoriz,
        unselectedIcon = Icons.Outlined.SwapHoriz,
        testTag = "nav_transactions"
    ),
    BottomNavDestination(
        route = InvoicesRoute,
        label = "Invoices",
        selectedIcon = Icons.AutoMirrored.Filled.ReceiptLong,
        unselectedIcon = Icons.AutoMirrored.Outlined.ReceiptLong,
        testTag = "nav_invoices"
    ),
    BottomNavDestination(
        route = CustomersRoute,
        label = "Customers",
        selectedIcon = Icons.Filled.People,
        unselectedIcon = Icons.Outlined.People,
        testTag = "nav_customers"
    ),
    BottomNavDestination(
        route = ReportsRoute,
        label = "Reports",
        selectedIcon = Icons.Filled.BarChart,
        unselectedIcon = Icons.Outlined.BarChart,
        testTag = "nav_reports"
    )
)
