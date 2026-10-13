package com.example.data.repository

import com.example.data.model.Coupon
import com.example.data.model.Product
import com.example.data.model.ProductAttribute
import com.example.data.model.ProductCategory
import com.example.data.model.ProductCategoryRef
import com.example.data.model.ProductImage
import com.example.data.model.ProductReview

object UStore4UWatchData {

    val categories = listOf(
        ProductCategory(
            id = 101,
            name = "Men's Watches",
            slug = "mens-watches",
            description = "Titan, Fastrack, Sonata, and Casio watches crafted for modern men. Chronographs, analog classics, and metal straps.",
            count = 420,
            image = ProductImage(1, "https://images.unsplash.com/photo-1524805444758-089113d48a6d?w=800&auto=format&fit=crop&q=80", "Men's Watches")
        ),
        ProductCategory(
            id = 102,
            name = "Women's Watches",
            slug = "womens-watches",
            description = "Fastrack Vyb, Sonata Poze, Titan Raga, and Rose Gold mesh dials designed for everyday elegance and festive charm.",
            count = 380,
            image = ProductImage(2, "https://images.unsplash.com/photo-1533139502658-0198f920d8e8?w=800&auto=format&fit=crop&q=80", "Women's Watches")
        ),
        ProductCategory(
            id = 103,
            name = "Wall Clocks",
            slug = "wall-clocks",
            description = "Classic musical pendulum clocks, Ajanta quartz, vintage wooden wall clocks, and modern decorative pieces for home & office.",
            count = 140,
            image = ProductImage(3, "https://images.unsplash.com/photo-1563861826100-9cb868fdbe1c?w=800&auto=format&fit=crop&q=80", "Wall Clocks")
        ),
        ProductCategory(
            id = 104,
            name = "Couple & Pair Watches",
            slug = "couple-watches",
            description = "Matching his & hers celebration watches including Titan Bandhan and Sonata Wedding Collections.",
            count = 65,
            image = ProductImage(4, "https://images.unsplash.com/photo-1522335789203-aabd1fc54bc9?w=800&auto=format&fit=crop&q=80", "Couple Watches")
        ),
        ProductCategory(
            id = 105,
            name = "Smart & Hybrid Watches",
            slug = "smart-watches",
            description = "AMOLED fitness trackers, Fastrack Reflex, Titan Smart, and Bluetooth calling hybrid timepieces.",
            count = 85,
            image = ProductImage(5, "https://images.unsplash.com/photo-1510017803434-a899398421b3?w=800&auto=format&fit=crop&q=80", "Smart Watches")
        ),
        ProductCategory(
            id = 106,
            name = "Chronograph & Sports",
            slug = "chronograph-watches",
            description = "Precision multi-dial stopwatches, racing tachymeter bezels, and 100m water-resistant sports watches.",
            count = 110,
            image = ProductImage(6, "https://images.unsplash.com/photo-1547996160-71dfabbce5ed?w=800&auto=format&fit=crop&q=80", "Chronograph Watches")
        ),
        ProductCategory(
            id = 107,
            name = "Luxury & Automatic",
            slug = "luxury-watches",
            description = "Ultra-slim sapphire crystal timepieces, exhibition caseback mechanical automatics, and premium Swiss & Japanese movements.",
            count = 95,
            image = ProductImage(7, "https://images.unsplash.com/photo-1508685096489-7aacd43bd3b1?w=800&auto=format&fit=crop&q=80", "Luxury Watches")
        ),
        ProductCategory(
            id = 108,
            name = "Kids & Youth",
            slug = "kids-watches",
            description = "Vibrant, durable, and playful watches from Zoop by Titan and Fastrack for young trendsetters.",
            count = 75,
            image = ProductImage(8, "https://images.unsplash.com/photo-1509042239860-f550ce710b93?w=800&auto=format&fit=crop&q=80", "Kids Watches")
        )
    )

