import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * =================================================================
 * 【学習メモ】ViewModel と StateFlow の仕組み・キーワード完全解説
 * =================================================================
 *
 * 1. 🔍 各キーワードの超わかりやすい解説
 * -----------------------------------------------------------------
 *
 * ① 【 MutableStateFlow 】（ミュータブル・ステート・フロー）
 *   - 意味: 「中身の値をあとから書き換える（Mutable）ことができる状態のパイプライン」
 *   - 役割: ViewModel の中で通信結果やローディング状態を更新するときに使う。
 *
 * ② 【 .asStateFlow() 】（アズ・ステート・フロー）
 *   - 意味: 「変更できる（Mutableな）StateFlow」を、外から直接書き換えられない
 *     「読み取り専用の StateFlow」に変換するガード機能。
 *   - 理由: 外部（UI画面）から勝手に値を書き換えられてバグが起きるのを防ぐため、
 *     ViewModel の中では隠し、外には見る専用として公開する（カプセル化）。
 *
 * ③ 【 collectAsState() / collectAsStateWithLifecycle() 】
 *   - 意味: StateFlow から流れてくる最新のデータを受け取り、Compose が読める「State」に変換する関数。
 *   - 役割: データが変わった瞬間に、自動で Compose に「画面を再描画して！」と伝える。
 *
 * ④ 【 val uiState by ... 】 の 「 by 」（プロパティ委譲）
 *   - 意味: 「変数の値を取り出すときに、裏側で `.value` を自動で肩代わりしてもらう仕組み」
 *   - なぜ使うのか？:
 *     - `by` がないと毎回 `uiState.value.isLoading` のように書かないといけない。
 *     - `by` をつけると `.value` を省略して **`uiState.isLoading`** のように
 *       スッキリ直感的に変数として扱えるようになる！
 *
 * -----------------------------------------------------------------
 * 💡 SwiftUI での対比概念
 * -----------------------------------------------------------------
 *   - ViewModel            -> ObservableObject / @Observable クラス
 *   - StateFlow            -> @Published プロパティ
 *   - collectAsState()     -> 自動購読
 * =================================================================
 */

// =================================================================
// 📱 実装サンプルコード（Zenn 記事ベース）
// =================================================================

// 📦 UI の状態をまとめるデータクラス
data class UserListUiState(
    val isLoading: Boolean = false,
    val users: List<User> = emptyList(),
    val error: String? = null
)

// 📦 ユーザーデータのモデル
data class User(
    val id: Int,
    val name: String,
    val email: String
)

// ダミーのリポジトリ（実際はAPI通信などを行う）
interface UserRepository {
    suspend fun getUsers(): List<User>
}

// 🧠 ① ViewModel（データの金庫・司令塔）
class UserListViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    // 【MutableStateFlow】内部だけで書き換え可能な状態
    private val _uiState = MutableStateFlow(UserListUiState())

    // 【asStateFlow()】外部（UI）には「読み取り専用」として公開する
    val uiState: StateFlow<UserListUiState> = _uiState.asStateFlow()

    init {
        loadUsers()
    }

    // データの読み込み処理
    fun loadUsers() {
        viewModelScope.launch {
            // ローディング開始（copy() で一部のプロパティだけを書き換える）
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                val users = userRepository.getusers() // データの取得（仮）
                // 成功：新しい状態に置き換え
                _uiState.value = UserListUiState(users = users)
            } catch (e: Exception) {
                // 失敗：エラーメッセージをセット
                _uiState.value = UserListUiState(error = "ユーザーの読み込みに失敗しました")
            }
        }
    }

    fun retry() {
        loadUsers()
    }
}

// 🎨 ② UI側（Composable）
@Composable
fun UserListScreen(
    viewModel: UserListViewModel
) {
    // 【 collectAsStateWithLifecycle() と by 】
    // ViewModel の StateFlow を安全に購読し、
    // `by` のおかげで `.value` を書かずに `uiState` として直接プロパティにアクセスできる。
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            // ローディング中
            uiState.isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            // エラー発生
            uiState.error != null -> {
                ErrorContent(
                    message = uiState.error!!,
                    onRetry = { viewModel.retry() },
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            // データ表示
            else -> {
                UserList(users = uiState.users)
            }
        }
    }
}

@Composable
fun ErrorContent(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = message, color = MaterialTheme.colorScheme.error)
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onRetry) {
            Text("再試行")
        }
    }
}

@Composable
fun UserList(users: List<User>) {
    LazyColumn {
        items(users) { user ->
            Text(
                text = "${user.name} (${user.email})",
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}