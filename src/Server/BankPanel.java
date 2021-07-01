package Server;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class BankPanel implements Runnable{
    private Socket socket ;
    private ObjectInputStream inputStream ;
    private ObjectOutputStream outputStream ;
    private User user ;
    private ArrayList<User> users ;
    private Account account ;

    public BankPanel(Socket socket , ArrayList<User>users){
        this.users = users ;
        this.socket = socket ;
        try {
            inputStream = new ObjectInputStream(socket.getInputStream()) ;
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        try {
            outputStream = new ObjectOutputStream(socket.getOutputStream()) ;
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        user = null ;
    }
    @Override

    public void run() {
        String s = "" ;
        try {
            while (!s.equals("exit")) {
                s = inputStream.readUTF();
                switch (s) {
                    case "log in":
                        logIn();
                        break;
                    case "sign up":
                        signUp();
                        break;
                    case "new account":
                        newAccount();
                        break;
                    case "get accounts":
                        getMyAccounts();
                        break;
                    case "open account":
                        openAccount();
                        break;
                    case "account status":
                }
            }
        }catch (IOException e){
            System.out.println(e.getMessage());
        }
    }

    private void logIn() throws IOException {
        String identity = inputStream.readUTF() ;
        String password = inputStream.readUTF() ;
        User temp = searchUser(identity);
        if (temp == null || !temp.getPassword().equals(password))
            outputStream.writeUTF("Nok");
        else {
            user = temp;
            outputStream.writeUTF("ok");
        }
    }

    private void signUp() throws IOException {
        String identity = inputStream.readUTF() ;
        String email = inputStream.readUTF() ;
        String phone = inputStream.readUTF() ;
        String password = inputStream.readUTF() ;
        User temp = searchUser(identity) ;
        if (temp == null){
            user = new User(identity , email , password , phone) ;
            users.add(user) ;
            outputStream.writeUTF("ok");
        }
        else{
            outputStream.writeUTF("Nok");
        }
    }

    private void newAccount() throws IOException {
        String type = inputStream.readUTF() ;
        AccountType accountType ;

        if (type.equals("A"))
            accountType = AccountType.A ;
        else
            accountType = AccountType.B ;

        String password = inputStream.readUTF() ;
        user.addMyAccount(new Account(accountType , password));
        outputStream.writeUTF("ok");
    }

    private User searchUser(String identity){
        if (users == null)
            return null ;
        for (int i = 0; i < users.size(); i++) {
            if (identity.equals(users.get(i).getIdentityNumber()))
                return users.get(i);
        }
        return null ;
    }

    private void getMyAccounts() throws IOException {
        outputStream.writeObject(user.getMyAccounts());
    }

    private void openAccount() throws IOException {
        account = (user.getMyAccounts()).get(inputStream.readInt()) ;
    }

    private void accountStatus() throws IOException {
        outputStream.writeUTF(account.getAccountType());
        outputStream.writeDouble(account.getBalance());
        outputStream.writeObject(account.getTransactions());
    }
}
