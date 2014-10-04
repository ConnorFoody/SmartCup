package com.example.connorfoody.smartcup;

/**
 * Created by connorfoody on 10/4/14.
 */

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import java.io.InputStream;
import java.util.UUID;

/**
 * Created by connorfoody on 10/3/14.
 */
public class BluetoothReader extends Thread {
    private Handler m_handler = null;
    private BluetoothAdapter m_adapter = null;
    private BluetoothSocket m_socket = null;

    private boolean bt_OK = false;
    private UUID m_UUID = null;
    private String m_address = "";

    public BluetoothReader(Handler handler, String address, UUID id){
        m_handler = handler;
        m_adapter = BluetoothAdapter.getDefaultAdapter();
        m_address = address;
        m_UUID = id;
    }

    public void run(){
        Message msg = null;
        if(m_adapter == null){
            // need to throw some kind of error for this
        }
        test();
        check_state();
        if(!bt_OK){ // if bluetooth is not enabled, go ahead and break
            msg = m_handler.obtainMessage(-1, "bluetooth not enabled");
            m_handler.sendMessage(msg);
            return;
        }

        // pointer to remote node
        BluetoothDevice device = m_adapter.getRemoteDevice(m_address);

        // make the connection
        try{
            m_socket = device.createRfcommSocketToServiceRecord(m_UUID);
        }
        catch(Exception e){
            e.printStackTrace();
            msg = m_handler.obtainMessage(-1, "could not build the bluetooth socket");
            m_handler.sendMessage(msg);
        }

        // b/c its power intensive to have discovery on
        m_adapter.cancelDiscovery();

        // establish connection
        try{
            m_socket.connect();

        }
        catch(Exception e){
            try{
                // try to close the socket
                m_socket.close();
            }
            catch (Exception ee){
                ee.printStackTrace();
                msg = m_handler.obtainMessage(-1, "trouble opening and closing the bluetooth socket");
                m_handler.sendMessage(msg);
            }
            msg = m_handler.obtainMessage(-1, "trouble with opening the bluetooth socket");
            e.printStackTrace();
        }

        // build the instream
        InputStream bt_is = null;
        try {
            bt_is = m_socket.getInputStream();
        }
        catch (Exception e){
            e.printStackTrace();
        }

        // setup buff reading

        int bytes = 0;
        while(true){
            try{
                byte[] buffer = new byte[1024];
                bytes = bt_is.read(buffer);
                String o_str = new String(buffer);
                msg = m_handler.obtainMessage(1, "msg: " + o_str);
                m_handler.sendMessage(msg);

            }
            catch(Exception e){
                e.printStackTrace();
                msg = m_handler.obtainMessage(-1, "reading/thread");
                m_handler.sendMessage(msg);
                break;
            }
        }
    }

    /**
     * Checks the state to see if bluetooth is enabled
     */
    private void check_state(){
        bt_OK = false;
        if(m_adapter == null){
            Message msg = m_handler.obtainMessage(-1, "bluetooth not supported");
            m_handler.sendMessage(msg);
        }
        else{
            if(m_adapter.isEnabled()){
                Message msg = m_handler.obtainMessage(1, "bluetooth enabled");
                m_handler.sendMessage(msg);
                bt_OK = true;
            }
            else{
                // its not enabled, give an error
                Message msg = m_handler.obtainMessage(-1, "bluetooth not enabled, please enable it");
                m_handler.sendMessage(msg);
            }
        }
    }
    private void test(){
        for(int i = 0; i < 4; i++){
            Message msg = m_handler.obtainMessage(1, "" + i);
            m_handler.sendMessage(msg);
            try{
                this.sleep(250);

            }catch(Exception e){
                e.printStackTrace();
                Message msg_err = m_handler.obtainMessage(-1, e);
                m_handler.sendMessage(msg_err);
            }
        }
        Message msg = m_handler.obtainMessage(1, "done");
        m_handler.sendMessage(msg);
    }
}
