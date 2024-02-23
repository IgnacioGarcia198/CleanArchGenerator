package com.garcia.ignacio.cleanarchgenerator.action

import com.garcia.ignacio.cleanarchgenerator.generator.Generator
import com.garcia.ignacio.cleanarchgenerator.generator.TextGenerator
import com.garcia.ignacio.cleanarchgenerator.ui.BlocGenerator
import com.garcia.ignacio.cleanarchgenerator.ui.camelToSnakeCase
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.command.WriteCommandAction

/**
 * Flutter action in the context menu
 *
 * This class will call the dialog and generate the bloc files
 */
class ActionGenerateBloc : AnAction() {
    private val textGenerator = TextGenerator()

    /**
     * Is called by the context action menu entry with an [actionEvent]
     */
    override fun actionPerformed(actionEvent: AnActionEvent) {
        val dialog = BlocGenerator()
        dialog.pack()
        if (dialog.showAndGet()) {
            generate(actionEvent.dataContext, dialog.data)
        }
    }

    /**
     * Generates the Flutter Clean-Architecture structure in a [dataContext].
     * If a [root] String is provided, it will create the structure in a new folder.
     */
    private fun generate(
        dataContext: DataContext,
        dialogData: BlocGenerator.DialogData,
    ) {
        if (dialogData.blocClass.isBlank() || dialogData.eventClass.isBlank() || dialogData.stateClass.isBlank()) return
        val featureName = dialogData.blocClass.removeSuffix("Bloc").lowercase()
        val project = CommonDataKeys.PROJECT.getData(dataContext) ?: return
        val selected = PlatformDataKeys.VIRTUAL_FILE.getData(dataContext) ?: return

        val folder = if (selected.isDirectory) selected else selected.parent
        WriteCommandAction.runWriteCommandAction(project) {
            val classMap = mutableMapOf<String, String>()
            with(dialogData) {
                classMap.addFileIfKeyNotEmpty(blocClass, textGenerator.generateBlocText(blocClass, eventClass, stateClass, events))
                classMap.addFileIfKeyNotEmpty(stateClass, textGenerator.generateStateText(stateClass, states))
                classMap.addFileIfKeyNotEmpty(eventClass, textGenerator.generateEventText(eventClass, events))
            }

            Generator.createFolderWithFilesAndText(
                project = project,
                rootFolder = folder,
                parentFolder = if (dialogData.blocSubFolder) featureName else "",
                classMap
            )
        }
    }

    private fun <T> MutableMap<String, T>.addFileIfKeyNotEmpty(key: String, value: T) {
        if (key.isNotBlank()) this["${key.camelToSnakeCase()}.dart"] = value
    }
}