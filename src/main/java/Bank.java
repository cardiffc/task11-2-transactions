import java.util.HashMap;
import java.util.Random;

public class Bank
{
    private static final long CHECKAMOUNT = 50000;
    /**
     * Делаем volatile, т.к. в силу многопоточности может быть ситуация с кэшем. Т.е. получили account, СБ его
     * заблокировала но в CPU Cache остался не блоченый. Транзакция пройдет. Или баланс изменился, а в кэше остался старый.
     * А так будет в MEMORY актуальные данные
     */
    private volatile HashMap<String, Account> accounts = new HashMap<>();
    private final Random random = new Random();

    public void setAccounts(HashMap<String, Account> accounts) {
        this.accounts = accounts;
    }

    public HashMap<String, Account> getAccounts() {
        return accounts;
    }

    public synchronized boolean isFraud(String fromAccountNum, String toAccountNum, long amount)
            throws InterruptedException
    {
        Thread.sleep(1);
        return random.nextBoolean();
    }

    public void transfer(String fromAccountNum, String toAccountNum, long amount) throws InterruptedException {
        Account fromAccount = accounts.get(fromAccountNum);
        Account toAccount = accounts.get(toAccountNum);
        /** Проверяем подозрительные транзакции и блокируем счета в случае чего */
        if (amount >= CHECKAMOUNT) {
            boolean toBlock = isFraud(fromAccountNum, toAccountNum, amount);
            if (toBlock) {
                /** Блокировка счетов */
                lockAccounts(fromAccount, toAccount, toBlock);
             }
        }
        if (fromAccount.isBlocked() || toAccount.isBlocked()) {
            // Тут должен быть ответ пользователю о том, что проведение заблокировано, но чтобы не грузить тесты убрал

        } else if (fromAccount.getMoney() > amount) {
            /** Переводим деньги */
            transferAmount(fromAccount, toAccount, amount);

        } else{
            System.out.println("Транзакция невозможна - недостаточно средств");
        }
    }
    public long getBalance (String accountNum) {
        Account account = accounts.get(accountNum);
        return account.getMoney();
    }
    public void createAccounts (int number) {
        for (int i = 0; i < number ; i++) {
            String accNumber = String.valueOf(Math.abs(random.nextLong()));
            long moneyAmount = Math.abs(random.nextLong());
            synchronized (accounts) {
                accounts.put(accNumber, new Account(moneyAmount, accNumber));
            }
        }
    }
    public synchronized void transferAmount(Account fromAccount, Account toAccount, long amount) {
        fromAccount.setMoney(fromAccount.getMoney() - amount);
        toAccount.setMoney(toAccount.getMoney() + amount);
    }
    public synchronized void lockAccounts (Account fromAccount, Account toAccount, boolean toBlock) {
        fromAccount.setBlocked(toBlock);
        toAccount.setBlocked(toBlock);
    }
}
