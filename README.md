# Sallie's Point of Sales

Aplikasi Point of Sales (POS) berbasis Android yang digunakan untuk mengelola produk, transaksi penjualan, laporan penjualan, data pegawai, dan pengaturan printer struk.

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

![Register](screenshots/register.jpeg)

### Login

![Login](screenshots/login.jpeg)

### Dashboard

![Dashboard](screenshots/dashboard.jpeg)

### Kategori

![Kategori](screenshots/datakategori.jpeg)

### Tambah Kategori

![Tambah Kategori](screenshots/modkategori.jpeg)

### Cabang

![Cabang](screenshots/datacabang.jpeg)

### Tambah Cabang

![Tambah Cabang](screenshots/modcabang.jpeg)

### Pegawai

![Pegawai](screenshots/pegawai.jpeg)

### Tambah Pegawai

![Tambah Pegawai](screenshots/modpegawai.jpeg)

### Pelanggan

![Pelanggan](screenshots/pelanggan.jpeg)

### Tambah Pelanggan

![Tambah Pelanggan](screenshots/modpelanggan.jpeg)

### Produk

![Produk](screenshots/dataproduk.jpeg)

### Tambah Produk

![Tambah Produk](screenshots/modproduk.jpeg)

### Transaksi

![Transaksi](screenshots/transaksi.jpeg)

### Pembayaran

![Pembayaran](screenshots/pembayaran.jpeg)

### Struk Nota

![Struk](screenshots/struk.jpeg)

### Laporan

![Laporan](screenshots/riwayat.jpeg)

### Akun

![Akun](screenshots/profil.jpeg)

### Pengaturan Printer

![Printer](screenshots/printer.png)

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
