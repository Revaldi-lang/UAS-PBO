package model;

public class Admin extends User {
    public Admin(int id, String username, String password) {
        super(id, username, password, "ADMIN");
    }

    @Override
    public String getWelcomeMessage() {
        return "Selamat datang, Administrator " + getUsername() + "! Anda memiliki hak penuh untuk mengelola menu dan pesanan.";
    }
}
