package ch.ethz.inf.vs.a3.solution.message;

import android.os.AsyncTask;
import android.widget.TextView;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.PriorityQueue;
import java.util.concurrent.ExecutionException;

import ch.ethz.inf.vs.a3.R;
import ch.ethz.inf.vs.a3.message.MessageComparator;
import ch.ethz.inf.vs.a3.udpclient.NetworkConsts;

/**
 * Created by james on 30.10.15.
 */
public class UDPClient {



    private AsyncTask<Void, Void, Void> async_cient;

    public void send(final String m, final String url, final String port) {
        //PASCAL: what is a "cient"? -  must be something clever, i will call mine cient too! :)
        async_cient = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                DatagramSocket ds = null;


                try {
                    InetAddress iadr = InetAddress.getByName(url);
                    ds = new DatagramSocket();
                    DatagramPacket dp;
                    dp = new DatagramPacket(m.getBytes(), m.length(), iadr, Integer.getInteger(port));
                    ds.setBroadcast(true);
                    ds.send(dp);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (ds != null) {
                        ds.close();
                    }
                }
                return null;
            }
            protected void onPostExecute(Void result) {
                super.onPostExecute(result);
            }
        };

        async_cient.execute(); //If that doesn't work, try the one below

        // async_client.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    //PASCAL: i do it the same way as jimmy does it in the method "send()", but i can't guarantee it works!
    public String retrieveLog(){

        AsyncTask<Void, Void, String> async_cient = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
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
                        priorityQueue.add(new Message(packet.getData().toString()));
                    }

                    //display messages

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
