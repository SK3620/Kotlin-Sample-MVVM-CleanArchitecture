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
 * 【学習メモ】mutableStateOf / remember と 再描画（再コンポーズ）の仕組み
 * =================================================================
 *
 * 1. mutableStateOf(初期値) とは？
 *    - Compose のランタイムと統合された「観察可能（オブザーバブル）な状態」を作る関数。
 *    - 単なる変数（var count = 0）ではなく、Compose に「この値の変化を監視してね」と伝える。
 *
 * 2. remember { ... } とは？
 *    - 画面の再描画（再コンポーズ）が発生しても、内部の値をメモリに保持し続ける仕組み。
 *    - これがないと、画面が更新されて関数が再実行されるたびに値が初期値にリセットされてしまう。
 *
 * -----------------------------------------------------------------
 * 仕組み：なぜ値を変えると画面が書き換わるのか？（Snapshots ＆ トラッキング）
 * -----------------------------------------------------------------
 * ① 依存関係の登録（Read Tracking）：
 *    コンポーザブル関数の中で `count` を「読み取った（Read）」瞬間、Compose は
 *    「この Text コンポーザブルは count の値に依存している」という紐付けを裏で自動記録する。
 *
 * ② 変更の通知（Snapshot State）：
 *    `count++` などで `value` が書き換わると、`mutableStateOf` は Compose の統合メカニズムへ
 *    「count の値が変わった！」と通知を発出する。
 *
 * ③ ピンポイント再コンポーズ（Recomposition）：
 *    通知を受けた Compose は、①で記録した「依存している最小単位のUIパーツ（この場合 Text）」
 *    だけをスケジュールに載せ、新しい値を使ってピンポイントで再描画する。
 *
 * -----------------------------------------------------------------
 * 記法の違い（3つの書き方）
 * -----------------------------------------------------------------
 * A) Delegate（プロパティ委譲） ※最も推奨・一般的
 *    var count by remember { mutableStateOf(0) }
 *    -> `import androidx.compose.runtime.getValue` と `setValue` が必要。
 *       通常の `Int` 型変数のように `count = 1` で直感的に読み書きできる。
 *
 * B) 通常のプロパティ参照
 *    val countState = remember { mutableStateOf(0) }
 *    -> 読み書きには `countState.value` とアクセサを指定する。
 *
 * C) 分解宣言（Destructuring）
 *    val (count, setCount) = remember { mutableStateOf(0) }
 *    -> 値（count）と、更新用関数（setCount）を個別に受け取る。
 * =================================================================
 */

@Composable
fun StateBasicsSample() {
    // 【解説】
    // by キーワードを使用（`getValue` / `setValue` のインポートが必要）
    // 初回実行時：remember が 0 をメモリに記憶し、count に割り当てる。
    // 再コンポーズ時：remember が記憶していた最新の count の値を返す。
    var count by remember { mutableStateOf(0) }

    Column(modifier = Modifier.padding(16.dp)) {
        // ① この Text は count を読み取っているため、Compose に依存関係が自動登録される
        Text(text = "現在のカウント: $count")

        Button(
            onClick = {
                // ② ボタンが押されて count の値が更新される
                // ③ Compose が検知し、この count を使っている Text だけをピンポイントで再描画する
                count++
            },
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("カウントアップ")
        }
    }
}

/**
 * rememberなしの場合の書き方
 * @Composable
 * fun Counter() {
 *     // remember なし
 *     var count = 0
 *
 *     Button(onClick = { count++ }) {
 *         Text("現在の値: $count")
 *     }
 * }
 */