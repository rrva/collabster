package se.rrva.collabster

import javafx.application.Platform
import tornadofx.*
import java.awt.SystemTray
import java.awt.TrayIcon
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.imageio.ImageIO


class CollabsterApp : App(CollabsterView::class, Styles::class) {

    init {
        if (!SystemTray.isSupported()) {
            println("Failed to display tray icon - no system tray support")
        }
        val resource = this.javaClass.getResourceAsStream("/tray_icon.png")
        val trayIcon = TrayIcon(ImageIO.read(resource))
        val systemTray = SystemTray.getSystemTray()
        systemTray.add(trayIcon)
        trayIcon.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                Platform.runLater {
                    FX.primaryStage.isIconified = false
                    FX.primaryStage.requestFocus()
                }
            }
        })
    }

    override fun stop() {
        println("onExit")
        Platform.exit()
        //val collabsterView = primaryView as CollabsterView
        //collabsterView.stop()
    }

    class Styles : Stylesheet() {
        init {
            /*label {
            fontSize = 20.px
            fontWeight = FontWeight.BOLD
            backgroundColor += c("#cecece")
        }*/

        }
    }

}


/*
Add the following VM options work a working run config

--patch-module collabster=$PROJECT_DIR$/out/production/resources

 */
fun main(args: Array<String>) {
    launch<CollabsterApp>(args)
}