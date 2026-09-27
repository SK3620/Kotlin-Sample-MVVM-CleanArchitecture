import androidx.activity.viewModels

/**
 * 【学習メモ】ViewModelの生成と「by」キーワードの謎
 */

// 1. なぜ ViewModel を手動でインスタンス化（= ViewModel()）してはいけないのか？
// --------------------------------------------------------------------------
/*
   × ダメな例: val viewModel = ProductListViewModel()

   理由①：画面回転（Configuration Change）に耐えられない
          AndroidのActivityは回転時に一度破棄されます。手動で作ると、新しいActivityと一緒に
          新しいViewModelが作られてしまい、保持していたデータが全部消えます。

   理由②：依存関係（Dependency Injection）が解決できない
          ViewModelがUseCaseやRepositoryを必要とする場合、手動だとそれらも全部自分で
          用意して組み立てる必要があり、コードが非常に複雑になります。
*/

// 2. 「by」キーワード（プロパティ委譲）とは何か？
// --------------------------------------------------------------------------
/*
   「by」は「この変数の管理（初期化や保持）は、専門家に丸投げします！（OSシステム裏側とかに丸投げしますみたいなこと？）」という宣言です。
   SwiftUIの @StateObject に近い役割を、Kotlinでは「by」という文法で実現しています。
   → なるほどね
*/

class MyActivity : androidx.activity.ComponentActivity() {

    // 例：by viewModels() (Hiltを使わない標準的な委譲)
    // 「今すぐ作るのではなく、Activityが準備できてから、システムにある保管庫(ViewModelStore)から
    //  既存のインスタンスを探して持ってくるか、なければ新規作成してね」と予約している。
    val simpleViewModel: MyViewModel by viewModels()

    // 例：by hiltViewModel() (ComposeでなくFragment等でHiltを使う場合)
    // 「インスタンスの保持だけでなく、中身の部品（UseCase等）の組み立てもHiltで自動でやってね」と丸投げ。
    // val hiltVM: ProductListViewModel by hiltViewModel() // ※Fragment等での書き方
}

// 3. Compose における「= hiltViewModel()」
// --------------------------------------------------------------------------
/*
   Compose関数の中では、「by」ではなく「=」を使います。
   しかし、これは「手動でnewしている」のとは全く意味が違います。
*/

@androidx.compose.runtime.Composable
fun ListingScreen() {
    // この hiltViewModel() 関数自体が「専門家」です。
    // 内部で Compose の仕組み（LocalViewModelStoreOwner）を利用して、
    // 画面が再描画されても同じインスタンスを返し続けるように設計されています。
    // なので、Composeでは "=" で受け取っても安全なのです。
    val viewModel: ProductListViewModel = androidx.hilt.navigation.compose.hiltViewModel()
}

// 4. SwiftUI との比較まとめ
// --------------------------------------------------------------------------
/*
   - 手動生成 (val vm = VM())  == SwiftUIのただの「let vm = VM()」
     (画面更新のたびに初期化され、状態が消える)

   - 委譲生成 (val vm by viewModels()) == SwiftUIの「@StateObject var vm = VM()」
     (システムがインスタンスを管理し、画面が回転してもデータを維持する)

   - Hilt (hiltViewModel()) == SwiftUIの「Environment機能 + 自動組み立て」
     (必要な部品を自動でガッチャンコして完成品を渡してくれる)
*/