package xyz.sushant.excusesapp.base.network

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*
import xyz.sushant.excusesapp.domain.dataTransfer.ExcuseDTO
import xyz.sushant.excusesapp.domain.entities.Excuse

interface ApiService {
    /**
     * Get a random excuse
     */
    @GET("/v1/excuse")
    suspend fun getRandomExcuse(): Response<List<ExcuseDTO>>

    /**
     * Get a specific excuse having specific id
     */
    @GET("/v1/excuse/id/{id}")
    fun getExcuseWithId(@Path("id") id: Int): Call<ExcuseDTO>

    /**
     * Get n random excuses
     */
    @GET("/v1/excuse/{count}")
    suspend fun getNRandomExcuses(@Path("count") count: Int) : Response<List<ExcuseDTO>>

    /**
     * Get a random excuse for a specific category
     * https://excuser.herokuapp.com/v1/excuse/office
     */
    @GET("/v1/excuse/{category}")
    suspend fun getExcuseForCategory(@Path("category") category: String) : Response<List<ExcuseDTO>>

    /**
     * Get a random excuse for a category using [Call]
     */
    @GET("/v1/excuse/{category}")
    fun getExcuseForCategoryViaCall(@Path("category") category: String): Call<List<ExcuseDTO>>

    /**
     * Get n random excuses for a specific category
     */
    @GET("/v1/excuse/{category}/{count}")
    suspend fun getNExcusesForCategory(@Path("category") category: String, @Path("count") count: Int) : Response<List<ExcuseDTO>>

    //INFO: Below are dummy api calls for demonstrating different possible request configurations using Retrofit

    /**
     * Dummy Api To Demonstrate how to add request body to a post request
     */
    @POST("/dummy/request")
    suspend fun dummyPostRequest(@Body excuse: Excuse) : Response<ExcuseDTO>

    /**
     * Dummy Api to demonstrate how to add query params to a request
     *
     */
    @GET("/dummy/search")
    suspend fun dummySearch(@Query("term") searchTerm: String) : Response<List<ExcuseDTO>>
}