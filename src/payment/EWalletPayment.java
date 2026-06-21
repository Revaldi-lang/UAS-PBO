package payment;

public class EWalletPayment implements Payment {
    private String provider; // e.g., "GoPay", "OVO", "Dana"
    private String phoneNumber;

    public EWalletPayment(String provider, String phoneNumber) {
        this.provider = provider;
        this.phoneNumber = phoneNumber;
    }

    public String getProvider() {
        return provider;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    @Override
    public String processPayment(double amount) {
        // Simulasi pembayaran E-Wallet sukses
        return "Pembayaran E-Wallet Berhasil!\n" +
               "Provider: " + provider + "\n" +
               "No. Handphone: " + phoneNumber + "\n" +
               "Total Bayar: Rp" + String.format("%,.0f", amount) + "\n" +
               "Status: Lunas (Simulasi E-Wallet OTP Terverifikasi)";
    }
}
