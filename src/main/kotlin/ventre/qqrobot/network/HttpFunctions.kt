package ventre.qqrobot.network

import com.alibaba.fastjson.JSONObject
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.*
import org.apache.commons.io.FileUtils
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import ventre.qqrobot.mainConfig
import java.io.File
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Proxy
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resumeWithException

object HttpFunctions {
    private val httpClient : OkHttpClient

    init {
        val proxy = mainConfig.get("proxy") as JSONObject
        val builder = OkHttpClient.Builder()
        with(builder) {
            if (proxy["on"] as Boolean) {
                proxy(Proxy(Proxy.Type.HTTP, InetSocketAddress(
                    proxy["host"] as String, proxy["port"] as Int ))).build()
            }
            connectTimeout(1,TimeUnit.MINUTES)
            readTimeout(1, TimeUnit.MINUTES)
        }
        httpClient = builder.build()
    }

    suspend fun getHttpResponse(url: String, heads : List<Pair<String, String>>):  ResponseBody? {
        val builder = Request.Builder()
        if(heads != null) {
            for (head in heads) {
                builder.addHeader(head.first, head.second)
            }
        }
        val request = builder
            .url(url)
            .method("GET", null)
            .build()
        val response = httpClient.newCall(request).await()

        if (response.code() != 200 || response.body() == null) {
            print("getHttpResponse failed:$url")
            response.body()?.close()
            return null
        }

        return response.body()
    }

    suspend fun httpGetImg(url: String,heads: List<Pair<String, String>>, savePath: String) : Boolean {
        val body = getHttpResponse(url,heads)
        body?.byteStream()?: return false
        try {
            FileUtils.copyInputStreamToFile(body.byteStream(), File(savePath))
            print("下载图片成功:$url\n")
        } catch (e: IOException) {
            e.printStackTrace()
            throw RuntimeException(e)
        } finally {
            body.close()
        }
        return true
    }

    fun createRetrofit(baseUrl: String): Retrofit {
        val jacksonConverterFactory = JacksonConverterFactory.create(JacksonFactory.objectMapper)
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(jacksonConverterFactory)
            .client(httpClient)
            .build()
    }


}

suspend fun Call.await(): Response {
    return suspendCancellableCoroutine { continuation ->
        continuation.invokeOnCancellation {
            cancel()
        }
        this.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                continuation.resumeWithException(e)
            }

            override fun onResponse(call: Call, response: Response) {
                continuation.resumeWith(Result.success(response))
            }
        })
    }
}