public class Account
{
    private long money;
    private String accNumber;
    private boolean isBlocked;

    public Account(long money, String accNumber) {
        this.money = money;
        this.accNumber = accNumber;
    }

    public void setMoney(long money) {
        this.money = money;
    }

    public void setAccNumber(String accNumber) {
        this.accNumber = accNumber;
    }

    public void setBlocked(boolean blocked) {
        isBlocked = blocked;
    }


    public long getMoney() {
        return money;
    }

    public String getAccNumber() {
        return accNumber;
    }

    public boolean isBlocked() {
        return isBlocked;
    }




}

