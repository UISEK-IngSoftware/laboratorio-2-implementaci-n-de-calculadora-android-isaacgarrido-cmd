package ec.edu.uisek.calculator

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight

@Composable
fun CalculatorScreen() {
    // Estado local usando remember y mutableStateOf
    var display by remember { mutableStateOf("0") }
    var number1 by remember { mutableStateOf("") }
    var number2 by remember { mutableStateOf("") }
    var operator by remember { mutableStateOf<String?>(null) }

    // Funciones de lógica de la calculadora
    fun enterNumber(number: String, num1: String, num2: String, op: String?, updateState: (String, String, String) -> Unit) {
        if (op == null) {
            val newNum1 = num1 + number
            updateState(newNum1, num2, newNum1)
        } else {
            val newNum2 = num2 + number
            updateState(num1, newNum2, newNum2)
        }
    }

    fun enterOperator(op: String, num1: String, updateState: (String?) -> Unit) {
        if (num1.isNotBlank()) {
            updateState(op)
        }
    }

    fun enterDecimal(num1: String, num2: String, op: String?, updateState: (String, String, String) -> Unit) {
        val currentNumber = if (op == null) num1 else num2
        if (!currentNumber.contains(".")) {
            if (op == null) {
                val newNum1 = num1 + "."
                updateState(newNum1, num2, newNum1)
            } else {
                val newNum2 = num2 + "."
                updateState(num1, newNum2, newNum2)
            }
        }
    }

    fun performCalculation(num1: String, num2: String, op: String?, updateState: (String, String, String?, String) -> Unit) {
        val n1 = num1.toDoubleOrNull()
        val n2 = num2.toDoubleOrNull()

        if (n1 != null && n2 != null && op != null) {
            val result = when (op) {
                "+" -> n1 + n2
                "−" -> n1 - n2
                "×" -> n1 * n2
                "÷" -> if (n2 != 0.0) n1 / n2 else Double.NaN
                else -> 0.0
            }

            val resultString = if (result.isNaN()) "Error" else result.toString().removeSuffix(".0")
            val newNum1 = if (result.isNaN()) "" else resultString
            updateState(newNum1, "", null, resultString)
        }
    }

    fun clearLast(num1: String, num2: String, op: String?, updateState: (String, String, String?, String) -> Unit) {
        if (op == null) {
            if (num1.isNotBlank()) {
                val newNum1 = num1.dropLast(1)
                updateState(newNum1, num2, op, if (newNum1.isBlank()) "0" else newNum1)
            }
        } else {
            if (num2.isNotBlank()) {
                val newNum2 = num2.dropLast(1)
                updateState(num1, newNum2, op, if (newNum2.isBlank()) "0" else newNum2)
            } else {
                updateState(num1, num2, null, num1)
            }
        }
    }

    fun clearAll(updateState: (String, String, String?, String) -> Unit) {
        updateState("", "", null, "0")
    }

    // Función para manejar clics en botones
    val onButtonClick: (String) -> Unit = { label ->
        when (label) {
            in "0".."9" -> enterNumber(label, number1, number2, operator) { newNum1, newNum2, newDisplay ->
                number1 = newNum1
                number2 = newNum2
                display = newDisplay
            }
            "." -> enterDecimal(number1, number2, operator) { newNum1, newNum2, newDisplay ->
                number1 = newNum1
                number2 = newNum2
                display = newDisplay
            }
            "=" -> performCalculation(number1, number2, operator) { newNum1, newNum2, newOp, newDisplay ->
                number1 = newNum1
                number2 = newNum2
                operator = newOp
                display = newDisplay
            }
            "AC" -> clearAll { newNum1, newNum2, newOp, newDisplay ->
                number1 = newNum1
                number2 = newNum2
                operator = newOp
                display = newDisplay
            }
            "C" -> clearLast(number1, number2, operator) { newNum1, newNum2, newOp, newDisplay ->
                number1 = newNum1
                number2 = newNum2
                operator = newOp
                display = newDisplay
            }
            else -> enterOperator(label, number1) { newOp ->
                operator = newOp
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = display,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            textAlign = TextAlign.End,
            fontSize = 56.sp,
            color = Color.White
        )

        CalculatorGrid(onButtonClick = onButtonClick)
    }
}

@Composable
fun CalculatorGrid(onButtonClick: (String) -> Unit) {
    val buttons = listOf(
        "7", "8", "9", "÷",
        "4", "5", "6", "×",
        "1", "2", "3", "−",
        "0", ".", "=", "+"
    )

    LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(buttons.size) { index ->
            val label = buttons[index]
            CalculatorButton(label = label, onClick = { onButtonClick(label) })
        }

        item(span = { GridItemSpan(2) }) { CalculatorButton(label = "AC", onClick = { onButtonClick("AC") }) }
        item {}
        item { CalculatorButton(label = "C", onClick = { onButtonClick("C") }) }
    }
}

@Composable
fun CalculatorButton(label: String, onClick: () -> Unit) {
    val buttonColor = when (label) {
        in "0".."9", "." -> Color(0xFF00BCD4) // Celeste/Blue for numbers and decimal
        "+", "−", "×", "÷", "=" -> Color(0xFF9C27B0) // Purple for operations
        "AC", "C" -> Color(0xFFB71C1C) // Dark red for clear buttons
        else -> Color.DarkGray
    }

    Box(
        modifier = Modifier
            .size(80.dp) // Fixed size for all buttons
            .background(buttonColor, shape = androidx.compose.foundation.shape.CircleShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}