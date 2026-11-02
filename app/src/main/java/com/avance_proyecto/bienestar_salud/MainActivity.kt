package com.avance_proyecto.bienestar_salud

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

// PALETA DE COLORES ===
val VerdeCabecera    = Color(0xFFB8D4A4)
val ChipSeleccionado = Color(0xFFB2EBF2)
val BotonPrincipal   = Color(0xFF00796B)
val FondoApp         = Color(0xFFF9F9F9)
val TextoPrincipal   = Color(0xFF212121)
val TextoChipActivo  = Color(0xFF004D5A)

enum class Pantalla { CHECKIN, PANEL }

// ACTIVITY PRINCIPAL ===
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { AppBienestar() }
    }
}

// CONTENEDOR PRINCIPAL ===
@Composable
fun AppBienestar() {
    var pantallaActual by remember { mutableStateOf(Pantalla.CHECKIN) }
    var datosCheckin   by remember { mutableStateOf(DatosCheckin()) }
    var estadoPanel    by remember { mutableStateOf(EstadoPanel()) }

    when (pantallaActual) {
        Pantalla.CHECKIN -> PantallaCheckin(
            datos     = datosCheckin,
            onCambio  = { datosCheckin = it },
            onEmpezar = { pantallaActual = Pantalla.PANEL }
        )
        Pantalla.PANEL -> PantallaPanel(
            datos         = datosCheckin,
            estado        = estadoPanel,
            onCambioPanel = { estadoPanel = it },
            onVolver      = { pantallaActual = Pantalla.CHECKIN }
        )
    }
}

// PANTALLA 1 — CHECK-IN DIARIO
@Composable
fun PantallaCheckin(
    datos: DatosCheckin,
    onCambio: (DatosCheckin) -> Unit,
    onEmpezar: () -> Unit
) {
    val fecha = SimpleDateFormat("EEEE d 'de' MMMM, yyyy", Locale("es", "MX"))
        .format(Calendar.getInstance().time)
        .replaceFirstChar { it.uppercase() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoApp)
    ) {
        // Cabecera — statusBarsPadding() + padding generoso para que no se sienta apretado
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(VerdeCabecera)
                .statusBarsPadding()
                .padding(horizontal = 24.dp, vertical = 24.dp)
        ) {
            Column {
                Text(
                    text       = "Buenos Días",
                    fontSize   = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color      = TextoPrincipal
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text     = fecha,
                    fontSize = 13.sp,
                    color    = Color(0xFF4A4A4A)
                )
            } }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            SelectorOpciones(icono       = "🌙",
                titulo      = "Horas de sueño",
                opciones    = opcionesSueno,
                seleccion   = datos.sueno,
                onSeleccion = { onCambio(datos.copy(sueno = it)) }
            )
            SelectorOpciones(icono       = "⚡",
                titulo      = "Nivel de energía",
                opciones    = opcionesEnergia,
                seleccion   = datos.energia,
                onSeleccion = { onCambio(datos.copy(energia = it)) }
            )
            SelectorOpciones(icono       = "😊",
                titulo      = "Estado de ánimo",
                opciones    = opcionesAnimo,
                seleccion   = datos.animo,
                onSeleccion = { onCambio(datos.copy(animo = it)) }
            )
        }

        // Botón fijo al fondo
        Button(
            onClick  = { onEmpezar() },
            enabled  = datos.estaCompleto(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp, vertical = 20.dp)
                .height(54.dp),
            shape  = RoundedCornerShape(30.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor         = BotonPrincipal,
                disabledContainerColor = Color(0xFFB0BEC5)
            )
        ) {
            Text(
                text       = "Empezar mi día",
                fontSize   = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color      = Color.White
            )
        } } }


