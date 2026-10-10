import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * =================================================================
 * 【学習メモ】State Hoisting（状態ホイスティング）とデータフローの仕組み
 * =================================================================
 *
 * 1. State Hoisting とは？
 *    - UI部品の内部から remember（状態）を取り除き、呼び出し元（親）へ引き上げる（Hoist）設計パターン。
 *    - UI部品を「ステートレス（状態を持たない）」にすることで、再利用性・テスト性を向上させる。
 *
 * -----------------------------------------------------------------
 * 💡 なぜ `count = count, onIncrement = { count++ }` と分けるのか？
 * -----------------------------------------------------------------
 * 【理由：カプセル化と安全性の確保（単一方向データフロー: UDF）】
 *   - `count = count`（読み取り専用）:
 *     子コンポーザブルには「現在の値」のみを見せる。子はこれを直接書き換えることができない。
 *   - `onIncrement = { count++ }`（更新依頼の窓口）:
 *     「ボタンが押されたら実行してね」というイベント処理（ラムダ式）だけを渡す。
 *
 *   ⇒ 子（UI）がデータを直接改ざんするのを防ぎ、「状態を変更できる権限を持つのは親だけ」
 *      という安全なルールを守るため。
 *
 * -----------------------------------------------------------------
 * 🔄 `count++` が実行された時の裏側の流れ（再コンポーズの仕組み）
 * -----------------------------------------------------------------
 * 1. [ユーザー操作] ボタンがタップされる。
 * 2. [イベント発火] 子で `onIncrement()` が呼び出され、親の `{ count++ }` が実行される。
 * 3. [状態更新]     親が保持する `mutableStateOf` の値が更新される（例: 0 -> 1）。
 * 4. [変更検知]     `mutableStateOf` が Compose システムへ「値が変わった！」と通知する。
 * 5. [再描画]       Compose が新しい値（count = 1）を使って `CounterContent` をピンポイントで再描画する。
 *
 * -----------------------------------------------------------------
 * 🆚 SwiftUI の `@Binding` との決定的な違い
 * -----------------------------------------------------------------
 * - SwiftUI (@Binding):
 *   - 双方向バインディング（Two-Way Binding）。
 *   - 子 View に `$count`（参照）を渡し、子の中で `count += 1` と書くと親の状態が直接書き換わる。
 *
 * - Jetpack Compose (State Hoisting):
 *   - 単一方向データフロー（Unidirectional Data Flow: UDF）。
 *   - 子に直接変数を書き換えさせず、「現在の値」と「更新イベント（コールバック）」を分けて渡す。
 * =================================================================
 */

// =================================================================
// 📱 SwiftUI 側の実装例（対比参考）
// =================================================================
/*
 // ① 子 View（Stateless）
 struct CounterContent: View {
     @Binding var count: Int // 親の状態の参照（Binding）をそのまま受け取る

     var body: some View {
         VStack {
             Text("現在のカウント: \(count)")
             Button("カウントアップ") {
                 count += 1 // 子から親の状態を直接書き換える
             }
         }
     }
 }

 // ② 親 View（Stateful）
 struct CounterScreen: View {
     @State private var count = 0

     var body: some View {
         CounterContent(count: $count) //$ をつけて参照（Binding）を渡す
     }
 }
*/

// =================================================================
// 🤖 Jetpack Compose 側の実装例
// =================================================================

/**
 * ① ステートレス（Stateless）コンポーザブル
 * UI の表示とイベントの通知だけに特化したコンポーネント。
 */
@Composable
fun CounterContent(
    count: Int,                    // 【値】親から流れてくる（表示のみ）
    onIncrement: () -> Unit,       // 【イベント】親へ操作を知らせる連絡窓口
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(text = "現在のカウント: $count")

        Button(
            onClick = onIncrement, // ボタン押下時に親の { count++ } をトリガー
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("カウントアップ")
        }
    }
}

/**
 * ② ステートフル（Stateful）コンポーザブル
 * 状態を所有・管理し、ステートレスな UI にデータを流し込む。
 */
@Composable
fun CounterScreen() {
    // 状態の所有権は親が保持する（SwiftUI の @State に相当）
    var count by remember { mutableStateOf(0) }

    // 値（count）と、更新イベント（{ count++ }）を分解して渡す
    CounterContent(
        count = count,
        onIncrement = { count++ },
        modifier = Modifier.padding(16.dp)
    )
}