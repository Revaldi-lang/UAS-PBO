@echo off
setlocal enabledelayedexpansion

echo ==================================================
echo   Food Ordering Management System - Compiler/Runner
echo ==================================================
echo.

REM 1. Tentukan compiler & java runner yang akan digunakan
set "JAVAC_CMD=javac"
set "JAVA_CMD=java"

REM Cek apakah javac ada di PATH
where javac >nul 2>nul
if %errorlevel% neq 0 (
    echo [INFO] compiler 'javac' tidak ditemukan di System PATH.
    echo [INFO] Mencoba mencari JDK di Android Studio JBR...
    
    set "JBR_PATH=C:\Program Files\Android\Android Studio\jbr\bin"
    if exist "!JBR_PATH!\javac.exe" (
        set "JAVAC_CMD=!JBR_PATH!\javac.exe"
        set "JAVA_CMD=!JBR_PATH!\java.exe"
        echo [INFO] JDK ditemukan di: !JBR_PATH!
    ) else (
        echo [ERROR] JDK / Compiler Java tidak ditemukan!
        echo Pastikan Java JDK terinstal dan ditambahkan ke System Environment PATH.
        echo Atau edit file 'run.bat' ini untuk menyesuaikan path ke JDK Anda.
        pause
        exit /b 1
    )
)

REM 2. Buat folder 'bin' jika belum ada
if not exist bin mkdir bin

echo [1/2] Melakukan Kompilasi Source Code...
"!JAVAC_CMD!" -cp "lib/*" -d bin src/database/DatabaseConfig.java src/model/User.java src/model/Admin.java src/model/Customer.java src/model/MenuItem.java src/model/Food.java src/model/Beverage.java src/model/OrderItem.java src/model/Order.java src/payment/Payment.java src/payment/CashPayment.java src/payment/EWalletPayment.java src/repository/UserRepository.java src/repository/MenuRepository.java src/repository/OrderRepository.java src/ui/LoginFrame.java src/ui/AdminFrame.java src/ui/CustomerFrame.java
if %errorlevel% neq 0 (
    echo.
    echo [ERROR] Kompilasi Gagal! Silakan cek error di atas.
    pause
    exit /b %errorlevel%
)
echo [1/2] Kompilasi Berhasil.
echo.

echo [2/2] Menjalankan Aplikasi...
"!JAVA_CMD!" -cp "bin;lib/*" ui.LoginFrame
if %errorlevel% neq 0 (
    echo.
    echo [INFO] Aplikasi dihentikan dengan kode: %errorlevel%
)
pause
