package ru.kishko.calculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                Calculator()
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun Calculator() {
    var input by rememberSaveable { mutableStateOf("0") }
    var savedValue by rememberSaveable { mutableStateOf<Double?>(null) }
    var operation by rememberSaveable { mutableStateOf<String?>(null) }
    var newNumber by rememberSaveable { mutableStateOf(true) }

    fun clear() {
        input = "0"
        savedValue = null
        operation = null
        newNumber = true
    }

    fun calculate(first: Double, second: Double, op: String): Double? {
        val result = when (op) {
            "+" -> first + second
            "-" -> first - second
            "*" -> first * second
            "/" -> {
                if (second == 0.0) return null
                first / second
            }

            else -> return null
        }

        return result.takeIf { it.isFinite() }
    }

    fun format(value: Double): String {
        if (value == 0.0) return "0"

        return String.format(Locale.US, "%.10f", value)
            .trimEnd('0')
            .trimEnd('.')
    }

    fun enterDigit(digit: String) {
        if (input == "Ошибка") clear()

        input = when {
            newNumber -> digit
            input == "0" -> digit
            input.length < 15 -> input + digit
            else -> input
        }

        newNumber = false
    }

    fun enterComma() {
        if (input == "Ошибка") clear()

        if (newNumber) {
            input = "0."
            newNumber = false
        } else if (!input.contains(".")) {
            input += "."
        }
    }

    fun chooseOperation(op: String) {
        val value = input.toDoubleOrNull() ?: return

        if (operation != null && !newNumber) {
            val result = calculate(savedValue ?: value, value, operation!!)

            if (result == null) {
                input = "Ошибка"
                savedValue = null
                operation = null
                newNumber = true
                return
            }

            input = format(result)
            savedValue = result
        } else {
            savedValue = value
        }

        operation = op
        newNumber = true
    }

    fun calculateResult() {
        val first = savedValue ?: return
        val second = input.toDoubleOrNull() ?: return
        val op = operation ?: return
        val result = calculate(first, second, op)

        input = if (result == null) {
            "Ошибка"
        } else {
            format(result)
        }

        savedValue = null
        operation = null
        newNumber = true
    }

    fun clearEntry() {
        input = "0"
        newNumber = false
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .semantics {
                testTagsAsResourceId = true
            }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                verticalArrangement = Arrangement.Bottom
            ) {
                Text(
                    text = input.replace('.', ','),
                    modifier = Modifier
                        .testTag("result")
                        .fillMaxWidth()
                        .weight(1f),
                    fontSize = 42.sp,
                    textAlign = TextAlign.End,
                    maxLines = 1,
                    overflow = TextOverflow.Clip
                )

                Spacer(modifier = Modifier.height(8.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        CalculatorButton("C", Modifier.weight(1f), ::clear)
                        CalculatorButton("CE", Modifier.weight(1f), ::clearEntry)
                        CalculatorButton("/", Modifier.weight(1f)) {
                            chooseOperation("/")
                        }
                        CalculatorButton("×", Modifier.weight(1f)) {
                            chooseOperation("*")
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        CalculatorButton("7", Modifier.weight(1f)) { enterDigit("7") }
                        CalculatorButton("8", Modifier.weight(1f)) { enterDigit("8") }
                        CalculatorButton("9", Modifier.weight(1f)) { enterDigit("9") }
                        CalculatorButton("−", Modifier.weight(1f)) {
                            chooseOperation("-")
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        CalculatorButton("4", Modifier.weight(1f)) { enterDigit("4") }
                        CalculatorButton("5", Modifier.weight(1f)) { enterDigit("5") }
                        CalculatorButton("6", Modifier.weight(1f)) { enterDigit("6") }
                        CalculatorButton("+", Modifier.weight(1f)) {
                            chooseOperation("+")
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        CalculatorButton("1", Modifier.weight(1f)) { enterDigit("1") }
                        CalculatorButton("2", Modifier.weight(1f)) { enterDigit("2") }
                        CalculatorButton("3", Modifier.weight(1f)) { enterDigit("3") }
                        CalculatorButton(",", Modifier.weight(1f), ::enterComma)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        CalculatorButton("0", Modifier.weight(2f)) { enterDigit("0") }
                        CalculatorButton("=", Modifier.weight(2f), ::calculateResult)
                    }
                }
            }
        }
    }
}

@Composable
private fun CalculatorButton(
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .padding(vertical = 2.dp)
            .height(58.dp)
    ) {
        Text(
            text = label,
            fontSize = 20.sp,
            maxLines = 1
        )
    }
}
