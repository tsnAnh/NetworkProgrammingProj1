package client

import tornadofx.App
import tornadofx.launch

class MyApp : App(MyChatView::class) {
    fun main(args: Array<String>) {
        launch<MyApp>()
    }
}

