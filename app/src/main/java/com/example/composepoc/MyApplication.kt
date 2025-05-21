package com.example.composepoc

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * アプリケーション全体のライフサイクルを管理するクラス。
 *
 * このクラスは Android アプリケーションのエントリーポイントとして機能し、
 * アプリ全体で共通して使用する設定や初期化処理などを行うのに適している。
 *
 * `@HiltAndroidApp` アノテーションを付けることで、Hilt による依存性注入の準備が整う。
 * このアノテーションを使用することで、Hilt はアプリケーションレベルのコンポーネントを自動生成し、
 * 各 Android コンポーネント（Activity, Fragment など）で依存関係を簡単に注入できるようになる。
 *
 * `Application` を継承しており、アプリ起動時に一度だけ初期化される。
 */

@HiltAndroidApp
class MyApplication : Application(){
}