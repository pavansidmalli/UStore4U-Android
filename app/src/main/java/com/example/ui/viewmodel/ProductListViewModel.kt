package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.UserPreferences
import com.example.data.model.Product
import com.example.data.model.ProductCategory
import com.example.data.repository.CartRepository
import com.example.data.repository.ProductRepository
import com.example.data.repository.SortOption
import com.example.data.repository.WishlistRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ProductListUiState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val products: List<Product> = emptyList(),
    val categories: List<ProductCategory> = emptyList(),
    val searchQuery: String = "",
    val selectedCategoryId: Long? = null,
    val selectedCategoryName: String? = null,
    val selectedBrand: String? = null,
    val minPrice: Double? = null,
    val maxPrice: Double? = null,
    val onlyInStock: Boolean = false,
    val onlyOnSale: Boolean = false,
    val sortOption: SortOption = SortOption.LATEST,
    val errorMessage: String? = null,
    val isFilterSheetOpen: Boolean = false
) {
    val activeFilterCount: Int
        get() {
            var count = 0
            if (selectedBrand != null) count++
            if (minPrice != null || maxPrice != null) count++
            if (onlyInStock) count++
            if (onlyOnSale) count++
            if (sortOption != SortOption.LATEST) count++
            return count
        }
}

class ProductListViewModel(
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository,
    private val wishlistRepository: WishlistRepository,
    private val userPreferences: UserPreferences,
    initialCategoryId: Long? = null,
    initialSearch: String? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        ProductListUiState(
            selectedCategoryId = initialCategoryId,
            searchQuery = initialSearch ?: ""
        )
    )
    val uiState: StateFlow<ProductListUiState> = _uiState.asStateFlow()

    val cartCount: StateFlow<Int> = cartRepository.cartCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val wishlistProductIds: StateFlow<Set<Long>> = wishlistRepository.wishlistItems
        .map { list -> list.map { it.productId }.toSet() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    val recentSearches: StateFlow<List<String>> = userPreferences.recentSearches

    private var searchJob: Job? = null

    init {
        loadCategories()
        loadProducts()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            val res = productRepository.getCategories()
            if (res.isSuccess) {
                val cats = res.getOrDefault(emptyList())
                val currentId = _uiState.value.selectedCategoryId
                val currentCat = cats.find { 
                    it.id == currentId ||
                    (currentId == 12L && it.id == 101L) ||
                    (currentId == 13L && it.id == 102L) ||
                    (currentId == 14L && it.id == 103L)
                }
                _uiState.value = _uiState.value.copy(
                    categories = cats,
                    selectedCategoryName = _uiState.value.selectedCategoryName ?: currentCat?.name
                )
            }
        }
    }

    fun loadProducts(isRefresh: Boolean = false) {
        viewModelScope.launch {
            if (isRefresh) {
                _uiState.value = _uiState.value.copy(isRefreshing = true, errorMessage = null)
            } else {
                _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            }

            val state = _uiState.value
            val result = productRepository.getProducts(
                page = 1,
                perPage = 30,
                searchQuery = state.searchQuery,
                categoryId = state.selectedCategoryId,
                brandFilter = state.selectedBrand,
                minPrice = state.minPrice,
                maxPrice = state.maxPrice,
                onlyInStock = state.onlyInStock,
                onlyOnSale = state.onlyOnSale,
                sortBy = state.sortOption
            )

            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isRefreshing = false,
                    products = result.getOrDefault(emptyList()),
                    errorMessage = null
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isRefreshing = false,
                    errorMessage = result.exceptionOrNull()?.message ?: "Failed to load watches"
                )
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(300) // Debounce search
            if (query.isNotBlank()) {
                userPreferences.addRecentSearch(query)
            }
            loadProducts()
        }
    }

    fun clearSearch() {
        _uiState.value = _uiState.value.copy(searchQuery = "")
        loadProducts()
    }

    fun selectRecentSearch(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        loadProducts()
    }

    fun clearRecentSearches() {
        userPreferences.clearRecentSearches()
    }

    fun setBrand(brand: String?) {
        val normalizedBrand = if (brand == "All Brands" || brand == "ALL") null else brand
        _uiState.value = _uiState.value.copy(selectedBrand = normalizedBrand)
        loadProducts()
    }

    fun setCategory(categoryId: Long?, categoryName: String? = null) {
        _uiState.value = _uiState.value.copy(
            selectedCategoryId = categoryId,
            selectedCategoryName = categoryName
        )
        loadProducts()
    }

    fun openFilterSheet() {
        _uiState.value = _uiState.value.copy(isFilterSheetOpen = true)
    }

    fun closeFilterSheet() {
        _uiState.value = _uiState.value.copy(isFilterSheetOpen = false)
    }

    fun applyFilters(
        categoryId: Long?,
        brand: String?,
        minPrice: Double?,
        maxPrice: Double?,
        inStock: Boolean,
        onSale: Boolean,
        sort: SortOption
    ) {
        val catName = _uiState.value.categories.find { it.id == categoryId }?.name
        _uiState.value = _uiState.value.copy(
            selectedCategoryId = categoryId,
            selectedCategoryName = catName,
            selectedBrand = brand,
            minPrice = minPrice,
            maxPrice = maxPrice,
            onlyInStock = inStock,
            onlyOnSale = onSale,
            sortOption = sort
        )
        loadProducts()
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
