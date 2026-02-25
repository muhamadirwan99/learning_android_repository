# News App - Aplikasi Berita Android

## 📱 Tentang Aplikasi

News App adalah aplikasi Android yang menampilkan berita terkini dari kategori sains menggunakan News API. Aplikasi ini dibangun dengan mengimplementasikan arsitektur MVVM (Model-View-ViewModel) dan menggunakan berbagai teknologi modern Android.

## ✨ Fitur Utama

- **📰 Headline News**: Menampilkan berita terbaru dari kategori sains (US)
- **🔖 Bookmark**: Simpan berita favorit untuk dibaca nanti
- **📶 Offline Mode**: Berita yang sudah diload bisa diakses secara offline
- **♻️ Auto Refresh**: Data berita otomatis di-refresh dari API
- **🎨 Material Design**: UI yang clean dengan TabLayout dan ViewPager2

## 🏗️ Arsitektur Aplikasi

Aplikasi ini menggunakan **Clean Architecture** dengan pattern **MVVM** dan **Repository Pattern**:

```
┌─────────────────────────────────────────────────────────┐
│                      UI Layer                           │
│  (Activity, Fragment, ViewModel, Adapter)               │
└─────────────────┬───────────────────────────────────────┘
                  │
┌─────────────────▼───────────────────────────────────────┐
│                 Domain Layer                            │
│              (Repository, UseCase)                      │
└─────────────────┬───────────────────────────────────────┘
                  │
        ┌─────────┴─────────┐
        │                   │
┌───────▼─────┐    ┌────────▼──────┐
│   Remote    │    │     Local     │
│  (API/      │    │   (Room DB)   │
│  Retrofit)  │    │               │
└─────────────┘    └───────────────┘
```

## 📁 Struktur Project

```
com.dicoding.newsapp/
├── data/                          # Layer Data
│   ├── local/                     # Database lokal
│   │   ├── entity/               
│   │   │   └── NewsEntity.kt     # Entity untuk Room Database
│   │   └── room/
│   │       ├── NewsDao.kt        # Data Access Object
│   │       └── NewsDatabase.kt   # Database configuration
│   ├── remote/                    # API Service
│   │   ├── response/
│   │   │   └── NewsResponse.kt   # Response model dari API
│   │   └── retrofit/
│   │       ├── ApiConfig.kt      # Konfigurasi Retrofit
│   │       └── ApiService.kt     # API endpoints
│   ├── NewsRepository.kt          # Repository (Single Source of Truth)
│   └── Result.kt                  # Sealed class untuk state management
│
├── di/                            # Dependency Injection
│   └── Injection.kt              # Manual DI container
│
├── ui/                            # Layer Presentasi
│   ├── HomeActivity.kt           # Activity utama dengan TabLayout
│   ├── NewsFragment.kt           # Fragment untuk menampilkan list berita
│   ├── NewsAdapter.kt            # RecyclerView Adapter
│   ├── NewsViewModel.kt          # ViewModel untuk business logic
│   ├── ViewModelFactory.kt       # Factory untuk create ViewModel
│   └── SectionsPagerAdapter.kt   # Adapter untuk ViewPager2
│
└── utils/                         # Helper Classes
    ├── AppExecutors.kt           # Thread executors management
    └── DateFormatter.kt          # Utility untuk format tanggal
```

## 🔄 Alur Data (Data Flow)

### 1. Fetch Berita dari API
```
UI (Fragment) → ViewModel → Repository → API Service
                                       ↓
                              Transform Response
                                       ↓
                              Check Bookmark State
                                       ↓
                              Save to Room Database
                                       ↓
                              Emit LiveData to UI
```

### 2. Toggle Bookmark
```
UI (Click Bookmark) → ViewModel → Repository → Update Room Database
                                                      ↓
                                              LiveData Auto Update
                                                      ↓
                                              UI Auto Refresh
```

## 🛠️ Teknologi yang Digunakan

### Core
- **Kotlin** - Bahasa pemrograman utama
- **Android SDK** - Platform development

### Architecture Components
- **ViewModel** - Manage UI data dengan lifecycle-aware
- **LiveData** - Observable data holder untuk reactive UI
- **Room Database** - Local database dengan SQLite
- **ViewBinding** - Type-safe view access

### Networking
- **Retrofit** - REST client untuk API calls
- **Gson** - JSON serialization/deserialization
- **OkHttp** - HTTP client dengan logging interceptor

