package com.garcia.ignacio.cleanarchgenerator.ui

import javax.swing.JTextField
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener

fun JTextField.onTextChanged(action: (String) -> Unit) {
    document.addDocumentListener(object : DocumentListener {
        override fun insertUpdate(e: DocumentEvent) {
            action(text)
        }

        override fun removeUpdate(e: DocumentEvent) {
            action(text)
        }

        override fun changedUpdate(e: DocumentEvent) {
            action(text)
        }
    })
}

fun String.camelToSnakeCase(): String {
    val pattern = "(?<=.)[A-Z]".toRegex()
    return this.replace(pattern, "_$0").lowercase()
}

fun String.dartClassToFile(): String {
    return camelToSnakeCase() + ".dart"
}

fun String.doCapitalize() = replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }