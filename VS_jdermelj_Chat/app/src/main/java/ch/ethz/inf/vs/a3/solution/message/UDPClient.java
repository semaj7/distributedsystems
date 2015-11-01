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

    public UDPClient(final String m, final String url, final String port) {
        message = m;
        this.url = url;
        this.port = port;
    }
    public final String message;
    public final String url;
    public final String port;
    public static DatagramSocket datagramSocket;
    private AsyncTask<Void, Void, Boolean> async_client;

    public boolean safeSend() { //With send until receiving ack
        async_client = new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {

                try {
                    InetAddress iAdr = InetAddress.getByName(url);
                    datagramSocket = new DatagramSocket();
                    datagramSocket.setSoTimeout(NetworkConsts.SOCKET_TIMEOUT);
                    DatagramPacket packet = new DatagramPacket(message.getBytes(), message.length(), iAdr, Integer.getInteger(port));
                    //datagramSocket.setBroadcast(true);
                    int attempt = 0;
                    byte[] buffer = new byte[NetworkConsts.PAYLOAD_SIZE];
                    DatagramPacket response = new DatagramPacket(buffer, buffer.length);

                    while (attempt < 5) { // We attempt to register up to 5 times.
                        try {
                            datagramSocket.send(packet);
                        } catch (IOException e) {
                            System.out.println("Error, couldn't send (in savesend). ");
                            e.printStackTrace();
                        }
                        try {

                            datagramSocket.receive(response);
                            if  (Arrays.toString(response.getData()).equalsIgnoreCase("ack")){
                                System.out.println("we got a response. it was an ack. ");
                                return true; //if the message is ack, we succeeded
                            }
                            else{  //else, we continue
                                System.out.println("we got a response, but it was not an ack ");
                                attempt++;
                                continue;
                            }
                        } catch (Exception e) {
                            System.out.println("we didn't get a response at all");
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
                return false;
            }

            protected void onPostExecute(Boolean result) {
                super.onPostExecute(result);
            }
        };

        System.out.println("----registering: execute");
        boolean ret = false;
        try {
            //TODO: is this really the way to do it? i read somewhere that .get() is blocking
            ret =  async_client.execute().get();
            // async_client.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        System.out.println("registering completed. success: " + ret);
        return ret;

    }

    public void send() { //Just send, without waiting for ack. if internal errors -> exception
        async_client = new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {

                try {
                    InetAddress iAdr = InetAddress.getByName(url);
                    datagramSocket = new DatagramSocket();
                    datagramSocket.setSoTimeout(NetworkConsts.SOCKET_TIMEOUT);
                    DatagramPacket packet = new DatagramPacket(message.getBytes(), message.length(), iAdr, Integer.getInteger(port));
                    //datagramSocket.setBroadcast(true);
                    datagramSocket.send(packet);
                } catch (Exception e) {
                    System.out.println("Error, couldn't send. ");
                    e.printStackTrace();
                } finally {
                    if (datagramSocket != null) {
                        datagramSocket.close();
                    }
                }
                return true;
            }

            protected void onPostExecute(Boolean result) {
                super.onPostExecute(result);
            }
        };

        async_client.execute();
        // async_client.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        System.out.println("sending completed");
        return;
    }

    public String retrieveLog(){

        //i assume the third parameter is for the return-type, so i set it to String instead of Void
        AsyncTask<Void, Void, String> async_cient = new AsyncTask<Void, Void, String>() {
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
            //TODO: is this really the way to do it? i read somewhere that .get() is blocking
            ret= async_cient.execute().get();
            // async_client.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return ret;
    }
}
