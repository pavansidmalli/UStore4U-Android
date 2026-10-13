package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.Product
import com.example.data.model.ProductReview
import com.example.data.repository.CartRepository
import com.example.data.repository.ProductRepository
import com.example.data.repository.WishlistRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ProductDetailUiState(
    val isLoading: Boolean = true,
    val product: Product? = null,
    val selectedImageIndex: Int = 0,
    val selectedVariations: Map<String, String> = emptyMap(),
    val quantity: Int = 1,
    val reviews: List<ProductReview> = emptyList(),
    val relatedProducts: List<Product> = emptyList(),
    val isAddedToCartSuccess: Boolean = false,
    val errorMessage: String? = null
)

class ProductDetailViewModel(
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository,
    private val wishlistRepository: WishlistRepository,
    private val productId: Long? = null,
    private val productSlug: String? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductDetailUiState())
    val uiState: StateFlow<ProductDetailUiState> = _uiState.asStateFlow()

    val cartCount: StateFlow<Int> = cartRepository.cartCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val isWishlist: StateFlow<Boolean> = wishlistRepository.wishlistItems
        .map { list -> list.any { it.productId == _uiState.value.product?.id } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    init {
        loadProduct()
    }

    fun loadProduct() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                val productResult = if (productId != null && productId > 0) {
                    productRepository.getProductById(productId)
                } else if (!productSlug.isNullOrBlank()) {
                    productRepository.getProductBySlug(productSlug)
                } else {
                    productRepository.getProductById(1001)
                }

                val product = productResult.getOrNull()
                if (product != null) {
                    val initialVariations = mutableMapOf<String, String>()
                    product.attributes.forEach { attr ->
                        if (attr.options.isNotEmpty()) {
                            initialVariations[attr.name] = attr.options.first()
                        }
                    }

                    val reviewsResult = productRepository.getProductReviews(product.id)
                    val relatedResult = productRepository.getRelatedProducts(product)

                    _uiState.value = ProductDetailUiState(
                        isLoading = false,
                        product = product,
                        selectedImageIndex = 0,
                        selectedVariations = initialVariations,
                        quantity = 1,
                        reviews = reviewsResult.getOrDefault(emptyList()),
                        relatedProducts = relatedResult.getOrDefault(emptyList())
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Product not found or unavailable."
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Failed to load watch details."
                )
            }
        }
    }

    fun selectImage(index: Int) {
        _uiState.value = _uiState.value.copy(selectedImageIndex = index)
    }

    fun selectVariation(attributeName: String, option: String) {
        val updated = _uiState.value.selectedVariations.toMutableMap()
        updated[attributeName] = option
        _uiState.value = _uiState.value.copy(selectedVariations = updated)
    }

    fun setQuantity(qty: Int) {
        if (qty in 1..99) {
            _uiState.value = _uiState.value.copy(quantity = qty)
        }
    }

    fun addToCart(onSuccess: (() -> Unit)? = null) {
        val p = _uiState.value.product ?: return
        val qty = _uiState.value.quantity
        val variationStr = _uiState.value.selectedVariations.entries.joinToString(", ") { "${it.key}: ${it.value}" }

        viewModelScope.launch {
            cartRepository.addToCart(p, quantity = qty, selectedVariation = variationStr)
            _uiState.value = _uiState.value.copy(isAddedToCartSuccess = true)
            onSuccess?.invoke()
        }
    }

    fun toggleWishlist() {
        val p = _uiState.value.product ?: return
        viewModelScope.launch {
            if (isWishlist.value) {
                wishlistRepository.removeFromWishlist(p.id)
            } else {
                wishlistRepository.addToWishlist(p)
            }
        }
    }

    fun resetCartSuccess() {
        _uiState.value = _uiState.value.copy(isAddedToCartSuccess = false)
    }
}
