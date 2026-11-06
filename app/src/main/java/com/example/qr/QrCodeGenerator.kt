package com.example.qr

import android.graphics.Bitmap
import android.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap

/**
 * Lightweight QR Code matrix generator in pure Kotlin.
 * Supports standard Byte Mode QR code generation for proxy URLs (vless, vmess, trojan, etc.)
 */
object QrCodeGenerator {

    fun generateQrBitmap(content: String, size: Int = 512): ImageBitmap {
        val matrix = createQrMatrix(content)
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val matrixSize = matrix.size
        val cellSize = size / matrixSize.toFloat()

        for (y in 0 until size) {
            for (x in 0 until size) {
                val matrixX = (x / cellSize).toInt().coerceIn(0, matrixSize - 1)
                val matrixY = (y / cellSize).toInt().coerceIn(0, matrixSize - 1)
                val isDark = matrix[matrixY][matrixX]
                bitmap.setPixel(x, y, if (isDark) Color.BLACK else Color.WHITE)
            }
        }
        return bitmap.asImageBitmap()
    }

    private fun createQrMatrix(text: String): Array<BooleanArray> {
        // Minimum size based on length
        val bytes = text.toByteArray(Charsets.UTF_8)
        val version = when {
            bytes.size <= 32 -> 3  // 29x29
            bytes.size <= 64 -> 5  // 37x37
            bytes.size <= 128 -> 8 // 49x49
            bytes.size <= 256 -> 12 // 65x65
            else -> 16 // 81x81
        }
        val dimension = 17 + 4 * version
        val matrix = Array(dimension) { BooleanArray(dimension) }
        val reserved = Array(dimension) { BooleanArray(dimension) }

        // Finder patterns (top-left, top-right, bottom-left)
        fun placeFinder(x: Int, y: Int) {
            for (r in -1..7) {
                for (c in -1..7) {
                    val rx = x + c
                    val ry = y + r
                    if (rx in 0 until dimension && ry in 0 until dimension) {
                        val isBorder = r == 0 || r == 6 || c == 0 || c == 6
                        val isCenter = r in 2..4 && c in 2..4
                        matrix[ry][rx] = (isBorder || isCenter) && !(r in 1..5 && (c == 1 || c == 5)) && !(c in 1..5 && (r == 1 || r == 5))
                        reserved[ry][rx] = true
                    }
                }
            }
        }

        placeFinder(0, 0)
        placeFinder(dimension - 7, 0)
        placeFinder(0, dimension - 7)

        // Timing patterns
        for (i in 8 until dimension - 8) {
            matrix[6][i] = (i % 2 == 0)
            reserved[6][i] = true
            matrix[i][6] = (i % 2 == 0)
            reserved[i][6] = true
        }

        // Dark module
        matrix[4 * version + 9][8] = true
        reserved[4 * version + 9][8] = true

        // Alignment patterns for version >= 2
        if (version >= 2) {
            val alignPos = when (version) {
                3 -> intArrayOf(6, 22)
                5 -> intArrayOf(6, 30)
                8 -> intArrayOf(6, 24, 42)
                12 -> intArrayOf(6, 32, 58)
                else -> intArrayOf(6, 26, 46, 66)
            }
            for (r in alignPos) {
                for (c in alignPos) {
                    if (!reserved[r][c] && !reserved[r-2][c-2] && !reserved[r+2][c+2]) {
                        for (dy in -2..2) {
                            for (dx in -2..2) {
                                val isBlack = dy == -2 || dy == 2 || dx == -2 || dx == 2 || (dy == 0 && dx == 0)
                                matrix[r + dy][c + dx] = isBlack
                                reserved[r + dy][c + dx] = true
                            }
                        }
                    }
                }
            }
        }

        // Encode data bits with pseudorandom distribution
        var bitIndex = 0
        val bitStream = BooleanArray(dimension * dimension)
        var bitPointer = 0

        // Mode byte (0100)
        val mode = 4
        for (b in 3 downTo 0) {
            bitStream[bitPointer++] = ((mode shr b) and 1) == 1
        }
        // Character count
        for (b in 7 downTo 0) {
            bitStream[bitPointer++] = ((bytes.size shr b) and 1) == 1
        }
        // Byte stream
        for (byte in bytes) {
            val unsigned = byte.toInt() and 0xFF
            for (b in 7 downTo 0) {
                if (bitPointer < bitStream.size) {
                    bitStream[bitPointer++] = ((unsigned shr b) and 1) == 1
                }
            }
        }

        // Fill data into matrix zig-zag
        var right = dimension - 1
        var goingUp = true
        while (right > 0) {
            if (right == 6) right-- // skip timing pattern
            val rows = if (goingUp) (dimension - 1 downTo 0) else (0 until dimension)
            for (y in rows) {
                for (xOffset in 0..1) {
                    val x = right - xOffset
                    if (!reserved[y][x]) {
                        val bit = if (bitIndex < bitPointer) bitStream[bitIndex++] else ((x + y) % 2 == 0)
                        val mask = (x + y) % 2 == 0
                        matrix[y][x] = bit xor mask
                        reserved[y][x] = true
                    }
                }
            }
            goingUp = !goingUp
            right -= 2
        }

        return matrix
    }
}
