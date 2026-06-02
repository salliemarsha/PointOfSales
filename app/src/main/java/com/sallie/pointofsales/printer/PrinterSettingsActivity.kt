package com.sallie.pointofsales.printer

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_printer_settings)

        initView()
        setupList()
        setupActions()
        loadPairedDevices()
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

    private fun setupList() {
        adapter = BluetoothDeviceAdapter(deviceList) { device ->
            connectedDevice = device
            tvPrinterName.text = device.name ?: "Unknown Printer"
            tvPrinterInfo.text = device.address
            Toast.makeText(this, "Printer selected", Toast.LENGTH_SHORT).show()
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
            tvPrinterInfo.text = "Terhubung"
            Toast.makeText(this, "Printer connected (mock)", Toast.LENGTH_SHORT).show()
        }

        btnDisconnect.setOnClickListener {
            connectedDevice = null
            tvPrinterName.text = "Belum Ada Printer"
            tvPrinterInfo.text = "Tidak terhubung"
            Toast.makeText(this, "Printer disconnected", Toast.LENGTH_SHORT).show()
        }

        btnTestPrint.setOnClickListener {
            if (connectedDevice == null) {
                Toast.makeText(this, "Belum ada printer terhubung", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Toast.makeText(this, "Test print berhasil (mock)", Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("MissingPermission")
    private fun loadPairedDevices() {
        val paired = bluetoothAdapter?.bondedDevices
        deviceList.clear()

        if (paired != null) {
            deviceList.addAll(paired)
        }

        adapter.notifyDataSetChanged()
    }
}