package com.tfdev.inventorymanagement.ui.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tfdev.inventorymanagement.data.dao.CategoryDao
import com.tfdev.inventorymanagement.data.entity.Category
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val categoryDao: CategoryDao
) : ViewModel() {

    val categories = categoryDao.getAllCategories()

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    fun addCategory(category: Category) {
        viewModelScope.launch {
            try {
                categoryDao.insertCategory(category)
                _message.value = "Kategori başarıyla eklendi"
            } catch (e: Exception) {
                _message.value = "Kategori eklenirken hata oluştu: ${e.message}"
            }
        }
    }

    fun updateCategory(category: Category) {
        viewModelScope.launch {
            try {
                categoryDao.updateCategory(category)
                _message.value = "Kategori başarıyla güncellendi"
            } catch (e: Exception) {
                _message.value = "Kategori güncellenirken hata oluştu: ${e.message}"
            }
        }
    }

    fun deleteCategory(category: Category) {
        viewModelScope.launch {
            try {
                categoryDao.deleteCategory(category)
                _message.value = "Kategori başarıyla silindi"
            } catch (e: Exception) {
                _message.value = "Kategori silinirken hata oluştu: ${e.message}"
            }
        }
    }
} 