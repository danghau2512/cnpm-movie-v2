package Model;

public class BookingRecord extends PaymentInfo {
    private String customerName;
    private String customerEmail;
    private String createdAt;
    private String paymentMethod;
    private boolean payable;
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String value) { customerName = value; }
    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String value) { customerEmail = value; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String value) { createdAt = value; }
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String value) { paymentMethod = value; }
    public boolean isPayable() { return payable; }
    public void setPayable(boolean value) { payable = value; }
}
