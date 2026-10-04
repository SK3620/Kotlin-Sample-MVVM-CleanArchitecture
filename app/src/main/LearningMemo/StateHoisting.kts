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
 * 【学習メモ】State Hoisting（状態ホイスティング）比較ノート
 * =================================================================
 *
 * 概念：
 *   コンポーザブル内部で保持している状態（remember）を取り除き、
 *   呼び出し元（親）へ引き上げる（Hoist）ことで、UIを「ステートレス」にするパターン。
 *
 * -----------------------------------------------------------------
 * ❌ Before の問題点（ステートフルな構成）
 * -----------------------------------------------------------------
 * 1. 再利用性の欠如：
 *    - カウント状態（count）がコンポーネント内部に固定化されているため、
 *      「初期値を変える」「ボタン押下時の動作を変える」といったカスタマイズができない。
 *
 * 2. テスト・Previewの困難さ：
 *    - 「count が 10 のときの描画確認」を Android Studio の @Preview や UIテストで再現しにくい。
 *
 * 3. 状態の一元管理が不可能：
 *    - 親画面や別のコンポーネントから `count` の値を出力・監視・リセットできない。
 *
 * -----------------------------------------------------------------
 * ⭕ After による解決メリット（ステートレス化）
 * -----------------------------------------------------------------
 * 1. 再利用性が大幅向上：
 *    - 表示ロジック専用の UI 部品になるため、どんな画面や数値データでも使い回せる。
 *
 * 2. テスト・Preview が非常に容易：
 *    - 引数に固定値（例: count = 10）を渡すだけで、特定の画面状態を瞬時にテスト可能。
 *
 * 3. 単一光源（Single Source of Truth）：
 *    - 状態の保持場所が親（または ViewModel）に集約され、状態のバグを追跡しやすくなる。
 * =================================================================
 */

// =================================================================
// ❌ BEFORE: ホイスティング前（ステートフル）
// =================================================================
@Composable
fun CounterBefore() {
    // 内部で状態を抱え込んでいるため、外から制御できない
    var count by remember { mutableStateOf(0) }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "カウント: $count")
        Button(onClick = { count++ }) {
            Text("加算")
        }
    }
}


// =================================================================
// ⭕ AFTER: ホイスティング後（単一方向データフロー: UDF）
// =================================================================

/**
 * ① ステートレス（Stateless）コンポーザブル
 * メリット: 表示・イベント通知に特化しているため、テストしやすく使い回しが効く。
 */
@Composable
fun CounterContent(
    count: Int,              // 状態（State）：親から流れてくる（ダウン）
    onIncrement: () -> Unit, // イベント（Event）：親へ通知する（アップ）
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(text = "カウント: $count")
        Button(
            onClick = onIncrement, // 押されたら親に処理を任せる
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("加算")
        }
    }
}

/**
 * ② ステートフル（Stateful）コンポーザブル
 * 役割: 状態（remember）を保持し、ステートレスな CounterContent に流し込む。
 */
@Composable
fun CounterScreen() {
    // 状態の所有権をここで保持する
    var count by remember { mutableStateOf(0) }

    CounterContent(
        count = count,
        onIncrement = { count++ },
        modifier = Modifier.padding(16.dp)
    )
}