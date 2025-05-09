package com.oza.editnote.presentation.screen.list

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.oza.editnote.R
import com.oza.editnote.domain.model.Page
import com.oza.editnote.presentation.screen.list.EditNoteTheme.PrimaryBlue
import com.oza.editnote.presentation.screen.list.Spacing.borderWidth
import com.oza.editnote.presentation.screen.list.Spacing.buttonHeight
import com.oza.editnote.presentation.screen.list.Spacing.buttonWidth
import com.oza.editnote.presentation.screen.list.Spacing.cornerRadius
import com.oza.editnote.presentation.screen.list.Spacing.smallButtonWidth
import com.oza.editnote.presentation.screen.list.Spacing.xl
import kotlinx.coroutines.launch

private object EditNoteTheme {
    val GreyBackground = Color(0xFFF5F8FA)
    val PrimaryBlue = Color(0xFF4CB3F8)
    val White = Color.White
}

object Spacing {
    val sm = 8.dp
    val md = 16.dp
    val lg = 20.dp
    val xl = 30.dp

    // ボタン専用
    val buttonHeight = 40.dp
    val buttonWidth = 90.dp
    val smallButtonWidth = 48.dp
    val borderWidth = 1.dp
    val cornerRadius = 8.dp
}

@Composable
fun PageListScreen(
    viewModel: PageListViewModel = hiltViewModel()
) {
    // ViewModel の各状態を購読
    val uiState by viewModel.uiState.collectAsState()
    val selectedId by viewModel.selectedPageId.collectAsState()
    val isEditMode by viewModel.isEditMode.collectAsState()
    val showDeleteForId by viewModel.showDeleteDialogFor.collectAsState()
    val showAddDialog by viewModel.showAddDialog.collectAsState()
    val newTitle by viewModel.newTitle.collectAsState()
    // 一時表示用のエラー／成功イベント
    val successEvent by viewModel.successEvent.collectAsState(initial = null)
    val errorEvent by viewModel.errorEvent.collectAsState(initial = null)
    // Snackbar 用ホスト
    val snackbarHostState = remember { SnackbarHostState() }

    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val drawerWidth = LocalConfiguration.current.screenWidthDp.dp / 2
    val listState = rememberLazyListState()

    var editTitle by remember { mutableStateOf("") }
    var editBody by remember { mutableStateOf("") }
    var isPageEditing by remember { mutableStateOf(false) }

    // ページ一覧をキャッシュ
    val pages = remember(uiState) {
        (uiState as? PageListUiState.Success)?.pages ?: emptyList()
    }

    // バリデーションフラグ：タイトルは1～50文字、本文は10～2000文字
    val isSaveEnabled by remember(editTitle, editBody) {
        derivedStateOf {
            editTitle.length in 1..50 && editBody.length in 10..2000
        }
    }
    // ドロワー開閉や選択変更時にリストをスクロール
    ScrollBehaviors(drawerState, selectedId, pages, listState)

    // 成功時に Snackbar を表示
    LaunchedEffect(successEvent) {
        successEvent?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearSuccessEvent()
        }
    }

    // エラー発生時に Snackbar を表示
    LaunchedEffect(errorEvent) {
        errorEvent?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearErrorEvent()
        }
    }

    when (uiState) {
        // ローディング中はプログレス
        PageListUiState.Loading -> LoadingContent()
        // UIState のエラーは画面中央にテキスト表示
        is PageListUiState.Error -> ErrorContent((uiState as PageListUiState.Error).message)
        // データ取得成功時の UI
        is PageListUiState.Success -> {
            ModalNavigationDrawer(
                drawerState = drawerState,

                // 左側のページ一覧ドロワー
                drawerContent = {
                    PageListDrawer(
                        uiState = (uiState as? PageListUiState.Success),
                        selectedId = selectedId,
                        isEditMode = isEditMode,
                        listState = listState,
                        width = drawerWidth,
                        onSelect = { id -> viewModel.selectPage(id.toInt()); scope.launch { drawerState.close() } },
                        onDeleteRequest = { id -> viewModel.showDeleteDialog(id.toInt()) },
                        onAddRequest = { viewModel.showAddDialog() },
                        onToggleEditMode = { viewModel.toggleEditMode(); if (!isEditMode) scope.launch { drawerState.open() } }
                    )
                }
            ) {
                Scaffold(
                    // SnackbarHost を設定
                    snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
                    // 上部アプリバー
                    topBar = {
                        PageTopBar(
                            uiState = (uiState as? PageListUiState.Success),
                            selectedId = selectedId,
                            isPageEditing = isPageEditing,
                            // 編集モードに入る
                            onStartEdit = { page ->
                                editTitle = page.title; editBody = page.body; isPageEditing = true
                            },
                            // 編集キャンセル
                            onCancelEdit = { isPageEditing = false },
                            // 保存処理
                            onSaveEdit = { page ->
                                if (isSaveEnabled) { // ← 無効時は押さない
                                    viewModel.updatePage(
                                        page.copy(
                                            title = editTitle,
                                            body = editBody
                                        )
                                    )
                                    isPageEditing = false
                                }
                            },
                            onMenuClick = { scope.launch { drawerState.open() } },
                            isSaveEnabled = isSaveEnabled
                        )
                    }
                ) { contentPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(contentPadding)
                    ) {
                        // 本文エリア (表示／編集)
                        PageContentArea(
                            uiState = uiState,
                            selectedId = selectedId,
                            isPageEditing = isPageEditing,
                            editTitle = editTitle,
                            onEditTitleChange = { editTitle = it },
                            editBody = editBody,
                            onEditBodyChange = { editBody = it }
                        )

                        // 削除確認ダイアログ
                        ConfirmDeleteDialog(
                            showForId = showDeleteForId,
                            uiState = (uiState as? PageListUiState.Success),
                            onDismiss = { viewModel.dismissDeleteDialog() },
                            onConfirm = { viewModel.deletePage() }
                        )

                        // 追加ダイアログ
                        AddPageDialog(
                            show = showAddDialog,
                            title = newTitle,
                            onTitleChange = { viewModel.updateNewTitle(it) },
                            onConfirm = { viewModel.createPage() },
                            onDismiss = { viewModel.dismissAddDialog() }
                        )
                    }
                }
            }
        }
    }
}

