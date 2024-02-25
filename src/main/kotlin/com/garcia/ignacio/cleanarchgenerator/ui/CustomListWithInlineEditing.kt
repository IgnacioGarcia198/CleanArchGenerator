import javax.swing.*
import javax.swing.table.DefaultTableModel
import java.awt.*
import java.awt.event.*
import java.util.*
import javax.swing.table.DefaultTableCellRenderer

class CustomListWithInlineEditing : JPanel() {
    private val tableModel: DefaultTableModel
    private val table: JTable
    private val addButton: JButton

    init {
        layout = BorderLayout()

        // Create a table model and table
        tableModel = DefaultTableModel()
        tableModel.addColumn("")
        tableModel.addColumn("")
        table = JTable(tableModel)

        // Set column width
        //table.columnModel.getColumn(0).preferredWidth = 200

        val button = JButton("x")
        button.isOpaque = true
        button.horizontalAlignment = SwingConstants.CENTER
        //button.border = null // Remove button border
        //button.margin = Insets(0, 0, 0, 0) // Reduce button margin
        button.addActionListener {
            // Handle button click
            val row = table.convertRowIndexToModel(table.selectedRow)
            (table.model as DefaultTableModel).removeRow(row)
            table.columnModel.getColumn(1).cellEditor.stopCellEditing()
        }
        // Set custom cell renderer and editor
        table.columnModel.getColumn(0).cellRenderer = InlineEditRenderer()
        table.columnModel.getColumn(0).cellEditor = InlineEditEditor()
        table.columnModel.getColumn(1).cellRenderer = ButtonRenderer(button)
        table.columnModel.getColumn(1).cellEditor = ButtonEditor(button)

        //table.columnModel.getColumn(0).maxWidth = 180 // Adjust as needed
        table.columnModel.getColumn(1).maxWidth = 20 // Adjust as needed
        table.autoResizeMode = JTable.AUTO_RESIZE_ALL_COLUMNS

        // Create a scroll pane for the table
        val scrollPane = JScrollPane(table)
        scrollPane.preferredSize = Dimension(200, 70)
        add(scrollPane, BorderLayout.CENTER)
        addButton = JButton("+")
        addButton.preferredSize = Dimension(20, 15)
        addButton.addActionListener {
            addItem("New Item")
        }

        val buttonPanel = JPanel()
        buttonPanel.layout = FlowLayout(FlowLayout.RIGHT)
        buttonPanel.add(addButton)
        //buttonPanel.preferredSize = Dimension(200, buttonPanel.height)
        add(buttonPanel, BorderLayout.SOUTH)
        buttonPanel.border = BorderFactory.createEmptyBorder(0, 0, 5, 0)
    }

    fun addItem(item: String) {
        tableModel.addRow(arrayOf(item))
    }

    fun getItems(): List<String> {
        val items = mutableListOf<String>()
        for (i in 0 until tableModel.rowCount) {
            items.add(tableModel.getValueAt(i, 0).toString())
        }
        return items
    }

    fun updateAllItems(bloc: (item: String) -> String) {
        val currentItems = getItems()

        tableModel.dataVector.clear()
        currentItems.forEach {
            addItem(bloc(it))
        }
    }

    private inner class InlineEditRenderer : DefaultTableCellRenderer() {
        override fun getTableCellRendererComponent(
            table: JTable?,
            value: Any?,
            isSelected: Boolean,
            hasFocus: Boolean,
            row: Int,
            column: Int
        ): Component {
            val renderer = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column)
            (renderer as JLabel).text = value?.toString() ?: ""
            return renderer
        }
    }

    private inner class InlineEditEditor : DefaultCellEditor(JTextField()) {
        init {
            (editorComponent as JTextField).addFocusListener(object: FocusListener {
                override fun focusGained(e: FocusEvent?) {
                    // NOP
                }

                override fun focusLost(e: FocusEvent?) {
                    stopCellEditing()
                }
            })
        }

        override fun getCellEditorValue(): Any {
            return (editorComponent as JTextField).text
        }
    }
}

private class ButtonRenderer(private val button: JButton) : DefaultTableCellRenderer() {
    override fun getTableCellRendererComponent(table: JTable?, value: Any?, isSelected: Boolean, hasFocus: Boolean, row: Int, column: Int): Component {
        return button
    }
}

private class ButtonEditor(
    private val button: JButton,
) : DefaultCellEditor(JCheckBox()) {
    override fun getTableCellEditorComponent(table: JTable?, value: Any?, isSelected: Boolean, row: Int, column: Int): Component {
        return button
    }

    override fun stopCellEditing(): Boolean {
        // Ensure the table finishes editing and removes the button
        return try {
            super.stopCellEditing()
        } catch (e: IndexOutOfBoundsException) {
            true
        }
    }

    override fun isCellEditable(e: EventObject?): Boolean {
        // Ensure the button is editable
        return true
    }
}

fun main() {
    SwingUtilities.invokeLater {
        val frame = JFrame("CustomListWithInlineEditing Test")
        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        frame.size = Dimension(200, 200)

        val customList = CustomListWithInlineEditing()
        frame.add(customList, BorderLayout.CENTER)

        val retrieveButton = JButton("Retrieve Items")
        retrieveButton.addActionListener {
            val items = customList.getItems()
            items.forEach { item -> println(item) }
        }
        frame.add(retrieveButton, BorderLayout.NORTH)

        frame.isVisible = true
    }
}
