package com.matias

import io.ktor.application.*
import io.ktor.http.cio.websocket.*
import io.ktor.routing.*
import io.ktor.websocket.*
import java.util.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {

    install(io.ktor.websocket.WebSockets)

    routing {

        val connections = Collections.synchronizedSet<DefaultWebSocketSession>(LinkedHashSet())

        webSocket("/chat") {

            send("Please type your name:")
            val name = (incoming.receive() as Frame.Text).readText();
            connections.forEach { it.send("$name has joined the chat") }
            connections += this
            send("Great! Nice to meet you $name, you can start chatting now. There are ${connections.count()} users here.")

            try {
                for (frame in incoming) {
                    frame as? Frame.Text ?: continue
                    val receivedText = frame.readText()
                    if (receivedText.isNotEmpty()) {
                        connections.forEach {
                            // We send the message to all the users except me
                            if (it != this) it.send("[$name]: $receivedText")
                        }
                    }
                }
            } catch (e: Exception) {
                log.error("There was an unexpected error!", e)
            } finally {
                connections -= this
                connections.forEach { it.send("$name has left the chat") }
            }
        }
    }
}

