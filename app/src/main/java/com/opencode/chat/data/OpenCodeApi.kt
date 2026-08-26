package com.opencode.chat.data

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.sse.EventSource
import okhttp3.sse.EventSourceListener
import okhttp3.sse.EventSources
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path
import java.util.concurrent.TimeUnit

interface OpenCodeService {
    @POST("session")
    suspend fun createSession(): CreateSessionResponse

    @POST("session/{id}/message")
    suspend fun sendMessage(@Path("id") id: String, @Body body: SendMessageRequest)
}

/**
 * عميل بسيط يتحدث مع خادم OpenCode عبر REST (لإنشاء الجلسات وإرسال الرسائل)
 * وعبر SSE على /event لاستقبال الأحداث اللحظية (ردود الوكيل streaming).
 *
 * ملاحظة: أسماء الحقول هنا (parts / providerID / modelID / text) مبنية على
 * التوثيق العام لـ OpenCode. افتح http://<host>:<port>/doc على متصفحك وقارنها
 * بالمخطط الفعلي عندك، وعدّل Models.kt أو المعالجة أدناه إذا لزم.
 */
class OpenCodeClient(baseUrl: String, private val password: String?) {

    private val normalizedBaseUrl = if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/"

    private fun basicAuthValue(): String? {
        if (password.isNullOrBlank()) return null
        val credentials = "opencode:$password"
        val encoded = android.util.Base64.encodeToString(
            credentials.toByteArray(Charsets.UTF_8),
            android.util.Base64.NO_WRAP
        )
        return "Basic $encoded"
    }

    private val authInterceptor = Interceptor { chain ->
        val builder = chain.request().newBuilder()
        basicAuthValue()?.let { builder.header("Authorization", it) }
        chain.proceed(builder.build())
    }

    private val httpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(0, TimeUnit.MILLISECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(normalizedBaseUrl)
        .client(httpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val service: OpenCodeService = retrofit.create(OpenCodeService::class.java)

    fun listenEvents(onEvent: (String) -> Unit, onError: (Throwable?) -> Unit): EventSource {
        val request = Request.Builder()
            .url("${normalizedBaseUrl}event")
            .apply { basicAuthValue()?.let { header("Authorization", it) } }
            .build()

        val listener = object : EventSourceListener() {
            override fun onEvent(eventSource: EventSource, id: String?, type: String?, data: String) {
                onEvent(data)
            }

            override fun onFailure(eventSource: EventSource, t: Throwable?, response: Response?) {
                onError(t)
            }
        }
        return EventSources.createFactory(httpClient).newEventSource(request, listener)
    }
}
