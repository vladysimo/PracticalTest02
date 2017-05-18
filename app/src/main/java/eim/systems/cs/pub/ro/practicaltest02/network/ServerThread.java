package eim.systems.cs.pub.ro.practicaltest02.network;

import android.util.Log;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import eim.systems.cs.pub.ro.practicaltest02.general.Constants;

public class ServerThread extends Thread {

    private boolean isRunning;

    private ServerSocket serverSocket;

    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    public ServerThread(int port) {
        try {
            Log.d(Constants.TAG, "[SERVER] Created server thread, listening on port " + port);
            serverSocket = new ServerSocket(port);
            isRunning = true;
        } catch (IOException ioException) {
            Log.e(Constants.TAG, "An exception has occurred: " + ioException.getMessage());
            if (Constants.DEBUG) {
                ioException.printStackTrace();
            }
        }
    }

    @Override
    public void run() {
        while (isRunning) {
            Socket socket = null;
            try {
                socket = serverSocket.accept();
                Log.d(Constants.TAG, "[SERVER] Incomming communication " + socket.getInetAddress() + ":" + socket.getLocalPort());
            } catch (SocketException socketException) {
                Log.e(Constants.TAG, "An exception has occurred: " + socketException.getMessage());
                if (Constants.DEBUG) {
                    socketException.printStackTrace();
                }
            } catch (IOException ioException) {
                Log.e(Constants.TAG, "An exception has occurred: " + ioException.getMessage());
                if (Constants.DEBUG) {
                    ioException.printStackTrace();
                }
            }
            if (socket != null) {
                ServerCommunicationThread serverCommunicationThread = new ServerCommunicationThread(socket);
                serverCommunicationThread.start();
            }
        }
    }

    public void stopServer() {
        try {
            isRunning = false;
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch(IOException ioException) {
            Log.e(Constants.TAG, "An exception has occurred: " + ioException.getMessage());
            if (Constants.DEBUG) {
                ioException.printStackTrace();
            }
        }
    }

}
