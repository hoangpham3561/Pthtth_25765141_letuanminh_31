package bai4_danhba;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ContactServiceImpl extends UnicastRemoteObject implements ContactService {
    private final Map<String, String> phoneBook;

    public ContactServiceImpl() throws RemoteException {
        super();
        this.phoneBook = new ConcurrentHashMap<>();
    }

    @Override
    public void addContact(String name, String phone) throws RemoteException {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Ten lien he khong duoc de trong!");
        }
        phoneBook.put(name.trim(), phone != null ? phone.trim() : "");
        System.out.println("[ContactServer] Da them/cap nhat: " + name + " -> " + phone);
    }

    @Override
    public String findContact(String name) throws RemoteException {
        if (name == null) return null;
        return phoneBook.get(name.trim());
    }

    @Override
    public boolean deleteContact(String name) throws RemoteException {
        if (name == null) return false;
        boolean removed = phoneBook.remove(name.trim()) != null;
        System.out.println("[ContactServer] Xoa lien he " + name + ": " + (removed ? "Thanh cong" : "Khong tim thay"));
        return removed;
    }
}
