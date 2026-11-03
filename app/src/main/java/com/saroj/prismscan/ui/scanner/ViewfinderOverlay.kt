package com.saroj.prismscan.ui.scanner

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.saroj.prismscan.ui.theme.ViewfinderBeamColor
import com.saroj.prismscan.ui.theme.ViewfinderSuccessColor

@Composable
fun ViewfinderOverlay(
    isSuccess: Boolean,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "viewfinder_laser")
    val laserPosition by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "laser_y"
    )

    val cornerPulse by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "corner_pulse"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        // Sizing the viewfinder box container
        val boxWidth = (canvasWidth * 0.72f).coerceIn(240.dp.toPx(), 360.dp.toPx())
        val boxHeight = boxWidth
        val left = (canvasWidth - boxWidth) / 2f
        val top = (canvasHeight - boxHeight) / 2.3f
        val right = left + boxWidth
        val bottom = top + boxHeight
        val cornerRadius = 24.dp.toPx()

        // Darkened scrim background with cut-out hole
        val scrimColor = Color(0x99000000)
        drawPath(
            path = Path().apply {
                addRect(Rect(0f, 0f, canvasWidth, canvasHeight))
                addRoundRect(
                    RoundRect(
                        rect = Rect(left, top, right, bottom),
                        cornerRadius = CornerRadius(cornerRadius, cornerRadius)
                    )
                )
                fillType = androidx.compose.ui.graphics.PathFillType.EvenOdd
            },
            color = scrimColor
        )

        // Viewfinder subtle border
        val borderColor = if (isSuccess) ViewfinderSuccessColor else Color(0x33FFFFFF)
        drawRoundRect(
            color = borderColor,
            topLeft = Offset(left, top),
            size = Size(boxWidth, boxHeight),
            cornerRadius = CornerRadius(cornerRadius, cornerRadius),
            style = Stroke(width = 2.dp.toPx())
        )

        // 4 Corner Brackets
        val bracketLength = 36.dp.toPx() * (if (isSuccess) 1.15f else cornerPulse)
        val bracketStroke = 4.dp.toPx()
        val bracketColor = if (isSuccess) ViewfinderSuccessColor else ViewfinderBeamColor

        // Top-Left Corner
        drawLine(
            color = bracketColor,
            start = Offset(left, top + cornerRadius),
            end = Offset(left, top + bracketLength),
            strokeWidth = bracketStroke,
            cap = StrokeCap.Round
        )
        drawLine(
            color = bracketColor,
            start = Offset(left + cornerRadius, top),
            end = Offset(left + bracketLength, top),
            strokeWidth = bracketStroke,
            cap = StrokeCap.Round
        )

        // Top-Right Corner
        drawLine(
            color = bracketColor,
            start = Offset(right, top + cornerRadius),
            end = Offset(right, top + bracketLength),
            strokeWidth = bracketStroke,
            cap = StrokeCap.Round
        )
        drawLine(
            color = bracketColor,
            start = Offset(right - cornerRadius, top),
            end = Offset(right - bracketLength, top),
            strokeWidth = bracketStroke,
            cap = StrokeCap.Round
        )

        // Bottom-Left Corner
        drawLine(
            color = bracketColor,
            start = Offset(left, bottom - cornerRadius),
            end = Offset(left, bottom - bracketLength),
            strokeWidth = bracketStroke,
            cap = StrokeCap.Round
        )
        drawLine(
            color = bracketColor,
            start = Offset(left + cornerRadius, bottom),
            end = Offset(left + bracketLength, bottom),
            strokeWidth = bracketStroke,
            cap = StrokeCap.Round
        )

        // Bottom-Right Corner
        drawLine(
            color = bracketColor,
            start = Offset(right, bottom - cornerRadius),
            end = Offset(right, bottom - bracketLength),
            strokeWidth = bracketStroke,
            cap = StrokeCap.Round
        )
        drawLine(
            color = bracketColor,
            start = Offset(right - cornerRadius, bottom),
            end = Offset(right - bracketLength, bottom),
            strokeWidth = bracketStroke,
            cap = StrokeCap.Round
        )

        // Scanning Laser Beam
        if (!isSuccess) {
            val laserY = top + (boxHeight * laserPosition)
            val laserGlowBrush = Brush.horizontalGradient(
                colors = listOf(
                    Color.Transparent,
                    ViewfinderBeamColor.copy(alpha = 0.8f),
                    Color.White,
                    ViewfinderBeamColor.copy(alpha = 0.8f),
                    Color.Transparent
                ),
                startX = left,
                endX = right
            )

            // Laser beam line
            drawLine(
                brush = laserGlowBrush,
                start = Offset(left + 8.dp.toPx(), laserY),
                end = Offset(right - 8.dp.toPx(), laserY),
                strokeWidth = 3.dp.toPx(),
                cap = StrokeCap.Round
            )

            // Laser beam soft gradient plume
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        ViewfinderBeamColor.copy(alpha = 0.25f),
                        Color.Transparent
                    ),
                    startY = laserY - 24.dp.toPx(),
                    endY = laserY
                ),
                topLeft = Offset(left + 8.dp.toPx(), laserY - 24.dp.toPx()),
                size = Size(boxWidth - 16.dp.toPx(), 24.dp.toPx())
            )
        }
    }
}
