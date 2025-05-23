package com.example.chainsproject.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chainsproject.data.model.Product
import com.example.chainsproject.data.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class AddProductViewModel @Inject constructor(
    private val repository: ProductRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AddProductUiState>(AddProductUiState.Initial)
    val uiState: StateFlow<AddProductUiState> = _uiState.asStateFlow()

    fun addProduct(
        name: String,
        code: String,
        productionDate: Date,
        shelfLife: Int,
        manufacturer: String,
        origin: String,
        description: String
    ) {
        viewModelScope.launch {
            try {
                _uiState.value = AddProductUiState.Loading
                val product = Product(
                    name = name,
                    code = code,
                    productionDate = productionDate,
                    shelfLife = shelfLife,
                    manufacturer = manufacturer,
                    origin = origin,
                    description = description,
                    createdAt = Date(),
                    updatedAt = Date()
                )
                repository.addProduct(product)
                _uiState.value = AddProductUiState.Success("产品添加成功")
            } catch (e: Exception) {
                _uiState.value = AddProductUiState.Error(e.message ?: "添加产品失败")
            }
        }
    }
}

sealed class AddProductUiState {
    object Initial : AddProductUiState()
    object Loading : AddProductUiState()
    data class Success(val message: String) : AddProductUiState()
    data class Error(val message: String) : AddProductUiState()
} 