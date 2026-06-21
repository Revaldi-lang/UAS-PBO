package payment;

public class CashPayment implements Payment {
    private double cashReceived;

    public CashPayment(double cashReceived) {
        this.cashReceived = cashReceived;
    }

    public double getCashReceived() {
        return cashReceived;
    }

    @Override
    public String processPayment(double amount) {
        if (cashReceived < amount) {
            return "Pembayaran Gagal: Uang tunai kurang sebesar Rp" + String.format("%,.0f", (amount - cashReceived));
        }
        double change = cashReceived - amount;
        return "Pembayaran Tunai Berhasil!\n" +
               "Total Bayar: Rp" + String.format("%,.0f", amount) + "\n" +
               "Uang Diterima: Rp" + String.format("%,.0f", cashReceived) + "\n" +
               "Kembalian: Rp" + String.format("%,.0f", change);
    }
}
