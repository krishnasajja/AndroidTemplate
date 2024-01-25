package com.ksajja.newone.network

import com.ksajja.newone.model.DeviceInfo
import com.ksajja.newone.model.Result
import com.ksajja.newone.model.Task
import retrofit2.Call
import retrofit2.http.*

/**
 * Created by shivakartik on 2/27/18.
 */

interface BackendService {

    @GET("auth/login")
    fun login(@Header("ksajjaId") ksajjaId: String, @Header("ksajjaToken") ksajjaToken: String): Call<Result<Boolean>>

    @POST("userDevice")
    fun sendDeviceInfo(@Body deviceInfo: DeviceInfo): Call<Result<DeviceInfo>>

    //Lists API
    @get:GET("lists")
    val lists: Call<Result<List<com.ksajja.newone.model.List>>>
    //There should not be a leading slash on annotation paths

    @POST("lists")
    fun createList(@Body list: com.ksajja.newone.model.List): Call<Result<com.ksajja.newone.model.List>>

    @DELETE("lists/{listId}")
    fun deleteList(@Path("listId") listId: String): Call<Result<Boolean>>

    @PUT("lists/{listId}")
    fun updateList(@Path("listId") listId: String, @Body list: com.ksajja.newone.model.List): Call<Result<com.ksajja.newone.model.List>>

    //Tasks API

    @POST("lists/{listId}/tasks")
    fun addTasks(@Path("listId") listId: String, @Body taskList: List<Task>): Call<Result<List<Task>>>

    @GET("lists/{listId}/tasks")
    fun getTasks(@Path("listId") listId: String): Call<Result<List<Task>>>

    @PUT("lists/tasks/{taskId}")
    fun updateTask(@Path("taskId") taskId: String, @Body task: Task): Call<Result<Task>>

    @DELETE("lists/tasks/{taskId}")
    fun deleteTask(@Path("taskId") taskId: String): Call<Result<Boolean>>

    @GET("lists/tasks/{taskId}")
    fun getTaskDetails(@Path("taskId") taskId: String): Call<Result<Task>>

    @GET("tasks")
    fun getTasks(): Call<Result<List<Task>>>
}
