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
 *
 *
 */

/**
 * =====================================================================
 * 📚 【学習用メモ】Flowってなに？ SwiftUI (Combine) との比較
 * =====================================================================
 *
 * Q. この flow { ... } って結局なにをやっているの？
 * A. 1つの処理の中で「複数の状態（ローディング ➔ 成功/失敗）」を
 *    順番に実況中継しながら画面に送り出す「データ用の水道管（パイプライン）」です。
 *
 * ---------------------------------------------------------------------
 * 💡 Swift / Combine で例えると？
 * ---------------------------------------------------------------------
 * Kotlinの `Flow` は、Swiftの `AnyPublisher` や `CurrentValueSubject` に似ています。
 *
 * [Kotlin]                       [SwiftUI / Combine]
 * flow {                        let subject = CurrentValueSubject(...)
 *     emit(Loading)      ==>    subject.send(.loading)
 *     emit(Success)      ==>    subject.send(.success)
 * }                             return subject.eraseToAnyPublisher()
 *
 * ---------------------------------------------------------------------
 * 🛠️ コードの分解解説
 * 1. flow { ... }     : データを順番に流すストーリー（パイプ）の定義
 * 2. emit(...)        : パイプの中に新しいデータを「ポトッと投げ入れる」命令
 * 3. .flowOn(Dispatchers.IO) : この通信処理は、裏側のスレッドで安全に実行する指示
 * ====================================================================
 */