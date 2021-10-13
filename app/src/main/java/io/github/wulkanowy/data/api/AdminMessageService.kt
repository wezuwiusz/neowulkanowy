package io.github.wulkanowy.data.api

import io.github.wulkanowy.data.db.entities.AdminMessage
import retrofit2.http.GET
import javax.inject.Singleton

@Singleton
interface AdminMessageService {

    @GET("/v1.json")
    suspend fun getAdminMessages(): List<AdminMessage>
}