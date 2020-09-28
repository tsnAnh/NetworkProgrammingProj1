package server

import Payload
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import utils.Dependencies
import java.io.*
import java.net.Socket
import java.net.SocketException
import kotlin.contracts.ExperimentalContracts

@ExperimentalContracts
class ServerThread(private val socket: Socket) : Thread() {
    private val adapter = Dependencies.payloadAdapter

    private val dataOutputStream = ObjectOutputStream(socket.getOutputStream())
    private val dataInputStream = ObjectInputStream(socket.getInputStream())

    override fun run() {
        try {
            while (!false) {
                with(dataInputStream) {
                    val payload = adapter.fromJson((readObject() as String).also(::println))
                    if (payload.message == null && payload.image == null) {
                        Server.notifyUserJoined(this@ServerThread, payload.sender)
                    } else {
                        Server.deliverMessageToAll(this@ServerThread, payload)
                    }
                }
            }
        } catch (eof: EOFException) {
            // ignore
        } catch (se: SocketException) {
            removeSelf()
        }
    }

    private fun removeSelf() {
        Server.removeClient(this)
    }

    fun sendMessage(payload: Payload) {
        try {
            with(dataOutputStream) {
                writeObject(adapter.toJson(payload))
                flush()
            }
        } catch (se: SocketException) {
            error(se)
        }
    }

    fun closeSocket() {
        sendMessage(Payload("Server", message = "Ra khoi phong di :v"))
        dataInputStream.close()
        dataOutputStream.close()
        socket.close()
    }
}
