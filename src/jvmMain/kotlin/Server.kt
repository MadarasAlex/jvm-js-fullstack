
import com.mongodb.ConnectionString
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.application.*
import io.ktor.server.plugins.compression.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.http.content.*
import io.ktor.server.routing.*
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.eq
import org.litote.kmongo.reactivestreams.KMongo


fun main() {
    val port = System.getenv("PORT")?.toInt() ?: 9090
//    val destinationsPreferences = mutableListOf(
//        DestinationTicketList("London ","Capital of England", 1),
//        DestinationTicketList("Sankt-Petersburg","Imperial Capital of Russia", 2),
//        DestinationTicketList("Lisbon","Sunniest capital of Europe",3)
//    )
    val connectionString: ConnectionString? = System.getenv("MONGODB_URI")?.let {
        ConnectionString("$it?retryWrites=false")
    }
    val client =
        if (connectionString != null) KMongo.createClient(connectionString).coroutine else KMongo.createClient().coroutine
    val database = client.getDatabase(connectionString?.database ?: "destinationsList")
    val collection = database.getCollection<DestinationTicketList>()

    embeddedServer(Netty, port) {
        install(ContentNegotiation) {
            json()
        }
        install(CORS) {
            allowMethod(HttpMethod.Get)
            allowMethod(HttpMethod.Post)
            allowMethod(HttpMethod.Delete)
            anyHost()
        }
        install(Compression) {
            gzip()
        }
        routing {
            get("/") {
                call.respondText(
                    this::class.java.classLoader.getResource("index.html")!!.readText(),
                    ContentType.Text.Html
                )
            }
            static("/") {
                resources("")
            }
            route(DestinationTicketList.path) {
                get {
                    call.respond(collection.find().toList())
                }
                post {
                    collection.insertOne(call.receive<DestinationTicketList>())
                    call.respond(HttpStatusCode.OK)
                }
                delete("/{id}") {
                    val id = call.parameters["id"]?.toInt() ?: error("Invalid delete request")
                    collection.deleteOne(DestinationTicketList::id eq id)
                    call.respond(HttpStatusCode.OK)
                }

            }
        }
    }.start(wait = true)



}
