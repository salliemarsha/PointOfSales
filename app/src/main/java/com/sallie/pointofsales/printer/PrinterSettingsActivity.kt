package com.sallie.pointofsales.printer

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.sallie.pointofsales.R

class PrinterSettingsActivity : AppCompatActivity() {

    private lateinit var toolbar: MaterialToolbar
    private lateinit var tvPrinterName: TextView
    private lateinit var tvPrinterInfo: TextView
    private lateinit var btnConnect: Button
    private lateinit var btnTestPrint: Button
    private lateinit var btnDisconnect: Button
    private lateinit var rvDevices: RecyclerView

    private val bluetoothAdapter: BluetoothAdapter? by lazy {
        BluetoothAdapter.getDefaultAdapter()
    }

    private val deviceList = ArrayList<BluetoothDevice>()
    private lateinit var adapter: BluetoothDeviceAdapter

    private var connectedDevice: BluetoothDevice? = null

    // Request permissions for Android 12+
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions.entries.all { it.value }
        if (granted) {
            loadPairedDevices()
        } else {
            Toast.makeText(this, "Izin Bluetooth diperlukan untuk fitur cetak", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_printer_settings)

        initView()
        setupList()
        setupActions()
        checkBluetoothPermissions()
    }

    private fun initView() {
        toolbar = findViewById(R.id.toolbarPrinterSettings)
        tvPrinterName = findViewById(R.id.tvPrinterName)
        tvPrinterInfo = findViewById(R.id.tvPrinterInfo)
        btnConnect = findViewById(R.id.btnConnectPrinter)
        btnTestPrint = findViewById(R.id.btnTestPrint)
        btnDisconnect = findViewById(R.id.btnDisconnectPrinter)
        rvDevices = findViewById(R.id.rvBluetoothDevices)

        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun checkBluetoothPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val permissions = arrayOf(
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.BLUETOOTH_SCAN
            )
            val neededPermissions = permissions.filter {
                ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
            }.toTypedArray()

            if (neededPermissions.isNotEmpty()) {
                requestPermissionLauncher.launch(neededPermissions)
            } else {
                loadPairedDevices()
            }
        } else {
            loadPairedDevices()
        }
    }

    private fun setupList() {
        adapter = BluetoothDeviceAdapter(deviceList) { device ->
            connectedDevice = device
            tvPrinterName.text = device.name ?: "Unknown Printer"
            tvPrinterInfo.text = device.address
        }

        rvDevices.layoutManager = LinearLayoutManager(this)
        rvDevices.adapter = adapter
    }

    private fun setupActions() {
        btnConnect.setOnClickListener {
            if (connectedDevice == null) {
                Toast.makeText(this, "Pilih printer terlebih dahulu", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            tvPrinterName.text = connectedDevice?.name ?: "Printer Connected"
            tvPrinterInfo.text = "Terhubung (Mock)"
            Toast.makeText(this, "Printer connected", Toast.LENGTH_SHORT).show()
        }

        btnDisconnect.setOnClickListener {
            connectedDevice = null
            tvPrinterName.text = "Belum Ada Printer"
            tvPrinterInfo.text = "Tidak terhubung"
        }

        btnTestPrint.setOnClickListener {
            if (connectedDevice == null) {
                Toast.makeText(this, "Belum ada printer terhubung", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            Toast.makeText(this, "Mencetak struk uji coba...", Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("MissingPermission")
    private fun loadPairedDevices() {
        try {
            val paired = bluetoothAdapter?.bondedDevices
            deviceList.clear()
            paired?.let { deviceList.addAll(it) }
            adapter.notifyDataSetChanged()
        } catch (e: SecurityException) {
            Toast.makeText(this, "Gagal memuat perangkat: Izin ditolak", Toast.LENGTH_SHORT).show()
        }
    }
}
