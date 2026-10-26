package com.example.composepoc.data.netwotk

import com.example.composepoc.data.model.ProductListDTO
import retrofit2.http.GET
import retrofit2.http.Path

// Retrofit を使用
interface ApiService {

    @GET("/products")
    suspend fun getAllProductListAPI() : List<ProductListDTO>

    @GET("/products/{Id}")
    suspend fun getProductDetailsAPI(@Path("Id") id : String) : ProductListDTO

}

/**
 *3. @Path("Id")
 * これは 「URL の中身を動的に入れ替える」 ための仕組みです。
 * 仕組み: @GET("/products/{Id}") の {Id} の部分に、引数で渡した id をスポッとはめ込みます。
 * 具体例: getProductDetailsAPI("123") と呼び出すと、実際の通信先は /products/123 になります
 */