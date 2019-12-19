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
                /** Лочим по instransic lock одного из счетов, т.к. нужно заблокировать одновременно оба. */
                synchronized (fromAccount) {
                    fromAccount.setBlocked(toBlock);
                    toAccount.setBlocked(toBlock);
                }
            }
        }
        if (fromAccount.isBlocked() || toAccount.isBlocked()) {
            // Тут должен быть ответ пользователю о том, что проведение заблокировано, но чтобы не грузить тесты убрал

        } else if (fromAccount.getMoney() > amount) {

            /**
             * Лочим аккаунт, т.к. вычитаем деньги и если не лочить то в силу состояния гонки можем получить результат
             * с отрицательным балансом в силу многопоточности
             */
            synchronized (fromAccount) {
                fromAccount.setMoney(fromAccount.getMoney() - amount);
            }
            /**
             * Если не ошибаюсь, то тут лочить смысла нет, т.к. мы добавляем деньги.
             * Если этот счет где-то выступает fromAccount (в другом потоке) то он и так залочен.
             * В противном случае в состоянии race condition будут только операции на добавление, которые все равно
             * все сработают и в этом состоянии в данном случае нет ничего страшного. Прошу поправить, если ошибаюсь.
             */
            toAccount.setMoney(toAccount.getMoney() + amount);
        } else{
            System.out.println("Трансакция невозможна - недостаточно средств");
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
}
