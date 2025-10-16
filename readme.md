[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-22041afd0340ce965d47ae6ef1cefeee28c7c493a6346c4f15d667ab976d596c.svg)](https://classroom.github.com/a/jKxTzPAY)
# 📝 Laboratorio 2: Implementación de lógica en calculadora usando Kotlin y Jetpack

## 👤 Datos del Estudiante

**Completa la siguiente información antes de comenzar:**

- **Nombre completo**: [Nombre del estudiante]
- **Carrera**: [Carrera del estudianete]

---

## Objetivo

El objetivo de este taller e que los estudiantes implementen la lógica de la calculadora desarrolada en el laboratorio 1, que cumpla los siguientes requisitos:
- Una pantalla de texto que muestre los números ingresados.
- Una cuadrícula de botones con los dígitos y operadores.
- Creación de botones 'AC', 'C'
- Manejo básico de estado con remember y mutableStateOf.
- Implementación de la lógica en los botones de la calculadora

**Se adjunta vista previa de la calculadora**

<img src="readme-img/calc.png" width="300px">

## Instrucciones

### 🏗️ Estructura del Proyecto Android

Antes de comenzar, es importante entender la estructura básica de un proyecto Android:

```
Calculator/
├── app/
│   ├── build.gradle.kts          # Configuración de dependencias de la app
│   ├── src/
│   │   ├── main/
│   │   │   ├── AndroidManifest.xml      # Configuración de la aplicación
│   │   │   ├── java/ec/edu/uisek/calculator/
│   │   │   │   ├── MainActivity.kt      # Actividad principal
|   |   |   |   ├── CalculatorScreen.kt
│   │   │   │   └── ui/theme/           # Archivos de tema y estilo
│   │   │   └── res/                    # Recursos (layouts, strings, colores)
│   │   ├── test/                       # Tests unitarios
│   │   └── androidTest/                # Tests de integración
│   └── proguard-rules.pro
├── build.gradle.kts               # Configuración del proyecto
└── gradle/                        # Archivos de Gradle
```

**Archivos clave para este laboratorio:**
- **`MainActivity.kt`**: Punto de entrada de la aplicación donde configuraremos Compose
- **`ui/theme/`**: Contiene los archivos de tema (colores, tipografía, formas)
- **`build.gradle.kts`**: Contiene las dependencias de Jetpack Compose

### 1️⃣ Modificación de la cuadrícula de botones
Antes de empezar, asegúrate de que tu módulo `app` incluye la dependencia de integración entre Lifecycle ViewModel y Compose. Abre `app/build.gradle.kts` y agrega la siguiente línea dentro del bloque `dependencies` (si aún no existe):

```kotlin
implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2")
```

Luego sincroniza el proyecto (Gradle sync) para poder usar `viewModel()` en tus `@Composable`.

Sigue estos pasos para adaptar la UI a la nueva arquitectura (UI desacoplada de la lógica mediante ViewModel):

1) Inyecta el `ViewModel` en `CalculatorScreen` y muestra el estado

   - Abre `CalculatorScreen.kt` y modifica el `Composable` principal para recibir (o crear) el `CalculatorViewModel` usando `viewModel()`.
   - En lugar de mantener el texto en estado local, muestra `state.display` que proviene del `ViewModel`.

   Ejemplo:

   ```kotlin
   // importa: import androidx.lifecycle.viewmodel.compose.viewModel
   @Composable
   fun CalculatorScreen(
       viewModel: CalculatorViewModel = viewModel()
   ) {
       val state = viewModel.state

       Column(modifier = Modifier.fillMaxSize()) {
           Text(
               text = state.display,
               modifier = Modifier.fillMaxWidth(),
               textAlign = TextAlign.End,
               fontSize = 56.sp
           )

           // Pasamos una función que envía eventos al ViewModel
           CalculatorGrid(onEvent = viewModel::onEvent)
       }
   }
   ```

2) Cambia `CalculatorGrid` para enviar eventos al `ViewModel`

   - Modifica la firma para aceptar `onEvent: (CalculatorEvent) -> Unit`.
   - Mapea cada etiqueta de botón a un `CalculatorEvent` antes de llamar a `onEvent(...)`.
   - Mantén los elementos especiales `AC` y `C` (usa `item(span = { GridItemSpan(2) })` para `AC`).

   Ejemplo:

   ```kotlin
   @Composable
   fun CalculatorGrid(onEvent: (CalculatorEvent) -> Unit) {
       val buttons = listOf(
           "7", "8", "9", "÷",
           "4", "5", "6", "×",
           "1", "2", "3", "−",
           "0", ".", "=", "+"
       )

       LazyVerticalGrid(columns = GridCells.Fixed(4)) {
           items(buttons.size) { index ->
               val label = buttons[index]
               CalculatorButton(label = label) {
                   when (label) {
                       in "0".."9" -> onEvent(CalculatorEvent.Number(label))
                       "." -> onEvent(CalculatorEvent.Decimal)
                       "=" -> onEvent(CalculatorEvent.Calculate)
                       else -> onEvent(CalculatorEvent.Operator(label))
                   }
               }
           }

           item(span = { GridItemSpan(2) }) { CalculatorButton(label = "AC") { onEvent(CalculatorEvent.AllClear) } }
           item {}
           item { CalculatorButton(label = "C") { onEvent(CalculatorEvent.Clear) } }
       }
   }
   ```

