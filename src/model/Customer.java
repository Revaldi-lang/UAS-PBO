package model;

public class Customer extends User {
    public Customer(int id, String username, String password) {
        super(id, username, password, "CUSTOMER");
    }

    @Override
    public String getWelcomeMessage() {
        return "Halo Pelanggan Setia " + getUsername() + "! Selamat datang di Warung Om Budi. Selamat memesan makanan favorit Anda!";
    }
}
