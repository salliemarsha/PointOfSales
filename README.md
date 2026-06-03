# Sallie's Point of Sales

## Deskripsi Proyek

Sallie's Point of Sales merupakan aplikasi Android yang dirancang untuk membantu proses penjualan pada toko atau usaha kecil. Aplikasi memungkinkan admin mengelola produk, cabang, transaksi, laporan, dan mencetak struk pembayaran secara langsung.

## Teknologi yang Digunakan

* Android Studio
* Java
* Firebase Realtime Database
* Firebase Authentication
* Material Design 3

## Fitur Utama

### Manajemen Produk

* Tambah produk
* Edit produk
* Hapus produk
* Kelola stok produk

### Manajemen Cabang

* Tambah cabang
* Edit cabang
* Hapus cabang

### Transaksi Penjualan

* Pilih cabang
* Pilih produk
* Tambah ke keranjang
* Hitung total belanja otomatis
* Simpan transaksi

### Pembayaran

* Input nominal pembayaran
* Hitung kembalian
* Simpan data transaksi

### Laporan

* Riwayat transaksi
* Detail transaksi
* Rekap penjualan

### Akun

* Edit profil
* Ubah PIN

### Printer

* Pengaturan printer Bluetooth
* Cetak struk transaksi

## Screenshot Aplikasi

### Register

![Register](screenshot/register.jpeg)

### Login

![Login](screenshot/login.jpeg)

### Dashboard

![Dashboard](screenshot/dashboard.jpeg)

### Kategori

![Kategori](screenshot/datakategori.jpeg)

### Tambah Kategori

![Tambah Kategori](screenshot/modkategori.jpeg)

### Cabang

![Cabang](screenshot/datacabang.jpeg)

### Tambah Cabang

![Tambah Cabang](screenshot/modcabang.jpeg)

### Pegawai

![Pegawai](screenshot/pegawai.jpeg)

### Tambah Pegawai

![Tambah Pegawai](screenshot/modpegawai.jpeg)

### Pelanggan

![Pelanggan](screenshot/pelanggan.jpeg)

### Tambah Pelanggan

![Tambah Pelanggan](screenshot/modpelanggan.jpeg)

### Produk

![Produk](screenshot/dataproduk.jpeg)

### Tambah Produk

![Tambah Produk](screenshot/modproduk.jpeg)

### Transaksi

![Transaksi](screenshot/transaksi.jpeg)

### Pembayaran

![Pembayaran](screenshot/pembayaran.jpeg)

### Struk Nota

![Struk](screenshot/struk.jpeg)

### Laporan

![Laporan](screenshot/riwayat.jpeg)

### Akun

![Akun](screenshot/profil.jpeg)

### Pengaturan Printer

![Printer](screenshot/printer.png)

## Struktur Database Firebase

### Produk

| Field       | Tipe   |
| ----------- | ------ |
| idProduk    | String |
| namaProduk  | String |
| hargaProduk | Int    |
| stokProduk  | Int    |
| cabang      | String |

### Cabang

| Field      | Tipe   |
| ---------- | ------ |
| idCabang   | String |
| namaCabang | String |

### Transaksi

| Field       | Tipe   |
| ----------- | ------ |
| idTransaksi | String |
| tanggal     | String |
| namaCabang  | String |
| totalBayar  | Int    |

### User

| Field | Tipe   |
| ----- | ------ |
| uid   | String |
| nama  | String |
| email | String |
| role  | String |

## Cara Menjalankan

1. Clone repository

```bash
git clone https://github.com/username/pointofsales.git
```

2. Buka project menggunakan Android Studio.

3. Sinkronisasi Gradle.

4. Tambahkan file google-services.json.

5. Jalankan aplikasi pada emulator atau perangkat Android.

## Author

Sallie Marsha

Project Android Point of Sales untuk tugas akhir sekolah.
