package com.sallie.pointofsales.printer

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothClass
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
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
import com.google.android.material.card.MaterialCardView
import com.sallie.pointofsales.R
import com.sallie.pointofsales.adapter.BluetoothDeviceAdapter
import com.sallie.pointofsales.model.PrinterDevice
import java.io.IOException
import java.util.UUID

class PrinterSettingsActivity : AppCompatActivity() {

    private lateinit var toolbar: MaterialToolbar
    private lateinit var tvPrinterName: TextView
    private lateinit var tvPrinterInfo: TextView
    private lateinit var cvStatusBadge: MaterialCardView
    private lateinit var viewIndicatorDot: View
    private lateinit var tvStatusBadgeText: TextView
    private lateinit var btnConnect: Button
    private lateinit var btnTestPrint: Button
    private lateinit var btnDisconnect: Button
    private lateinit var rvDevices: RecyclerView

    private val bluetoothAdapter: BluetoothAdapter? by lazy {
        @Suppress("DEPRECATION")
        BluetoothAdapter.getDefaultAdapter()
    }

    private val printerList = ArrayList<PrinterDevice>()
    private lateinit var adapter: BluetoothDeviceAdapter

    private var selectedPrinter: PrinterDevice? = null
    
    private val PRINTER_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.entries.all { it.value }) {
            loadFilteredPrinters()
        } else {
            Toast.makeText(this, "Izin Bluetooth diperlukan untuk fitur ini", Toast.LENGTH_SHORT).show()
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
        cvStatusBadge = findViewById(R.id.cvStatusBadge)
        viewIndicatorDot = findViewById(R.id.viewIndicatorDot)
        tvStatusBadgeText = findViewById(R.id.tvStatusBadgeText)
        btnConnect = findViewById(R.id.btnConnectPrinter)
        btnTestPrint = findViewById(R.id.btnTestPrint)
        btnDisconnect = findViewById(R.id.btnDisconnectPrinter)
        rvDevices = findViewById(R.id.rvBluetoothDevices)

        updateConnectionUI("Disconnected", "Belum Ada Printer", "Sambungkan printer untuk mulai mencetak")

        val sharedPrefs = getSharedPreferences("printer_prefs", Context.MODE_PRIVATE)
        val lastAddress = sharedPrefs.getString("last_printer_address", null)
        val lastName = sharedPrefs.getString("last_printer_name", null)
        
        if (lastAddress != null && lastName != null) {
            updateConnectionUI("Saved", lastName, "Tersimpan: $lastAddress")
        }

        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun updateConnectionUI(status: String, name: String, info: String) {
        tvPrinterName.text = name
        tvPrinterInfo.text = info
        
        when (status) {
            "Connected" -> {
                cvStatusBadge.setCardBackgroundColor(Color.parseColor("#DCFCE7")) // Green light
                cvStatusBadge.strokeColor = Color.parseColor("#BBF7D0")
                viewIndicatorDot.setBackgroundColor(Color.parseColor("#22C55E")) // Green
                tvStatusBadgeText.text = "Terhubung"
                tvStatusBadgeText.setTextColor(Color.parseColor("#166534"))
            }
            "Connecting..." -> {
                cvStatusBadge.setCardBackgroundColor(Color.parseColor("#FEF9C3")) // Yellow light
                cvStatusBadge.strokeColor = Color.parseColor("#FEF08A")
                viewIndicatorDot.setBackgroundColor(Color.parseColor("#EAB308")) // Yellow
                tvStatusBadgeText.text = "Menghubungkan"
                tvStatusBadgeText.setTextColor(Color.parseColor("#854D0E"))
            }
            "Saved" -> {
                cvStatusBadge.setCardBackgroundColor(Color.parseColor("#DBEAFE")) // Blue light
                cvStatusBadge.strokeColor = Color.parseColor("#BFDBFE")
                viewIndicatorDot.setBackgroundColor(Color.parseColor("#3B82F6")) // Blue
                tvStatusBadgeText.text = "Tersimpan"
                tvStatusBadgeText.setTextColor(Color.parseColor("#1E40AF"))
            }
            else -> { // Disconnected / Failed
                cvStatusBadge.setCardBackgroundColor(Color.parseColor("#FEE2E2")) // Red light
                cvStatusBadge.strokeColor = Color.parseColor("#FECACA")
                viewIndicatorDot.setBackgroundColor(Color.parseColor("#EF4444")) // Red
                tvStatusBadgeText.text = "Terputus"
                tvStatusBadgeText.setTextColor(Color.parseColor("#991B1B"))
            }
        }
    }

    private fun checkBluetoothPermissions() {
        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            arrayOf(Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT)
        } else {
            arrayOf(Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.ACCESS_FINE_LOCATION)
        }

        val neededPermissions = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }.toTypedArray()

        if (neededPermissions.isNotEmpty()) {
            requestPermissionLauncher.launch(neededPermissions)
        } else {
            loadFilteredPrinters()
        }
    }

    private fun setupList() {
        adapter = BluetoothDeviceAdapter(printerList) { printer ->
            selectedPrinter = printer
            @SuppressLint("MissingPermission")
            val name = printer.device.name ?: "Unknown Printer"
            updateConnectionUI("Selected", name, "Siap diverifikasi: ${printer.device.address}")
            
            printerList.forEach { if (it.status != "Saved") it.status = "Available" }
            if (printer.status != "Saved") printer.status = "Selected"
            adapter.notifyDataSetChanged()
        }

        rvDevices.layoutManager = LinearLayoutManager(this)
        rvDevices.adapter = adapter
    }

    private fun setupActions() {
        btnConnect.setOnClickListener {
            val printer = selectedPrinter
            if (printer == null) {
                Toast.makeText(this, "Pilih printer dari daftar terlebih dahulu", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            verifyAndConnect(printer)
        }

        btnDisconnect.setOnClickListener {
            val sharedPrefs = getSharedPreferences("printer_prefs", Context.MODE_PRIVATE)
            sharedPrefs.edit().clear().apply()
            
            selectedPrinter = null
            updateConnectionUI("Disconnected", "Belum Ada Printer", "Sambungkan printer untuk mulai mencetak")
            loadFilteredPrinters()
            Toast.makeText(this, "Konfigurasi printer dihapus", Toast.LENGTH_SHORT).show()
        }

        btnTestPrint.setOnClickListener {
            val sharedPrefs = getSharedPreferences("printer_prefs", Context.MODE_PRIVATE)
            val address = sharedPrefs.getString("last_printer_address", null)
            
            if (address == null) {
                Toast.makeText(this, "Printer belum terhubung secara valid", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            Toast.makeText(this, "Mengirim perintah cetak uji coba...", Toast.LENGTH_SHORT).show()
        }
    }

    private fun verifyAndConnect(printer: PrinterDevice) {
        @SuppressLint("MissingPermission")
        val name = printer.device.name ?: "Thermal Printer"
        updateConnectionUI("Connecting...", name, "Memverifikasi koneksi hardware...")
        
        printer.status = "Connecting..."
        adapter.notifyDataSetChanged()

        Thread {
            var socket: BluetoothSocket? = null
            try {
                @SuppressLint("MissingPermission")
                socket = printer.device.createRfcommSocketToServiceRecord(PRINTER_UUID)
                socket.connect()
                
                val sharedPrefs = getSharedPreferences("printer_prefs", Context.MODE_PRIVATE)
                sharedPrefs.edit().apply {
                    putString("last_printer_address", printer.device.address)
                    @SuppressLint("MissingPermission")
                    putString("last_printer_name", printer.device.name)
                    apply()
                }

                Handler(Looper.getMainLooper()).post {
                    printer.status = "Connected"
                    adapter.notifyDataSetChanged()
                    @SuppressLint("MissingPermission")
                    updateConnectionUI("Connected", printer.device.name ?: "Thermal Printer", "Terhubung: ${printer.device.address}")
                    Toast.makeText(this, "Printer Valid & Terhubung", Toast.LENGTH_SHORT).show()
                }
                
                socket.close()
            } catch (e: IOException) {
                Handler(Looper.getMainLooper()).post {
                    printer.status = "Failed"
                    adapter.notifyDataSetChanged()
                    updateConnectionUI("Failed", name, "Gagal: Printer mati atau di luar jangkauan")
                    Toast.makeText(this, "Verifikasi Gagal: Pastikan printer menyala", Toast.LENGTH_LONG).show()
                }
            }
        }.start()
    }

    @SuppressLint("MissingPermission", "NotifyDataSetChanged")
    private fun loadFilteredPrinters() {
        if (bluetoothAdapter == null) return
        
        val paired = bluetoothAdapter?.bondedDevices
        printerList.clear()

        val sharedPrefs = getSharedPreferences("printer_prefs", Context.MODE_PRIVATE)
        val lastAddress = sharedPrefs.getString("last_printer_address", null)

        if (paired != null) {
            for (device in paired) {
                val btClass = device.bluetoothClass
                val majorClass = btClass.majorDeviceClass
                val deviceClass = btClass.deviceClass

                // STRICT FILTERING
                val isPrinterClass = majorClass == BluetoothClass.Device.Major.IMAGING || 
                                     deviceClass == 1664 // Generic Printer class for some thermal printers

                val name = device.name?.lowercase() ?: ""
                val hasPrinterName = name.contains("printer") || name.contains("thermal") || 
                                     name.contains("mpt") || name.contains("pos-") || 
                                     name.contains("58mm") || name.contains("80mm")

                // Filter out non-printer keywords even if they somehow match imaging
                val isExcluded = name.contains("headset") || name.contains("audio") || 
                                 name.contains("phone") || name.contains("watch") ||
                                 name.contains("keyboard") || name.contains("earbud")

                if ((isPrinterClass || hasPrinterName) && !isExcluded) {
                    val status = if (device.address == lastAddress) "Saved" else "Available"
                    printerList.add(PrinterDevice(device, status))
                }
            }
        }

        if (printerList.isEmpty()) {
            Toast.makeText(this, "Tidak ditemukan printer thermal di daftar perangkat yang dipasangkan", Toast.LENGTH_LONG).show()
        }

        adapter.notifyDataSetChanged()
    }
}
