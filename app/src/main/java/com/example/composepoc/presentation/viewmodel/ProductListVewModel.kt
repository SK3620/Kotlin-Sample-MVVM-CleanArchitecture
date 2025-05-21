package com.example.composepoc.presentation.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.composepoc.core.common.UiState
import com.example.composepoc.domain.usecase.GetProductListUseCase
import com.example.composepoc.presentation.state.ProductListState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class ProductListVewModel @Inject constructor(
    private val productListUseCase: GetProductListUseCase
) : ViewModel() {

    // 状態を更新できる内部変数
    private val _productList = mutableStateOf(ProductListState())
    // View側から状態を読むだけの公開変数
    val productList : State<ProductListState> get() = _productList

    init {
        productListUseCase.invoke().onEach {
            when(it){
                is UiState.Loading->{
                    _productList.value = ProductListState(isLoading = true)
                }
                is UiState.Success->{
                    _productList.value = ProductListState(data = it.data)
                }
                is UiState.Error->{
                    _productList.value = ProductListState(error = it.message.toString())
                }
            }
        }.launchIn(viewModelScope)
    }
}