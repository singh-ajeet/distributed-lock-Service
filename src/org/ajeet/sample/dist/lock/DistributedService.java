package org.ajeet.sample.dist.lock;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class DistributedService {
    private MulticastReceiverThread receiverThread;
    private MulticastSocket receivingSocket;
    private MulticastSocket sendSocket;
    private volatile boolean stopped;
    private InetAddress multicastAddre;
    private LockEventListener listener;
    
    final void init() throws IOException {
    	receivingSocket = new MulticastSocket(4446);
        multicastAddre = InetAddress.getByName("230.0.0.1");
        receivingSocket.joinGroup(multicastAddre);
        receiverThread = new MulticastReceiverThread();
        
        sendSocket = new MulticastSocket(4446);
        sendSocket.joinGroup(multicastAddre);

        receiverThread.start();
        
     }

     public final void dispose() {
    	//System.out.println("Stoping receiver ...");
        stopped = true;
        receiverThread.interrupt();
        System.out.println("MulticastReceiverThread: Stopped.");
    }

    public void registerListener(LockEventListener listener){
    	this.listener = listener;
    }
    
    private final class MulticastReceiverThread extends Thread {
        /**
         * Constructor
         */
        public MulticastReceiverThread() {
            super("Multicast Heartbeat Receiver Thread");
            //setDaemon(true);
        }

        @Override
        public final void run() {
        	System.out.println("============= MulticastReceiverThread ================");
            byte[] buf = new byte[1024];
            try {
                while (!stopped) {
                    DatagramPacket packet = new DatagramPacket(buf, buf.length);
                    try {
                    	System.out.println("MulticastReceiverThread: Waiting for packet ...");
                    	receivingSocket.receive(packet);
                    	String source = packet.getAddress().getHostAddress();
                        byte[] payload = packet.getData();
                        String content = new String(payload);
                        Message msg = new Message(content, source);
                        listener.onMessage(msg);
                    } catch (IOException e) {
                        if (!stopped) {
                            System.out.println("Error receiving heartbeat. " + e.getMessage() );
                        }
                    }
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
 }


 
 

        @Override
        public final void interrupt() {
            try {
            	receivingSocket.leaveGroup(multicastAddre);
            } catch (IOException e) {
                System.out.println("Error leaving group");
            }
            receivingSocket.close();
            super.interrupt();
        }
    }

    public final void sendMessage(Message msg) {
    	System.out.println("============== Sending Msg ==================" );
            try {
                        byte[] buffer =  new byte[1024];
                        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, multicastAddre, 4446);
                        packet.setData(msg.getContent().getBytes());
                        System.out.println("MulticastSenderThread: Sending packets ..");
                        sendSocket.send(packet);
             } catch (IOException e) {
            	System.out.println("Error on multicast socket");
            	e.printStackTrace();
            } catch (Throwable e) {
                e.printStackTrace();
            } 
    }

}
