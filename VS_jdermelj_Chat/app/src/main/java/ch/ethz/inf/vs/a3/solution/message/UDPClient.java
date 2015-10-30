package ch.ethz.inf.vs.a3.solution.message;

import android.os.AsyncTask;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.SocketAddress;

/**
 * Created by james on 30.10.15.
 */
public class UDPClient {

    //PASCAL: @jimmy, i bruche es statisches attribut datagramSocket, damit i ir ChatActivity drmit cha schaffe!
    //ha iz mau es läärs gmacht, damiti scho druf cha zuegriffe :)
    public static DatagramSocket datagramSocket;


    private AsyncTask<Void, Void, Void> async_cient;

    public void send(final String m, final String url, final String port) {
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

}
