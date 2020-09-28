package server

import Payload
import java.awt.image.BufferedImage
import java.net.ServerSocket
import java.net.SocketException
import kotlin.contracts.ExperimentalContracts

@ExperimentalContracts
fun main() {
    with(Server) {
        init()
        startServer()
    }
}

private val words = listOf(
    "somewords",
    "wtf",
    "woohoo"
)

@ExperimentalContracts
object Server {
    private lateinit var serverSocket: ServerSocket
    private val clients = mutableListOf<ServerThread>()

    fun init(port: Int = 5000) {
        serverSocket = ServerSocket(port)
        println("Server initialization finished!")
    }

    fun startServer() {
        println("Server started on port 5000")
        while (!false) {
            val thread = serverSocket.accept()
            println("${thread.inetAddress.toString().removePrefix("/")} connected!")
            thread?.let { socket ->
                clients.add(ServerThread(socket).also {
                    it.start()
                })
            }
        }
    }

    fun deliverMessageToAll(senderThread: ServerThread? = null, payload: Payload) {
        for (serverThread in clients) {
            try {
                if (payload.message != null) {
                    if (senderThread == serverThread) {
                        continue
                    }
                    if (payload.message.contains(words.joinToString(prefix = "(?i)", separator = "|").toRegex())) {
                        senderThread?.closeSocket()
                        continue
                    }
                }

                serverThread.sendMessage(payload)
            } catch (_: SocketException) {
                clients.remove(serverThread)
                println("removed")
            } catch (e: Exception) {
                error(e)
            }
        }
    }

    fun notifyUserJoined(sender1: ServerThread, sender: String) {
        deliverMessageToAll(sender1, payload = Payload("Server", "User $sender joined!"))
    }

    fun removeClient(serverThread: ServerThread) {
        clients.remove(serverThread)
        println("Removed")
    }
}