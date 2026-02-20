package compose.project.demo.composedemo.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class RemoteRocketLaunchesDataSourceTest {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `latestLaunches should return list of rocket launches on success`() = runTest {
        // Arrange
        val mockResponse = """
      [
        {
          "flight_number": 1,
          "name": "FalconSat",
          "date_utc": "2006-03-24T22:30:00.000Z",
          "details": "Engine failure at 33 seconds and loss of vehicle",
          "success": false,
          "links": {
            "patch": {
              "small": "https://images2.imgbox.com/3c/0e/T8iJcSN3_o.png",
              "large": "https://images2.imgbox.com/40/e3/GypSkayF_o.png"
            },
            "article": "https://www.space.com/2196-spacex-inaugural-falcon-1-rocket-lost-launch.html"
          }
        }
      ]
    """.trimIndent()

        val mockEngine = MockEngine { request ->
            assertEquals("https://api.spacexdata.com/v5/launches", request.url.toString())
            respond(
                content = mockResponse,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val httpClient = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(json)
            }
        }

        val dataSource = RemoteRocketLaunchesDataSource(
            httpClient = httpClient,
            ioDispatcher = Dispatchers.Unconfined
        )

        // Act
        val result = dataSource.latestLaunches().first()

        // Assert
        assertEquals(1, result.size)
        assertEquals(1, result[0].flightNumber)
        assertEquals("FalconSat", result[0].missionName)

        httpClient.close()
    }

    @Test
    fun `latestLaunches should return empty list when API returns empty array`() = runTest {
        val mockEngine = MockEngine { _ ->
            respond(
                content = "[]",
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val httpClient = HttpClient(mockEngine) {
            install(ContentNegotiation) { json(json) }
        }

        val dataSource = RemoteRocketLaunchesDataSource(httpClient, Dispatchers.Unconfined)
        val result = dataSource.latestLaunches().first()

        assertEquals(0, result.size)
        httpClient.close()
    }

    @Test
    fun `latestLaunches should throw exception when API returns error`() = runTest {
        val mockEngine = MockEngine { _ ->
            respond(
                content = "Error",
                status = HttpStatusCode.InternalServerError
            )
        }

        val httpClient = HttpClient(mockEngine) {
            install(ContentNegotiation) { json(json) }
        }

        val dataSource = RemoteRocketLaunchesDataSource(httpClient, Dispatchers.Unconfined)

        assertFailsWith<Exception> {
            dataSource.latestLaunches().first()
        }
        httpClient.close()
    }
}