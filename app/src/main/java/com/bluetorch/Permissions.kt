    package com.bluetorch

    import android.Manifest
    import android.app.Activity
    import android.app.AlertDialog
    import android.content.Context
    import android.content.pm.PackageManager
    import android.os.Build
    import androidx.annotation.RequiresApi
    import androidx.core.app.ActivityCompat
    import androidx.core.content.ContextCompat

    fun Context.hasPermission(permissionType: String): Boolean {
        return ContextCompat.checkSelfPermission(this, permissionType) == PackageManager.PERMISSION_GRANTED
    }

    fun Context.hasRequiredBluetoothPermissions(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            hasPermission(Manifest.permission.BLUETOOTH_SCAN) &&
                    hasPermission(Manifest.permission.BLUETOOTH_CONNECT)
        } else {
            hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    fun Context.requestRelevantRuntimePermissions() {
        if (hasRequiredBluetoothPermissions()) return
        when {
            Build.VERSION.SDK_INT < Build.VERSION_CODES.S -> {
                requestLocationPermission()
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                requestBluetoothPermissions()
            }
        }
    }
    const val PERMISSION_REQUEST_CODE = 1


    fun Context.requestLocationPermission() {
        val activity = this as? Activity ?: return
        activity.runOnUiThread {
            AlertDialog.Builder(activity)
                .setTitle("Location permission required")
                .setMessage(
                    "Starting from Android M (6.0), the system requires apps to be granted " +
                            "location access in order to scan for BLE devices."
                )
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    ActivityCompat.requestPermissions(
                        activity,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        PERMISSION_REQUEST_CODE
                    )
                }
                .show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    fun Context.requestBluetoothPermissions() {
        val activity = this as? Activity ?: return
        activity.runOnUiThread {
            AlertDialog.Builder(activity)
                .setTitle("Bluetooth permission required")
                .setMessage(
                    "Starting from Android 12, the system requires apps to be granted " +
                            "Bluetooth access in order to scan for and connect to BLE devices."
                )
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    ActivityCompat.requestPermissions(
                        activity,
                        arrayOf(
                            Manifest.permission.BLUETOOTH_SCAN,
                            Manifest.permission.BLUETOOTH_CONNECT
                        ),
                        PERMISSION_REQUEST_CODE
                    )
                }
                .show()
        }
    }