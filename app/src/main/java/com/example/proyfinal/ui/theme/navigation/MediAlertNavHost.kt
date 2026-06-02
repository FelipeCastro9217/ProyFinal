package com.example.proyfinal.ui.theme.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.proyfinal.ui.theme.screens.*
import com.example.proyfinal.viewmodel.AuthViewModel
import com.example.proyfinal.viewmodel.HistorialViewModel
import com.example.proyfinal.viewmodel.MedicamentoViewModel

object Rutas {
    const val LOGIN        = "login"
    const val REGISTRO     = "registro"
    const val MEDICAMENTOS = "medicamentos"
    const val AGREGAR      = "agregar"
    const val HISTORIAL    = "historial"
    const val PERFIL       = "perfil"
}

@Composable
fun MediAlertNavHost(navController: NavHostController) {

    val authVM: AuthViewModel        = viewModel()
    val medVM:  MedicamentoViewModel = viewModel()
    val histVM: HistorialViewModel   = viewModel()

    val startDestination = if (authVM.usuarioActual != null) Rutas.MEDICAMENTOS else Rutas.LOGIN

    NavHost(navController = navController, startDestination = startDestination) {

        composable(Rutas.LOGIN) {
            LoginScreen(
                authViewModel  = authVM,
                onLoginExitoso = {
                    navController.navigate(Rutas.MEDICAMENTOS) {
                        popUpTo(Rutas.LOGIN) { inclusive = true }
                    }
                },
                onIrARegistro  = {
                    authVM.resetError()          // limpia error antes de cambiar pantalla
                    navController.navigate(Rutas.REGISTRO)
                }
            )
        }

        composable(Rutas.REGISTRO) {
            RegistroScreen(
                authViewModel     = authVM,
                onRegistroExitoso = {
                    navController.navigate(Rutas.MEDICAMENTOS) {
                        popUpTo(Rutas.LOGIN) { inclusive = true }
                    }
                },
                onVolver = {
                    authVM.resetError()          // limpia error antes de volver a Login
                    navController.popBackStack()
                }
            )
        }

        composable(Rutas.MEDICAMENTOS) {
            val userId = authVM.usuarioActual?.uid ?: ""
            MedicamentosScreen(
                medicamentoViewModel = medVM,
                userId               = userId,
                onAgregarClick       = { navController.navigate(Rutas.AGREGAR) },
                onHistorialClick     = { navController.navigate(Rutas.HISTORIAL) },
                onPerfilClick        = { navController.navigate(Rutas.PERFIL) },
                onCerrarSesion       = {
                    authVM.cerrarSesion()
                    navController.navigate(Rutas.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Rutas.AGREGAR) {
            val userId = authVM.usuarioActual?.uid ?: ""
            AgregarMedicamentoScreen(
                medicamentoViewModel = medVM,
                userId    = userId,
                onGuardado = { navController.popBackStack() },
                onVolver   = { navController.popBackStack() }
            )
        }

        composable(Rutas.HISTORIAL) {
            HistorialScreen(
                historialViewModel = histVM,
                onVolver = { navController.popBackStack() }
            )
        }

        composable(Rutas.PERFIL) {
            PerfilScreen(
                userEmail = authVM.usuarioActual?.email ?: "",
                onVolver  = { navController.popBackStack() }
            )
        }
    }
}
