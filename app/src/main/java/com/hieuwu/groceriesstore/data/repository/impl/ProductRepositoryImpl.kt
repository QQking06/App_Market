package com.hieuwu.groceriesstore.data.repository.impl

import com.hieuwu.groceriesstore.data.database.dao.LineItemDao
import com.hieuwu.groceriesstore.data.database.dao.ProductDao
import com.hieuwu.groceriesstore.data.database.entities.Product
import com.hieuwu.groceriesstore.data.database.entities.asDomainModel
import com.hieuwu.groceriesstore.data.network.Api
import com.hieuwu.groceriesstore.data.network.dto.ProductDto
import com.hieuwu.groceriesstore.data.repository.ProductRepository
import com.hieuwu.groceriesstore.domain.models.ProductModel
import io.github.jan.supabase.postgrest.Postgrest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import timber.log.Timber

class ProductRepositoryImpl(
    private val productDao: ProductDao,
    private val lineItemDao: LineItemDao,
    private val supabasePostgrest: Postgrest // Tetap dibiarkan agar tidak merusak Dependency Injection
) : ProductRepository {

    override val products: Flow<List<ProductModel>> =
        productDao.getAll().map {
            it.asDomainModel()
        }

    override suspend fun refreshDatabase() {
        try {
            // --- KODE LAMA (SUPABASE) SAYA MATIKAN ---
            // val result = supabasePostgrest[RemoteTable.Product.tableName]
            //     .select().decodeList<ProductDto>()

            // --- KODE BARU (LARAVEL VIA NGROK) ---
            // Ini akan mengambil data dari: https://regainable-mikel-overhugely.ngrok-free.dev/api/products
            val result = Api.retrofitService.getProducts()

            val products = result.map { it.asEntity() }
            productDao.insertAll(products)

        } catch (e: Exception) {
            Timber.e("Error fetching data from Laravel: ${e.message}")
        }
    }

    override suspend fun updateLineItemQuantityById(quantity: Int, id: Long) {
        try {
            lineItemDao.updateQuantityById(quantity, id)
        } catch (e: Exception) {
            Timber.e(e.message)
        }
    }

    override suspend fun removeLineItemById(id: Long) {
        try {
            lineItemDao.removeLineItemById(id)
        } catch (e: Exception) {
            Timber.e(e.message)
        }
    }

    override fun searchProductsListByName(name: String?) =
        productDao.searchProductByName(name).map { it.asDomainModel() }

    override fun getAllProductsByCategory(categoryId: String): Flow<List<ProductModel>> {
        return try {
            productDao.getAllByCategory(categoryId).map {
                it.asDomainModel()
            }
        } catch (e: Exception) {
            Timber.e(e.message)
            flow {}
        }
    }

    override fun getProductById(productId: String): Flow<ProductModel> {
        try {
            val productFlow = productDao.getById(productId)
            return productFlow.map {
                it.asDomainModel()
            }
        } catch (e: Exception) {
            Timber.e(e)
        }
        return flow {}
    }

    private fun ProductDto.asEntity(): Product = Product(
        id = productId,
        name = name,
        nutrition = nutrition,
        description = description,
        image = image,
        price = price,
        category = category,
    )
}