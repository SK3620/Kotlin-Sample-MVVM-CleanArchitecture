/*
 * =========================================================
 * Hilt - @Module / @Binds / @Provides
 * =========================================================
 *
 * まず全体像
 *
 * @Module
 *   → DIの設定を書く場所
 *
 * @Binds
 *   → 「このinterfaceが欲しかったら、この実装を使って」
 *
 * @Provides
 *   → 「このオブジェクトは、この方法で作って」
 */


// =========================================================
// ① interface
// =========================================================

interface UserRepository {

    fun getUser()
}


// =========================================================
// ② 実装
// =========================================================

class UserRepositoryImpl : UserRepository {

    override fun getUser() {
        println("ユーザーを取得")
    }
}


// =========================================================
// ③ @Module
// =========================================================

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    // -----------------------------------------------------
    // @Binds
    // -----------------------------------------------------
    //
    // 「UserRepositoryが欲しいなら、
    //  UserRepositoryImplを使って」
    //
    // という対応関係をHiltに教える。
    //
    // interface ← 実装
    // UserRepository ← UserRepositoryImpl
    //

    @Binds
    abstract fun bindUserRepository(
        impl: UserRepositoryImpl
    ): UserRepository


    // -----------------------------------------------------
    // @Provides
    // -----------------------------------------------------
    //
    // 「UserRepositoryを渡してと言われたら、
    //  UserRepositoryImpl()を作って渡して」
    //
    // という作り方をHiltに教える。
    //
    // ※ @Bindsと@Providesは基本的に
    //    どちらか一方を使う。
    //

    // @Provides
    // fun provideUserRepository(): UserRepository {
    //     return UserRepositoryImpl()
    // }
}


/*
 * =========================================================
 * @Binds と @Provides の違い
 * =========================================================
 *
 *
 * @Binds
 *
 *     UserRepository
 *          ↑
 *          │
 *     UserRepositoryImpl
 *
 * 「UserRepositoryとして
 *  UserRepositoryImplを使ってね」
 *
 *
 *
 * @Provides
 *
 *     UserRepository
 *          ↑
 *          │
 *     UserRepositoryImpl()
 *
 * 「UserRepositoryが必要なら、
 *  UserRepositoryImpl()を作って渡してね」
 *
 *
 * =========================================================
 *
 * 超ざっくり覚えるなら
 *
 * @Module
 * → DI設定を書く箱
 *
 * @Binds
 * → 「AとしてBを使う」
 *
 * @Provides
 * → 「Aをこうやって作る」
 *
 * =========================================================
 */