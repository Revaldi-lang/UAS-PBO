package payment;

public class QRISPayment implements Payment {

    @Override
    public String processPayment(double amount) {
        return "Pembayaran QRIS Berhasil!\n" +
               "Total Bayar: Rp" + String.format("%,.0f", amount) + "\n" +
               "Status: Lunas (QRIS Terverifikasi)";
    }
}