    val products = listOf(
        Product(
            id = 1001,
            name = "Titan Athleisure Grey Dial Analog Leather Strap Watch for Men",
            slug = "titan-athleisure-grey-dial-leather-watch-men",
            permalink = "https://ustore4u.com/product/titan-athleisure-grey-dial-watch-men",
            price = "4295.00",
            regularPrice = "5495.00",
            salePrice = "4295.00",
            onSale = true,
            featured = true,
            sku = "U4U-TITAN-ATL-01",
            stockStatus = "instock",
            stockQuantity = 14,
            ratingCount = 89,
            averageRating = "4.9",
            brand = "Titan",
            movement = "High Precision Japanese Quartz Analog",
            caseDiameter = "43mm",
            waterResistance = "5 ATM / 50m Water Resistance",
            glassType = "Scratch-Resistant Mineral Glass",
            strapMaterial = "Genuine Tan Brown Leather",
            warranty = "2-Year Manufacturer Warranty",
            description = "<p>The Titan Athleisure Grey Dial Men's Watch brings modern sport-luxe styling with a multi-layered textured slate dial, contrasting rose gold indices, and a premium genuine leather strap. Built for boardroom meetings and weekend adventures alike.</p><p><strong>Available with 2-Hour Express Delivery in Kukatpally (10 km radius)</strong> and backed by a 7-day exchange guarantee.</p>",
            shortDescription = "Sport-luxe Titan timepiece featuring slate grey textured dial, date window, and supple tan leather strap.",
            categories = listOf(
                ProductCategoryRef(101, "Men's Watches", "mens-watches"),
                ProductCategoryRef(106, "Chronograph & Sports", "chronograph-watches")
            ),
            images = listOf(
                ProductImage(101, "https://images.unsplash.com/photo-1524805444758-089113d48a6d?w=800&auto=format&fit=crop&q=80", "Titan Athleisure Grey Dial Watch"),
                ProductImage(102, "https://images.unsplash.com/photo-1522335789203-aabd1fc54bc9?w=800&auto=format&fit=crop&q=80", "Titan Athleisure Leather Strap Close-up"),
                ProductImage(103, "https://images.unsplash.com/photo-1508685096489-7aacd43bd3b1?w=800&auto=format&fit=crop&q=80", "Titan Athleisure Wrist View")
            ),
            attributes = listOf(
                ProductAttribute(1, "Brand", listOf("Titan")),
                ProductAttribute(2, "Dial Color", listOf("Slate Grey")),
                ProductAttribute(3, "Strap Color", listOf("Tan Brown")),
                ProductAttribute(4, "Express Delivery", listOf("2-Hour Kukatpally Eligible"))
            ),
            relatedIds = listOf(1002, 1003, 1007)
        ),
        Product(
            id = 1002,
            name = "Fastrack Vyb Manifester Quartz Analog Green Dial Metal Strap Watch For Girls",
            slug = "fastrack-vyb-manifester-green-dial-metal-strap",
            permalink = "https://ustore4u.com/product/fastrack-vyb-manifester-green-dial",
            price = "2195.00",
            regularPrice = "2695.00",
            salePrice = "2195.00",
            onSale = true,
            featured = true,
            sku = "U4U-FT-VYB-02",
            stockStatus = "instock",
            stockQuantity = 22,
            ratingCount = 114,
            averageRating = "4.8",
            brand = "Fastrack",
            movement = "Quartz Analog Precision 3-Hand",
            caseDiameter = "36mm",
            waterResistance = "3 ATM / 30m",
            glassType = "Mineral Crystal Glass",
            strapMaterial = "Stainless Steel Milanese Mesh (Silver)",
            warranty = "1-Year Official Warranty",
            description = "<p>Make a bold statement with the Fastrack Vyb Manifester watch. Boasting an emerald green sunray dial framed by polished silver accents and a fluid stainless steel mesh bracelet. Effortlessly chic for campus, brunch, and evening occasions.</p>",
            shortDescription = "Vibrant emerald sunray dial with polished stainless steel mesh strap from Fastrack Vyb series.",
            categories = listOf(
                ProductCategoryRef(102, "Women's Watches", "womens-watches")
            ),
            images = listOf(
                ProductImage(201, "https://images.unsplash.com/photo-1533139502658-0198f920d8e8?w=800&auto=format&fit=crop&q=80", "Fastrack Vyb Emerald Green Dial"),
                ProductImage(202, "https://images.unsplash.com/photo-1509042239860-f550ce710b93?w=800&auto=format&fit=crop&q=80", "Fastrack Vyb Mesh Strap")
            ),
            attributes = listOf(
                ProductAttribute(1, "Brand", listOf("Fastrack")),
                ProductAttribute(2, "Dial Color", listOf("Emerald Green")),
                ProductAttribute(3, "Strap Material", listOf("Stainless Steel Mesh"))
            ),
            relatedIds = listOf(1003, 1010, 1006)
        ),
        Product(
            id = 1003,
            name = "Sonata Poze Quartz Analog Multicoloured Dial Leather Strap Watch For Women",
            slug = "sonata-poze-multicolour-dial-leather-watch-women",
            permalink = "https://ustore4u.com/product/sonata-poze-multicoloured-dial-women",
            price = "1495.00",
            regularPrice = "1895.00",
            salePrice = "1495.00",
            onSale = true,
            featured = true,
            sku = "U4U-SN-POZE-03",
            stockStatus = "instock",
            stockQuantity = 19,
            ratingCount = 76,
            averageRating = "4.7",
            brand = "Sonata",
            movement = "Reliable Quartz Movement",
            caseDiameter = "34mm",
            waterResistance = "3 ATM Water Resistant",
            glassType = "Durable Mineral Glass",
            strapMaterial = "Genuine Pastel Blush Leather",
            warranty = "1-Year Warranty",
            description = "<p>The Sonata Poze collection celebrates youthful vibrance. Featuring an artistic multicoloured pastel dial with rose gold hour markers and a soft blush pink leather strap that ensures all-day comfort.</p>",
            shortDescription = "Sonata Poze pastel multicoloured dial with chic blush pink leather strap.",
            categories = listOf(
                ProductCategoryRef(102, "Women's Watches", "womens-watches")
            ),
            images = listOf(
                ProductImage(301, "https://images.unsplash.com/photo-1509042239860-f550ce710b93?w=800&auto=format&fit=crop&q=80", "Sonata Poze Multicolour Dial"),
                ProductImage(302, "https://images.unsplash.com/photo-1533139502658-0198f920d8e8?w=800&auto=format&fit=crop&q=80", "Sonata Poze Wrist Strap")
            ),
            attributes = listOf(
                ProductAttribute(1, "Brand", listOf("Sonata")),
                ProductAttribute(2, "Strap Color", listOf("Blush Pink")),
                ProductAttribute(3, "Dial Color", listOf("Multicoloured Pastel"))
            ),
            relatedIds = listOf(1002, 1010)
        ),
        Product(
            id = 1004,
            name = "Ajanta Classic Musical Pendulum Quartz Wall Clock",
            slug = "ajanta-classic-musical-pendulum-wall-clock",
            permalink = "https://ustore4u.com/product/ajanta-musical-pendulum-clock",
            price = "1650.00",
            regularPrice = "2030.00",
            salePrice = "1650.00",
            onSale = true,
            featured = true,
            sku = "U4U-CLK-AJANTA-04",
            stockStatus = "instock",
            stockQuantity = 28,
            ratingCount = 92,
            averageRating = "4.8",
            brand = "Ajanta",
            movement = "Silent Sweep Quartz with Hourly Musical Chime",
            caseDiameter = "48cm x 30cm",
            waterResistance = "Indoor Use Only",
            glassType = "High Transparency Float Glass",
            strapMaterial = "Rich Mahogany Wood Grain Finish",
            warranty = "1-Year Replacement Warranty",
            description = "<p>Transform your living room or foyer with this Ajanta Classic Musical Pendulum Wall Clock. Features hourly melodic chimes, smooth oscillating pendulum, large golden Roman numerals, and a vintage wood-grain cabinet.</p><p><strong>Fast delivery available across Hyderabad & Telangana.</strong></p>",
            shortDescription = "Vintage mahogany-finish wall clock with swinging pendulum and hourly musical melody.",
            categories = listOf(
                ProductCategoryRef(103, "Wall Clocks", "wall-clocks")
            ),
            images = listOf(
                ProductImage(401, "https://images.unsplash.com/photo-1563861826100-9cb868fdbe1c?w=800&auto=format&fit=crop&q=80", "Ajanta Classic Musical Pendulum Wall Clock"),
                ProductImage(402, "https://images.unsplash.com/photo-1508057198894-247b23fe5ade?w=800&auto=format&fit=crop&q=80", "Ajanta Clock Dial Close-up")
            ),
            attributes = listOf(
                ProductAttribute(1, "Brand", listOf("Ajanta")),
                ProductAttribute(2, "Type", listOf("Musical Pendulum Clock")),
                ProductAttribute(3, "Finish", listOf("Mahogany Wood Grain"))
            ),
            relatedIds = listOf(1005, 1001)
        ),
        Product(
            id = 1005,
            name = "Oreva Contemporary Living Room Decorative Wall Clock",
            slug = "oreva-contemporary-decorative-wall-clock",
            permalink = "https://ustore4u.com/product/oreva-contemporary-wall-clock",
            price = "1170.00",
            regularPrice = "1450.00",
            salePrice = "1170.00",
            onSale = true,
            featured = false,
            sku = "U4U-CLK-OREVA-05",
            stockStatus = "instock",
            stockQuantity = 35,
            ratingCount = 58,
            averageRating = "4.6",
            brand = "Oreva",
            movement = "Silent Sweep Non-Ticking Quartz",
            caseDiameter = "32cm Round",
            waterResistance = "Dust Resistant",
            glassType = "Anti-Glare Acrylic Glass",
            strapMaterial = "Brushed Copper Metallic Rim",
            warranty = "1-Year Official Warranty",
            description = "<p>Minimalist, completely silent non-ticking Oreva wall clock designed for bedrooms, study rooms, and modern offices. Featuring bold 3D numerals against a textured ivory background.</p>",
            shortDescription = "Modern non-ticking silent sweep round wall clock with brushed metallic copper rim.",
            categories = listOf(
                ProductCategoryRef(103, "Wall Clocks", "wall-clocks")
            ),
            images = listOf(
                ProductImage(501, "https://images.unsplash.com/photo-1508057198894-247b23fe5ade?w=800&auto=format&fit=crop&q=80", "Oreva Round Wall Clock")
            ),
            attributes = listOf(
                ProductAttribute(1, "Brand", listOf("Oreva")),
                ProductAttribute(2, "Dial Color", listOf("Ivory White")),
                ProductAttribute(3, "Movement", listOf("Silent Sweep Quartz"))
            ),
            relatedIds = listOf(1004)
        ),
        Product(
            id = 1006,
            name = "Titan Bandhan Champagne Dial Couple Pair Watches",
            slug = "titan-bandhan-champagne-couple-pair-watches",
            permalink = "https://ustore4u.com/product/titan-bandhan-champagne-pair",
            price = "7995.00",
            regularPrice = "9995.00",
            salePrice = "7995.00",
            onSale = true,
            featured = true,
            sku = "U4U-TITAN-BDH-06",
            stockStatus = "instock",
            stockQuantity = 9,
            ratingCount = 47,
            averageRating = "4.9",
            brand = "Titan",
            movement = "Twin Quartz Analog Movements",
            caseDiameter = "Men: 41mm | Women: 32mm",
            waterResistance = "5 ATM / 50m",
            glassType = "Scratch-Resistant Mineral Crystal",
            strapMaterial = "Dual-Tone Gold & Stainless Steel Bracelet",
            warranty = "2-Year Titan National Warranty",
            description = "<p>Celebrate love and milestones with the iconic Titan Bandhan collection. Set contains two matching dual-tone stainless steel and gold-plated timepieces featuring rich sunray champagne dials and date displays.</p>",
            shortDescription = "Matching pair of his & hers luxury dual-tone watches from Titan Bandhan with champagne dials.",
            categories = listOf(
                ProductCategoryRef(104, "Couple & Pair Watches", "couple-watches"),
                ProductCategoryRef(107, "Luxury & Automatic", "luxury-watches")
            ),
            images = listOf(
                ProductImage(601, "https://images.unsplash.com/photo-1522335789203-aabd1fc54bc9?w=800&auto=format&fit=crop&q=80", "Titan Bandhan Couple Set"),
                ProductImage(602, "https://images.unsplash.com/photo-1524805444758-089113d48a6d?w=800&auto=format&fit=crop&q=80", "Titan Bandhan Men's Watch"),
                ProductImage(603, "https://images.unsplash.com/photo-1533139502658-0198f920d8e8?w=800&auto=format&fit=crop&q=80", "Titan Bandhan Women's Watch")
            ),
            attributes = listOf(
                ProductAttribute(1, "Brand", listOf("Titan")),
                ProductAttribute(2, "Style", listOf("Couple Set (2 Watches)")),
                ProductAttribute(3, "Finish", listOf("Dual-Tone Gold & Silver"))
            ),
            relatedIds = listOf(1001, 1007, 1010)
        ),
        Product(
            id = 1007,
            name = "Titan Edge Ultra-Slim Sapphire Ceramic Analog Watch",
            slug = "titan-edge-ultra-slim-sapphire-watch",
            permalink = "https://ustore4u.com/product/titan-edge-ultra-slim-ceramic",
            price = "12495.00",
            regularPrice = "14995.00",
            salePrice = "12495.00",
            onSale = true,
            featured = true,
            sku = "U4U-TITAN-EDGE-07",
            stockStatus = "instock",
            stockQuantity = 6,
            ratingCount = 33,
            averageRating = "5.0",
            brand = "Titan",
            movement = "In-House Ultra-Thin Quartz Caliber (1.15mm thickness)",
            caseDiameter = "39mm x 4.4mm Ultra-Slim",
            waterResistance = "3 ATM Water Resistance",
            glassType = "High-Purity Sapphire Crystal Glass",
            strapMaterial = "Hypoallergenic Gloss Black Ceramic",
            warranty = "2-Year International Titan Warranty",
            description = "<p>Titan Edge is heralded as one of the world's slimmest ceramic quartz watches. Engineered with a featherlight 4.4mm slim profile, anti-reflective sapphire glass, and a mirror-finish ceramic case and bracelet.</p>",
            shortDescription = "World-class ultra-slim 4.4mm ceramic timepiece with pure sapphire glass.",
            categories = listOf(
                ProductCategoryRef(101, "Men's Watches", "mens-watches"),
                ProductCategoryRef(107, "Luxury & Automatic", "luxury-watches")
            ),
            images = listOf(
                ProductImage(701, "https://images.unsplash.com/photo-1547996160-71dfabbce5ed?w=800&auto=format&fit=crop&q=80", "Titan Edge Black Ceramic"),
                ProductImage(702, "https://images.unsplash.com/photo-1522335789203-aabd1fc54bc9?w=800&auto=format&fit=crop&q=80", "Titan Edge Ultra-Slim Profile")
            ),
            attributes = listOf(
                ProductAttribute(1, "Brand", listOf("Titan")),
                ProductAttribute(2, "Glass", listOf("Sapphire Crystal")),
                ProductAttribute(3, "Case Material", listOf("Ceramic"))
            ),
            relatedIds = listOf(1001, 1006, 1012)
        ),
        Product(
            id = 1008,
            name = "Fastrack Reflex Play AMOLED Smart Watch with BT Calling",
            slug = "fastrack-reflex-play-smart-watch",
            permalink = "https://ustore4u.com/product/fastrack-reflex-play-smartwatch",
            price = "3495.00",
            regularPrice = "5995.00",
            salePrice = "3495.00",
            onSale = true,
            featured = true,
            sku = "U4U-FT-RFX-08",
            stockStatus = "instock",
            stockQuantity = 25,
            ratingCount = 145,
            averageRating = "4.8",
            brand = "Fastrack",
            movement = "Digital Microcontroller with Real-time OS",
            caseDiameter = "1.43\" Ultra AMOLED 466x466 Display",
            waterResistance = "IP68 Water & Dust Proof",
            glassType = "2.5D Curved Gorilla Glass",
            strapMaterial = "Breathable Midnight Black Silicone",
            warranty = "1-Year Smart Warranty",
            description = "<p>Fastrack Reflex Play features a stunning Always-On 1.43-inch AMOLED display, single-chip Bluetooth calling with noise cancellation, 100+ sports modes, AI voice assistant, 24/7 SpO2, and up to 7-day battery life.</p>",
            shortDescription = "1.43\" AMOLED Bluetooth calling smartwatch with health tracking and IP68 resistance.",
            categories = listOf(
                ProductCategoryRef(105, "Smart & Hybrid Watches", "smart-watches")
            ),
            images = listOf(
                ProductImage(801, "https://images.unsplash.com/photo-1510017803434-a899398421b3?w=800&auto=format&fit=crop&q=80", "Fastrack Reflex Play Smart Watch"),
                ProductImage(802, "https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=800&auto=format&fit=crop&q=80", "Fastrack Reflex AMOLED Display")
            ),
            attributes = listOf(
                ProductAttribute(1, "Brand", listOf("Fastrack")),
                ProductAttribute(2, "Display", listOf("1.43\" AMOLED")),
                ProductAttribute(3, "Calling", listOf("Bluetooth Calling"))
            ),
            relatedIds = listOf(1001, 1002)
        ),
        Product(
            id = 1009,
            name = "Sonata Formal Champagne Gold Dial Stainless Steel Men's Watch",
            slug = "sonata-formal-champagne-gold-mens-watch",
            permalink = "https://ustore4u.com/product/sonata-formal-gold-dial-men",
            price = "1895.00",
            regularPrice = "2295.00",
            salePrice = "1895.00",
            onSale = true,
            featured = false,
            sku = "U4U-SN-GLD-09",
            stockStatus = "instock",
            stockQuantity = 30,
            ratingCount = 64,
            averageRating = "4.6",
            brand = "Sonata",
            movement = "Reliable Quartz 3-Hand",
            caseDiameter = "40mm",
            waterResistance = "3 ATM Water Resistant",
            glassType = "Mineral Glass",
            strapMaterial = "Polished Gold Stainless Steel Link",
            warranty = "1-Year Official Warranty",
            description = "<p>A timeless formal classic for celebratory wear, weddings, and office dressing. The Sonata Gold Dial features crisp golden indices and a durable stainless steel link strap.</p>",
            shortDescription = "Traditional gold-tone formal watch for men with sunray champagne dial.",
            categories = listOf(
                ProductCategoryRef(101, "Men's Watches", "mens-watches")
            ),
            images = listOf(
                ProductImage(901, "https://images.unsplash.com/photo-1524805444758-089113d48a6d?w=800&auto=format&fit=crop&q=80", "Sonata Gold Watch Men")
            ),
            attributes = listOf(
                ProductAttribute(1, "Brand", listOf("Sonata")),
                ProductAttribute(2, "Color", listOf("Champagne Gold"))
            ),
            relatedIds = listOf(1001, 1003)
        ),
        Product(
            id = 1010,
            name = "Titan Raga Viva Rose Gold Mother of Pearl Women's Watch",
            slug = "titan-raga-viva-rose-gold-women-watch",
            permalink = "https://ustore4u.com/product/titan-raga-viva-rose-gold",
            price = "5995.00",
            regularPrice = "7495.00",
            salePrice = "5995.00",
            onSale = true,
            featured = true,
            sku = "U4U-TITAN-RAGA-10",
            stockStatus = "instock",
            stockQuantity = 11,
            ratingCount = 52,
            averageRating = "4.9",
            brand = "Titan",
            movement = "Swiss-Inspired Quartz Caliber",
            caseDiameter = "30mm Jewellery Case",
            waterResistance = "3 ATM",
            glassType = "Curved Mineral Crystal",
            strapMaterial = "Jewellery Clasp Rose Gold Bangle Bracelet",
            warranty = "2-Year Titan Warranty",
            description = "<p>Embrace timeless feminine beauty with Titan Raga Viva. Designed with an iridescent natural mother-of-pearl dial, Swarovski crystal indices, and an intricate sculpted rose gold bangle strap.</p>",
            shortDescription = "Mother-of-pearl dial with Swarovski crystal markers and sculpted rose gold bracelet.",
            categories = listOf(
                ProductCategoryRef(102, "Women's Watches", "womens-watches"),
                ProductCategoryRef(107, "Luxury & Automatic", "luxury-watches")
            ),
            images = listOf(
                ProductImage(1001, "https://images.unsplash.com/photo-1533139502658-0198f920d8e8?w=800&auto=format&fit=crop&q=80", "Titan Raga Viva Rose Gold")
            ),
            attributes = listOf(
                ProductAttribute(1, "Brand", listOf("Titan")),
                ProductAttribute(2, "Collection", listOf("Titan Raga")),
                ProductAttribute(3, "Dial", listOf("Natural Mother of Pearl"))
            ),
            relatedIds = listOf(1002, 1006)
        ),
        Product(
            id = 1011,
            name = "Casio Vintage Digital Gold Stainless Steel Unisex Watch",
            slug = "casio-vintage-digital-gold-unisex-watch",
            permalink = "https://ustore4u.com/product/casio-vintage-digital-gold",
            price = "3995.00",
            regularPrice = "4495.00",
            salePrice = "3995.00",
            onSale = true,
            featured = false,
            sku = "U4U-CASIO-VNT-11",
            stockStatus = "instock",
            stockQuantity = 18,
            ratingCount = 88,
            averageRating = "4.8",
            brand = "Casio",
            movement = "Casio Digital Quartz Module with ElectroLuminescence",
            caseDiameter = "36.8mm Unisex",
            waterResistance = "Water Resistant (Daily Splash)",
            glassType = "Resin Glass",
            strapMaterial = "Adjustable Gold Ion-Plated Stainless Steel",
            warranty = "2-Year Casio India Warranty",
            description = "<p>The immortal retro icon. Features 1/100-second stopwatch, daily alarm, hourly time signal, auto-calendar, amber EL backlight, and classic gold vintage casing.</p>",
            shortDescription = "Iconic retro gold digital watch with stopwatch, EL backlight, and alarm.",
            categories = listOf(
                ProductCategoryRef(101, "Men's Watches", "mens-watches"),
                ProductCategoryRef(102, "Women's Watches", "womens-watches")
            ),
            images = listOf(
                ProductImage(1101, "https://images.unsplash.com/photo-1508685096489-7aacd43bd3b1?w=800&auto=format&fit=crop&q=80", "Casio Vintage Digital Gold")
            ),
            attributes = listOf(
                ProductAttribute(1, "Brand", listOf("Casio")),
                ProductAttribute(2, "Movement", listOf("Digital Quartz"))
            ),
            relatedIds = listOf(1001, 1008)
        ),
        Product(
            id = 1012,
            name = "Titan Octane Chronograph Deep Blue Dial Sports Watch",
            slug = "titan-octane-chronograph-blue-dial-watch",
            permalink = "https://ustore4u.com/product/titan-octane-chronograph-blue",
            price = "8995.00",
            regularPrice = "10995.00",
            salePrice = "8995.00",
            onSale = true,
            featured = true,
            sku = "U4U-TITAN-OCT-12",
            stockStatus = "instock",
            stockQuantity = 8,
            ratingCount = 41,
            averageRating = "4.9",
            brand = "Titan",
            movement = "Multi-Function High Beat Chronograph",
            caseDiameter = "44mm Solid Stainless Steel",
            waterResistance = "10 ATM / 100m Water Resistance",
            glassType = "Mineral Crystal with Anti-Scratch Coating",
            strapMaterial = "Solid Solid-Link Stainless Steel",
            warranty = "2-Year Titan Warranty",
            description = "<p>Built for adrenaline and precision. The Titan Octane Chronograph showcases a deep ocean blue sunray dial with 3 functional subdials, date aperture, tachymeter scale, and 100-meter water resistance.</p>",
            shortDescription = "Multi-function chronograph with oceanic blue dial and 100m water resistance.",
            categories = listOf(
                ProductCategoryRef(101, "Men's Watches", "mens-watches"),
                ProductCategoryRef(106, "Chronograph & Sports", "chronograph-watches")
            ),
            images = listOf(
                ProductImage(1201, "https://images.unsplash.com/photo-1547996160-71dfabbce5ed?w=800&auto=format&fit=crop&q=80", "Titan Octane Chronograph")
            ),
            attributes = listOf(
                ProductAttribute(1, "Brand", listOf("Titan")),
                ProductAttribute(2, "Water Resistance", listOf("100m"))
            ),
            relatedIds = listOf(1001, 1007)
        )
    )

