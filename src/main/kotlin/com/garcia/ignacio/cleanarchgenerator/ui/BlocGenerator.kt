package com.garcia.ignacio.cleanarchgenerator.ui

import CustomListWithInlineEditing
import com.intellij.openapi.ui.DialogWrapper
import java.awt.event.KeyEvent
import javax.swing.*
import kotlin.system.exitProcess

class BlocGenerator(bloc: String = "SampleBloc") : DialogWrapper(true) {
    private lateinit var panel: JPanel
    private lateinit var blocClassName: JTextField
    private lateinit var eventClassName: JTextField
    private lateinit var eventsList: CustomListWithInlineEditing
    private lateinit var stateClasName: JTextField
    private lateinit var statesList: CustomListWithInlineEditing
    private lateinit var subFolderCheckBox: JCheckBox

    private val blocNameRoot get() = blocClassName.text.removeSuffix("Bloc")
    private val stateNameRoot get() = stateClasName.text.removeSuffix("State")
    private val eventNameRoot get() = eventClassName.text.removeSuffix("State")

    private val rootBlocName = bloc.removeSuffix("Bloc")
    private val fullBlocName = rootBlocName + "Bloc"

    private var oldStateClassNameRoot = rootBlocName
    private var oldEventClassNameRoot = rootBlocName

    val data get() = DialogData(
        blocClassName.text,
        stateClasName.text,
        eventClassName.text,
        statesList.getItems(),
        eventsList.getItems(),
        subFolderCheckBox.model.isSelected,
    )

    init {
        title = "Generate Flutter Bloc"
        init()
    }

    private fun onCancel() {
        // add your code here if necessary
        dispose()
    }

    override fun createCenterPanel(): JComponent {
        panel = JPanel()
        panel.layout = BoxLayout(panel, BoxLayout.Y_AXIS)
        panel.border = BorderFactory.createEmptyBorder(10, 10, 10, 10)
        isModal = true
        subFolderCheckBox = JCheckBox("Create subfolder")

        blocClassName = JTextField().apply {
            text = fullBlocName
        }
        eventClassName = JTextField().apply { text = blocNameRoot + "Event" }
        eventsList = CustomListWithInlineEditing()

        stateClasName = JTextField().apply { text = blocNameRoot + "State" }
        statesList = CustomListWithInlineEditing().apply {
            addItem(stateNameRoot + "Idle")
            addItem(stateNameRoot + "Loading")
            addItem(stateNameRoot + "Done")
        }
        blocClassName.onTextChanged {
            eventClassName.text = blocNameRoot + "Event"
            stateClasName.text = blocNameRoot + "State"
        }
        stateClasName.onTextChanged {
            statesList.updateAllItems { oldText -> stateNameRoot + oldText.removePrefix(oldStateClassNameRoot) }
            oldStateClassNameRoot = stateNameRoot
        }
        eventClassName.onTextChanged {
            eventsList.updateAllItems { oldText -> eventNameRoot + oldText.removePrefix(oldEventClassNameRoot) }
            oldEventClassNameRoot = eventNameRoot
        }
        panel.add(JLabel("Bloc Class name:"))
        panel.add(blocClassName)
        panel.add(JLabel("Event Class name:"))
        panel.add(eventClassName)
        panel.add(eventsList)
        panel.add(JLabel("State Class name:"))
        panel.add(stateClasName)
        panel.add(statesList)
        panel.add(subFolderCheckBox)

        // call onCancel() on ESCAPE
        (contentPane as JPanel).registerKeyboardAction(
            { onCancel() },
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
            JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT
        )
        return panel
    }

    data class DialogData(
        val blocClass: String,
        val stateClass: String,
        val eventClass: String,
        val states: List<String>,
        val events: List<String>,
        val blocSubFolder: Boolean,
    )

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val dialog = BlocGenerator()
            dialog.pack()
            dialog.showAndGet()
            println("DIALOG DATA: ${dialog.data}")
            exitProcess(0)
        }
    }
}
