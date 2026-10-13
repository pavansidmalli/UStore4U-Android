package com.example.data.api

object WooCommerceConfig {
    const val DEFAULT_STORE_URL = "https://ustore4u.com/"
    const val STORE_NAME = "UStore4U"
    const val STORE_TAGLINE = "1000+ Styles • Watches & Wall Clocks"
    const val STORE_LOCATION = "Kukatpally, Hyderabad, Telangana, India"
    
    const val API_VERSION_PATH = "wp-json/wc/v3/"
    const val WP_API_VERSION_PATH = "wp-json/wp/v2/"
    
    // Contact & Support Details from ustore4u.com
    const val CONTACT_PHONE = "+91 91776 85544"
    const val CONTACT_PHONE_RAW = "+919177685544"
    const val SUPPORT_EMAIL = "info@ustore4u.com"
    const val SERVICE_EMAIL = "service@ustore4u.com"
    
    // Express local delivery & Exchange policy
    const val LOCAL_DELIVERY_RADIUS = "10 km of Kukatpally"
    const val EXPRESS_DELIVERY_BANNER = "⚡ 2-Hour Express Delivery in Kukatpally (10 km radius)"
    const val EXCHANGE_POLICY_TEXT = "No Return • Only Exchange (Within 7 Days)"

    // Official Web Handoff & Customer URLs
    const val CHECKOUT_URL = "https://ustore4u.com/checkout/"
    const val CART_URL = "https://ustore4u.com/cart/"
    const val MY_ACCOUNT_URL = "https://ustore4u.com/my-account/"
    const val CONTACT_URL = "https://ustore4u.com/contact-us/"
    const val PRIVACY_URL = "https://ustore4u.com/privacy-policy/"
    const val TERMS_URL = "https://ustore4u.com/terms-and-conditions/"
    const val REFUND_URL = "https://ustore4u.com/refund-returns-policy/"

    // Currency Settings (INR for Indian operations)
    const val CURRENCY_SYMBOL = "₹"
    const val CURRENCY_CODE = "INR"
    
    // Shipping Thresholds (INR)
    const val FREE_SHIPPING_THRESHOLD = 999.0
    const val STANDARD_SHIPPING_COST = 49.0
    const val EXPRESS_SHIPPING_COST = 99.0
}
