/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ClientPackage;
import java.io.*;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jame
 */
public class SendRequestUserThread implements Runnable{
    Socket socket;
    DataOutputStream dos;        
    String typeRequest;
    String receiver;
    String content;
    Thread t;
    public SendRequestUserThread(Socket s, String typeRequest, String receiver, String content) 
    {
        try 
        {
            socket = s;
            t = new Thread(this);
            dos = new DataOutputStream(s.getOutputStream());
            this.typeRequest =  typeRequest;
            this.receiver = receiver;
            this.content = content;
            System.out.println("Khoi tao thread");
        } 
        catch (IOException ex) 
        {
            Logger.getLogger(SendRequestUserThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    @Override
    public void run(){
        try {
            System.out.println("Da nhan duoc yeu cau logout");
            dos.writeUTF(typeRequest);
            dos.writeUTF(receiver);
            dos.writeUTF(content);
            dos.flush();
        } catch (IOException ex) {
            Logger.getLogger(SendRequestUserThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

class sendFileThread implements Runnable{
    Socket socket;
    DataOutputStream dos;        
    String typeRequest;
    String receiver;
    File file;
    Thread t;
    public sendFileThread(Socket s, String typeRequest, String receiver, File f) 
    {
        try 
        {
            socket = s;
            t = new Thread(this);
            dos = new DataOutputStream(s.getOutputStream());
            this.typeRequest =  typeRequest;
            this.receiver = receiver;
            file = f;
            System.out.println("Khoi tao thread");
        } 
        catch (IOException ex) 
        {
            Logger.getLogger(SendRequestUserThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    @Override
    public void run(){
        try {
            System.out.println("Da nhan duoc yeu cau gui file");
            
            dos.writeUTF(typeRequest);
            dos.writeUTF(receiver);
            dos.writeUTF(file.getName());
            dos.writeUTF(String.valueOf(file.length()));
//            dos.flush();
//            bw.close();
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));            
            int fileSize = (int)file.length();
            byte[] bytes = new byte[fileSize];
            System.out.println("File name: " + file.getName());
            System.out.println("receiver: " + receiver);
            System.out.println("File size: " + fileSize);
            bis.read(bytes, 0, fileSize);
            
          
            int bufferSize = 2048;
            int offset = 0;
            while(fileSize > 0)
            {
                dos.write(bytes, offset, Math.min(fileSize, bufferSize));
                offset += Math.min(fileSize, bufferSize);
                fileSize -= bufferSize;
            }
            dos.flush();
//            dos.close();
            
            System.out.println("Gui file cho server");

        } catch (IOException ex) {
            Logger.getLogger(SendRequestUserThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}