3) Asegura la existencia de los tipos auxiliares (ejemplo mínimo)

   - Para que lo anterior compile necesitarás un `sealed class` con los eventos y un estado simple. Pide a los estudiantes que creen (o verifiquen) estos elementos en `CalculatorViewModel.kt`:

   ```kotlin
   // eventos
   sealed class CalculatorEvent {
       data class Number(val value: String): CalculatorEvent()
       object Decimal: CalculatorEvent()
       data class Operator(val op: String): CalculatorEvent()
       object Calculate: CalculatorEvent()
       object Clear: CalculatorEvent()
       object AllClear: CalculatorEvent()
   }

   // estado
   data class CalculatorState(val display: String = "")
   ```

4) Firma mínima del ViewModel

   - El `ViewModel` debe exponer `state: CalculatorState` y una función `onEvent(event: CalculatorEvent)` que procese la lógica y actualice `state`.

   Ejemplo de firmas que los estudiantes pueden usar:

   ```kotlin
   class CalculatorViewModel : ViewModel() {
       var state by mutableStateOf(CalculatorState())
           private set

       fun onEvent(event: CalculatorEvent) {
           // aquí implementas la lógica: números, operadores, clear, all clear, calcular, etc.
       }
   }
   ```

5) Prueba rápida en Preview / Activity

   - En `MainActivity` o en la función `@Preview` llama a `CalculatorScreen()`; Compose inyectará el ViewModel por defecto.

Con estos pasos el estudiante habrá transformado la cuadrícula para que la UI solo represente el estado y el `ViewModel` maneje la lógica de la calculadora.

### 2️⃣ Implementación: crear `CalculatorViewModel.kt` paso a paso

En esta sección vemos cómo crear el archivo `CalculatorViewModel.kt` desde cero. Sigue los pasos y copia el código de ejemplo para tener una implementación funcional que puedas estudiar y mejorar.

Ruta recomendada: `app/src/main/java/ec/edu/uisek/calculator/CalculatorViewModel.kt`

1) Crear la clase de estado

   - Crea una `data class` llamada `CalculatorState` que contenga solo lo que la UI necesita leer. En este ejercicio solo necesitamos `display: String`.

   ```kotlin
   data class CalculatorState(
       val display: String = "0"
   )
   ```

2) Definir los eventos de la UI

   - Crea un `sealed class` `CalculatorEvent` con las acciones que el usuario puede realizar: ingresar número, operador, decimal, calcular, borrar último y borrar todo.

   ```kotlin
   sealed class CalculatorEvent {
       data class Number(val number: String) : CalculatorEvent()
       data class Operator(val operator: String) : CalculatorEvent()
       object Clear : CalculatorEvent()
       object AllClear : CalculatorEvent()
       object Calculate : CalculatorEvent()
       object Decimal : CalculatorEvent()
   }
   ```

3) Implementar el `ViewModel`

   - El `ViewModel` mantiene variables internas para los operandos (`number1`, `number2`) y el `operator`.
   - Expone `state` como `mutableStateOf(CalculatorState())` para que Compose observe los cambios.
   - Implementa `onEvent(event: CalculatorEvent)` que enruta los eventos a funciones privadas que realizan la lógica.

4) Código completo de referencia (implementación funcional)

   - Copia el siguiente código en `CalculatorViewModel.kt`. Está comentado y listo para que lo analices y lo pruebes.

