package com.oza.editnote.ui.theme

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.oza.editnote.R

// 日本語用フォントファミリー：
// Noto Sans JP の各ウェイトを登録
val NotoSansJP = FontFamily(
    // 通常ウェイト（400）
    Font(R.font.noto_sans_jp_regular, FontWeight.Normal),
    // 中間ウェイト（500）
    Font(R.font.noto_sans_jp_medium,  FontWeight.Medium),
    // 太字ウェイト（700）
    Font(R.font.noto_sans_jp_bold,    FontWeight.Bold)
)
