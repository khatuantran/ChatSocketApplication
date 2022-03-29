/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ServerPackage;
import java.net.*;
import java.io.*; 
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author jame
 */
public class RequestUserThread implements Runnable{

    Thread t;
    private Socket socket;
    private DataInputStream dis;
    private DataOutputStream dos;
    private String name;
    
    

    public DataInputStream getDis() {
        return dis;
    }
    public Socket getSocket()
    {
        return socket;
    }
    public DataOutputStream getDos() {
        return dos;
    }

    public String getName() {
        return name;
    }
    public RequestUserThread(Socket s, String name) 
    {
       
        try 
        {
            t = new Thread(this);
            socket = s;
            this.name = name;
            dis = new DataInputStream(socket.getInputStream());
            dos = new DataOutputStream(socket.getOutputStream());
        } 
        catch (IOException ex) 
        {
            Logger.getLogger(RequestUserThread.class.getName()).log(Level.SEVERE, null, ex);
        }
       
        
    }
        
    
    
    @Override
    public void run(){
        while(!Thread.interrupted())
        {
            try 
            {
                String request = dis.readUTF();
                System.out.println(request);
                if(request == null)
                {
                    break;
                }
                if(request.equals("text"))
                {
                    String receiver = dis.readUTF();
                    String content = dis.readUTF();
                    System.out.println(receiver);
                    System.out.println(content);
                    for (int i = 0; i < ServerThread.handleThreads.size(); i++)
                    {
                        RequestUserThread temp = ServerThread.handleThreads.get(i);
                        if(temp.getName().equals(receiver))
                        {
                            System.out.println("Da tim thay user can nhan");
                            temp.getDos().writeUTF("Message from");
                            temp.getDos().writeUTF(this.name);
                            temp.getDos().writeUTF(content);                            
                            temp.getDos().flush();
                        }
                    }
                }
                else if(request.equals("send file"))
                {
                    String receiver = dis.readUTF();
                    String fileName = dis.readUTF();
                    int fileSize = Integer.parseInt(dis.readUTF());
                    int bufferSize =  2048;
                    byte bytes[] = new byte[bufferSize];
                    System.out.println(receiver);
                    System.out.println("File nhan o sever: " + "");
                    for (int i = 0; i < ServerThread.handleThreads.size(); i++)
                    {
                        RequestUserThread temp = ServerThread.handleThreads.get(i);
                        
                        if(temp.getName().equals(receiver))
                        {
                            System.out.println("Da tim thay user can nhan");
                            temp.getDos().writeUTF("send file from");
                            temp.getDos().writeUTF(this.name);
                            temp.getDos().writeUTF(fileName);
                            temp.getDos().writeUTF(String.valueOf(fileSize));
                            while(fileSize > 0)
                            {
                                dis.read(bytes, 0, Math.min(fileSize, bufferSize));
                                temp.getDos().write(bytes, 0, Math.min(fileSize, bufferSize));
                                fileSize -= bufferSize;
                            }
                            temp.getDos().flush();
                            System.out.println("Da gui xong cho clien can nhan");

//                            InputStream is = socket.getInputStream();
//                            OutputStream os = temp.getSocket().getOutputStream();
//                            byte[] bytes = new byte[16 * 1024];
//                            int count;
//                            while((count = is.read(bytes)) > 0)
//                            {
//                                os.write(bytes, 0, count);
//                            }
                        }
                    }
                }
                else if(request.equals("Sign Out"))
                {
                    System.out.println("Dan nhan sign out o server");

                    ServerThread.handleThreads.remove(this);
                    ServerThread.updateOnlineUser();
                    dos.close();
                    dis.close();
                    System.out.println("Da break");
                    break;
                   
                }
            } 
            catch (IOException ex) 
            {
                Logger.getLogger(RequestUserThread.class.getName()).log(Level.SEVERE, null, ex);
            }           
        }
        try 
        {
            socket.close();
        } 
        catch (Exception e) 
        {
            System.out.println(e.getMessage());
        }
    }
}
