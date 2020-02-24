package se.rrva.collabster

import javafx.animation.Animation
import javafx.animation.KeyFrame
import javafx.animation.Timeline
import javafx.application.Platform
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.scene.control.*
import javafx.scene.input.MouseEvent
import javafx.scene.layout.BorderPane
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import tornadofx.FX
import tornadofx.View
import tornadofx.action
import tornadofx.seconds
import java.awt.Color
import java.awt.Font
import java.awt.RenderingHints
import java.awt.SystemTray
import java.awt.image.BufferedImage
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.text.DateFormat
import java.text.SimpleDateFormat


class CollabsterView : View() {
    override val root: BorderPane by fxml()

    val collabstersList: ListView<BorderPane> by fxid("collabstersList")
    val driverLabel: Label by fxid("driverLabel")
    val rotationTimeTextField: TextField by fxid("rotationTime")

    init {
        val list = loadCollabsters()
        list.forEach {
            addCollabster(it)
        }
        Platform.runLater {
            updateSystemTray(0)
        }
    }


    @Suppress("UNUSED")
    fun onListClick(@Suppress("UNUSED_PARAMETER") event: MouseEvent) {
        val clickedCollabster = collabstersList.getSelectionModel().getSelectedItem() as BorderPane
        collabstersList.items.remove(clickedCollabster)
        collabstersList.items.add(0, clickedCollabster)
        collabstersList.selectionModel.select(clickedCollabster)
        //TODO driverLabel.text = clickedCollabster.text().text

        saveCollabsters()
    }

    @Suppress("UNUSED")
    fun addCollabster() {
        val textInputDialog = TextInputDialog()
        textInputDialog.title = "Add a collabster"
        textInputDialog.headerText =
            "Adding a collabster to the list of collabsters so that more collabsters can collaborate with the code"

        val showAndWait = textInputDialog.showAndWait()

        showAndWait.map {
            addCollabster(it)
        }

        saveCollabsters()
    }

    private fun addCollabster(it: String) {
        val removeButton = Button("-")
        removeButton.padding = Insets(0.0, 4.0, 0.0, 4.0)
        val pane = BorderPane()
        pane.left = Label(it)
        pane.right = removeButton
        collabstersList.items.add(pane)
        removeButton.action {
            collabstersList.items.remove(pane)
            saveCollabsters()
        }
    }

    @Serializable
    data class SavedCollabsters(val list: List<String>)

    private fun saveCollabsters() {
        val json = Json(JsonConfiguration.Stable)
        val map = collabstersList.items.map { it.left as Label }.map { it.text }
        val out = json.stringify(SavedCollabsters.serializer(),  SavedCollabsters(map))
        Files.writeString(Path.of("collabsters.json"), out)
        Platform.runLater {
            updateSystemTray(rotationTimeMinutes() * 60L)
        }
    }

    private fun loadCollabsters(): List<String> {
        val json = Json(JsonConfiguration.Stable)
        val path = Path.of("collabsters.json")
        return if (File(path.toUri()).exists()) {
            json.parse(SavedCollabsters.serializer(),
                Files.readString(path)).list
        } else {
            emptyList()
        }
    }

    val timeFormat: DateFormat = SimpleDateFormat("mm:ss")
    var endTime = 0L
    var timeline: Timeline? = null

    @Suppress("UNUSED")
    fun startTimer() {
        endTime = System.currentTimeMillis() + (rotationTimeMinutes() * 60) * 1000

        driverLabel.text = currentCollabster()
        FX.primaryStage.isAlwaysOnTop = false
        FX.primaryStage.isIconified = true

        timeline = Timeline(
            KeyFrame(
                0.5.seconds, EventHandler {
                    val diff = endTime - System.currentTimeMillis()
                    if (diff < 0) {
                        Platform.runLater {
                            timeline?.stop()
                            FX.primaryStage.isIconified = false
                            FX.primaryStage.requestFocus()
                            FX.primaryStage.isAlwaysOnTop = true
                            rotateCollabsters()
                            driverLabel.text = "Next: ${currentCollabster()}"
                        }
                    } else {
                        updateSystemTray(diff)
                    }
                }
            ))
        timeline?.cycleCount = Animation.INDEFINITE
        timeline?.play()
        updateSystemTray(endTime - System.currentTimeMillis())
    }

    private fun rotationTimeMinutes(): Int {
        return try {
            rotationTimeTextField.text.toInt()
        } catch (e: Exception) {
            7
        }
    }

    private fun updateSystemTray(diff: Long) {
        val systemTray = SystemTray.getSystemTray()
        val image = BufferedImage(300, 50, BufferedImage.TYPE_INT_ARGB)
        val g2d = image.createGraphics()
        g2d.color = Color.black
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        g2d.font = Font("San Fransisco Display", Font.PLAIN, 32)
        g2d.drawString(timeFormat.format(diff) + " " + currentCollabster(), 0, 38)
        g2d.dispose()
        systemTray.trayIcons[0].image = image
    }

    private fun currentCollabster() =
        (collabstersList.items.firstOrNull()?.left as? Label)?.text ?: "Dr alban"

    private fun rotateCollabsters() {
        timeline?.stop()
        if (collabstersList.items.isEmpty()) {
            return
        } else {
            val firstCollabster = collabstersList.items.first()

            collabstersList.items.remove(firstCollabster)
            collabstersList.items.add(firstCollabster)
            collabstersList.selectionModel.select(collabstersList.items.first())
            saveCollabsters()
        }
    }
}

