package com.example.connorfoody.smartcup;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.app.Notification.*;
import android.app.Notification;
import android.app.NotificationManager;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.GraphViewStyle;
import com.jjoe64.graphview.LineGraphView;

import java.util.UUID;

public class MainBasic extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_basic);

        //BluetoothReader m_reader = new BluetoothReader(debug_handler,"20:14:04:18:23:59", UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
        BluetoothReader m_reader = new BluetoothReader(graph_handler,"20:14:04:18:23:59", UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
        m_reader.start();

        TextView text = (TextView) findViewById(R.id.output);
        text.setText("");

        times_read = 0;

        predict_series = new GraphViewSeries(new GraphViewData[] {});
        data_series = new GraphViewSeries(new GraphViewData[] {});
        goal_series = new GraphViewSeries(new GraphViewData[] {});
        goal_series.getStyle().color = Color.RED;
        data_series.getStyle().color = Color.BLUE;


        // set up the graph
        graph = new LineGraphView(this, "");
        ((LineGraphView) graph).setDrawBackground(true);

        graph.addSeries(goal_series);
        graph.addSeries(data_series);

        graph.setViewPort(1, 100);
        graph.setScalable(true);

        graph.getGraphViewStyle().setNumVerticalLabels(4);
        graph.getGraphViewStyle().setVerticalLabelsWidth(100);

        graph.getGraphViewStyle().setNumHorizontalLabels(5);
        graph.getGraphViewStyle().setGridStyle(GraphViewStyle.GridStyle.VERTICAL);

        LinearLayout layout = (LinearLayout) findViewById(R.id.graph);
        layout.addView(graph);

        m_notification = new Notification.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle("My notification")
                .setContentText("Hello World!").build();
        m_notification_manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

    }
    private GraphViewSeries predict_series;
    private GraphViewSeries data_series;
    private GraphViewSeries goal_series;
    private GraphView graph;
    private int times_read;
    Notification m_notification;
    NotificationManager m_notification_manager;
    // simple handler to help with debugging
    public Handler debug_handler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            TextView text = (TextView) findViewById(R.id.output);
            if(msg.what == 1){
                text.setText("\n " + ((SCMessage) msg.obj).temp);
            }
            else if(msg.what == -1){
                text.append("\nERROR: " + msg.obj + "\n");
            }
            else{
                super.handleMessage(msg);
            }
        }
    };
    // handler for the graph
    public Handler graph_handler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            if(msg.what == 1){
                SCMessage m_msg = (SCMessage)msg.obj;
                if(m_msg.state == TemperatureProcessor.max){

                }
                else if(m_msg.state == TemperatureProcessor.decreasing){
                }
                if(times_read == 50){
                    m_notification_manager.notify(1, m_notification);
                }
                data_series.appendData(new GraphViewData((double)times_read * 0.5, (double)Math.round(m_msg.temp * 10.0) / 10.0 ), true, 100);
                goal_series.appendData(new GraphViewData((double)times_read * 0.5, 136), true, 100);
                times_read++;
            }
            else if(msg.what == -1){
                // if we have an error
                TextView text = (TextView) findViewById(R.id.output);
                text.append("\nERROR: " + msg.obj + "\n");
                System.out.println("ERROR: " + msg.obj);
            }
            else{
                super.handleMessage(msg);
            }
        }
    };
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_basic, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
