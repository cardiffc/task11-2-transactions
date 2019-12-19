public class Transaction {
    private int id;
    private long amount;
    private String fromAcoount;
    private String toAccount;
    private boolean isFraud;

    public Transaction(int id, long amount, String fromAcoount, String toAccount) {
        //this.id = id;
        this.amount = amount;
        this.fromAcoount = fromAcoount;
        this.toAccount = toAccount;
        this.isFraud = false;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public String getFromAcoount() {
        return fromAcoount;
    }

    public void setFromAcoount(String fromAcoount) {
        this.fromAcoount = fromAcoount;
    }

    public String getToAccount() {
        return toAccount;
    }

    public void setToAccount(String toAccount) {
        this.toAccount = toAccount;
    }

    public boolean isFraud() {
        return isFraud;
    }

    public void setFraud(boolean fraud) {
        isFraud = fraud;
    }





}
