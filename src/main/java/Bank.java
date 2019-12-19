import java.util.HashMap;
import java.util.List;
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
    /**
     * Создадим generic-объект для того, чтобы использовать его intransic lock, а не всего объекта класа bank или коллекции
     */
    private static Object lock = new Object();
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
        Thread.sleep(1000);
        return random.nextBoolean();
    }

    /**
     * TODO: реализовать метод. Метод переводит деньги между счетами.
     * Если сумма транзакции > 50000, то после совершения транзакции,
     * она отправляется на проверку Службе Безопасности – вызывается
     * метод isFraud. Если возвращается true, то делается блокировка
     * счетов (как – на ваше усмотрение)
     */
    public void transfer(String fromAccountNum, String toAccountNum, long amount) throws InterruptedException {
        Account fromAccount = accounts.get(fromAccountNum);
        Account toAccount = accounts.get(toAccountNum);
        // Проверяем подозрительные транзакции и блокируем счета в случае чего
        if (amount >= CHECKAMOUNT) {
            boolean toBlock = isFraud(fromAccountNum,toAccountNum,amount);
            synchronized (lock) {
                fromAccount.setBlocked(toBlock);
                toAccount.setBlocked(toBlock);
            }
        }
        if (fromAccount.isBlocked() || toAccount.isBlocked()) {
            System.out.println("Извините, проведение транзакции невозможно по причине блокировки");
        } else {
            synchronized (lock) {
                fromAccount.setMoney(fromAccount.getMoney() - amount);
                toAccount.setMoney(toAccount.getMoney() + amount);
            }
        }
    }

    /**
     * TODO: реализовать метод. Возвращает остаток на счёте.
     */
    public long getBalance (String accountNum)
    {
        Account account = accounts.get(accountNum);
        return account.getMoney();
    }
    public void createAccounts (int number) {
        for (int i = 0; i < number ; i++) {
            String accNumber = String.valueOf(Math.abs(random.nextLong()));
            long moneyAmount = Math.abs(random.nextLong());
            synchronized (lock) {
                accounts.put(accNumber, new Account(moneyAmount, accNumber));
            }

        }

    }

}