### UI Components
- **RecyclerView** - Efficient list display
- **ViewPager2** - Swipeable tabs
- **TabLayout** - Tab navigation
- **Material Design Components** - Modern UI components

### Image Loading
- **Glide** - Image loading dan caching library

### Asynchronous
- **Kotlin Coroutines** - Async operations
- **LiveData** - Reactive data updates

## 🔑 Konsep Penting yang Diimplementasikan

### 1. **Repository Pattern**
Repository berfungsi sebagai **single source of truth** yang mengelola data dari multiple sources (API dan Database):
- Fetch data dari API
- Cache data ke local database
- Provide data ke ViewModel melalui LiveData

**Kenapa?** Untuk memisahkan concerns dan membuat kode lebih testable.

### 2. **MVVM Architecture**
- **Model**: Data layer (Repository, Entity, Response)
- **View**: UI layer (Activity, Fragment, Adapter)
- **ViewModel**: Business logic layer (menghubungkan Model dan View)

**Kenapa?** Memisahkan UI logic dari business logic, survive configuration changes.

### 3. **Room Database**
Local database untuk:
- Offline access
- Caching data dari API
- Menyimpan bookmark user

**Kenapa?** Agar app tetap bisa digunakan tanpa internet dan data tidak hilang.

### 4. **Sealed Class untuk State Management**
`Result<T>` sealed class dengan 3 state:
- `Loading` - Tampilkan progress indicator
- `Success<T>` - Tampilkan data
- `Error` - Tampilkan error message

**Kenapa?** Type-safe state handling dan exhaustive when expressions.

### 5. **DiffUtil di RecyclerView**
Menghitung perbedaan antara list lama dan baru secara efisien.

**Kenapa?** Hanya update item yang berubah, bukan re-render semua item (performance).

### 6. **Singleton Pattern**
Digunakan di Repository, Database, dan ViewModel Factory.

**Kenapa?** Ensure hanya ada 1 instance di seluruh app lifecycle (consistency dan memory efficiency).

### 7. **Dependency Injection Manual**
`Injection.kt` menyediakan dependencies ke seluruh app.

**Kenapa?** Centralized dependency management, mudah di-test dan di-maintain.

### 8. **Coroutines untuk Async Operations**
Semua network dan database operations menggunakan coroutines.

**Kenapa?** Non-blocking async operations tanpa callback hell.

## 📝 Cara Kerja Fitur Utama

### Headline News Tab
1. Fragment request data ke ViewModel
2. ViewModel memanggil Repository.getHeadlineNews()
3. Repository emit state Loading
4. Repository fetch data dari API
5. Repository transform ArticlesItem → NewsEntity
6. Repository check bookmark state (preserve user bookmarks)
7. Repository delete old non-bookmarked news
8. Repository insert new data ke database
9. Repository observe database dan emit Success dengan data
10. Fragment observe LiveData dan update RecyclerView

### Bookmark Tab
1. Fragment request bookmarked news ke ViewModel
2. ViewModel memanggil Repository.getBookmarkedNews()
3. Repository query database (WHERE bookmarked = 1)
4. Return LiveData yang auto-update ketika data berubah
5. Fragment observe dan display di RecyclerView

### Toggle Bookmark
1. User klik bookmark icon di NewsAdapter
2. Adapter call lambda onBookmarkClick(news)
3. Lambda di Fragment call ViewModel.saveNews() atau deleteNews()
4. ViewModel launch coroutine di viewModelScope
5. Repository.setBookmarkedNews() update database
6. LiveData auto-emit perubahan
7. UI auto-refresh bookmark status

## 🚀 Setup dan Instalasi

### Prerequisites
- Android Studio Arctic Fox atau lebih baru
- JDK 11 atau lebih baru
- Min SDK 21 (Android 5.0 Lollipop)

### Langkah-langkah

1. **Clone Repository**
   ```bash
   git clone https://github.com/yourusername/MyNewsAppRepository.git
   cd MyNewsAppRepository
   ```

