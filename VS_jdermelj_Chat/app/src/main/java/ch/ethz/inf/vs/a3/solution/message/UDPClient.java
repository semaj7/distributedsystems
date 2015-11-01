package ch.ethz.inf.vs.a3.solution.message;

import android.os.AsyncTask;
import android.provider.ContactsContract;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.PriorityQueue;
import java.util.concurrent.ExecutionException;

import ch.ethz.inf.vs.a3.message.MessageComparator;
import ch.ethz.inf.vs.a3.udpclient.NetworkConsts;

/**
 * Created by james on 30.10.15.
 */
public class UDPClient {

    public UDPClient(final String m, final String url, final String port) {
        message = m;
        this.url = url;
        this.port = port;
    }
    public final String message;
    public final String url;
    public final String port;
    public static DatagramSocket datagramSocket;
    private AsyncTask<Void, Void, Void> async_cient;

    public boolean send() {
        async_cient = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {

                try {
                    InetAddress iadr = InetAddress.getByName(url);
                    datagramSocket = new DatagramSocket();
                    DatagramPacket packet;
                    packet = new DatagramPacket(message.getBytes(), message.length(), iadr, Integer.getInteger(port));
                    datagramSocket.setBroadcast(true);
                    datagramSocket.send(packet);
                    int attempt = 0;
                    //  DatagramPacket response = new DatagramPacket();
                    while (attempt < 5) {
                        try {
                            datagramSocket.send(packet);
                        } catch (IOException e) {
                            e.printStackTrace();
                            return null;
                        }
                        try {
                            // datagramSocket.receive(response);
                            return null;
                        } catch (Exception e) {
                            attempt++;
                            continue;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();

                } finally {
                    if (datagramSocket != null) {
                        datagramSocket.close();
                    }
                }
                return null;
            }

            protected void onPostExecute(Void result) {
                super.onPostExecute(result);
            }
        };

        System.out.println("----registering: execute");
        async_cient.execute(); //If that doesn't work, try the one below
        System.out.println("registered");
        return true;

        // async_client.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    //PASCAL: i do it the same way as jimmy does it in the method "send()", but i can't guarantee it works!
    public String retrieveLog(){

        //i assume the third parameter is for the return-type, so i set it to String instead of Void
        AsyncTask<Void, Void, String> async_cient = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                //this will be the one we display
                String logString="";

                //creating a priorityQueue with, say, at largest 50 messages.
                PriorityQueue<Message> priorityQueue;
                priorityQueue=new PriorityQueue<Message>(50, new MessageComparator());

                //create a new socket for listening
                DatagramSocket ds;
                try {
                    ds = new DatagramSocket();
                    //listen for messages and buffer them
                    //use a socket timeout (what's this?)
                    ds.setSoTimeout(NetworkConsts.SOCKET_TIMEOUT);
                    while(true) {
                        byte[] buffer = new byte[NetworkConsts.PAYLOAD_SIZE];
                        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                        try {
                            ds.receive(packet);
                        } catch (SocketTimeoutException e) {
                            break;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        //add packet to the PriorityQueue
                        //here i use the fantastic parser which is built into the constructor of the Message.class
                        priorityQueue.add(new Message(packet.getData().toString()));
                    }

                    //extract the message in order from the priorityQueue and add them to the string, each on a new line.
                    while(!priorityQueue.isEmpty()) {
                        logString += "\n"+priorityQueue.poll().getContent();
                    }

                } catch (SocketException e) {
                    e.printStackTrace();
                }
                return logString;
            }
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
            }
        };

        String ret="";
        //If that doesn't work, try the one below
        try {
            ret= async_cient.execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return ret;
        // async_client.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
}