// PANTALLA 2 — PANEL DE HÁBITOS
@Composable
fun PantallaPanel(
    datos: DatosCheckin,
    estado: EstadoPanel,
    onCambioPanel: (EstadoPanel) -> Unit,
    onVolver: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoApp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(VerdeCabecera)
                .statusBarsPadding()
                .padding(horizontal = 24.dp, vertical = 24.dp)
        ) {
            Text(text       = "TU DÍA DE HOY",
                fontSize   = 24.sp,
                fontWeight = FontWeight.Bold,
                color      = TextoPrincipal
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Resumen del check-in
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape    = RoundedCornerShape(14.dp),
                colors   = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Resumen de check-in", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                    Spacer(Modifier.height(10.dp))
                    FilaResumen(Icons.Outlined.Bedtime,            "Sueño",   datos.sueno)
                    FilaResumen(Icons.Outlined.Bolt,               "Energía", datos.energia)
                    FilaResumen(Icons.Outlined.SentimentSatisfied, "Ánimo",   datos.animo)
                }
            }

            // Contador de agua
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape    = RoundedCornerShape(14.dp),
                colors   = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Outlined.WaterDrop,
                            contentDescription = "Agua",
                            tint     = Color(0xFF1565C0),
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text       = "Vasos de agua: ${estado.vasosAgua} / 8",
                            fontWeight = FontWeight.SemiBold,
                            fontSize   = 15.sp
                        )
                    }
                    Button(
                        onClick        = { onCambioPanel(estado.agregarVaso()) },
                        colors         = ButtonDefaults.buttonColors(containerColor = BotonPrincipal),
                        shape          = RoundedCornerShape(20.dp),
                        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 6.dp)
                    ) {
                        Text("+1", fontSize = 15.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    } } }

            // Hábitos del día
            Column {
                Text("Hábitos del día", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(Modifier.height(6.dp))
                HabitoItem(Icons.Outlined.PauseCircle,   "Pausa activa",   estado.pausaHecha,        { onCambioPanel(estado.copy(pausaHecha = it)) })
                HabitoItem(Icons.Outlined.LocalDrink,    "Hidratación",    estado.hidratacionHecha,   { onCambioPanel(estado.copy(hidratacionHecha = it)) })
                HabitoItem(Icons.Outlined.Psychology,    "Concentración",  estado.concentracionHecha, { onCambioPanel(estado.copy(concentracionHecha = it)) })
            }

            // Tareas pendientes
            Column {
                Text("Tareas pendientes", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(Modifier.height(6.dp))
                estado.tareas.forEach { tarea ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        shape  = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF9C4))
                    ) {
                        Row(
                            modifier          = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Outlined.PushPin, contentDescription = null, tint = Color(0xFF795548), modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(tarea, fontSize = 14.sp)
                        }
                    } } } }

        // Botón de regreso fijo al fondo
        OutlinedButton(
            onClick  = onVolver,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp, vertical = 16.dp)
                .height(48.dp),
            shape  = RoundedCornerShape(30.dp),
            border = BorderStroke(1.5.dp, BotonPrincipal)
        ) {
            Icon(Icons.Outlined.ArrowBack, contentDescription = "Volver", tint = BotonPrincipal, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(6.dp))
            Text("Volver al check-in", color = BotonPrincipal, fontWeight = FontWeight.SemiBold)
        }
    } }

// COMPONENTES REUTILIZABLES
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SelectorOpciones(icono: String, titulo: String, opciones: List<String>,
    seleccion: String, onSeleccion: (String) -> Unit
) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(icono, fontSize = 20.sp)
            Spacer(Modifier.width(8.dp))
            Text(
                text       = titulo,
                fontWeight = FontWeight.SemiBold,
                fontSize   = 15.sp,
                color      = TextoPrincipal
            )
        }
        Spacer(Modifier.height(10.dp))
        FlowRow(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement   = Arrangement.spacedBy(8.dp)
        ) {
            opciones.forEach { opcion ->
                FilterChip(
                    selected = opcion == seleccion,
                    onClick  = { onSeleccion(opcion) },
                    label    = {
                        Text(
                            text     = opcion,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = ChipSeleccionado,
                        selectedLabelColor     = TextoChipActivo
                    )
                )
            } } } }

// Fila del resumen con ícono de Material
@Composable
fun FilaResumen(icono: ImageVector, etiqueta: String, valor: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier          = Modifier.padding(vertical = 4.dp)
    ) {
        Icon(icono, contentDescription = etiqueta, tint = BotonPrincipal, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(8.dp))
        Text("$etiqueta:  $valor", fontSize = 14.sp, color = TextoPrincipal)
    }
}

// Ítem de hábito con ícono de Material y tachado al completarse
@Composable
fun HabitoItem(icono: ImageVector, texto: String, marcado: Boolean, onCambio: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCambio(!marcado) }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked         = marcado,
            onCheckedChange = onCambio,
            colors          = CheckboxDefaults.colors(checkedColor = BotonPrincipal)
        )
        Icon(
            imageVector        = icono,
            contentDescription = texto,
            tint               = if (marcado) Color(0xFFBDBDBD) else BotonPrincipal,
            modifier           = Modifier.size(20.dp)
        )
        Spacer(Modifier.width(10.dp))
        Text(
            text           = texto,
            fontSize       = 15.sp,
            color          = if (marcado) Color(0xFFBDBDBD) else TextoPrincipal,
            textDecoration = if (marcado) TextDecoration.LineThrough else TextDecoration.None
        )
    } }