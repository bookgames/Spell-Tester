package com.example.spelltester.data.remote

import android.util.*
import org.json.*
import retrofit2.*
import retrofit2.converter.gson.*

object RemoteService {
    private  val BASE_URL = "https://api.github.com/"
    private  val RELATIVE_URL = "repos/KhaledHawwas/Spell-Tester/contents/"
    private  val TAG = "Kh_RE"
    val instance: GitHubService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofit.create(GitHubService::class.java)
    }

    fun getData(
        file: String, success: (String) -> Unit
    ) {
        getData(file, success) { Log.d(TAG, "failure: $it") }
    }

    fun getData(file: String, success: (String) -> Unit, failure: (String) -> Unit) {
        instance.getData(BASE_URL + RELATIVE_URL + file)
            .enqueue(object : Callback<okhttp3.ResponseBody> {
                override fun onResponse(
                    call: Call<okhttp3.ResponseBody>,
                    response: Response<okhttp3.ResponseBody>
                ) {
                    if (!response.isSuccessful) {
                        Log.d(TAG, "onResponse: ${response.errorBody()?.string()}")
                        failure(response.errorBody()?.string() ?: "Unknown error")
                        return
                    }
                    val fileData = response.body()?.string()
                    if (fileData != null) {
                        success(fileData.getContent())
                    } else {
                        Log.d(TAG, "fileData is null")
                        failure("fileData is null")
                    }
                }
                override fun onFailure(call: Call<okhttp3.ResponseBody>, t: Throwable) {
                    Log.d(TAG, t.message ?: "Unknown error")
                    failure(t.message ?: "Unknown error")
                }
            })
    }

}

fun String.getContent(): String {
    val jsonObject = JSONObject(this)
    val contentBase64 = jsonObject.getString("content")
    return String(Base64.decode(contentBase64, Base64.DEFAULT))
}
