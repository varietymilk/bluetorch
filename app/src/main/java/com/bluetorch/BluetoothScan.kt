package com.bluetorch

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Context.BLUETOOTH_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.getSystemService




/**
 * Prompts the user to enable Bluetooth via a system dialog.
 *
 * For Android 12+, [Manifest.permission.BLUETOOTH_CONNECT] is required to use
 * the [BluetoothAdapter.ACTION_REQUEST_ENABLE] intent.
 */
fun promptEnableBluetooth(
    context: Context,
    bluetoothAdapter: BluetoothAdapter,
    bluetoothEnablingResult: ActivityResultLauncher<Intent>
    ) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
        !context.hasPermission(Manifest.permission.BLUETOOTH_CONNECT)
    ) {
        // Insufficient permission to prompt for Bluetooth enabling
        return
    }
    if (!bluetoothAdapter.isEnabled) {
        val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        bluetoothEnablingResult.launch(intent)
    }
}

private fun startBleScan(context: Context) {
    if (!context.hasRequiredBluetoothPermissions()) {
        context.requestRelevantRuntimePermissions()
    } else {
        // TODO: Actually perform scan
    }
}

@Composable
fun BLEScreen() {
    val context = LocalContext.current
    val activity = context.findActivity()
    if (activity == null) {
        Text("Activity not available")
        return
    }
    var showDialog by remember { mutableStateOf(false) }
    var dialogMessage by remember { mutableStateOf("") }
    var permissionsToRequest by remember { mutableStateOf(emptyArray<String>()) }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        // Handle post-permission logic
        if (context.hasRequiredBluetoothPermissions()) {
            // Start BLE scan
        }
    }

    LaunchedEffect(Unit) {
        if (!context.hasRequiredBluetoothPermissions()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                dialogMessage = "Bluetooth permissions required to scan/connect BLE devices."
                permissionsToRequest = arrayOf(
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.BLUETOOTH_CONNECT
                )
            } else {
                dialogMessage = "Location permission required to scan BLE devices."
                permissionsToRequest = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
            }
            showDialog = true
        } else {
            // Start BLE scan
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { },
            confirmButton = {
                TextButton(onClick = {
                    launcher.launch(permissionsToRequest)
                    showDialog = false
                }) {
                    Text("OK")
                }
            },
            title = { Text("Permission Required") },
            text = { Text(dialogMessage) }
        )
    }

    // UI Content
    Text("BLE Scan Screen")
}

fun onRequestPermissionsResult(
    context: Context,
    requestCode: Int,
    permissions: Array<out String>,
    grantResults: IntArray
) {
    if (requestCode != PERMISSION_REQUEST_CODE) return

    val containsPermanentDenial = permissions.zip(grantResults.toTypedArray()).any {
        it.second == PackageManager.PERMISSION_DENIED &&
                !ActivityCompat.shouldShowRequestPermissionRationale(context as Activity, it.first)
    }
    val containsDenial = grantResults.any { it == PackageManager.PERMISSION_DENIED }
    val allGranted = grantResults.all { it == PackageManager.PERMISSION_GRANTED }
    when {
        containsPermanentDenial -> {
            // TODO: Handle permanent denial (e.g., show AlertDialog with justification)
            // Note: The user will need to navigate to App Settings and manually grant
            // permissions that were permanently denied
        }
        containsDenial -> {
            context.requestRelevantRuntimePermissions()
        }
        allGranted && context.hasRequiredBluetoothPermissions() -> {
            startBleScan(context)
        }
        else -> {
            // Unexpected scenario encountered when handling permissions
            (context as? Activity)?.recreate()
        }
    }
}
