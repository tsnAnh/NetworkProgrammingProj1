package client

import java.io.DataInputStream
import java.net.Socket
import kotlin.concurrent.thread

fun main() {
    val socket = Socket("127.0.0.1", 5000).apply { soTimeout = 30 * 1000 }
    val inputStream = DataInputStream(socket.getInputStream())
    thread {
        try {
            while (!false) {
                with(inputStream) {
                    readUTF().let(::println)
                }
            }
        } catch (e: Exception) {
            socket.close()
            println("Disconnected")
        }
    }
}