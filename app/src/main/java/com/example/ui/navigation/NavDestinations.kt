package com.example.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object Onboarding : Screen("onboarding")
    data object Home : Screen("home")
    data object ProductList : Screen("products?categoryId={categoryId}&categoryName={categoryName}&searchQuery={searchQuery}&filterType={filterType}") {
        fun createRoute(
            categoryId: Long? = null,
            categoryName: String? = null,
            searchQuery: String? = null,
            filterType: String? = null
        ): String {
            val params = mutableListOf<String>()
            if (categoryId != null) params.add("categoryId=$categoryId")
            if (categoryName != null) params.add("categoryName=$categoryName")
            if (searchQuery != null) params.add("searchQuery=$searchQuery")
            if (filterType != null) params.add("filterType=$filterType")
            return if (params.isEmpty()) "products" else "products?" + params.joinToString("&")
        }
    }
    data object ProductDetail : Screen("product_detail/{productId}") {
        fun createRoute(productId: Long): String = "product_detail/$productId"
    }
    data object Cart : Screen("cart")
    data object Checkout : Screen("checkout")
    data object OrderConfirmation : Screen("order_confirmation/{orderId}") {
        fun createRoute(orderId: Long): String = "order_confirmation/$orderId"
    }
    data object Wishlist : Screen("wishlist")
    data object Orders : Screen("orders")
    data object OrderTracking : Screen("order_tracking?orderId={orderId}") {
        fun createRoute(orderId: Long? = null): String {
            return if (orderId != null && orderId > 0) "order_tracking?orderId=$orderId" else "order_tracking"
        }
    }
    data object Account : Screen("account")
    data object Settings : Screen("settings")
}

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val isTextOnly: Boolean = false
) {
    data object Home : BottomNavItem(
        route = Screen.Home.route,
        title = "Home",
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home
    )
    data object Search : BottomNavItem(
        route = "products?searchQuery=",
        title = "Search",
        selectedIcon = Icons.Filled.Search,
        unselectedIcon = Icons.Outlined.Search
    )
    data object Menu : BottomNavItem(
        route = "products",
        title = "MENU",
        selectedIcon = Icons.Filled.Menu,
        unselectedIcon = Icons.Outlined.Menu,
        isTextOnly = true
    )
    data object Cart : BottomNavItem(
        route = Screen.Cart.route,
        title = "Bag",
        selectedIcon = Icons.Filled.ShoppingBag,
        unselectedIcon = Icons.Outlined.ShoppingBag
    )
    data object Account : BottomNavItem(
        route = Screen.Account.route,
        title = "Account",
        selectedIcon = Icons.Filled.Person,
        unselectedIcon = Icons.Outlined.Person
    )
}
