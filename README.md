# 🖼️StoryApp

**StoryApp** adalah aplikasi Android yang dibangun sebagai tugas submission akhir pada kelas [Android Intermediate](https://www.dicoding.com/academies/352-belajar-pengembangan-aplikasi-android-intermediate) di Dicoding. 
Aplikasi ini memungkinkan pengguna untuk berbagi cerita melalui foto dan teks, serta melihat lokasi cerita jika diizinkan.
<i>Gunakan repositori ini sebagai referensi saja, setiap plagiarisme akan terdeteksi secara otomatis</i>

---

## 🚀 Fitur Utama

- 👤 **Autentikasi Pengguna**  
  Login dan registrasi menggunakan email dan password.

- 📩 **Unggah Cerita**  
  Pengguna dapat mengunggah foto (dari galeri atau kamera) ditambah teks/deskripsi

- 👀 **Lihat Daftar Cerita**  
  Menampilkan cerita dari pengguna lain dalam tampilan feed.

- 🗺️ **Detail Cerita & Lokasi**  
  Ketika tersedia, tampilkan lokasi cerita di Google Maps

- 📡 **Penggunaan Offline / Database lokal**
  Menyimpan data cerita secara lokal dengan Room untuk Single Source of Truth dan pengalaman offline

---

## 🛠️ Teknologi yang Digunakan

- Bahasa pemrograman: **Kotlin** (100%)
- Arsitektur: *MVVM* (Model–View–ViewModel)
- Library dan Tools:
  - **Room** — penyimpanan lokal, Single Source of Truth
  - **Retrofit** — untuk request API
  - **Google Maps API** — untuk menampilkan lokasi cerita
  - **Paging 3** — untuk pagination tampilan cerita
  - **Glide** — untuk loading gambar secara efisien

---

## 📸 Screenshot Halaman Aplikasi
<table>
  <tr>
    <th>Login</th>
    <th>Register</th>
    <th>Home</th>
  </tr>
  <tr>
    <td><img src="https://github.com/user-attachments/assets/7f9a9fb8-6042-425a-8500-ac8f90f66187" alt="Gambar Halaman Login" width="300"></td>
    <td><img src="https://github.com/user-attachments/assets/fbe50afb-5aea-4760-915b-db2fba32c945" alt="Gambar Halaman Register" width="300"></td>
    <td><img src="https://github.com/user-attachments/assets/37c05c0e-a7e3-4576-be5e-d8d1ed52a176" alt="Gambar Halaman Home" width="300"></td>
  </tr>
  <tr>
    <th>Detail</th>
    <th>Upload</th>
    <th>Settings</th>
  </tr>
  <tr>
    <td><img src="https://github.com/user-attachments/assets/e99d6b18-487b-48fd-add3-c9f8265261a5" alt="Gambar Halaman Detail" width="300"></td>
    <td><img src="https://github.com/user-attachments/assets/e417b129-84f5-4f37-9927-d9207702602b" alt="Gambar Halaman Upload" width="300"></td>
    <td><img src="https://github.com/user-attachments/assets/3b6a3b03-ada4-4d3b-8879-2cc351d6185c" alt="Gambar Halaman Settings" width="300"></td>
  </tr>
  <tr>
    <th>Maps</th>
  </tr>
  <tr>
    <td><img src="https://github.com/user-attachments/assets/70893d3c-2fbc-41b1-84dd-13b1a2b86959" alt="Gambar Halaman Maps" width="300"></td>
  </tr>
</table>

---

## ▶️ Cara Menjalankan Aplikasi

1. **Clone repository ini**  
   ```bash
   git clone https://github.com/ahmadr1d1/StoryApp.git
   ```
2. Buka di Android Studio
3. Pastikan anda memiliki API Key dari Dicoding Story dan MAPS Api key <i>"Alza...."</i> lalu letakkan pada local.properties seperti ini :
   ```bash
   API_BASE_URL_DICODING_STORY=https://*****
   MAPS_API_KEY=AIza*****.git
   ```
4. Jalankan di emulator atau physical device anda
