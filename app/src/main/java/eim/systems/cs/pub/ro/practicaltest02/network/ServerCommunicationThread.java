package eim.systems.cs.pub.ro.practicaltest02.network;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.ResponseHandler;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.BasicResponseHandler;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import eim.systems.cs.pub.ro.practicaltest02.general.Constants;
import eim.systems.cs.pub.ro.practicaltest02.general.Utilities;

public class ServerCommunicationThread extends Thread {

    private Socket socket;

    public ServerCommunicationThread(Socket socket) {
        if (socket != null) {
            this.socket = socket;
            Log.d(Constants.TAG, "[COMM SERVER] Created communication thread with: " + socket.getInetAddress() + ":" + socket.getLocalPort());
        }
    }

    public void run() {
        try {
            BufferedReader reader = Utilities.getReader(socket);
            PrintWriter writer = Utilities.getWriter(socket);

            String queryText = reader.readLine();

            Log.d(Constants.TAG, "[COMM SERVER] Getting information");
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(Constants.API_SERVER + queryText);
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            String pageSourceCode = httpClient.execute(httpGet, responseHandler);

            String newData = "";
            JSONObject content = new JSONObject(pageSourceCode);
            JSONArray jsonArray = content.getJSONArray("RESULTS");
            for (int k = 0; k < jsonArray.length(); k++) {
                JSONObject jsonObject = jsonArray.getJSONObject(k);
                String tempData =  jsonObject.getString("name");
                newData += tempData + " ";
            }

            writer.println(newData);
            writer.flush();

        } catch (IOException ioException) {
            Log.e(Constants.TAG, "An exception has occurred: " + ioException.getMessage());
            if (Constants.DEBUG) {
                ioException.printStackTrace();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        finally {
            if (socket != null) {
                try {
                    socket.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
