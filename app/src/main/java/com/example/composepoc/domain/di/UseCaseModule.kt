package com.example.composepoc.domain.di

import com.example.composepoc.data.respository.RepositoryImpl
import com.example.composepoc.domain.usecase.GetProductDetailUseCase
import com.example.composepoc.domain.usecase.GetProductListUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * このクラスは、Hiltの設定ファイル
 * アプリで使う部品の作り方をHitlという組み立て屋さんに教えている説明書
 */

@InstallIn(SingletonComponent::class)
@Module
class UseCaseModule {

    /**
     * 部品の提供方法」 を定義する目印
     * 関数の引数 (repositoryImpl): Hiltは賢いので、「このUseCaseを作るにはRepositoryが必要だな」 と判断し、 別の場所で作っておいたRepositoryを勝手に持ってきて合体させてくれます。
     * ViewModel が GetProductListUseCase を欲しがる →  productListUseCaseProvider は RepositpryImpl を欲しがる
    */
    @Provides
    @Singleton // static let shared = のこと
    fun productListUseCaseProvider(repositoryImpl: RepositoryImpl) : GetProductListUseCase{
        return GetProductListUseCase(repositoryImpl)
    }

    @Provides
    @Singleton
    fun productDetailUseCaseProvider(repositoryImpl: RepositoryImpl) : GetProductDetailUseCase{
        return GetProductDetailUseCase(repositoryImpl)
    }

}

/**
 * swift
 * // Swiftでのイメージ
 * class UseCaseModule {
 *     // @Singleton に相当する保持変数
 *     static let sharedProductListUseCase = GetProductListUseCase(repository: RepositoryImpl())
 *
 *     // @Provides に相当する提供口
 *     func productListUseCaseProvider() -> GetProductListUseCase {
 *         return Self.sharedProductListUseCase
 *     }
 * }
 */