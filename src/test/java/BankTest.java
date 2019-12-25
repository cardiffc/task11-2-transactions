import junit.framework.TestCase;
import org.junit.Assert;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * ВНИМАНИЕ! ДЛЯ ПРОХОЖДЕНИЯ ТЕСТОВ НЕОБХОДИМО УМЕНЬШИТЬ ЗАДЕРЖКУ В РАБОТЕ СБ ДО 1мсек ИНАЧЕ НА БОЛЬШОМ КОЛИЧЕСТВЕ
 * ТРАНСАКЦИЙ ТЕСТЫ БУДУТ ИДТМ ОЧЕНЬ ДОЛГО
 */

public class BankTest extends TestCase {
    Random random = new Random();
    Bank bank;
    int threadsCount;
    int accountsNum;
    HashMap<String,Account> accounts;
    int transCount;
    int transToCheck;
    int transPerThread;
    int transToCheckThread;
    long startTotalAmount;
    ArrayList<String> accountNumbers = new ArrayList<>();

    @Override
    protected void setUp() throws Exception {
        bank = new Bank();
        threadsCount = 10;
        /** Задаем количество и создаем счета */
        accountsNum = 20;
        bank.createAccounts(accountsNum);
        accounts = bank.getAccounts();
        /** Задаем количество предполагаемых трансакций */
        transCount = 10000;
        /** Получим кол-во трансакций на поток */
        transPerThread = transCount / threadsCount;
        transToCheckThread = transToCheck / threadsCount;
        /** Получим все номера счетов для отдельной обработки и стартовую сумму по всем счетам */
        startTotalAmount = 0;
        for (Map.Entry account : accounts.entrySet()) {
            accountNumbers.add((String) account.getKey());
            startTotalAmount += accounts.get(account.getKey()).getMoney();
        }
    }
    /** Тестируем метод создания счетов на нормальную работу в многопотоке */
    public void testCreateAccountsForRaceCondition() {
        Bank bank = new Bank();
        ArrayList<Thread> threads = new ArrayList<>();
        for (int i = 0; i < 2 ; i++) {
            threads.add(new Thread(() -> bank.createAccounts(1000)));
        }
        threads.forEach(Thread::start);
        try {
            for (Thread thread : threads) {
                thread.join();
            }
        } catch (InterruptedException e) { e.printStackTrace();}

        Assert.assertEquals(2000,bank.getAccounts().size());
    }
    /** Тестируем метод создания счетов на отсутствие стартовых отрицательных балансов */
    public void testCreateAccountsForNegativeBalance() {
        Bank bank = new Bank();
        ArrayList<Thread> threads = new ArrayList<>();
        for (int i = 0; i < 2 ; i++) {
            threads.add(new Thread(() -> bank.createAccounts(1000)));
        }
        threads.forEach(Thread::start);
        try {
            for (Thread thread : threads) {
                thread.join();
            }
        } catch (InterruptedException e) { e.printStackTrace();}
        int negativeCount = 0;

            for (Map.Entry account : bank.getAccounts().entrySet())             {
                Account testAccount = bank.getAccounts().get(account.getKey());
                negativeCount += (testAccount.getMoney() >= 0) ? 0 : 1;
            }
        Assert.assertEquals(0, negativeCount);
    }

    /** Тест сравнивает общее количество денег на всех счетах до проведения транзакций и после */
    public void testTransferForMoney()
    {
        makeThreads();
       /** Получим сумму по всем счетам после проведения 1000000 трансакций */
       long finishTotalAmount = 0;
       for (Map.Entry account : accounts.entrySet()) {
           finishTotalAmount += accounts.get(account.getKey()).getMoney();
       }
       /** Сравним начальные и конечные показатели суммы на всех счетах */
       Assert.assertEquals(startTotalAmount, finishTotalAmount);
    }
    public void testTransferForNegativeBalance() {
        makeThreads();
        /** Получим количество счетов с отрицательным балансом **/
        int negativeCount = 0;
        for (Map.Entry account : accounts.entrySet()) {
            negativeCount += (bank.getAccounts().get(account.getKey()).getMoney() < 0) ? 1 : 0;
        }
        /** Проверяем отсутствие отрицательного баланса */
        Assert.assertEquals(0, negativeCount);
    }

    /** Тест проверяет, что количество заблокированных СБ счетов отличается от нуля. Поскольку все идет на
     * псевдослучайных вычислениях на малом количестве трансакций может быть ноль.
     */
    public void testBlockedAccounts() {
        makeThreads();
        int blockedAccounts = 0;
        for (Map.Entry account : accounts.entrySet()) {
            if (accounts.get(account.getKey()).isBlocked()) {
                blockedAccounts++;
            }
        }
        Assert.assertNotEquals(0,blockedAccounts);
    }

    /** Метод для запуска потоков и отработки транзакций */
    public void makeThreads () {
        /** Создаем потоки */
        ArrayList<Thread> threads = new ArrayList<>();
        for (int i = 0; i < threadsCount; i++) {
            threads.add(new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int j = 0; j < transPerThread; j++) {
                        String fromAccount = accountNumbers.get(random.nextInt(accountNumbers.size()));
                        String toAccount = accountNumbers.get(random.nextInt(accountNumbers.size()));
                        try {
                            bank.transfer(fromAccount, toAccount, (j == random.nextInt(500))
                                    ? 52500 : random.nextInt(49000));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }));
        }
        /** Запускаем и ждем потоки */
        threads.forEach(Thread::start);
        try {
            for (Thread thread : threads) {
                thread.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
