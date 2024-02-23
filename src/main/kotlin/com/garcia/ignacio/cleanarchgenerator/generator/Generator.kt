/*
 * Copyright: Copyright (c) 2019 Arne Rantzen <arne@rantzen.net>
 * License: GPL-3
 * Last Edited: 07.12.19, 23:26
 */

package com.garcia.ignacio.cleanarchgenerator.generator

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.garcia.ignacio.cleanarchgenerator.ui.Notifier
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.fileEditor.FileDocumentManager
import java.io.IOException

/**
 * Generator Factory to create structure
 */
interface Generator {
    companion object {
        /**
         * Creates a [parent] folder and its [children] in a given [folder].
         * [project] is needed for the notifications if there is an error or a warning situation.
         * @return null if an error occurred or the a map of all virtual files created
         */
        fun createFolder(
            project: Project,
            folder: VirtualFile,
            parent: String,
            vararg children: String
        ): Map<String, VirtualFile>? {
            try {
                for (child in folder.children) {
                    if (child.name == parent) {
                        Notifier.warning(project, "Directory [$parent] already exists")
                        return null
                    }
                }
                val mapOfFolder = mutableMapOf<String, VirtualFile>()
                mapOfFolder[parent] = folder.createChildDirectory(folder, parent)
                for (child in children) {
                    mapOfFolder[child] =
                        mapOfFolder[parent]?.createChildDirectory(mapOfFolder[parent], child) ?: throw IOException()
                }
                return mapOfFolder
            } catch (e: IOException) {
                Notifier.warning(project, "Couldn't create $parent directory")
                e.printStackTrace()
                return null
            }
        }

        fun createFolderWithFilesAndText(
            project: Project,
            rootFolder: VirtualFile,
            parentFolder: String,
            filesAndText: Map<String, String>,
        ): Map<String, VirtualFile>? {
            var parent = parentFolder
            try {
                val mapOfFolder = mutableMapOf<String, VirtualFile>()
                if (parentFolder.isBlank()) {
                    parent = rootFolder.name
                    mapOfFolder[parent] = rootFolder
                } else {
                    for (child in rootFolder.children) {
                        if (child.name == parent) {
                            Notifier.warning(project, "Directory [$parent] already exists")
                            return null
                        }
                    }
                    mapOfFolder[parent] = rootFolder.createChildDirectory(rootFolder, parent)
                }
                val parentDir = mapOfFolder[parent]

                for (child in filesAndText.keys) {
                    val childFile = parentDir?.createChildData(parentDir, child) ?: throw IOException()
                    filesAndText[child]?.let { text ->
                        writeFile(childFile, project, text)
                    }
                }
                return mapOfFolder
            } catch (e: IOException) {
                Notifier.warning(project, "Couldn't create $parent directory")
                e.printStackTrace()
                return null
            }
        }

        fun createFileWithText(
            project: Project,
            parentFolder: VirtualFile,
            fileName: String,
            text: String,
        ) {
            val childFile = parentFolder.createChildData(parentFolder, fileName)
            writeFile(childFile, project, text)
        }

        private fun writeFile(
            childFile: VirtualFile,
            project: Project,
            text: String
        ) {
            FileDocumentManager.getInstance().getDocument(childFile)?.let { document ->
                // Perform the write operation
                WriteCommandAction.runWriteCommandAction(project) {
                    ApplicationManager.getApplication().runWriteAction {
                        document.setText(text)
                    }
                }
                // Save the document to persist changes
                FileDocumentManager.getInstance().saveDocument(document)
            }
        }
    }
}