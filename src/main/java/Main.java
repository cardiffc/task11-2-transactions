import java.util.*;

public class Main {
    private static Random random = new Random();

    public static void main(String[] args) {
        Bank bank = new Bank();
        int clientCount = random.nextInt(100);
        HashMap<String,Account> bankAccounts = new HashMap<>();
        for (int i = 0; i < clientCount ; i++) {
            String accNumber = String.valueOf(Math.abs(random.nextLong()));
            bankAccounts.put(accNumber, new Account(Math.abs(random.nextLong()), accNumber));
        }
        bank.setAccounts(bankAccounts);

    }

//    private static void fillAcoounts (HashMap<String,Account> accounts)
//    {
//        int
//
//    }
}
