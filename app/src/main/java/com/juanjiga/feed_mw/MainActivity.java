package com.juanjiga.feed_mw;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;

import java.util.HashMap;
import java.util.LinkedList;

public class MainActivity extends AppCompatActivity {

    static final String DATA_TITLE = "T";
    static final String DATA_LINK  = "L";
    static final String DATA_DESCRIPTION = "D";
    static LinkedList<HashMap<String, String>> data;
    static String rssUrlMarca = "http://estaticos.marca.com/rss/futbol/atletico.xml";
    static String rssUrlAs = "http://masdeporte.as.com/tag/rss/atletico_madrid/a";
    static String rssUrlMundoatleti ="http://www.mundodeportivo.com/feed/rss/futbol/atletico-madrid";
    private ProgressDialog progressDialog;

    private final Handler progressHandler = new Handler() {
        @SuppressWarnings("unchecked")
        public void handleMessage(Message msg) {
            if (msg.obj != null) {
                data = (LinkedList<HashMap<String, String>>)msg.obj;
                setData(data);
            }
            progressDialog.dismiss();
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.mipmap.ic_launcher);

        setTitle("RSS Atlético de Madrid");
        ListView lv = (ListView) findViewById(R.id.lstData);

        ImageButton boton_as = (ImageButton) findViewById(R.id.button_as);
        boton_as.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setTitle("Feed del As");
                loadData(rssUrlAs);
            }
        });
        ImageButton boton_marca = (ImageButton) findViewById(R.id.button_marca);
        boton_marca.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setTitle("Feed del Marca");
                loadData(rssUrlMarca);
            }
        });
        ImageButton boton_md = (ImageButton) findViewById(R.id.button_md);
        boton_md.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setTitle("Feed del Mundo Deportivo");
                loadData(rssUrlMundoatleti);
            }
        });
        //ListView lv = (ListView) findViewById(R.id.lstData);
        lv.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> av, View v, int position,
                                    long id) {
                HashMap<String, String> entry = data.get(position);
                Intent browserAction = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(entry.get(DATA_LINK)));
                startActivity(browserAction);
            }
        });
    }
    private void setData(LinkedList<HashMap<String, String>> data){
        SimpleAdapter sAdapter = new SimpleAdapter(getApplicationContext(), data, R.layout.fila,
                new String[] { DATA_TITLE, DATA_LINK, DATA_DESCRIPTION },
                new int[] { R.id.textView1, R.id.textView2, R.id.textView3});
        ListView lv = (ListView) findViewById(R.id.lstData);
        lv.setAdapter(sAdapter);
    }
    private void loadData(final String rssUrl) {
        progressDialog = ProgressDialog.show(
                MainActivity.this,
                "",
                "Cargando datos...",
                true);

        new Thread(new Runnable(){
            @Override
            public void run() {
                XMLParser parser = new XMLParser(rssUrl);
                Message msg = progressHandler.obtainMessage();
                msg.obj = parser.parse();
                progressHandler.sendMessage(msg);
            }}).start();
    }
}
