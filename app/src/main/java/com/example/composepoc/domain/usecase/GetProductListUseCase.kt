package com.example.composepoc.domain.usecase

import com.example.composepoc.core.common.UiState
import com.example.composepoc.data.respository.RepositoryImpl
import com.example.composepoc.domain.model.ProductItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.lang.Exception
import javax.inject.Inject

class GetProductListUseCase @Inject constructor(
    private val repositoryImpl : RepositoryImpl
)  {
    operator fun invoke() : Flow<UiState<List<ProductItem>>> = flow {
        emit(UiState.Loading())
        try {
            emit(UiState.Success(data = repositoryImpl.getProductList()))
        }catch (e : Exception){
            emit(UiState.Error(message = e.message.toString()))
        }
    }.flowOn(Dispatchers.IO)

}

/**
 * swiftUIで例えると、、、？
 * swift
 * // Swift (Combine)
 * func invoke() -> AnyPublisher<UiState, Never> {
 *     return Deferred {
 *         Future { promise in
 *             // ここでデータを送る
 *         }
 *     }.eraseToAnyPublisher()
 * }
 */