/**
 * ページリスト用のスクロール挙動を管理するヘルパー
 * ドロワー開閉時や選択ページ変更時にリストを自動スクロール
 */
@Composable
private fun ScrollBehaviors(
    drawerState: DrawerState,
    selectedId: Int?,
    pages: List<Page>,
    listState: LazyListState
) {
    LaunchedEffect(drawerState.isOpen, selectedId) {
        if (drawerState.isOpen && pages.isNotEmpty()) {
            pages.indexOfFirst { it.id == selectedId }
                .takeIf { it >= 0 }
                ?.let { listState.animateScrollToItem(it) }
        }
    }
    LaunchedEffect(pages.size) {
        if (pages.isNotEmpty()) listState.animateScrollToItem(pages.lastIndex)
    }
}

/**
 * 左側に表示するナビゲーションドロワー
 * ページ一覧と「追加」「編集モード切替」ボタン
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PageListDrawer(
    uiState: PageListUiState.Success?,
    selectedId: Int?,
    isEditMode: Boolean,
    listState: LazyListState,
    width: Dp,
    onSelect: (Long) -> Unit,
    onDeleteRequest: (Long) -> Unit,
    onAddRequest: () -> Unit,
    onToggleEditMode: () -> Unit
) {
    ModalDrawerSheet(modifier = Modifier.width(width)) {
        Column(Modifier.fillMaxSize()) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(top = 20.dp, bottom = 8.dp)
            ) {
                uiState?.pages?.forEach { page ->
                    item(page.id!!) {
                        PageRowItem(
                            page = page,
                            isSelected = page.id == selectedId?.toInt(),
                            isEditMode = isEditMode,
                            onClick = { onSelect(page.id.toLong()) },
                            onDelete = { onDeleteRequest(page.id.toLong()) }
                        )
                        Divider()
                    }
                }
            }
            BottomDrawerActions(
                isEditMode = isEditMode,
                onAdd = onAddRequest,
                onToggleEdit = onToggleEditMode
            )
        }
    }
}

/**
 * ドロワー内の各ページ行アイテム
 * ページタイトル表示と削除アイコン（編集モード時）を持つ
 */
