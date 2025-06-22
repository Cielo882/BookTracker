# BookTracker 📚

this application  allows users to search for books online , mark them as read, rate them, and keep a local record of read books. It is built using Kotlin and follows the Clean Architecture pattern, separating concerns across data, domain, and presentation layers.

## ✨ Features

- 🔍 Search books by title or keyword using the Gutendex API.
- ✅ Mark books as read and store them in local database.
- ⭐ Rate the books you’ve read (0–5 stars).
- 📄 View a list of your read books with ratings.
- 🌓 Supports dark mode (via `AppCompatDelegate`).

## 🛠 Tech Stack

- **Language**: Kotlin
- **Architecture**: Clean Architecture (Presentation, Domain, Data)
- **Networking**: Retrofit + Gson
- **Database**: Room (with DAOs and Entities)
- **UI**: RecyclerView, Material Components
- **MVVM**: ViewModel + LiveData
- **Dependency Management**: Gradle

## 📦 Structure


```
appLibros/
├── data/
│ ├── remote/ # Gutendex API, DTOs
│ ├── local/ # Room DAO and entities
│ └── repository/ # BookRepository implementation
├── domain/
│ ├── model/ # Book domain models
│ ├── usecase/ # Use cases (search, add/remove/read/update)
│ └── repository/ # Repository interface
├── presentation/
│ ├── adapter/ # RecyclerView adapter
│ └── viewmodel/ # BookView ViewModel
└── MainActivity.kt # Main UI logic
```
Clone the repository:

   ```bash
   git clone https://github.com/Cielo882/BookTracker.git

   cd appLibros
```
## 📄 License
This project is licensed under the MIT License 
