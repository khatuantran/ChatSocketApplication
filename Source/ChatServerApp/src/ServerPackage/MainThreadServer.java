/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ServerPackage;

import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jame
 */
class ServerThread implements Runnable{
    private  int port;
    Thread t;
//    MainThreadServer mainThread;
//    Socket socket;
    private ServerSocket serverSocket;
    private DataInputStream dis;
    private DataOutputStream dos;
    private Socket socket; 
    
    private ArrayList<User> users;
    public static ArrayList<RequestUserThread> handleThreads;
    private final String filePath = ".\\users.txt";
    public ServerThread(int port) 
    {
        try 
        {
            serverSocket = new ServerSocket(port);
            handleThreads = new ArrayList<>();
            this.port = port;
            this.readUserList();
            t = new Thread(this);
            System.out.println("Khoi tao server thread thanh cong");
        } 
        catch (IOException ex) {
            Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    public void closeSeverSocket()
    {

        try 
        {
            if(serverSocket != null)
            {
                if(socket != null)
                {
                    socket.close();
                    dos.close();
                    dis.close();
                }
                serverSocket.close();
                
            }
        } 
        catch (IOException ex) 
        {
//            Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
        }   

        
  }
    public void readUserList()
    {
        try
        {
            users = new ArrayList<>();
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), "utf-8"));
            String str;
            while((str = br.readLine()) != null)
            {
                String[] temp = str.split("-");
                User user = new User(temp[0], temp[1], temp[2]);
                System.out.println(user.toString());
                users.add(user);
            }
            br.close();
        }
        catch(IOException e)
        {
            System.out.println(e.getMessage());
        }
    }
    
    public void addUser(String name, String userName, String password)
    {
        User user = new User(name, userName, password);
        users.add(user);
    }
    
    public boolean isExistUser(String username, String name)
    {
        for(int i = 0; i < users.size(); i++)
        {
            String userName = users.get(i).getUserName();
            String nameOfUser = users.get(i).getName();
            if(userName.equals(username) || name.equals(nameOfUser))
            {
                return true;                    
            }
        }
        return false;
    }
    
    public boolean isCorectPassword(String username, String password)
    {
        for(int i = 0; i < users.size(); i++)
        {
            if(users.get(i).getUserName().equals(username) && users.get(i).getPassword().equals(password))                
            {               
                return true;
            }
        }
        return false;
    }
    
    public void writeUserList()
    {
        try
        {
            BufferedWriter br = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath), "utf-8"));
            if(users.size() >= 1)
            {
                br.write(users.get(0).toString());
            }
            for(int i = 1; i < users.size(); i++)
            {
                br.write("\n" + users.get(i).toString());
            }
            br.close();
        }
        catch(IOException e)
        {
            System.out.println(e.getMessage());
        }
        
    }
    public String findNameByUserName(String username)
    {
        String name = "";
        for (int i = 0; i < users.size(); i++)
        {
            if(users.get(i).getUserName().equals(username))
            {
                name = users.get(i).getName();
                break;
            }
        }
        return name;
    }
    public static void updateOnlineUser()
    {
        System.out.println("so nguoi online" + handleThreads.size());
        for (int i = 0; i < handleThreads.size(); i++)
        {
            
            RequestUserThread temp = handleThreads.get(i);
            try 
            {
                int numberUsers = handleThreads.size() - 1;
                DataOutputStream dos = temp.getDos();
                dos.writeUTF("update");
                dos.writeUTF(String.valueOf(numberUsers));
                for(int j = 0; j < handleThreads.size(); j++)
                {
                    if(j != i)
                    {
                        dos.writeUTF(handleThreads.get(j).getName());
                    }
                }
                dos.flush();
            } 
            catch (IOException ex) 
            {
                Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    boolean isOnline(String name)
    {
        for(int i = 0; i < handleThreads.size(); i++)
        {
            if(handleThreads.get(i).getName().equals(name))
            {
                return true;
            }
        }
        return false;
    }
    @Override
    public void run(){                

        try 
        {
            while(!Thread.interrupted()) 
            {                  
                socket = serverSocket.accept();
                System.out.println("Da aceept");
                dos = new DataOutputStream(socket.getOutputStream());
                dis = new DataInputStream(socket.getInputStream());
                String request = dis.readUTF();                
                if(request.equals("Sign Up"))
                {
                    System.out.println("Da vao SignUp");
                    String name = dis.readUTF();
                    String username = dis.readUTF();
                    String password = dis.readUTF();
                    if(isExistUser(username, name))
                    {
                        dos.writeUTF("Error");
                        dos.flush();                       
                    }
                    else
                    {
                        this.addUser(name, username, password);
                        writeUserList();
                        dos.writeUTF("Success");
                        dos.flush();
//                        break;
                    }
                }
                else if(request.equals("Sign In"))
                {
                    System.out.println("Da vao Sign In");
                    String username = dis.readUTF();
                    String password = dis.readUTF();
                    System.out.println(username);
                    System.out.println(password);
                    if(isCorectPassword(username, password))
                    {
                        
                        
                        String name = findNameByUserName(username);
                        if(isOnline(name))
                        {
                            dos.writeUTF("User Online");
                            dos.flush();
                        }
                        else
                        {
                            dos.writeUTF(name);
                            dos.flush();
                            RequestUserThread requestHandler = new RequestUserThread(socket, name);
                            handleThreads.add(requestHandler);
//                          String a = handleThreads.get(0).getName();
//                          System.out.println(a);
                            requestHandler.t.start();
                            updateOnlineUser();
                        }                                                 
                        
                    }
                    else
                    {
                        dos.writeUTF("Error");
                        dos.flush();
//                        break;
                    }
                    
                }
            }
        } 
        catch (IOException ex) {
            Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
}



class User{
    private String userName;
    private  String password;
    private String name;


    public User(String name, String userName, String password) {
        this.userName = userName;
        this.password = password;
        this.name = name;
    }

    @Override
    public String toString() {
        return name + "-" + userName + "-" + password;
    }
    
    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }  
}