@Composable
private fun PageRowItem(
    page: Page,
    isSelected: Boolean,
    isEditMode: Boolean,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 56.dp)
            .clickable(onClick = onClick)
            .background(if (isSelected) Color(0xFFF5F8FA) else Color.Transparent)
            .padding(horizontal = 10.dp)
    ) {
        Text(
            text = page.title,
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp),
            color = if (isSelected) Color(0xFF4CB3F8) else MaterialTheme.colorScheme.onSurface,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
        if (isEditMode) {
            IconButton(onClick = onDelete) {
                Icon(
                    painter = painterResource(R.drawable.delete),
                    contentDescription = "delete",
                    tint = Color(0xFFB3B3B3),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

/**
 * ドロワー下部のアクションボタン群
 * 「新規追加」「完了」「編集切替」を表示
 */
@Composable
private fun BottomDrawerActions(
    isEditMode: Boolean,
    onAdd: () -> Unit,
    onToggleEdit: () -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
    ) {
        if (isEditMode) {
            AddNewPageButton(modifier = Modifier.weight(1f), onClick = onAdd)
            DoneButton(modifier = Modifier.weight(1f), onClick = onToggleEdit)
        } else {
            EditButton(onClick = onToggleEdit)
        }
    }
}

/**
 * アプリのトップバー。
 * メニュー、ロゴ、タイトル、及び編集／キャンセル／保存ボタンを表示
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PageTopBar(
    uiState: PageListUiState.Success?,
    selectedId: Int?,
    isPageEditing: Boolean,
    onStartEdit: (Page) -> Unit,
    onCancelEdit: () -> Unit,
    onSaveEdit: (Page) -> Unit,
    onMenuClick: () -> Unit,
    isSaveEnabled: Boolean
) {
    CenterAlignedTopAppBar(
        modifier = Modifier.padding(10.dp),
        navigationIcon = {
            IconButton(onClick = onMenuClick) {
                Icon(Icons.Default.Menu, contentDescription = null)
            }
        },
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.offset(x = (-20).dp)
            ) {
                Image(
                    painterResource(R.drawable.logo),
                    contentDescription = "Logo",
                    modifier = Modifier.size(32.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    "EditNote",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        actions = {
            val page = uiState?.pages?.firstOrNull { it.id?.toLong()?.toInt() == selectedId }
            if (page != null) {
                if (isPageEditing) {
                    CancelButton(onClick = onCancelEdit)
                    Spacer(Modifier.width(10.dp))
                    SaveButton(
                        enabled = isSaveEnabled,
                        onClick = { onSaveEdit(page) },
                        modifier = Modifier
                    )
                } else {
                    EditButton(
                        onClick = { onStartEdit(page) })
                }
            }
        }
    )
}

/**
 * 画面中央のコンテンツエリア。
 * ローディング／エラー／成功の各状態に応じて表示を切り替え
 */
@Composable
private fun PageContentArea(
    uiState: PageListUiState,
    selectedId: Int?,
    isPageEditing: Boolean,
    editTitle: String,
    onEditTitleChange: (String) -> Unit,
    editBody: String,
    onEditBodyChange: (String) -> Unit
) {
    when (uiState) {
        PageListUiState.Loading -> LoadingContent()
        is PageListUiState.Error -> ErrorContent(uiState.message)
        is PageListUiState.Success -> uiState.pages
            .firstOrNull { it.id == selectedId }
            ?.let { page ->
                if (isPageEditing) {
                    EditContent(
                        title = editTitle,
                        onTitleChange = onEditTitleChange,
                        body = editBody,
                        onBodyChange = onEditBodyChange
                    )
                } else {
                    DisplayContent(page = page)
                }
            }
    }
}

/** 読み込み中に表示するプログレスインジケーター*/
@Composable
private fun LoadingContent() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

/** エラー発生時に表示するエラーメッセージ */
@Composable
private fun ErrorContent(message: String) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = message, color = MaterialTheme.colorScheme.error)
    }
}

/**
 * 編集モード時に表示するコンテンツ
 * タイトル・本文の TextField を含む
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditContent(
    title: String,
    onTitleChange: (String) -> Unit,
    body: String,
    onBodyChange: (String) -> Unit
) {
    val titleLength = title.length
    val bodyLength = body.length
    val titleError = titleLength !in 1..50
    val bodyError = bodyLength !in 10..2000

    Column(
        Modifier
            .fillMaxSize()
            .padding(Spacing.md)
    ) {
        Surface(
            shape = RoundedCornerShape(Spacing.sm),
            color = EditNoteTheme.GreyBackground,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(Spacing.lg)
            ) {
                Surface(
                    shape = RoundedCornerShape(Spacing.sm),
                    color = EditNoteTheme.White,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = Spacing.sm)
                ) {
                    Column(Modifier.padding(Spacing.sm)) {
                        OutlinedTextField(
                            value = title,
                            onValueChange = onTitleChange,
                            label = { Text("タイトル") },
                            isError = titleError,
                            modifier = Modifier.fillMaxWidth(),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                containerColor = Color.Transparent
                            )
                        )
                        Text("${titleLength} / 50", style = MaterialTheme.typography.labelSmall)
                    }
                }

                Surface(
                    shape = RoundedCornerShape(Spacing.sm),
                    color = EditNoteTheme.White,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    Column(Modifier.padding(Spacing.sm)) {
                        OutlinedTextField(
                            value = body,
                            onValueChange = onBodyChange,
                            label = { Text("本文") },
                            isError = bodyError,
                            modifier = Modifier
                                .fillMaxSize()
                                .weight(1f),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                containerColor = Color.Transparent
                            )
                        )
                        Text("${bodyLength} / 2000", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }
    }
}


/**
 * 表示モード時に表示するコンテンツ
 * タイトルとスクロール可能な本文テキストを含む
 */
