package payment;

public interface Payment {
    /**
     * Memproses pembayaran sebesar jumlah tertentu.
     * @param amount Jumlah yang harus dibayar.
     * @return Pesan hasil proses pembayaran (struk/status).
     */
    String processPayment(double amount);
}
