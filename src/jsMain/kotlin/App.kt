import react.*
import kotlinx.coroutines.*
import react.dom.html.ReactHTML.h1
import react.dom.html.ReactHTML.li
import react.dom.html.ReactHTML.ul

private val scope = MainScope()

val App = FC<Props> {
    var destinationTicketList by useState(emptyList<DestinationTicketList>())

    useEffectOnce {
        scope.launch {
            destinationTicketList = getDestinationTicketList()
        }
    }
    h1 {
        +"Full-Stack Travelling Ticket List"
    }
    ul {
        destinationTicketList.sortedByDescending(DestinationTicketList::priority).forEach { item ->
            li {
                key = item.toString()
                onClick = {
                    scope.launch {
                        deleteDestinationTicketList(item)
                        destinationTicketList = getDestinationTicketList()
                    }
                }
                +"[${item.priority}] ${item.name} - ${item.desc} "
            }
        }
    }
    var name = ""
    inputComponent {
        onSubmit = { input ->
            name = input
        }
    }
    h1{
        +"Short description of the destination:"
    }
    inputComponent {
        onSubmit = { input ->
            val cartItem = DestinationTicketList(name.replace("!", ""), input, name.count { it == '!' })
            scope.launch {
                addDestinationTicketList(cartItem)
                destinationTicketList = getDestinationTicketList()
            }
        }
    }

}
