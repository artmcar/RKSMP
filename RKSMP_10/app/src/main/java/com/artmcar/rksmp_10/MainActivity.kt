package com.artmcar.rksmp_10

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.artmcar.rksmp_10.ui.theme.RKSMP_10Theme
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import java.util.Locale
import android.Manifest
import android.annotation.SuppressLint
import androidx.annotation.RequiresPermission

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RKSMP_10Theme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    val context = LocalContext.current
    val fusedLocationClient =
        LocationServices.getFusedLocationProviderClient(context)
    var address by remember { mutableStateOf("") }
    var coordinates by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorText by remember { mutableStateOf("") }
    val permissionLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            val granted = permissions.values.all { it }
            @SuppressLint("MissingPermission")
            if (granted) {
                getLocation(
                    context = context,
                    fusedLocationClient = fusedLocationClient,
                    onLoading = { isLoading = it },
                    onSuccess = { addr, latLng ->
                        address = addr
                        coordinates = latLng
                    },
                    onError = { errorText = it }
                )
            } else {
                errorText = "Разрешение не предоставлено"
            }
        }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = {
                permissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        ) {
            Text("Получить мой адрес")
        }
        Spacer(modifier = Modifier.height(20.dp))
        if (isLoading) {
            CircularProgressIndicator()
        }
        if (address.isNotEmpty()) {
            Text(
                text = address,
                fontSize = 20.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(text = coordinates)
        }
        if (errorText.isNotEmpty()) {
            Text(
                text = errorText,
                color = Color.Red
            )
        }
    }
}
@RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
fun getLocation(
    context: Context,
    fusedLocationClient: FusedLocationProviderClient,
    onLoading: (Boolean) -> Unit,
    onSuccess: (String, String) -> Unit,
    onError: (String) -> Unit
) {
    onLoading(true)
    fusedLocationClient.getCurrentLocation(
        Priority.PRIORITY_HIGH_ACCURACY,
        null
    ).addOnSuccessListener { location ->
        if (location == null) {
            onLoading(false)
            onError("Не удалось получить местоположение")
            return@addOnSuccessListener
        }
        val latitude = location.latitude
        val longitude = location.longitude
        val geocoder =
            Geocoder(context.applicationContext, Locale.getDefault())
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            geocoder.getFromLocation(
                latitude,
                longitude,
                1,
                object : Geocoder.GeocodeListener {
                    override fun onGeocode(addresses: MutableList<Address>) {
                        if (addresses.isNotEmpty()) {
                            val address = addresses[0]
                            val fullAddress = """
                                ${address.thoroughfare ?: ""}
                                ${address.locality ?: ""}
                                ${address.countryName ?: ""}
                            """.trimIndent()
                            onSuccess(
                                fullAddress,
                                "Lat: %.5f, Lng: %.5f".format(latitude, longitude)
                            )
                        } else {
                            onError("Адрес не найден")
                        }
                        onLoading(false)
                    }
                    override fun onError(errorMessage: String?) {
                        onLoading(false)
                        onError(errorMessage ?: "Ошибка геокодирования")
                    }
                }
            )
        } else {
            try {
                val addresses =
                    geocoder.getFromLocation(latitude, longitude, 1)
                if (!addresses.isNullOrEmpty()) {
                    val address = addresses[0]
                    val fullAddress = """
                        ${address.thoroughfare ?: ""}
                        ${address.locality ?: ""}
                        ${address.countryName ?: ""}
                    """.trimIndent()
                    onSuccess(
                        fullAddress,
                        "Lat: %.5f, Lng: %.5f".format(latitude, longitude)
                    )
                } else {
                    onError("Адрес не найден")
                }
            } catch (e: Exception) {
                onError("Ошибка геокодирования")
            }
            onLoading(false)
        }
    }.addOnFailureListener {
        onLoading(false)
        onError("Ошибка получения координат")
    }
}