    val sampleCoupons = listOf(
        Coupon(
            id = 1,
            code = "USTORE10",
            amount = "10",
            discountType = "percent",
            description = "10% off storewide on all watches and wall clocks"
        ),
        Coupon(
            id = 2,
            code = "KUKATPALLY50",
            amount = "50",
            discountType = "fixed_cart",
            description = "₹50 flat discount for Kukatpally express deliveries"
        ),
        Coupon(
            id = 3,
            code = "WELCOME15",
            amount = "15",
            discountType = "percent",
            description = "15% off first order on UStore4U App"
        )
    )

    val coupons = sampleCoupons

    val sampleReviews = listOf(
        ProductReview(
            id = 1,
            productId = 1001,
            reviewer = "Rahul Sharma (Hyderabad)",
            reviewerEmail = "rahul.s@example.com",
            review = "Received in less than 2 hours in Kukatpally! Genuine Titan watch with stamped warranty card. Impressive service.",
            rating = 5,
            dateCreated = "2026-08-28T14:30:00"
        ),
        ProductReview(
            id = 2,
            productId = 1004,
            reviewer = "Srinivas Rao",
            reviewerEmail = "srinivas.r@example.com",
            review = "The Ajanta musical pendulum wall clock looks magnificent in our living room. Soft melodious chimes and perfect finish.",
            rating = 5,
            dateCreated = "2026-08-25T11:15:00"
        ),
        ProductReview(
            id = 3,
            productId = 1002,
            reviewer = "Pooja V.",
            reviewerEmail = "pooja.v@example.com",
            review = "Fastrack Vyb looks even better in real life! The emerald green dial sparkles beautifully with the mesh band.",
            rating = 5,
            dateCreated = "2026-08-20T16:45:00"
        )
    )
}
