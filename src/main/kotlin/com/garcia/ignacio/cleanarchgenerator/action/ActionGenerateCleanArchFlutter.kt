package com.garcia.ignacio.cleanarchgenerator.action

import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.vfs.VirtualFile
import com.garcia.ignacio.cleanarchgenerator.generator.Generator
import com.garcia.ignacio.cleanarchgenerator.generator.TextGenerator
import com.garcia.ignacio.cleanarchgenerator.ui.FlutterCleanArchGenerator
import com.garcia.ignacio.cleanarchgenerator.ui.camelToSnakeCase
import com.garcia.ignacio.cleanarchgenerator.ui.dartClassToFile

/**
 * Flutter action in the context menu
 *
 * This class will call the dialog and generate the Flutter Clean-Architecture structure
 */
class ActionGenerateCleanArchFlutter : AnAction() {
    private val textGenerator = TextGenerator()
    /**
     * Is called by the context action menu entry with an [actionEvent]
     */
    override fun actionPerformed(actionEvent: AnActionEvent) {
        val dialog = FlutterCleanArchGenerator()
        if (dialog.showAndGet()) {
            generate(actionEvent.dataContext, dialog.data)
        }
    }

    /**
     * Generates the Flutter Clean-Architecture structure in a [dataContext].
     * If a [root] String is provided, it will create the structure in a new folder.
     */
    private fun generate(dataContext: DataContext, dialogData: FlutterCleanArchGenerator.DialogData) {
        val project = CommonDataKeys.PROJECT.getData(dataContext) ?: return
        val selected = PlatformDataKeys.VIRTUAL_FILE.getData(dataContext) ?: return
        val root = dialogData.feature.camelToSnakeCase()

        var folder = if (selected.isDirectory) selected else selected.parent
        WriteCommandAction.runWriteCommandAction(project) {
            if (root.isNotBlank()) {
                val result = Generator.createFolder(
                    project, folder, root
                ) ?: return@runWriteCommandAction
                folder = result[root]
            }
            if (dialogData.splitDataSources) {
                val mapOrFalse = Generator.createFolder(
                    project, folder,
                    "data",
                    "repositories"
                ) ?: return@runWriteCommandAction
                mapOrFalse["data"]?.let { data: VirtualFile ->
                    Generator.createFolder(
                        project, data,
                        "local",
                        "models", "data_sources"
                    )
                    Generator.createFolder(
                        project, data,
                        "remote",
                        "models", "data_sources"
                    )
                }
            } else {
                Generator.createFolder(
                    project, folder,
                    "data",
                    "repositories", "data_sources", "models"
                )
            }
            Generator.createFolder(
                project, folder,
                "domain",
                "repositories", "use_cases", "entities"
            )
            Generator.createFolder(
                project, folder,
                "presentation",
                "bloc", "pages", "widgets"
            )
            Generator.createFileWithText(
                project,
                folder.findChild("domain")?.findChild("repositories") ?: error("no such folder..."),
                dialogData.repositoryClass.dartClassToFile(),
                textGenerator.generateRepositoryText(dialogData.repositoryClass)
            )
            Generator.createFileWithText(
                project,
                folder.findChild("data")?.findChild("repositories") ?: error("no such folder..."),
                dialogData.repositoryImplClass.dartClassToFile(),
                textGenerator.generateRepositoryImplText(dialogData.repositoryImplClass, dialogData.repositoryClass)
            )
            dialogData.useCases.forEach {
                Generator.createFileWithText(
                    project,
                    folder.findChild("domain")?.findChild("use_cases") ?: error("no such folder..."),
                    it.dartClassToFile(),
                    textGenerator.generateUseCaseText(it, dialogData.repositoryClass)
                )
            }
        }
    }
}