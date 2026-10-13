package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.RecentlyViewedEntity
import com.example.data.model.Product
import com.example.data.model.ProductCategory
import com.example.data.repository.CartRepository
import com.example.data.repository.ProductRepository
import com.example.data.repository.SortOption
import com.example.data.repository.WishlistRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class HomeUiState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val categories: List<ProductCategory> = emptyList(),
    val featuredProducts: List<Product> = emptyList(),
    val newArrivals: List<Product> = emptyList(),
    val bestSellers: List<Product> = emptyList(),
    val specialOffers: List<Product> = emptyList(),
    val allProducts: List<Product> = emptyList(),
    val filteredProducts: List<Product> = emptyList(),
    val selectedSort: SortOption = SortOption.LATEST,
    val selectedPricePreset: String? = null,
    val minPrice: Double? = null,
    val maxPrice: Double? = null,
    val isFilterSheetVisible: Boolean = false,
    val isSortMenuVisible: Boolean = false,
    val errorMessage: String? = null
) {
    val isFilterOrSortActive: Boolean
        get() = selectedSort != SortOption.LATEST || minPrice != null || maxPrice != null || selectedPricePreset != null
}

class HomeViewModel(
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository,
    private val wishlistRepository: WishlistRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    val cartCount: StateFlow<Int> = cartRepository.cartCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val wishlistProductIds: StateFlow<Set<Long>> = wishlistRepository.wishlistItems
        .map { list -> list.map { it.productId }.toSet() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    val recentlyViewed: StateFlow<List<RecentlyViewedEntity>> = productRepository.getRecentlyViewed()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        loadHomeData()
    }

    fun loadHomeData(isRefresh: Boolean = false) {
        viewModelScope.launch {
            if (isRefresh) {
                _uiState.value = _uiState.value.copy(isRefreshing = true, errorMessage = null)
            } else {
                _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            }

            try {
                val categoriesDeferred = async(Dispatchers.IO) { productRepository.getCategories() }
                val featuredDeferred = async(Dispatchers.IO) { productRepository.getFeaturedProducts() }
                val newArrivalsDeferred = async(Dispatchers.IO) { productRepository.getNewArrivals() }
                val bestSellersDeferred = async(Dispatchers.IO) { productRepository.getBestSellers() }
                val specialOffersDeferred = async(Dispatchers.IO) { productRepository.getSpecialOffers() }
                val allProductsDeferred = async(Dispatchers.IO) { productRepository.getProducts(perPage = 50) }

                val categoriesResult = categoriesDeferred.await()
                val featuredResult = featuredDeferred.await()
                val newArrivalsResult = newArrivalsDeferred.await()
                val bestSellersResult = bestSellersDeferred.await()
                val specialOffersResult = specialOffersDeferred.await()
                val allProductsResult = allProductsDeferred.await()

                val allProducts = allProductsResult.getOrDefault(emptyList())

                val currentState = _uiState.value
                val filtered = applyFilterAndSort(
                    products = allProducts,
                    sort = currentState.selectedSort,
                    minPrice = currentState.minPrice,
                    maxPrice = currentState.maxPrice
                )

                _uiState.value = currentState.copy(
                    isLoading = false,
                    isRefreshing = false,
                    categories = categoriesResult.getOrDefault(emptyList()),
                    featuredProducts = featuredResult.getOrDefault(emptyList()),
                    newArrivals = newArrivalsResult.getOrDefault(emptyList()),
                    bestSellers = bestSellersResult.getOrDefault(emptyList()),
                    specialOffers = specialOffersResult.getOrDefault(emptyList()),
                    allProducts = allProducts,
                    filteredProducts = filtered,
                    errorMessage = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isRefreshing = false,
                    errorMessage = e.message ?: "Failed to load watch collections."
                )
            }
        }
    }

    fun setSortOption(sort: SortOption) {
        val current = _uiState.value
        val filtered = applyFilterAndSort(
            products = current.allProducts,
            sort = sort,
            minPrice = current.minPrice,
            maxPrice = current.maxPrice
        )
        _uiState.value = current.copy(
            selectedSort = sort,
            filteredProducts = filtered,
            isSortMenuVisible = false
        )
    }

    fun setPricePreset(presetLabel: String?, min: Double?, max: Double?) {
        val current = _uiState.value
        val newPreset = if (current.selectedPricePreset == presetLabel) null else presetLabel
        val newMin = if (newPreset == null) null else min
        val newMax = if (newPreset == null) null else max

        val filtered = applyFilterAndSort(
            products = current.allProducts,
            sort = current.selectedSort,
            minPrice = newMin,
            maxPrice = newMax
        )
        _uiState.value = current.copy(
            selectedPricePreset = newPreset,
            minPrice = newMin,
            maxPrice = newMax,
            filteredProducts = filtered
        )
    }

    fun setPriceRange(min: Double?, max: Double?, sort: SortOption = _uiState.value.selectedSort) {
        val current = _uiState.value
        val filtered = applyFilterAndSort(
            products = current.allProducts,
            sort = sort,
            minPrice = min,
            maxPrice = max
        )
        _uiState.value = current.copy(
            selectedSort = sort,
            selectedPricePreset = null,
            minPrice = min,
            maxPrice = max,
            filteredProducts = filtered,
            isFilterSheetVisible = false
        )
    }

    fun resetFilters() {
        val current = _uiState.value
        val filtered = applyFilterAndSort(
            products = current.allProducts,
            sort = SortOption.LATEST,
            minPrice = null,
            maxPrice = null
        )
        _uiState.value = current.copy(
            selectedSort = SortOption.LATEST,
            selectedPricePreset = null,
            minPrice = null,
            maxPrice = null,
            filteredProducts = filtered,
            isFilterSheetVisible = false,
            isSortMenuVisible = false
        )
    }

    fun setFilterSheetVisible(visible: Boolean) {
        _uiState.value = _uiState.value.copy(isFilterSheetVisible = visible)
    }

    fun setSortMenuVisible(visible: Boolean) {
        _uiState.value = _uiState.value.copy(isSortMenuVisible = visible)
    }

    private fun applyFilterAndSort(
        products: List<Product>,
        sort: SortOption,
        minPrice: Double?,
        maxPrice: Double?
    ): List<Product> {
        var result = products

        if (minPrice != null) {
            result = result.filter { (it.price.toDoubleOrNull() ?: 0.0) >= minPrice }
        }
        if (maxPrice != null) {
            result = result.filter { (it.price.toDoubleOrNull() ?: 0.0) <= maxPrice }
        }

        return when (sort) {
            SortOption.LATEST -> result.sortedByDescending { it.id }
            SortOption.PRICE_ASC -> result.sortedBy { it.price.toDoubleOrNull() ?: 0.0 }
            SortOption.PRICE_DESC -> result.sortedByDescending { it.price.toDoubleOrNull() ?: 0.0 }
            SortOption.POPULARITY -> result.sortedByDescending { it.ratingCount }
            SortOption.RATING -> result.sortedByDescending { it.averageRating.toDoubleOrNull() ?: 0.0 }
        }
    }

    fun toggleWishlist(product: Product) {
        viewModelScope.launch {
            if (wishlistProductIds.value.contains(product.id)) {
                wishlistRepository.removeFromWishlist(product.id)
            } else {
                wishlistRepository.addToWishlist(product)
            }
        }
    }

    fun quickAddToCart(product: Product) {
        viewModelScope.launch {
            cartRepository.addToCart(product, quantity = 1)
        }
    }
}

