
/*
@Stable
interface State<out T> {
    val value: T
}

data class ProductListState(
    val isLoading: Boolean = false,
    val data: List<ProductItem>? = null,
    var error: String = ""
)

val productList : State<ProductListState> get() = _productList




import SwiftUI
import Combine

// データモデル（一覧画面の状態）
struct ProductItem {
    let id: Int
    let title: String
}

struct ProductListState {
    var isLoading: Bool = false
    var data: [ProductItem]? = nil
    var error: String = ""
}

// ViewModel（状態を管理するクラス）
class ProductListViewModel: ObservableObject {
    @Published var productList: ProductListState = ProductListState()
}

 */