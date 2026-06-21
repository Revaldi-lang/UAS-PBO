# Food Ordering Management System (Aplikasi Pemesanan Makanan)

Aplikasi **Food Ordering Management System** adalah sebuah sistem informasi berbasis desktop yang dirancang untuk mengelola menu makanan/minuman, pemesanan pelanggan, serta manajemen transaksi pembayaran. Proyek ini dibangun menggunakan bahasa pemrograman **Java (Java Swing)** untuk antarmuka pengguna (GUI) dan **SQLite** sebagai sistem manajemen database relasional.

Proyek ini dirancang sebagai tugas **Ujian Akhir Semester (UAS) Pemrograman Berorientasi Objek (PBO)** dengan mengimplementasikan prinsip-prinsip desain perangkat lunak modern dan pilar OOP.

---

## 🌟 Fitur Utama Aplikasi

### 1. Sistem Multi-Role (Autentikasi)
* **Login & Registrasi:** Pengguna dapat mendaftarkan akun baru secara mandiri. Peran default untuk pendaftaran baru adalah **Customer**.
* **Admin Default:** Akun Administrator bawaan disediakan untuk mengelola kontrol penuh sistem (`username: admin`, `password: admin123`).

### 2. Panel Kontrol Administrator (Admin Dashboard)
* **Kelola Menu (CRUD):** Tambah menu baru, edit nama/harga/kategori/detail spesifikasi menu, serta hapus menu (*Soft Delete*).
* **Manajemen Antrean Pesanan:** Melihat daftar pesanan masuk dari pelanggan dan menyelesaikan status pesanan (*COMPLETED*).

### 3. Portal Pelanggan (Customer Portal)
* **Jelajahi Menu:** Mencari hidangan makanan atau minuman dengan filter kategori yang responsif.
* **Keranjang Belanja:** Memasukkan menu ke dalam keranjang, mengatur jumlah porsi, serta menghapus item terpilih dari keranjang.
* **Pembayaran Dinamis:**
  * **Tunai (Cash):** Simulasi pembayaran dengan input uang diterima dan kalkulasi kembalian.
  * **E-Wallet:** Simulasi integrasi transaksi cashless (GoPay, OVO, Dana, LinkAja) dengan nomor handphone.
* **Riwayat Pesanan:** Melacak status pesanan (`PENDING` atau `COMPLETED`) dari transaksi yang pernah dilakukan sebelumnya.

---

## 🏗️ Penerapan Konsep PBO (OOP Principles)

Aplikasi ini mendemonstrasikan pilar PBO dengan sangat jelas melalui kode program berikut:

1. **Abstraction (Abstraksi):**
   * Kelas abstrak [User](file:///c:/Users/user/OneDrive/Desktop/UAS%20PBO/src/model/User.java) dan [MenuItem](file:///c:/Users/user/OneDrive/Desktop/UAS%20PBO/src/model/MenuItem.java) mendefinisikan kontrak dasar (*template*) objek tanpa merinci implementasinya secara spesifik.
   * Interface [Payment](file:///c:/Users/user/OneDrive/Desktop/UAS%20PBO/src/payment/Payment.java) mengabstraksikan metode pemrosesan transaksi pembayaran (`processPayment`).
2. **Inheritance (Pewarisan):**
   * Kelas `Admin` dan `Customer` mewarisi properti serta metode dari kelas `User`.
   * Kelas `Food` dan `Beverage` mewarisi properti dasar dari kelas `MenuItem`.
3. **Polymorphism (Polimorfisme):**
   * **Overriding Method:** Metode `getWelcomeMessage()` diimplementasikan secara berbeda pada kelas `Admin` dan `Customer` untuk menyapa user sesuai rolenya.
   * **Polimorfisme Dinamis (Strategy Pattern):** Metode pembayaran dipilih secara dinamis pada saat *checkout* menggunakan kelas [CashPayment](file:///c:/Users/user/OneDrive/Desktop/UAS%20PBO/src/payment/CashPayment.java) atau [EWalletPayment](file:///c:/Users/user/OneDrive/Desktop/UAS%20PBO/src/payment/EWalletPayment.java) yang mengimplementasikan interface `Payment`.
4. **Encapsulation (Enkapsulasi):**
   * Menggunakan akses *modifier* `private` untuk menyembunyikan variabel state objek di dalam model, dan menyediakan akses kontrol yang aman melalui metode *getter* dan *setter*.

---

## 🗄️ Struktur Proyek & Source Code

Folder proyek ini memiliki struktur sebagai berikut:
```text
UAS PBO/
│
├── library/                 # Pustaka eksternal (driver SQLite JDBC jar)
├── bin/                     # Output file kompilasi Java (.class)
├── src/                     # Folder utama source code Java (.java)
│   ├── database/            # Konfigurasi koneksi & inisialisasi tabel DB
│   ├── model/               # Representasi data objek (User, MenuItem, Order, dll)
│   ├── payment/             # Logika transaksi pembayaran (Cash, E-Wallet)
│   ├── repository/          # Akses data (SQL Queries) memisahkan DB dan UI
│   └── ui/                  # Tampilan grafis JFrame Swing (Login, Admin, Customer)
│
├── food_ordering.db         # File database SQLite (terbuat otomatis)
└── run.bat                  # Script batch untuk kompilasi dan menjalankan program
```

---

## 📊 Skema Database (Relasional)

Aplikasi ini menggunakan 4 tabel saling berelasi di SQLite:
1. **`users`:** Menyimpan data pengguna (`id`, `username`, `password`, `role`).
2. **`menu_items`:** Menyimpan daftar hidangan menu (`id`, `name`, `price`, `category`, `detail`, `is_deleted`). Kolom `is_deleted` digunakan untuk **Soft Delete** agar riwayat transaksi masa lalu tidak rusak ketika admin menghapus menu.
3. **`orders`:** Menyimpan header pesanan (`id`, `customer_name`, `order_date`, `total_price`, `payment_method`, `status`).
4. **`order_items`:** Menyimpan detail item pesanan yang berelasi ke tabel `orders` (Cascade Delete) dan `menu_items`.

---

## 🚀 Cara Menjalankan Aplikasi (Windows)

Anda tidak perlu mengonfigurasi IDE secara rumit untuk menjalankan aplikasi ini. Kami telah menyediakan *runner script* otomatis:

1. Pastikan Anda telah menginstal **Java Development Kit (JDK)** di komputer Anda.
2. Cukup klik ganda (double-click) pada file **[run.bat](file:///c:/Users/user/OneDrive/Desktop/UAS%20PBO/run.bat)** yang berada di direktori utama proyek.
3. Script bat tersebut akan otomatis:
   * Mencari lokasi instalasi Java di sistem (termasuk JBR bawaan Android Studio jika PATH belum tersetting).
   * Membuat folder `bin`.
   * Mengompilasi seluruh source code di folder `src`.
   * Menjalankan GUI utama aplikasi (`ui.LoginFrame`).

### Akun Bawaan untuk Uji Coba:
* **Akun Administrator:**
  * Username: `admin`
  * Password: `admin123`
* **Akun Customer Default:**
  * Username: `budi`
  * Password: `budi123` (atau daftarkan username baru Anda di menu Register)
