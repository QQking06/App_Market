package com.hieuwu.groceriesstore.data.network

import com.hieuwu.groceriesstore.data.network.dto.ProductDto
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET

// 1. LINK NGROK (Wajib diakhiri "/api/")
private const val BASE_URL = "https://regainable-mikel-overhugely.ngrok-free.dev/api/"

// 2. KARTU PASS (Agar tidak dihadang halaman peringatan Ngrok)
private val client = OkHttpClient.Builder()
    .addInterceptor { chain ->
        val request = chain.request().newBuilder()
            .addHeader("ngrok-skip-browser-warning", "true") // Kunci rahasia
            .build()
        chain.proceed(request)
    }
    .build()

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .client(client) // Pasang kartu pass di sini
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .build()

interface RecipesApiService {
    // Fungsi mengambil produk dari Laravel
    @GET("products")
    suspend fun getProducts(): List<ProductDto>
}

object Api {
    val retrofitService: RecipesApiService by lazy {
        retrofit.create(RecipesApiService::class.java)
    }
}