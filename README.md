# Swipey

This application allows users to **display products**, **filter products by category**, **search products by name**, **add products with images**, **save products locally for offline usage**, **upload products to the server once connected to the internet**, **refresh the product list via swipe**, **show a splash screen**, and implement **unit testing**.

## **Features**
- **Display Products**: Display all available products in a list view.
- **Display Products by Category**: Filter and display products based on user-selected categories.
- **Search Products by Name**: Search for products using a keyword.
- **Add Products with Image**: Allows users to add new products with images.
- **Save Product Locally if Internet is Not Available**: Products are saved locally when the internet connection is unavailable.
- **Upload the Products to Server when Network Connected**: Synchronize locally saved products to the server using **WorkManager**.
- **Refresh Products List**: Users can refresh the products list by swiping on the homescreen.
- **Splash Screen**: A welcome splash screen appears when the application starts.
- **Implemented Unit Testing**: Comprehensive unit tests ensure stability and reliability of the application.

## **Tools & Technologies Used**
- **Architecture**: MVVM (Model-View-ViewModel)
- **Networking**: **Retrofit** – A type-safe REST client for Android.
- **Dependency Injection**: **KOIN** – A modern dependency injection framework.
- **Coroutines**: **Kotlin Coroutines** – Efficient asynchronous programming.
- **Room Database**: Used for local data storage and caching.
- **WorkManager**: For background tasks like syncing data to the server.
- **Unit Testing**: **JUnit** – For testing Android applications.
- **Testing Framework**: **Espresso** – UI testing for Android.
- **Mocking**: **Mockk** – For mocking objects in tests.

## **Dependencies**
- **Retrofit**: Used for API calls.
- **KOIN**: Dependency Injection framework to manage app components.
- **Kotlin Coroutines**: For asynchronous operations in the app.
- **Room**: Local database for storing and managing product data.
- **WorkManager**: For handling background tasks like network syncing.
- **Espresso**: UI testing framework for testing user interface.
- **Mockk**: Mocking framework for unit testing.

## **Building and Running the App**
1. **Clone the Repository**:
   ```bash
   git clone <repository-url>
2. **Add your baseUrl in Constants.kt**
3. **Sync the Gradle Files**
4. **Run the Application**
5. **Allow all the Permissions**
6. **Now Enjoy Swipey**