2. **Dapatkan API Key dari News API**
   - Daftar di [News API](https://newsapi.org/)
   - Copy API key Anda

3. **Tambahkan API Key**
   
   Buat atau edit file `local.properties` di root project:
   ```properties
   API_KEY=your_api_key_here
   ```

   Atau tambahkan di `build.gradle.kts` (app level):
   ```kotlin
   android {
       ...
       buildTypes {
           debug {
               buildConfigField("String", "API_KEY", "\"your_api_key_here\"")
           }
           release {
               buildConfigField("String", "API_KEY", "\"your_api_key_here\"")
           }
       }
   }
   ```

4. **Sync Gradle**
   
   Android Studio → File → Sync Project with Gradle Files

5. **Run Aplikasi**
   
   Klik tombol Run atau tekan `Shift + F10`

## 📚 Dependencies

```gradle
// Room Database
implementation "androidx.room:room-runtime:2.x.x"
implementation "androidx.room:room-ktx:2.x.x"
ksp "androidx.room:room-compiler:2.x.x"

// Retrofit & OkHttp
implementation "com.squareup.retrofit2:retrofit:2.x.x"
implementation "com.squareup.retrofit2:converter-gson:2.x.x"
implementation "com.squareup.okhttp3:logging-interceptor:4.x.x"

// Glide
implementation "com.github.bumptech.glide:glide:4.x.x"

// Coroutines
implementation "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.x.x"
implementation "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.x.x"

// Lifecycle Components
implementation "androidx.lifecycle:lifecycle-viewmodel-ktx:2.x.x"
implementation "androidx.lifecycle:lifecycle-livedata-ktx:2.x.x"

// ViewPager2 & TabLayout
implementation "androidx.viewpager2:viewpager2:1.x.x"
implementation "com.google.android.material:material:1.x.x"
```

## 🐛 Troubleshooting

### API Key Error
**Problem**: App crash atau tidak menampilkan data
**Solution**: Pastikan API_KEY sudah di-setup dengan benar di `BuildConfig` atau `local.properties`

### Database Error
**Problem**: App crash saat save bookmark
**Solution**: Clear app data atau uninstall-reinstall app

### Network Error
**Problem**: Tidak bisa load berita
**Solution**: 
- Cek koneksi internet
- Cek apakah API key masih valid
- Cek quota API (News API free tier limited)

## 📖 Penjelasan Code Pattern

### LiveData Transformation
```kotlin
// Mengubah LiveData<List<NewsEntity>> → LiveData<Result<List<NewsEntity>>>
val localData: LiveData<Result<List<NewsEntity>>> = 
    newsDao.getNews().map { Result.Success(it) }
```
**Kenapa?** Untuk wrapping data dalam Result sealed class agar UI bisa handle state.

### ViewModelScope
```kotlin
viewModelScope.launch {
    repository.setBookmarkedNews(news, true)
}
```
**Kenapa?** Coroutine auto-cancel ketika ViewModel di-clear (prevent memory leak).

### Nullable Binding
```kotlin
private var _binding: FragmentNewsBinding? = null
private val binding get() = _binding
```
**Kenapa?** Binding hanya valid antara onCreateView dan onDestroyView, null di luar itu untuk avoid memory leak.

### DiffUtil Callback
```kotlin
override fun areItemsTheSame(old: NewsEntity, new: NewsEntity) = 
    old.title == new.title
```
**Kenapa?** DiffUtil perlu tau item mana yang "sama" untuk calculate minimal changes.

## 🎯 Best Practices yang Diterapkan

1. ✅ **Single Responsibility Principle** - Setiap class punya 1 tanggung jawab
2. ✅ **Separation of Concerns** - UI, Business Logic, dan Data layer terpisah
3. ✅ **Dependency Injection** - Loose coupling antar komponen
4. ✅ **Repository Pattern** - Single source of truth untuk data
5. ✅ **Reactive Programming** - LiveData untuk automatic UI updates
6. ✅ **Memory Leak Prevention** - Nullable binding, viewModelScope
7. ✅ **Thread Safety** - Volatile + synchronized untuk singleton
8. ✅ **Error Handling** - Try-catch di network calls, null safety
9. ✅ **Resource Management** - String resources untuk i18n support
10. ✅ **Code Documentation** - Inline comments yang menjelaskan "kenapa"

## 🔮 Future Improvements

- [ ] Implement Paging 3 untuk infinite scroll
- [ ] Add search functionality
- [ ] Support multiple news categories
- [ ] Add dark mode support
- [ ] Implement Dependency Injection dengan Hilt/Dagger
- [ ] Add unit tests dan instrumentation tests
- [ ] Implement WorkManager untuk periodic news sync
- [ ] Add sharing functionality
- [ ] Implement notification untuk breaking news
- [ ] Add news detail screen

## 📄 License

This project is created for educational purposes.

## 👨‍💻 Developer

Developed with ❤️ for learning Android development

## 📞 Contact

Jika ada pertanyaan atau masukan, silakan buat issue di repository ini.

---

**Happy Coding! 🚀**

