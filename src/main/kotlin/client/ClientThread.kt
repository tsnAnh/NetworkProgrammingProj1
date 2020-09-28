package client

import Payload
import javafx.application.Platform
import javafx.scene.image.Image
import utils.Dependencies
import java.io.*
import java.net.Socket
import java.net.SocketTimeoutException
import java.util.*

class ClientThread(private val view: MyChatView, private val socketMessage: Socket) : Thread() {
    private val adapter = Dependencies.payloadAdapter
    private val dataOutputStream = ObjectOutputStream(socketMessage.getOutputStream())
    private val dataInputStream = ObjectInputStream(socketMessage.getInputStream())

    override fun run() {
        try {
            while (!false) {
                val payload = adapter.fromJson(dataInputStream.readObject() as String)
                if (payload.image != null) {
                    val bis = ByteArrayInputStream(Base64.getDecoder().decode(payload.image.also(::println)))
                    Platform.runLater { view.appendImage(payload.sender, Image(bis), payload.time) }
                } else {
                    Platform.runLater { view.appendMessage(payload) }
                }
            }
        } catch (sto: SocketTimeoutException) {
            view.showDisconnectDialogAndExit()
        } catch (e: Exception) {
            error(e)
        }
    }

    fun sendMessage(payload: Payload) {
        try {
            with(dataOutputStream) {
                writeObject(adapter.toJson(payload))
                flush()
            }
        } catch (e: Exception) {
            error(e)
        }
    }
}