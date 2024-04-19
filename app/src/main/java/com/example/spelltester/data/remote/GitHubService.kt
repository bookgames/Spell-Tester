package com.example.spelltester.data.remote

import okhttp3.*
import retrofit2.Call
import retrofit2.http.*

interface GitHubService {
    @GET
    fun getData(@Url url: String): Call<ResponseBody>
}
