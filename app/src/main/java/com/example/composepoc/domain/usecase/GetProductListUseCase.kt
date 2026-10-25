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


/*
 =====================================================================
 【学習メモ】Kotlin Flow の combine 基礎 & UiState 実例
 =====================================================================

 1. combine とは？
 ---------------------------------------------------------------------
 ・複数の Flow が持つ「最新の値」を組み合わせて、新しい値を作るための演算子。
 ・Swift の Combine における `combineLatest` と同じ概念。

   val combinedFlow = combine(flowA, flowB) { valueA, valueB ->
       // flowA や flowB のどちらかの値が変わるたびにここが再実行され、
       // 新しい値を返すことができる
   }

 ・特徴：
   - どちらか片方の値が変わるだけでも、組み合わせの再計算が行われる。
   - すべての Flow から「最低1回ずつ」値が届いてから、初めて結果が出力される。
   - `combine` 自体も新しい Flow を作るため、値を受け取るには `collect` や
     Compose の `collectAsStateWithLifecycle()` が必要。


 2. UIState との組み合わせ（実例）
 ---------------------------------------------------------------------
 ・画面の状態（ローディング中か、商品リストがあるか等）を複数の Flow から
   バラバラに管理するのではなく、`combine` を使って 1つの `UiState` クラスに
   まとめるのが Android（Jetpack Compose）のモダンな設計。
*/

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine

// ① 画面に表示する個別のデータモデル
data class Product(
    val id: Int,
    val name: String
)

// ② 画面の状態（UiState）をまとめたデータクラス
data class ProductUiState(
    val products: List<Product> = emptyList(),
    val isLoading: Boolean = false
)

// ③ ViewModel での状態管理の例
class ProductViewModel : ViewModel() {

    // 非公開の MutableStateFlow（商品リスト）
    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products = _products.asStateFlow()

    // 非公開の MutableStateFlow（ローディング状態）
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    // -----------------------------------------------------------------
    // 【重要】combine を使って 2つの Flow から 1つの UiState を作り出す
    // -----------------------------------------------------------------
    val uiState = combine(
        _products,
        _isLoading
    ) { productsValue, isLoadingValue ->

        // _products または _isLoading の値が更新されるたびに、
        // 最新の値を元に新しい ProductUiState を生成して流す
        ProductUiState(
            products = productsValue,
            isLoading = isLoadingValue
        )
    }

    // データの読み込みシミュレーション
    fun loadProducts() {
        // ローディング開始（ここで _isLoading が変わり、combine が発火する）
        _isLoading.value = true

        // データをセット（ここで _products が変わり、combine が発火する）
        _products.value = listOf(
            Product(1, "Apple"),
            Product(2, "Banana")
        )

        // ローディング終了（ここで再度 _isLoading が変わり、combine が発火する）
        _isLoading.value = false
    }
}


/*
 3. Compose 側での受け取り方（参考）
 ---------------------------------------------------------------------
 @Composable
 fun ProductScreen(viewModel: ProductViewModel = viewModel()) {
     // UiState の Flow を Compose の State としてライフサイクル安全に購読する
     val uiState by viewModel.uiState.collectAsStateWithLifecycle(
         initialValue = ProductUiState()
     )

     // uiState のプロパティに応じて画面を切り替える
     when {
         uiState.isLoading -> {
             // ローディング中の表示
         }
         uiState.products.isEmpty() -> {
             // 商品が空の場合の表示
         }
         else -> {
             // 商品リストの表示
             uiState.products.forEach { product ->
                 // Text(product.name)
             }
         }
     }
 }
*/