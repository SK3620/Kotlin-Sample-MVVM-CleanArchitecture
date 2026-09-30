package com.example.composepoc.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.composepoc.presentation.screens.component.listItem
import com.example.composepoc.presentation.viewmodel.ProductListVewModel

//Preview下にあるのが慣習なので移動させちゃった
@Composable
fun ListingScreen(){

    /*
    val viewModel : ProductListVewModel = hiltViewModel()
    型であるProductListVewModelをもとに、hiltViewModel() がHilt管理下にあるViewModelとして取得し、
    @HiltViewModel class ProductListVewModel... を取得してくるイメージ
     */
    // çomposableにVM持ってるとPreview作れまてん.....byぷーちゃん
    //This preview uses a ViewModel. ViewModels often trigger operations not supported by Compose Preview, such as database access, I/ O operations, or network requests. You can read more about preview limitations in our external documentation. Show Exception
    //https://qiita.com/Nagumo-7960/items/60678c4196c70f07615f
    val viewModel : ProductListVewModel = hiltViewModel()
    val context  = LocalContext.current
    var result = viewModel.productList.value // 今のデータの状態（読み込み中/成功/失敗）を監視

    if(result.isLoading){
        Column(modifier = Modifier.fillMaxSize(), // 画面の最大サイズまで広がって！
        horizontalAlignment = Alignment.CenterHorizontally, // 左右の中心へ
        verticalArrangement = Arrangement.Center // 上下にの中心へ
        ){
            CircularProgressIndicator(modifier = Modifier.size(50.dp))
        }
    }

    /*
    LazyColumn:
    遅延読み込み　スクロールされたタイミングで必要なアイテムが新たに描画されます。
     */

    result.data?.let {
        Column(modifier = Modifier.fillMaxSize()) {
            LazyColumn {
                items(it) { item-> // swiftUIでいう、List() { item in 〜 } のこと
                    listItem(item) { product-> // ここでクロージャを渡している
                        Toast.makeText(context, product.title, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    if(result.error.isNotEmpty()){
        Column(modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center) {
            Text(text = result.error.toString())
        }
    }
}


/*
ステータスバー（時計や電池）やナビゲーションバーを含めた、実際のスマホ画面のような状態でプレビューを表示します。
これがないと、コンポーネント単体（部品だけ） が浮いているように見えます。
 */
@Preview(showSystemUi = true, showBackground = true)
@Composable
fun PreviewlistingScreen(){
    ListingScreen()
}