package xyz.sushant.excusesapp.base.network

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import xyz.sushant.excusesapp.domain.entities.Excuse

interface ApiService {
    //Get a random excuse
    @GET("/v1/excuse")
    suspend fun getRandomExcuse(): Response<List<Excuse>>

    //Get a specific excuse having specific id
    @GET("/v1/excuse/id/{id}")
    fun getExcuseWithId(@Path("id") id: Int): Call<Excuse>

    //Get n random excuses
    @GET("/v1/excuse/{count}")
    suspend fun getNRandomExcuses(@Path("count") count: Int) : Response<List<Excuse>>

    // Get a random excuse for a specific category
    // https://excuser.herokuapp.com/v1/excuse/office
    @GET("/v1/excuse/{category}")
    suspend fun getExcuseForCategory(@Path("category") category: String) : Response<List<Excuse>>

    //Get n random excuses for a specific category
    @GET("/v1/excuse/{category}/{count}")
    suspend fun getNExcusesForCategory(@Path("category") category: String, @Path("count") count: Int) : Response<List<Excuse>>
}