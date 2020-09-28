package client

import Payload
import javafx.application.Platform
import javafx.geometry.Pos
import javafx.scene.control.*
import javafx.scene.image.Image
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.scene.text.FontWeight
import javafx.stage.FileChooser
import tornadofx.*
import java.io.ByteArrayInputStream
import java.net.Socket
import java.util.*
import kotlin.system.exitProcess

class MyChatView : View("MyChat") {
    private lateinit var myScrollPane: ScrollPane
    private lateinit var tfPort: TextField
    private lateinit var tfAddress: TextField
    private lateinit var tfUsername: TextField
    private lateinit var clientThread: ClientThread
    private lateinit var chatBubbles: VBox
    private lateinit var tfChatMessage: TextField
    private lateinit var topBox: HBox

    override val root = borderpane {
        minWidth = 700.0
        prefHeight = 500.0

        top {
            topBox = hbox {
                alignment = Pos.CENTER
                spacing = 4.0
                paddingLeft = 16.0
                paddingRight = 16.0
                label("Name")
                tfUsername = textfield {
                    promptText = "Your name"
                }

                label("Address: ")
                tfAddress = textfield("127.0.0.1")

                label("Port: ")
                tfPort = textfield("${5000}") {
                    prefColumnCount = 4
                }
                button("Connect") {
                    action {
                        val socket = Socket(tfAddress.text, tfPort.text.toInt()).apply {
                            soTimeout = 30 * 1000
                        }
                        clientThread = ClientThread(this@MyChatView, socket)
                        if (socket.isBound) {
                            println("Connected")
                            with(clientThread) {
                                start()
                                sendMessage(Payload(tfUsername.text))
                            }
                            this.text = "Exit"
                            topBox.children.forEach { child -> if (child !is Button) child.isDisable = true }
                            action {
                                exitProcess(0)
                            }
                        }
                    }
                }
            }
        }

        center {
            myScrollPane = scrollpane {
                chatBubbles = vbox()
            }
        }

        bottom {
            hbox {
                alignment = Pos.CENTER
                tfChatMessage = textfield()
                button("Send") {
                    action {
                        clientThread.sendMessage(
                            Payload(
                                tfUsername.text,
                                tfChatMessage.text
                            )
                        )
                    }
                }
                button("Img") {
                    action {
                        showFileChooser()
                    }
                }
            }
        }
    }

    private val bubble = { payload: Payload ->
        vbox {
            text(payload.sender) {
                style {
                    fontWeight = FontWeight.BOLD
                }
            }
            paddingAll = 8.0
            text(payload.message) {
                style {
                    backgroundColor += c("#DDDDDD")
                    borderRadius += box(8.0.px)
                }
            }
            text("${payload.time}") {
                style {
                    fontSize = 10.px
                    baseColor = c("#DDDDDD")
                }
            }
        }
    }

    private fun showFileChooser() {
        val image = chooseFile(
            "Choose an image",
            arrayOf(FileChooser.ExtensionFilter("Image", listOf("*.jpg"))),
            mode = FileChooserMode.Single
        )[0]
        val payload = Payload(tfUsername.text, image = Base64.getEncoder().encodeToString(image.readBytes()))
        clientThread.sendMessage(payload)
    }

    fun appendMessage(payload: Payload) {
        if (payload.image == null) chatBubbles.add(bubble(payload))
        myScrollPane.hvalue = 1.0
    }

    fun showDisconnectDialogAndExit(s: String = "Disconnected") {
        Platform.runLater {
            alert(Alert.AlertType.INFORMATION, s, buttons = arrayOf(ButtonType.OK))
            exitProcess(0)
        }
    }

    fun showDialog(s: String) {
        Platform.runLater {
            alert(Alert.AlertType.INFORMATION, s, buttons = arrayOf(ButtonType.OK))
        }
    }

    fun appendImage(sender: String, image: Image, time: Date) {
        println(sender + "  " + tfUsername.text)
        if (sender == tfUsername.text) return
        chatBubbles.add(
            vbox {
                paddingAll = 8.0

                text(sender) {
                    style {
                        fontWeight = FontWeight.BOLD
                    }
                }
                imageview(image) {
                    fitWidth = 400.0
                    fitHeight = 300.0
                    preserveRatioProperty()
                }
                text(time.toString()) {
                    style {
                        fontSize = 10.px
                        baseColor = c("#DDDDDD")
                    }
                }
            }
        )
    }
}


