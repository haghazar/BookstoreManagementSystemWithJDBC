package am.picsartacademy.bookstore;

import java.util.Date;

public class Book {
    private int saleId;
    private int bookId;
    private int customerId;
    private Date dateOfSale;
    private int quantitySold;
    private double totalPrice;

    public Book(int saleId, int bookId, int customerId, Date dateOfSale, int quantitySold, double totalPrice) {
        this.saleId = saleId;
        this.bookId = bookId;
        this.customerId = customerId;
        this.dateOfSale = dateOfSale;
        this.quantitySold = quantitySold;
        this.totalPrice = totalPrice;
    }

    public int getSaleId() {
        return saleId;
    }

    public void setSaleId(int saleId) {
        this.saleId = saleId;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public Date getDateOfSale() {
        return dateOfSale;
    }

    public void setDateOfSale(Date dateOfSale) {
        this.dateOfSale = dateOfSale;
    }

    public int getQuantitySold() {
        return quantitySold;
    }

    public void setQuantitySold(int quantitySold) {
        this.quantitySold = quantitySold;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    @Override
    public String toString() {
        return "Sale{" +
                "saleId=" + saleId +
                ", bookId=" + bookId +
                ", customerId=" + customerId +
                ", dateOfSale=" + dateOfSale +
                ", quantitySold=" + quantitySold +
                ", totalPrice=" + totalPrice +
                '}';
    }
}