@Composable
private fun DisplayContent(page: Page) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(Spacing.md)
    ) {
        Surface(
            shape = RoundedCornerShape(Spacing.sm),
            color = EditNoteTheme.GreyBackground,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(Spacing.lg)
            ) {
                Text(
                    text = page.title,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(start = xl)
                )
                Spacer(Modifier.height(Spacing.sm))
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(EditNoteTheme.White, RoundedCornerShape(Spacing.sm))
                        .verticalScroll(rememberScrollState())
                        .padding(Spacing.sm)
                ) {
                    Text(text = page.body)
                }
            }
        }
    }
}

/**
 * 削除確認用ダイアログ
 * ユーザーに削除の最終確認を促す
 */
@Composable
private fun ConfirmDeleteDialog(
    showForId: Int?,
    uiState: PageListUiState.Success?,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    if (showForId != null) {
        val title = uiState?.pages?.firstOrNull { it.id == showForId }?.title.orEmpty()
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("「$title」を削除します", style = MaterialTheme.typography.titleMedium) },
            text = { Text("この操作は元に戻せません。よろしいですか？") },
            confirmButton = {
                TextButton(onClick = onConfirm) { Text("削除", color = Color.Red) }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) { Text("キャンセル") }
            }
        )
    }
}

/**
 * ページ追加用ダイアログ
 * タイトル入力フィールドを含む
 */
@Composable
private fun AddPageDialog(
    show: Boolean,
    title: String,
    onTitleChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    if (show) {
        val titleLength = title.length
        val isTitleError = titleLength !in 1..50

        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("新しいページを追加") },
            text = {
                Column {
                    OutlinedTextField(
                        value = title,
                        onValueChange = onTitleChange,
                        label = { Text("タイトル") },
                        isError = isTitleError
                    )
                    Text("${titleLength} / 50", style = MaterialTheme.typography.labelSmall)
                }
            },
            confirmButton = {
                TextButton(
                    onClick = onConfirm,
                    enabled = !isTitleError
                ) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) { Text("Cancel") }
            }
        )
    }
}


/**
 * 汎用ボタンコンポーネント群：
 * EditButton, CancelButton, SaveButton, DoneButton, AddNewPageButton
 * それぞれアイコン＋ラベルを持ったボタン
 */
@Composable
fun EditButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(cornerRadius),
        color = PrimaryBlue,
        modifier = modifier
            .width(buttonWidth)
            .height(buttonHeight)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .clickable(onClick = onClick)
                .padding(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.edit),
                contentDescription = "Edit",
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Edit",
                color = Color.White,
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

@Composable
fun CancelButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(cornerRadius),
        color = Color(0xFFB3B3B3),
        modifier = modifier
            .width(smallButtonWidth)
            .height(buttonHeight)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .clickable(onClick = onClick)
                .padding(horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.cancel),
                contentDescription = "Cancel",
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = "Cancel",
                color = Color.White,
                style = MaterialTheme.typography.labelSmall,
                maxLines = 1
            )
        }
    }
}

@Composable
fun SaveButton(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    val backgroundColor = if (enabled) PrimaryBlue else PrimaryBlue.copy(alpha = 0.3f)

    Surface(
        shape = RoundedCornerShape(cornerRadius),
        color = backgroundColor,
        modifier = modifier
            .width(smallButtonWidth)
            .height(buttonHeight)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .then(
                    if (enabled) Modifier.clickable(onClick = onClick)
                    else Modifier   // 無効時はクリック不許可
                )
                .padding(horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.save),
                contentDescription = "Save",
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = "Save",
                color = Color.White,
                style = MaterialTheme.typography.labelSmall,
                maxLines = 1
            )
        }
    }
}


@Composable
fun DoneButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(cornerRadius),
        color = PrimaryBlue,
        modifier = modifier
            .width(buttonWidth)
            .height(buttonHeight)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .clickable(onClick = onClick)
                .padding(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.done),
                contentDescription = "Done",
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = "Done",
                color = Color.White,
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

@Composable
fun AddNewPageButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(cornerRadius),
        color = Color.White,
        border = BorderStroke(borderWidth, PrimaryBlue),
        modifier = modifier
            .width(buttonWidth)
            .height(buttonHeight)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .clickable(onClick = onClick)
                .padding(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.add),
                contentDescription = "New Page",
                tint = PrimaryBlue,
                modifier = Modifier.size(16.dp)
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = "New Page",
                color = PrimaryBlue,
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}