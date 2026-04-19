import okhttp3.OkHttpClient
import okhttp3.Protocol
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ItunesNetworkClient {
    private const val baseUrl = "https://itunes.apple.com/"

    // Создаем клиент, который будет работать строго по HTTP/1.1, как ReqBin
    private val client = OkHttpClient.Builder()
        .protocols(listOf(Protocol.HTTP_1_1))
        .build()

    val itunesApi: ItunesApiService by lazy {
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client) // Подключаем наш клиент
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ItunesApiService::class.java)
    }
}