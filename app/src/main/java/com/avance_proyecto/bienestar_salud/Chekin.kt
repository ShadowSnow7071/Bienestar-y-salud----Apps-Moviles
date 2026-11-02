// ⚠️ Cambia el nombre del paquete al de tu proyecto en Android Studio
package com.avance_proyecto.bienestar_salud

// Guarda lo que el usuario registra en el check-in diario
data class DatosCheckin(
    val sueno: String = "",     // Rango de horas de sueño seleccionado
    val energia: String = "",   // Nivel de energía seleccionado
    val animo: String = ""      // Estado de ánimo seleccionado
) {
    // Devuelve true solo si el usuario completó los tres campos antes de continuar
    fun estaCompleto() = sueno.isNotEmpty() && energia.isNotEmpty() && animo.isNotEmpty()
}

// Guarda el estado del panel de hábitos durante el día
data class EstadoPanel(
    val vasosAgua: Int = 0,                    // Cuántos vasos lleva el usuario
    val pausaHecha: Boolean = false,            // Si completó una pausa activa
    val hidratacionHecha: Boolean = false,      // Si marcó su meta de hidratación
    val concentracionHecha: Boolean = false,    // Si mantuvo concentración en una sesión
    val tareas: List<String> = listOf(         // Tareas pendientes del día
        "Tarea pendiente 1",
        "Tarea pendiente 2"
    )
) {
    // Agrega un vaso al contador; el límite es 8 vasos diarios
    fun agregarVaso() = if (vasosAgua < 8) copy(vasosAgua = vasosAgua + 1) else this
}

// OPCIONES DE CADA SELECTOR EN EL CHECK-IN ===

val opcionesSueno = listOf("-5 horas", "5-7 horas", "8-10 horas", "+10 horas")
val opcionesEnergia = listOf("Bajo", "Medio", "Alto")
val opcionesAnimo = listOf("Feliz", "Triste", "Enojado", "Aburrido", "Cansado")