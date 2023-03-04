import io.ktor.http.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*

import kotlinx.browser.window

val jsonClient = HttpClient {
    install(ContentNegotiation) {
        json()
    }
}

suspend fun getDestinationTicketList(): List<DestinationTicketList> {
    return jsonClient.get(DestinationTicketList.path).body()
}

suspend fun addDestinationTicketList(destinationTicketList: DestinationTicketList) {
    jsonClient.post(DestinationTicketList.path) {
        contentType(ContentType.Application.Json)
        setBody(destinationTicketList)
    }
}

suspend fun deleteDestinationTicketList(destinationTicketList: DestinationTicketList) {
    jsonClient.delete(DestinationTicketList.path + "/${destinationTicketList.id}")
}
