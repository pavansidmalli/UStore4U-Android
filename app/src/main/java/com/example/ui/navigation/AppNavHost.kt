package com.example.ui.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.UStore4UApp
import com.example.ui.screens.AccountScreen
import com.example.ui.screens.CartScreen
import com.example.ui.screens.CheckoutScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.OrderConfirmationScreen
import com.example.ui.screens.OrdersScreen
import com.example.ui.screens.OrderTrackingScreen
import com.example.ui.screens.ProductDetailScreen
import com.example.ui.screens.ProductListScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.screens.WishlistScreen
import com.example.ui.theme.ChampagneGold
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.PureWhite
import com.example.ui.theme.WarmGold
import com.example.ui.viewmodel.AccountViewModel
import com.example.ui.viewmodel.CartViewModel
import com.example.ui.viewmodel.CheckoutViewModel
import com.example.ui.viewmodel.HomeViewModel
import com.example.ui.viewmodel.OrdersViewModel
import com.example.ui.viewmodel.OrderTrackingViewModel
import com.example.ui.viewmodel.ProductDetailViewModel
import com.example.ui.viewmodel.ProductListViewModel
import com.example.ui.viewmodel.WishlistViewModel

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    modifier: Modifier = Modifier
) {
    val app = UStore4UApp.instance
    val userPreferences = app.userPreferences
    val isOnboardingDone by userPreferences.onboardingCompleted.collectAsState()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val bottomNavItems = listOf(
        BottomNavItem.Home,
        BottomNavItem.Search,
        BottomNavItem.Menu,
        BottomNavItem.Cart,
        BottomNavItem.Account
    )

    val showBottomBar = currentRoute in listOf(
        Screen.Home.route,
        "products",
        Screen.ProductList.route,
        Screen.Wishlist.route,
        Screen.Orders.route,
        Screen.Account.route,
        Screen.Cart.route
    )

    val cartCount by app.cartRepository.cartCount.collectAsState(initial = 0)

    Scaffold(
        bottomBar = {
            AnimatedVisibility(
                visible = showBottomBar,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it })
            ) {
                androidx.compose.foundation.layout.Column {
                    androidx.compose.material3.HorizontalDivider(
                        thickness = 0.8.dp,
                        color = MaterialTheme.colorScheme.outline
                    )
                    Surface(
                        color = MaterialTheme.colorScheme.surface,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                            .testTag("main_bottom_nav")
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.SpaceAround,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            bottomNavItems.forEach { item ->
                                val selected = when (item) {
                                    BottomNavItem.Home -> currentRoute == Screen.Home.route
                                    BottomNavItem.Search -> currentRoute?.contains("search") == true
                                    BottomNavItem.Menu -> currentRoute?.startsWith("products") == true && currentRoute?.contains("search") != true
                                    BottomNavItem.Cart -> currentRoute == Screen.Cart.route
                                    BottomNavItem.Account -> currentRoute == Screen.Account.route
                                }

                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxSize()
                                        .clickable {
                                            navController.navigate(item.route) {
                                                popUpTo(navController.graph.findStartDestination().id) {
                                                    saveState = true
                                                }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        }
                                        .testTag("nav_${item.title.lowercase()}"),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    if (item.isTextOnly) {
                                        Text(
                                            text = item.title,
                                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                            fontSize = 15.sp,
                                            letterSpacing = 0.8.sp,
                                            color = if (selected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Spacer(modifier = Modifier.height(3.dp))
                                        if (selected) {
                                            Box(
                                                modifier = Modifier
                                                    .width(28.dp)
                                                    .height(2.5.dp)
                                                    .background(MaterialTheme.colorScheme.primary)
                                            )
                                        } else {
                                            Spacer(modifier = Modifier.height(2.5.dp))
                                        }
                                    } else {
                                        if (item == BottomNavItem.Cart && cartCount > 0) {
                                            BadgedBox(
                                                badge = {
                                                    Badge(
                                                        containerColor = MaterialTheme.colorScheme.primary,
                                                        contentColor = MaterialTheme.colorScheme.onPrimary
                                                    ) {
                                                        Text("$cartCount", fontSize = 9.sp)
                                                    }
                                                }
                                            ) {
                                                Icon(
                                                    imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                                                    contentDescription = item.title,
                                                    tint = if (selected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
                                                    modifier = Modifier.size(24.dp)
                                                )
                                            }
                                        } else {
                                            Icon(
                                                imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                                                contentDescription = item.title,
                                                tint = if (selected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(3.dp))
                                        if (selected) {
                                            Box(
                                                modifier = Modifier
                                                    .width(18.dp)
                                                    .height(2.5.dp)
                                                    .background(MaterialTheme.colorScheme.primary)
                                            )
                                        } else {
                                            Spacer(modifier = Modifier.height(2.5.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Splash.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            // Splash Destination
            composable(Screen.Splash.route) {
                SplashScreen(
                    onSplashFinished = {
                        val destination = if (isOnboardingDone) Screen.Home.route else Screen.Onboarding.route
                        navController.navigate(destination) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    }
                )
            }

            // Onboarding Destination
            composable(Screen.Onboarding.route) {
                OnboardingScreen(
                    onFinishOnboarding = {
                        userPreferences.setOnboardingCompleted(true)
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Onboarding.route) { inclusive = true }
                        }
                    }
                )
            }

            // Home Destination
            composable(Screen.Home.route) {
                val homeViewModel = remember {
                    HomeViewModel(
                        productRepository = app.productRepository,
                        cartRepository = app.cartRepository,
                        wishlistRepository = app.wishlistRepository
                    )
                }

                HomeScreen(
                    viewModel = homeViewModel,
                    onNavigateToProduct = { productId ->
                        navController.navigate(Screen.ProductDetail.createRoute(productId))
                    },
                    onNavigateToCategory = { catId, catName ->
                        navController.navigate(Screen.ProductList.createRoute(categoryId = catId, categoryName = catName))
                    },
                    onNavigateToSearch = {
                        navController.navigate(Screen.ProductList.createRoute())
                    },
                    onNavigateToCart = {
                        navController.navigate(Screen.Cart.route)
                    },
                    onNavigateToAccount = {
                        navController.navigate(Screen.Account.route)
                    },
                    onNavigateToAllProducts = { filterType ->
                        navController.navigate(Screen.ProductList.createRoute(filterType = filterType))
                    }
                )
            }

            // Product List / Search Destination
            composable(
                route = Screen.ProductList.route,
                arguments = listOf(
                    navArgument("categoryId") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    },
                    navArgument("categoryName") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    },
                    navArgument("searchQuery") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    },
                    navArgument("filterType") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    }
                )
            ) { backStackEntry ->
                val categoryIdStr = backStackEntry.arguments?.getString("categoryId")
                val categoryId = categoryIdStr?.toLongOrNull()
                val categoryName = backStackEntry.arguments?.getString("categoryName")
                val searchQuery = backStackEntry.arguments?.getString("searchQuery")
                val filterType = backStackEntry.arguments?.getString("filterType")

                val listViewModel = remember(categoryId, searchQuery, filterType) {
                    ProductListViewModel(
                        productRepository = app.productRepository,
                        cartRepository = app.cartRepository,
                        wishlistRepository = app.wishlistRepository,
                        userPreferences = app.userPreferences,
                        initialCategoryId = categoryId,
                        initialSearch = searchQuery
                    ).apply {
                        if (filterType == "deals") {
                            applyFilters(categoryId, null, null, null, inStock = false, onSale = true, sort = com.example.data.repository.SortOption.LATEST)
                        } else if (filterType == "bestsellers") {
                            applyFilters(categoryId, null, null, null, inStock = false, onSale = false, sort = com.example.data.repository.SortOption.POPULARITY)
                        }
                    }
                }

                ProductListScreen(
                    viewModel = listViewModel,
                    title = categoryName,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToProduct = { productId ->
                        navController.navigate(Screen.ProductDetail.createRoute(productId))
                    },
                    onNavigateToCart = { navController.navigate(Screen.Cart.route) }
                )
            }

            // Product Detail Destination
            composable(
                route = Screen.ProductDetail.route,
                arguments = listOf(
                    navArgument("productId") {
                        type = NavType.LongType
                        defaultValue = 1001L
                    }
                )
            ) { backStackEntry ->
                val productId = backStackEntry.arguments?.getLong("productId") ?: 1001L

                val detailViewModel = remember(productId) {
                    ProductDetailViewModel(
                        productRepository = app.productRepository,
                        cartRepository = app.cartRepository,
                        wishlistRepository = app.wishlistRepository,
                        productId = productId
                    )
                }

                ProductDetailScreen(
                    viewModel = detailViewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToCart = { navController.navigate(Screen.Cart.route) },
                    onNavigateToProduct = { nextId ->
                        navController.navigate(Screen.ProductDetail.createRoute(nextId))
                    },
                    onBuyNowClick = {
                        navController.navigate(Screen.Checkout.route)
                    }
                )
            }

            // Cart Destination
            composable(Screen.Cart.route) {
                val cartViewModel = remember {
                    CartViewModel(
                        cartRepository = app.cartRepository,
                        productRepository = app.productRepository
                    )
                }

                CartScreen(
                    viewModel = cartViewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToCheckout = { navController.navigate(Screen.Checkout.route) },
                    onExploreWatches = { navController.navigate("products") }
                )
            }

            // Checkout Destination
            composable(Screen.Checkout.route) {
                val checkoutViewModel = remember {
                    CheckoutViewModel(
                        cartRepository = app.cartRepository,
                        orderRepository = app.orderRepository,
                        userRepository = app.userRepository
                    )
                }

                CheckoutScreen(
                    viewModel = checkoutViewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onOrderSuccess = { orderId ->
                        navController.navigate(Screen.OrderConfirmation.createRoute(orderId)) {
                            popUpTo(Screen.Cart.route) { inclusive = true }
                        }
                    }
                )
            }

            // Order Confirmation Destination
            composable(
                route = Screen.OrderConfirmation.route,
                arguments = listOf(
                    navArgument("orderId") {
                        type = NavType.LongType
                        defaultValue = 0L
                    }
                )
            ) { backStackEntry ->
                val orderId = backStackEntry.arguments?.getLong("orderId") ?: 0L
                val orders by app.orderRepository.orders.collectAsState()
                val order = orders.find { it.id == orderId } ?: orders.firstOrNull()

                OrderConfirmationScreen(
                    order = order,
                    onNavigateHome = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                    },
                    onNavigateOrders = {
                        navController.navigate(Screen.Orders.route) {
                            popUpTo(Screen.Home.route)
                        }
                    },
                    onNavigateToTracking = { trackOrderId ->
                        navController.navigate(Screen.OrderTracking.createRoute(trackOrderId))
                    }
                )
            }

            // Wishlist Destination
            composable(Screen.Wishlist.route) {
                val wishlistViewModel = remember {
                    WishlistViewModel(
                        wishlistRepository = app.wishlistRepository,
                        cartRepository = app.cartRepository
                    )
                }

                WishlistScreen(
                    viewModel = wishlistViewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToProduct = { id ->
                        navController.navigate(Screen.ProductDetail.createRoute(id))
                    },
                    onNavigateToCart = { navController.navigate(Screen.Cart.route) },
                    onExploreWatches = { navController.navigate("products") }
                )
            }

            // Orders Destination
            composable(Screen.Orders.route) {
                val ordersViewModel = remember {
                    OrdersViewModel(
                        orderRepository = app.orderRepository,
                        cartRepository = app.cartRepository,
                        productRepository = app.productRepository
                    )
                }

                OrdersScreen(
                    viewModel = ordersViewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToCart = { navController.navigate(Screen.Cart.route) },
                    onNavigateToTracking = { trackOrderId ->
                        navController.navigate(Screen.OrderTracking.createRoute(trackOrderId))
                    },
                    onNavigateToProduct = { productId ->
                        navController.navigate(Screen.ProductDetail.createRoute(productId))
                    },
                    onExploreWatches = { navController.navigate("products") }
                )
            }

            // Order Tracking Destination
            composable(
                route = Screen.OrderTracking.route,
                arguments = listOf(
                    navArgument("orderId") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    }
                )
            ) { backStackEntry ->
                val orderIdStr = backStackEntry.arguments?.getString("orderId")
                val orderId = orderIdStr?.toLongOrNull()

                val trackingViewModel = remember(orderId) {
                    OrderTrackingViewModel(
                        orderRepository = app.orderRepository,
                        cartRepository = app.cartRepository,
                        userPreferences = app.userPreferences,
                        initialOrderId = orderId
                    )
                }

                OrderTrackingScreen(
                    viewModel = trackingViewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToCart = { navController.navigate(Screen.Cart.route) },
                    onNavigateToProduct = { productId ->
                        navController.navigate(Screen.ProductDetail.createRoute(productId))
                    },
                    onExploreWatches = { navController.navigate("products") }
                )
            }

            // Account Destination
            composable(Screen.Account.route) {
                val accountViewModel = remember {
                    AccountViewModel(
                        userRepository = app.userRepository,
                        cartRepository = app.cartRepository,
                        orderRepository = app.orderRepository,
                        userPreferences = app.userPreferences
                    )
                }

                AccountScreen(
                    viewModel = accountViewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToOrders = { navController.navigate(Screen.Orders.route) },
                    onNavigateToWishlist = { navController.navigate(Screen.Wishlist.route) },
                    onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                    onNavigateToCart = { navController.navigate(Screen.Cart.route) },
                    onNavigateToTracking = { trackOrderId ->
                        navController.navigate(Screen.OrderTracking.createRoute(trackOrderId))
                    },
                    onExploreWatches = { navController.navigate("products") }
                )
            }

            // Settings Destination
            composable(Screen.Settings.route) {
                SettingsScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}
