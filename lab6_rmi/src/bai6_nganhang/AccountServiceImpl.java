package bai6_nganhang;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class AccountServiceImpl extends UnicastRemoteObject implements AccountService {
    private double balance;

    public AccountServiceImpl(double initialBalance) throws RemoteException {
        super();
        this.balance = initialBalance;
    }

    @Override
    public synchronized double getBalance() throws RemoteException {
        return this.balance;
    }

    @Override
    public synchronized void deposit(double amount) throws RemoteException {
        if (amount <= 0) {
            throw new IllegalArgumentException("So tien nap phai lon hon 0!");
        }
        this.balance += amount;
        System.out.println("[AccountServer] Nap " + amount + " -> So du moi: " + this.balance);
    }

    @Override
    public synchronized boolean withdraw(double amount) throws RemoteException {
        if (amount <= 0) {
            throw new IllegalArgumentException("So tien rut phai lon hon 0!");
        }
        if (this.balance >= amount) {
            this.balance -= amount;
            System.out.println("[AccountServer] Rut " + amount + " thanh cong -> So du con: " + this.balance);
            return true;
        } else {
            System.out.println("[AccountServer] Rut " + amount + " that bai! So du hien tai: " + this.balance);
            return false;
        }
    }
}
