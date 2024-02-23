package com.garcia.ignacio.cleanarchgenerator.ui

import CustomListWithInlineEditing
import com.intellij.openapi.ui.DialogWrapper
import java.awt.event.KeyEvent
import javax.swing.*
import kotlin.system.exitProcess

class FlutterCleanArchGenerator(val feature: String = "sample") : DialogWrapper(true) {
    private lateinit var panel: JPanel
    private lateinit var featureName: JTextField
    private lateinit var repositoryClassName: JTextField
    private lateinit var repositoryImplClassName: JTextField
    private lateinit var useCasesList: CustomListWithInlineEditing
    private lateinit var splitDataSourceCheckbox: JCheckBox

    private val featureNameText get() = featureName.text
    private val repositoryClassNameText get() = repositoryClassName.text


    val data get() = DialogData(
        featureName.text,
        repositoryClassName.text,
        repositoryImplClassName.text,
        useCasesList.getItems(),
        splitDataSourceCheckbox.model.isSelected,
    )

    init {
        title = "Generate Flutter Clean Arch Folder Structure"
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
        splitDataSourceCheckbox = JCheckBox("Split data_sources to local and remote")

        featureName = JTextField().apply {
            text = feature
        }
        repositoryClassName = JTextField().apply { text = featureNameText.doCapitalize() + "Repository" }

        repositoryImplClassName = JTextField().apply { text = repositoryClassNameText + "Impl" }
        useCasesList = CustomListWithInlineEditing()
        featureName.onTextChanged {
            repositoryClassName.text = featureNameText.doCapitalize() + "Repository"
        }
        repositoryClassName.onTextChanged {
            repositoryImplClassName.text = repositoryClassNameText + "Impl"
        }
        panel.add(JLabel("Feature name:"))
        panel.add(featureName)
        panel.add(JLabel("Repository interface name:"))
        panel.add(repositoryClassName)
        panel.add(JLabel("Repository implementation class name:"))
        panel.add(repositoryImplClassName)
        panel.add(JLabel("Use case classes:"))
        panel.add(useCasesList)
        panel.add(splitDataSourceCheckbox)

        // call onCancel() on ESCAPE
        (contentPane as JPanel).registerKeyboardAction(
            { onCancel() },
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
            JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT
        )
        return panel
    }

    data class DialogData(
        val feature: String,
        val repositoryClass: String,
        val repositoryImplClass: String,
        val useCases: List<String>,
        val splitDataSources: Boolean,
    )

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val dialog = FlutterCleanArchGenerator()
            dialog.pack()
            dialog.showAndGet()
            println("DIALOG DATA: ${dialog.data}")
            exitProcess(0)
        }
    }
}
