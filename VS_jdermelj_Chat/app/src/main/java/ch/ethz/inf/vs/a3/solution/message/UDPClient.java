package ch.ethz.inf.vs.a3.solution.message;

import android.os.AsyncTask;
import android.provider.ContactsContract;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.PriorityQueue;
import java.util.concurrent.ExecutionException;

import ch.ethz.inf.vs.a3.message.MessageComparator;
import ch.ethz.inf.vs.a3.udpclient.NetworkConsts;

/**
 * Created by james on 30.10.15.
 */
public class UDPClient {



    private AsyncTask<String, Integer, Boolean> async_client;
    public final Callback callback;

    public UDPClient(Callback cb){
        callback =cb;
    }
    public UDPClient(){
        callback = null;
    }
    public void safeSend(String message, String url, String port) { //With send until receiving ack

        final String u = url;
        final int p = Integer.parseInt(port);
        final String m = message;

        async_client = new AsyncTask<String, Integer, Boolean>() {
            @Override
            protected Boolean doInBackground(String... params) {

                try {
                    InetAddress iAdr = InetAddress.getByName(params[1]);
                    DatagramSocket socket;
                    socket = new DatagramSocket();
                    socket.setSoTimeout(NetworkConsts.SOCKET_TIMEOUT);
                    //socket.setSoTimeout(500); //only for debugging.
                    DatagramPacket packet = new DatagramPacket
                            (params[0].getBytes(), params[0].length(), iAdr, p);
                    //datagramSocket.setBroadcast(true);
                    int attempt = 0;
                    byte[] buffer = new byte[NetworkConsts.PAYLOAD_SIZE];
                    DatagramPacket response = new DatagramPacket(buffer, buffer.length);

                    while (attempt < 5) { // We attempt to register up to 5 times.
                        try {
                            socket.send(packet);
                        } catch (IOException e) {
                            System.out.println("Error, couldn't send (in savesend). ");
                            e.printStackTrace();
                        }
                        try {
                            socket.receive(response);
                            if (Arrays.toString(response.getData()).equalsIgnoreCase("ack")) {
                                System.out.println("we got a response. it was an ack. ");
                                return true;//if the message is ack, we succeeded
                            } else {  //else, we continue
                                System.out.println("we got a response, but it was not an ack ");
                                attempt++;
                                continue;
                            }
                        } catch (SocketTimeoutException e) {
                        //TODO: At the moment, it always lands here. We already tried different ip-addresses.
                            System.out.println("we didn't get a response at all");
                            attempt++;
                             //return true; // decomment this, if you want to see the rest
                            continue;

                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();

                }
                return false;
            }

            protected void onPostExecute(Boolean result) {
                if (result) {
                    callback.startChatActivity();
                } else {
                    callback.registrationFailed();
                }
            }
        };

        System.out.println("----registering: execute");
        async_client.execute(m, u);
        return;
    }

    public String retrieveLog(){

        //i assume the third parameter is for the return-type, so i set it to String instead of Void
        AsyncTask<Void, Void, String> async_client = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                //this will be the one we display
                String logString="";

                //creating a priorityQueue with, say, at most 50 messages.
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
        try {
            ret= async_client.execute().get();
            // async_client.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return ret;
    }
}
