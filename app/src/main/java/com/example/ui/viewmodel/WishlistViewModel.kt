package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.WishlistEntity
import com.example.data.repository.CartRepository
import com.example.data.repository.WishlistRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class WishlistViewModel(
    private val wishlistRepository: WishlistRepository,
    private val cartRepository: CartRepository
) : ViewModel() {

    val wishlistItems: StateFlow<List<WishlistEntity>> = wishlistRepository.wishlistItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val cartCount: StateFlow<Int> = cartRepository.cartCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun removeFromWishlist(productId: Long) {
        viewModelScope.launch {
            wishlistRepository.removeFromWishlist(productId)
        }
    }

    fun moveToCart(item: WishlistEntity) {
        viewModelScope.launch {
            wishlistRepository.moveToCart(item)
        }
    }

    fun clearWishlist() {
        viewModelScope.launch {
            wishlistRepository.clearWishlist()
        }
    }
}