```kotlin
package ec.edu.uisek.calculator

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

// 1. Clase de estado: no cambia, la UI solo necesita mostrar un valor.
data class CalculatorState(
    val display: String = "0" // Empezamos con "0" en pantalla
)

// 2. Eventos: no cambian, siguen siendo las acciones del usuario.
sealed class CalculatorEvent {
    data class Number(val number: String) : CalculatorEvent()
    data class Operator(val operator: String) : CalculatorEvent()
    object Clear : CalculatorEvent()
    object AllClear : CalculatorEvent()
    object Calculate : CalculatorEvent()
    object Decimal : CalculatorEvent()
}

// 3. El ViewModel: el cerebro de la calculadora (VERSIÓN MEJORADA)
class CalculatorViewModel : ViewModel() {

    // --- Estado Interno del ViewModel (la lógica) ---
    private var number1: String = ""
    private var number2: String = ""
    private var operator: String? = null

    // --- Estado que observa la UI ---
    var state by mutableStateOf(CalculatorState())
        private set

    // El "router" de eventos, no cambia.
    fun onEvent(event: CalculatorEvent) {
        when (event) {
            is CalculatorEvent.Number -> enterNumber(event.number)
            is CalculatorEvent.Operator -> enterOperator(event.operator)
            is CalculatorEvent.Decimal -> enterDecimal()
            is CalculatorEvent.AllClear -> clearAll()
            is CalculatorEvent.Clear -> clearLast() // Lo mejoramos para que sea más inteligente
            is CalculatorEvent.Calculate -> performCalculation()
        }
    }

    private fun enterNumber(number: String) {
        if (operator == null) { // Estamos introduciendo el primer número
            number1 += number
            state = state.copy(display = number1)
        } else { // Estamos introduciendo el segundo número
            number2 += number
            state = state.copy(display = number2)
        }
    }

    private fun enterOperator(op: String) {
        // Si ya hay un número, asignamos el operador
        if (number1.isNotBlank()) {
            operator = op
        }
    }

    private fun enterDecimal() {
        val currentNumber = if (operator == null) number1 else number2
        if (!currentNumber.contains(".")) {
            if (operator == null) {
                number1 += "."
                state = state.copy(display = number1)
            } else {
                number2 += "."
                state = state.copy(display = number2)
            }
        }
    }

    private fun performCalculation() {
        val num1 = number1.toDoubleOrNull()
        val num2 = number2.toDoubleOrNull()

        if (num1 != null && num2 != null && operator != null) {
            val result = when (operator) {
                "+" -> num1 + num2
                "−" -> num1 - num2
                "×" -> num1 * num2
                "÷" -> if (num2 != 0.0) num1 / num2 else Double.NaN // Manejar división por cero
                else -> 0.0
            }

            // Preparamos para la siguiente operación
            clearAll()
            // Mostramos el resultado y lo guardamos como el primer número de la siguiente posible operación
            val resultString = if (result.isNaN()) "Error" else result.toString().removeSuffix(".0")
            number1 = if (result.isNaN()) "" else resultString
            state = state.copy(display = resultString)
        }
    }

    private fun clearLast() {
        // Borra el último dígito del número que se está escribiendo
        if (operator == null) {
            if (number1.isNotBlank()) {
                number1 = number1.dropLast(1)
                state = state.copy(display = if (number1.isBlank()) "0" else number1)
            }
        } else {
            if (number2.isNotBlank()) {
                number2 = number2.dropLast(1)
                state = state.copy(display = if (number2.isBlank()) "0" else number2)
            } else {
                // Si no hay segundo número, borramos el operador
                operator = null
                state = state.copy(display = number1)
            }
        }
    }

    private fun clearAll() {
        number1 = ""
        number2 = ""
        operator = null
        state = state.copy(display = "0")
    }
}
```

5) Recomendaciones y extensiones

   - Añade validaciones adicionales (longitudes máximas, formato) para evitar entradas inválidas.
   - Implementa tests unitarios que creen el ViewModel y llamen a `onEvent` con secuencias (ej.: 1,2,+,3,=) y verifiquen `state.display`.
   - Si quieres soporte avanzado, implementa precedencia de operadores o una pila de operandos.

Con esto tienes una implementación completa y comentada del `ViewModel` que los estudiantes pueden copiar, ejecutar y modificar.



### 4️⃣ Puntos clave que deben revisar los estudiantes
- Uso de dp para dimensiones y sp para texto.
- Uso de remember { mutableStateOf("") } para mantener el estado del TextField.
- Uso de it en lambdas como parámetro implícito.
- Creación de un layout con Column y LazyVerticalGrid para organizar pantalla + botones.

### 5️⃣ Objetivo del ejercicio
Al finalizar, cada estudiante debería poder:
- Ver un TextField en la parte superior que refleja el texto ingresado.
- Ver una cuadrícula de 4×4 botones debajo del TextField.
- Interactuar con los botones y ver cómo se actualiza la pantalla.
- Controlar la calculadora mediante un viewmodel


### 6️⃣ Datos del docente

Para cualquier inquietud de este ejercicio puedes contactar al docente

- UISEK - Google Chat: pablo.perez@uisek.edu.ec
- PUCE - Microsoft Teams: paperez@puce.edu.ec
