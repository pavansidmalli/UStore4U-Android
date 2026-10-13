package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ProductCategory
import com.example.data.repository.SortOption
import com.example.ui.theme.GrayBorder
import com.example.ui.theme.LiteAshBg
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.PureWhite
import com.example.ui.theme.SubtleDivider

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun FilterBottomSheet(
    onDismissRequest: () -> Unit,
    categories: List<ProductCategory> = emptyList(),
    selectedCategoryId: Long? = null,
    selectedBrand: String? = null,
    minPrice: Double? = null,
    maxPrice: Double? = null,
    onlyInStock: Boolean = false,
    onlyOnSale: Boolean = false,
    currentSort: SortOption = SortOption.LATEST,
    onApplyFilters: (
        categoryId: Long?,
        brand: String?,
        minPrice: Double?,
        maxPrice: Double?,
        inStock: Boolean,
        onSale: Boolean,
        sort: SortOption
    ) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var tempCategoryId by remember { mutableStateOf(selectedCategoryId) }
    var tempBrand by remember { mutableStateOf(selectedBrand) }
    var tempMinPrice by remember { mutableStateOf(minPrice ?: 0.0) }
    var tempMaxPrice by remember { mutableStateOf(maxPrice ?: 15000.0) }
    var tempInStock by remember { mutableStateOf(onlyInStock) }
    var tempOnSale by remember { mutableStateOf(onlyOnSale) }
    var tempSort by remember { mutableStateOf(currentSort) }

    val brands = listOf("All Brands", "Titan", "Fastrack", "Sonata", "Ajanta", "Casio", "Helix")
    val pricePresets = listOf(
        "Under ₹1,500" to (0.0 to 1500.0),
        "₹1,500 - ₹3,000" to (1500.0 to 3000.0),
        "₹3,000 - ₹6,000" to (3000.0 to 6000.0),
        "₹6,000 - ₹12,000" to (6000.0 to 12000.0),
        "Above ₹12,000" to (12000.0 to 20000.0)
    )

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = PureWhite,
        shape = RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp),
        modifier = Modifier.testTag("filter_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "SORT & FILTER WATCHES",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = ObsidianBlack
                )
                TextButton(
                    onClick = {
                        tempCategoryId = null
                        tempBrand = null
                        tempMinPrice = 0.0
                        tempMaxPrice = 15000.0
                        tempInStock = false
                        tempOnSale = false
                        tempSort = SortOption.LATEST
                    },
                    modifier = Modifier.testTag("reset_filters_button")
                ) {
                    Text(
                        "RESET ALL",
                        color = ObsidianBlack,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        letterSpacing = 0.8.sp
                    )
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = SubtleDivider)

            // Sort Options
            Text(
                text = "SORT BY",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                color = ObsidianBlack
            )
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                SortOption.entries.forEach { option ->
                    val isSelected = tempSort == option
                    FilterChip(
                        selected = isSelected,
                        onClick = { tempSort = option },
                        label = {
                            Text(
                                option.title.uppercase(),
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                letterSpacing = 0.5.sp
                            )
                        },
                        shape = RoundedCornerShape(2.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ObsidianBlack,
                            selectedLabelColor = PureWhite,
                            containerColor = PureWhite,
                            labelColor = ObsidianBlack
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = GrayBorder,
                            selectedBorderColor = ObsidianBlack,
                            borderWidth = 1.dp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Quick Price Range Presets
            Text(
                text = "PRICE RANGES",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                color = ObsidianBlack
            )
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                pricePresets.forEach { (label, range) ->
                    val isSelected = tempMinPrice == range.first && tempMaxPrice == range.second
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            if (isSelected) {
                                tempMinPrice = 0.0
                                tempMaxPrice = 15000.0
                            } else {
                                tempMinPrice = range.first
                                tempMaxPrice = range.second
                            }
                        },
                        label = {
                            Text(
                                label,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        shape = RoundedCornerShape(2.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ObsidianBlack,
                            selectedLabelColor = PureWhite,
                            containerColor = PureWhite,
                            labelColor = ObsidianBlack
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = GrayBorder,
                            selectedBorderColor = ObsidianBlack,
                            borderWidth = 1.dp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Price Range Slider
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "CUSTOM RANGE",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "₹${tempMinPrice.toInt()} - ₹${tempMaxPrice.toInt()}${if (tempMaxPrice >= 15000.0) "+" else ""}",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = ObsidianBlack
                )
            }
            RangeSlider(
                value = (tempMinPrice.toFloat().coerceIn(0f, 15000f))..(tempMaxPrice.toFloat().coerceIn(0f, 15000f)),
                onValueChange = { range ->
                    tempMinPrice = range.start.toDouble()
                    tempMaxPrice = range.endInclusive.toDouble()
                },
                valueRange = 0f..15000f,
                steps = 14,
                colors = SliderDefaults.colors(
                    thumbColor = ObsidianBlack,
                    activeTrackColor = ObsidianBlack,
                    inactiveTrackColor = GrayBorder
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Brands Filter
            Text(
                text = "BRANDS & MAKERS",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                color = ObsidianBlack
            )
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                brands.forEach { brand ->
                    val isSelected = (brand == "All Brands" && tempBrand == null) || tempBrand == brand
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            tempBrand = if (brand == "All Brands") null else brand
                        },
                        label = {
                            Text(
                                brand.uppercase(),
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        shape = RoundedCornerShape(2.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ObsidianBlack,
                            selectedLabelColor = PureWhite,
                            containerColor = PureWhite,
                            labelColor = ObsidianBlack
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = GrayBorder,
                            selectedBorderColor = ObsidianBlack,
                            borderWidth = 1.dp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Category Filter
            if (categories.isNotEmpty()) {
                Text(
                    text = "CATEGORY",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = ObsidianBlack
                )
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val isAllSelected = tempCategoryId == null
                    FilterChip(
                        selected = isAllSelected,
                        onClick = { tempCategoryId = null },
                        label = {
                            Text(
                                "ALL WATCHES",
                                fontSize = 12.sp,
                                fontWeight = if (isAllSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        shape = RoundedCornerShape(2.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ObsidianBlack,
                            selectedLabelColor = PureWhite,
                            containerColor = PureWhite,
                            labelColor = ObsidianBlack
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isAllSelected,
                            borderColor = GrayBorder,
                            selectedBorderColor = ObsidianBlack,
                            borderWidth = 1.dp
                        )
                    )
                    categories.forEach { cat ->
                        val isCatSelected = tempCategoryId == cat.id
                        FilterChip(
                            selected = isCatSelected,
                            onClick = { tempCategoryId = cat.id },
                            label = {
                                Text(
                                    cat.name.uppercase(),
                                    fontSize = 12.sp,
                                    fontWeight = if (isCatSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            shape = RoundedCornerShape(2.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ObsidianBlack,
                                selectedLabelColor = PureWhite,
                                containerColor = PureWhite,
                                labelColor = ObsidianBlack
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isCatSelected,
                                borderColor = GrayBorder,
                                selectedBorderColor = ObsidianBlack,
                                borderWidth = 1.dp
                            )
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Toggles (In stock / On sale)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("IN STOCK ONLY", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = ObsidianBlack)
                    Text("Hide out of stock timepieces", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Switch(
                    checked = tempInStock,
                    onCheckedChange = { tempInStock = it },
                    colors = SwitchDefaults.colors(
                        checkedTrackColor = ObsidianBlack,
                        checkedThumbColor = PureWhite
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("ON SALE / SPECIAL OFFERS", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = ObsidianBlack)
                    Text("Show discounted watches only", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Switch(
                    checked = tempOnSale,
                    onCheckedChange = { tempOnSale = it },
                    colors = SwitchDefaults.colors(
                        checkedTrackColor = ObsidianBlack,
                        checkedThumbColor = PureWhite
                    )
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Action Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onDismissRequest,
                    shape = RoundedCornerShape(2.dp),
                    border = BorderStroke(1.dp, GrayBorder),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("CANCEL", color = ObsidianBlack, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
                Button(
                    onClick = {
                        onApplyFilters(
                            tempCategoryId,
                            tempBrand,
                            if (tempMinPrice > 0.0) tempMinPrice else null,
                            if (tempMaxPrice < 15000.0) tempMaxPrice else null,
                            tempInStock,
                            tempOnSale,
                            tempSort
                        )
                        onDismissRequest()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ObsidianBlack, contentColor = PureWhite),
                    shape = RoundedCornerShape(2.dp),
                    modifier = Modifier.weight(1.5f).testTag("apply_filters_button")
                ) {
                    Text("APPLY FILTERS", fontWeight = FontWeight.Bold, fontSize = 13.sp, letterSpacing = 0.8.sp)
                }
            }
        }
    }
}

