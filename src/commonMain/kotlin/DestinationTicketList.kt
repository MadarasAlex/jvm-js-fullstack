import kotlinx.serialization.Serializable

@Serializable
data class DestinationTicketList(val name: String, val desc: String, val priority: Int) {
    val id: Int = name.hashCode()
    companion object {
        const val path = "/destinationTickets"
    }
}