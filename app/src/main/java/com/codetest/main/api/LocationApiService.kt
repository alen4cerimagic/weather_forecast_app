package com.codetest.main.api

import com.codetest.main.KeyUtil
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface LocationApi {
    @GET
    fun get(@Url url: String): Single<JsonObject>

    @POST
    fun post(@Url url: String, @Body body: JsonObject): Single<JsonObject>

    @DELETE
    fun delete(@Url url: String): Completable
}

class Interceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val apiKey = KeyUtil().getKey()

        val request = chain.request().newBuilder().addHeader("X-Api-Key", apiKey).build()
        return chain.proceed(request)
    }
}

class LocationApiService {
    private val api: LocationApi
    private val compositeDisposable: CompositeDisposable

    companion object {
        private val instance = LocationApiService()
        fun getApi(): LocationApiService =
            instance
    }

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://weather-api.example.com/")
            .client(OkHttpClient().newBuilder().addInterceptor(Interceptor()).build())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
            .build()

        api = retrofit.create(LocationApi::class.java)
        compositeDisposable = CompositeDisposable()
    }

    fun get(url: String, success: (JsonObject) -> Unit, error: (Throwable?) -> Unit) {
        compositeDisposable.add(api.get(url)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = {
                    success(it)
                },
                onError = {
                    error(it)
                }
            ))
    }

    fun post(
        url: String,
        body: JsonObject,
        success: (JsonObject) -> Unit,
        error: (Throwable?) -> Unit
    ) {
        compositeDisposable.add(api.post(url, body)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = {
                    success(it)
                },
                onError = {
                    error(it)
                }
            ))
    }

    fun delete(url: String, success: () -> Unit, error: (Throwable?) -> Unit) {
        compositeDisposable.add(api.delete(url)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onComplete = {
                    success()
                },
                onError = {
                    error(it)
                }
            ))
    }

    fun dispose() = compositeDisposable.clear()
}