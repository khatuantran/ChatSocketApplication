/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ClientPackage;

import java.io.*;
import java.net.*;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JList;
import java.util.HashMap;
/**
 *
 * @author jame
 */
public class ReciveRespondUserThread extends Thread{
    Socket socket;
    DataInputStream dis;        
    Thread t;
    JList<String> onlineUser;
    JList<String> chatMessage;
    HashMap<String, Vector<String>> hashMap; //luu danh sach chat cua user nay voi cac user khac
    HashMap<String, ByteArrayOutputStream> fileDownload;
    public ReciveRespondUserThread(Socket s,JList<String> chatMessage, JList<String> onlineUser) 
    {
        try 
        {
            socket = s;
            t = new Thread(this);
            dis = new DataInputStream(s.getInputStream());
            this.onlineUser = onlineUser;
            this.chatMessage = chatMessage;
            hashMap = new HashMap<String, Vector<String>>(); 
            fileDownload = new HashMap<String, ByteArrayOutputStream>();
        } 
        catch (IOException ex) 
        {
            Logger.getLogger(SendRequestUserThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public Vector<String> findNameNewUser(Vector<String> onlineUserList)
    {
        Vector<String> newUser = new Vector<>();
        for(int i = 0; i < onlineUserList.size(); i++)
        {
            String name = onlineUserList.get(i);
            if (!hashMap.containsKey(name))
            {
                System.out.println("user moi: " + name);
                newUser.add(name);
            }
        }
        return newUser;
    }
    
    
    public void setChatList(String nameUser)
    {
        if(nameUser == null)
        {
            chatMessage.setListData(new Vector<String>());
        }
        else
        {
            Vector<String> chatList = hashMap.get(nameUser);
            System.out.println("Chat with" + nameUser);
            chatMessage.setListData(chatList);
        }
    }
    
    public void addMessage(String sendMessage, String reciever)
    {
        
        hashMap.get(reciever).add("You: " + sendMessage); // them mot tin nhan vao hop thoai voi 1 user khac
        System.out.println(hashMap.get(reciever).size());
        setChatList(reciever);
    }
    public String findNameDeleteUser(Vector<String> onlineUserList)
    {
        String nameUser = "";
        Vector<String> currentOnlineUser = new Vector<>(hashMap.keySet());
        for(int i = 0; i < currentOnlineUser.size(); i++)
        {
            nameUser = currentOnlineUser.get(i);
            if(!onlineUserList.contains(nameUser))
            {
                System.out.println("User da bi xoa la:" + nameUser);
                break;
            }
        }   
        return nameUser;
    }
    
    @Override
    public void run(){
        try 
        {
           while(!Thread.interrupted())
           {
              String typeRequest =  dis.readUTF();
              if(typeRequest == null)
              {
                  System.out.println("Request empty, close thread"); // yeu cau sign out
                  break;                                //va thoat khoi thread
              }
              if(typeRequest.equals("Message from"))
              {
                  String fromUser = dis.readUTF();
                  String content = dis.readUTF();
                  hashMap.get(fromUser).add(fromUser + ": " + content);
                  onlineUser.setSelectedValue(fromUser, true);
                  setChatList(fromUser);
//                  setChatList(fromUser);
//                  Vector<String> newChatList = hashMap.get(fromUser);
//                  newChatList.add(content);
//                  hashMap.replace(fromUser, hashMap.get(fromUser), newChatList);
                  System.out.println("Da add tin nhan" + fromUser + ": " + content );
              }
              else if(typeRequest.equals("send file from"))
              {
                  String fromUser = dis.readUTF();
//                  InputStream is = new InputStream();
                  String fileName = dis.readUTF();
                  int fileSize = Integer.parseInt(dis.readUTF());
                  ByteArrayOutputStream file = new ByteArrayOutputStream();
                  
                  int offset = 0;
                  int bufferSize = 2048;                  
                  byte bufferRecive[] = new byte[bufferSize];

//                  DataInputStream dis = new DataInputStream(socket.getInputStream());
//                  String a = dis.readUTF();
//                  System.out.println("test: " + a);
                  System.out.println(fromUser);
                  System.out.println(fileName);
                  System.out.println(fileSize);
                  while(fileSize > 0)
                  {
                      int temp = Math.min(fileSize, bufferSize);
                      dis.read(bufferRecive, 0, temp);
                      file.write(bufferRecive, 0, temp);
                      fileSize -= bufferSize;
                  }
                  System.out.println("Doc file tu server gui ve thanh cong");
                  
                  hashMap.get(fromUser).add(fromUser + ": " + fileName);
                  onlineUser.setSelectedValue(fromUser, true);
                  
                  fileDownload.put(fileName, file);
                  setChatList(fromUser);
              }
              else if(typeRequest.equals("update"))
              {
                  String numberUsers = dis.readUTF();
                  
                  if(!numberUsers.equals(""))
                  {                      
                      Vector<String> listUser = new Vector<>();
                      for(int i = 0; i < Integer.parseInt(numberUsers); i++)
                      {
                          String temp = dis.readUTF();
                          listUser.add(temp);
                      }
                      
                      if(listUser.size() > hashMap.size())
                      {
                          Vector<String> newUsers = findNameNewUser(listUser);
                          for(int i = 0; i < newUsers.size(); i++)
                          {
                              String nameOfUser = newUsers.get(i);
                              Vector<String> chatMessangeList = new Vector<>();
                              hashMap.put(nameOfUser, chatMessangeList);
                              System.out.println("put " + nameOfUser);
                          }
                          System.out.println("oneline list: " + hashMap.size());
                      } 
                      else if (listUser.size() < hashMap.size())
                      { 
                            String nameOfUser = findNameDeleteUser(listUser);
                            System.out.println("nameOfUser delete: " + nameOfUser);
                            hashMap.remove(nameOfUser);
                          
                            System.out.println("oneline list: " + hashMap.size());
                          
                            String a[] = hashMap.keySet().toArray(new String[hashMap.size()]);
                            System.out.println("oneline list: " );
                            for(int i = 0; i < hashMap.size(); i++)
                            {
                                System.out.println(a[i]);
                            }
                      }
                      onlineUser.setListData(listUser);
                      
//                      String a[] = hashMap.keySet().toArray(new String[hashMap.size()]);
//                      System.out.println("oneline list: " );
//                      for(int i = 0; i < hashMap.size(); i++)
//                      {
//                          System.out.println(a[i]);
//                      }
                  }
                  
              }
           }
        } 
        catch (IOException ex) 
        {
//            System.out.println("LOi la o day ne");
//            Logger.getLogger(SendRequestUserThread.class.getName()).log(Level.SEVERE, null, ex);
            
        }
    }
}
