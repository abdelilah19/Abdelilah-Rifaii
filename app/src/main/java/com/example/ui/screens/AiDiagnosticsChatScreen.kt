package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.*
import com.example.ui.viewmodel.NetworkViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiDiagnosticsChatScreen(
    viewModel: NetworkViewModel,
    onNavigateBack: () -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val chatText by viewModel.chatInput.collectAsStateWithLifecycle()
    val messagesList by viewModel.chatMessages.collectAsStateWithLifecycle()
    val isAiThinking by viewModel.isAiLoading.collectAsStateWithLifecycle()

    val chatListScrollState = rememberLazyListState()

    // Slide bottom automatically when new logs arrive
    LaunchedEffect(messagesList.size) {
        if (messagesList.isNotEmpty()) {
            chatListScrollState.animateScrollToItem(messagesList.size - 1)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = viewModel.getString("ai_diag").uppercase(),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.5.sp
                            )
                        )
                        Text(
                            text = "Powered by Gemini 2.5 Flash",
                            style = MaterialTheme.typography.labelSmall,
                            color = ElectricBlue
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Introductory notice banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Psychology,
                        contentDescription = null,
                        tint = ElectricBlue,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = viewModel.getString("ai_advisor_desc"),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))

            // Main chat messages view
            LazyColumn(
                state = chatListScrollState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp)
            ) {
                items(messagesList) { message ->
                    val isFromUser = message.second
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = if (isFromUser) Arrangement.End else Arrangement.Start
                    ) {
                        Card(
                            shape = RoundedCornerShape(
                                topStart = 16.dp,
                                topEnd = 16.dp,
                                bottomStart = if (isFromUser) 16.dp else 2.dp,
                                bottomEnd = if (isFromUser) 2.dp else 16.dp
                            ),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isFromUser) ElectricBlueSecondary else MaterialTheme.colorScheme.surface
                            ),
                            modifier = Modifier
                                .widthIn(max = 290.dp)
                                .border(
                                    1.dp,
                                    if (isFromUser) Color.Transparent else MaterialTheme.colorScheme.outline.copy(alpha = 0.1f),
                                    RoundedCornerShape(
                                        topStart = 16.dp,
                                        topEnd = 16.dp,
                                        bottomStart = if (isFromUser) 16.dp else 2.dp,
                                        bottomEnd = if (isFromUser) 2.dp else 16.dp
                                    )
                                )
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = message.first,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = if (isFromUser) Color.White else TextWhite,
                                        lineHeight = 20.sp
                                    )
                                )
                            }
                        }
                    }
                }

                // Pulser active loading thinking indicator
                if (isAiThinking) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                            horizontalArrangement = Arrangement.Start
                        ) {
                            Card(
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                modifier = Modifier.border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f), RoundedCornerShape(14.dp))
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(16.dp),
                                        color = ElectricBlue,
                                        strokeWidth = 2.dp
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = viewModel.getString("ai_thinking"),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))

            // Text input container overlay
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = chatText,
                    onValueChange = { viewModel.chatInput.value = it },
                    placeholder = {
                        Text(
                            text = viewModel.getString("ask_ai_placeholder"),
                            fontSize = 13.sp
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("chat_input_field"),
                    maxLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricBlue,
                        focusedLabelColor = ElectricBlue
                    ),
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Send,
                        keyboardType = KeyboardType.Text
                    ),
                    keyboardActions = KeyboardActions(
                        onSend = {
                            viewModel.executeAiQuery()
                            keyboardController?.hide()
                        }
                    )
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = {
                        viewModel.executeAiQuery()
                        keyboardController?.hide()
                    },
                    enabled = chatText.trim().isNotEmpty() && !isAiThinking,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(
                            if (chatText.trim().isNotEmpty() && !isAiThinking) ElectricBlueSecondary else MaterialTheme.colorScheme.outline.copy(alpha = 0.12f)
                        )
                        .testTag("send_chat_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Send Message Button",
                        tint = if (chatText.trim().isNotEmpty() && !isAiThinking) Color.White else Color.Gray,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
