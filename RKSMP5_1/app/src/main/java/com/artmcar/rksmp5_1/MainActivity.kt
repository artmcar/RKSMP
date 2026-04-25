package com.artmcar.rksmp5_1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.artmcar.rksmp5_1.ui.theme.RKSMP5_1Theme
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NotesViewModel : ViewModel() {
    var notes = mutableStateListOf<Note>()
        private set
    fun loadNotesOnce(filesDir: File) {
        if (notes.isNotEmpty()) return

        val files = filesDir.listFiles { file ->
            file.extension == "txt"
        } ?: return
        notes.addAll(
            files.mapNotNull { file ->
                try {
                    val text = file.readText()

                    val name = file.name.removeSuffix(".txt")
                    val parts = name.split("_", limit = 2)

                    val timestamp = parts[0].toLong()
                    val title = parts.getOrNull(1) ?: ""

                    Note(file.name, title, text, timestamp)
                } catch (e: Exception) {
                    null
                }
            }.sortedByDescending { it.timestamp }
        )
    }
    fun addNote(note: Note) {
        notes.add(0, note)
    }
    fun deleteNote(fileName: String, filesDir: File) {
        File(filesDir, fileName).delete()
        notes.removeAll { it.fileName == fileName }
    }
    fun updateNote(old: Note, newContent: String, newTitle: String, filesDir: File) {
        File(filesDir, old.fileName).delete()

        val timestamp = System.currentTimeMillis()
        val safeTitle = newTitle.replace(" ", "_")

        val fileName = if (safeTitle.isBlank()) {
            "${timestamp}.txt"
        } else {
            "${timestamp}_${safeTitle}.txt"
        }
        val file = File(filesDir, fileName)
        file.writeText(newContent)
        notes.removeAll { it.fileName == old.fileName }
        notes.add(0, Note(fileName, newTitle, newContent, timestamp))
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
                RKSMP5_1Theme {
                val vm: NotesViewModel = viewModel()
                val context = LocalContext.current
                val filesDir = context.filesDir

                var screen by remember { mutableStateOf("list") }
                var selectedNote by remember { mutableStateOf<Note?>(null) }

                LaunchedEffect(true) {
                    vm.loadNotesOnce(filesDir)
                }

                when (screen) {
                    "list" -> NotesListScreen(
                        notes = vm.notes,
                        onAddClick = { screen = "create" },
                        onNoteClick = {
                            selectedNote = it
                            screen = "edit"
                        },
                        onDelete = { vm.deleteNote(it, filesDir) }
                    )

                    "create" -> CreateScreen(
                        onBack = { screen = "list" },
                        onSave = { title, content ->

                            val timestamp = System.currentTimeMillis()
                            val safeTitle = title.replace(" ", "_")

                            val fileName = if (safeTitle.isBlank()) {
                                "${timestamp}.txt"
                            } else {
                                "${timestamp}_${safeTitle}.txt"
                            }

                            val file = File(filesDir, fileName)
                            file.writeText(content)

                            vm.addNote(Note(fileName, title, content, timestamp))
                            screen = "list"
                        }
                    )

                    "edit" -> selectedNote?.let { note ->
                        EditScreen(
                            note = note,
                            onBack = { screen = "list" },
                            onSave = { title, content ->
                                vm.updateNote(note, content, title, filesDir)
                                screen = "list"
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NotesListScreen(
    notes: List<Note>,
    onAddClick: () -> Unit,
    onNoteClick: (Note) -> Unit,
    onDelete: (String) -> Unit
) {
    var noteToDelete by remember { mutableStateOf<String?>(null) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onAddClick) {
                Icon(Icons.Default.Add, null)
            }
        }
    ) { padding ->

        if (notes.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.TopCenter
            ) {
                Text(
                    "У вас пока нет записей\nНажмите + чтобы создать запись",
                    modifier = Modifier.padding(top = 40.dp)
                )
            }
        } else {
            LazyColumn(modifier = Modifier.padding(padding)) {
                items(notes) { note ->
                    NoteItem(
                        note = note,
                        onClick = onNoteClick,
                        onLongClick = { noteToDelete = it }
                    )
                }
            }
        }
        if (noteToDelete != null) {
            AlertDialog(
                onDismissRequest = { noteToDelete = null },
                confirmButton = {
                    TextButton(onClick = {
                        onDelete(noteToDelete!!)
                        noteToDelete = null
                    }) {
                        Text("Удалить")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { noteToDelete = null }) {
                        Text("Отмена")
                    }
                },
                title = { Text("Удалить запись?") }
            )
        }
    }
}

@Composable
fun NoteItem(
    note: Note,
    onClick: (Note) -> Unit,
    onLongClick: (String) -> Unit
) {
    val formatter = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .combinedClickable(
                onClick = { onClick(note) },
                onLongClick = { onLongClick(note.fileName) }
            )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {

            Text(
                text = note.title.ifEmpty { "Без заголовка" },
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = note.content.take(30).replace("\n", " ")
            )

            Spacer(Modifier.height(4.dp))

            Text(text = formatter.format(Date(note.timestamp)))
        }
    }
}

@Composable
fun CreateScreen(
    onBack: () -> Unit,
    onSave: (String, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }

    Column {
        TopBar("Новая запись", onBack) {
            onSave(title, content)
        }

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Заголовок (опционально)") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )

        OutlinedTextField(
            value = content,
            onValueChange = { content = it },
            label = { Text("Ваша запись") },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(8.dp)
        )
    }
}

@Composable
fun EditScreen(
    note: Note,
    onBack: () -> Unit,
    onSave: (String, String) -> Unit
) {
    var title by remember { mutableStateOf(note.title) }
    var content by remember { mutableStateOf(note.content) }

    Column {
        TopBar("Редактировать", onBack) {
            onSave(title, content)
        }

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Заголовок (опционально)") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )

        OutlinedTextField(
            value = content,
            onValueChange = { content = it },
            label = { Text("Ваша запись") },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(8.dp)
        )
    }
}

@Composable
fun TopBar(
    title: String,
    onBack: () -> Unit,
    onSave: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        IconButton(onClick = onBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
        }

        Text(
            text = title,
            modifier = Modifier.weight(1f)
        )

        IconButton(onClick = onSave) {
            Icon(Icons.Default.Check, null)
        }
    }
}
