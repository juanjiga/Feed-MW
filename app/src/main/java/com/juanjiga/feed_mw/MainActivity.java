package com.juanjiga.feed_mw;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;

import java.util.HashMap;
import java.util.LinkedList;

public class MainActivity extends AppCompatActivity {

    static final String DATA_TITLE = "T";
    static final String DATA_LINK  = "L";
    static LinkedList<HashMap<String, String>> data;
    static String feedUrl;
    static String feedmarcaUrl = "http://estaticos.marca.com/rss/futbol/atletico.xml";
    static String feedasUrl = "http://masdeporte.as.com/tag/rss/atletico_madrid/a";
    static String feedmundodUrl = "http://mundo-deportivo10.webnode.es/rss/all.xml";
    private ProgressDialog progressDialog;

    /**
     * Android nos presenta la restricciones que no podemos alterar los elementos de interfaz
     * gr‡fica en un hilo de ejecuci—n que no sea el principal por lo que es necesario utilizar
     * un manejador(Handler) para enviar un mensaje de un hilo a otro cuando la carga de datos
     * haya terminado.
     */
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

        setTitle("Feed Atlético de Madrid");
        ListView lv = (ListView) findViewById(R.id.lstData);

        Button boton_as = (Button) findViewById(R.id.button_as);
        boton_as.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setTitle("Noticias del As");
                feedUrl=feedasUrl;
                loadData();

                /*ListView lv = (ListView) findViewById(R.id.lstData);
                if (lv.getAdapter() != null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setMessage("Ya ha cargado datos, ¿está seguro de hacerlo de nuevo?")
                            .setCancelable(false)
                            .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    loadData();
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            })
                            .create()
                            .show();
                } else {
                    feedUrl=feedmarcaUrl;
                    loadData();
                }*/
            }
        });
        Button boton_marca = (Button) findViewById(R.id.button_marca);
        boton_marca.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setTitle("Noticias del Marca");
                feedUrl=feedmarcaUrl;
                loadData();
            }
        });
        Button boton_mundod = (Button) findViewById(R.id.button_mundod);
        boton_mundod.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setTitle("Noticias en Mundo Deportivo");
                feedasUrl=feedmundodUrl;
                loadData();
            }
        });

        //ListView lv = (ListView) findViewById(R.id.lstData);
        lv.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> av, View v, int position,
                                    long id) {
                /**
                 * Obtenemos el elemento sobre el que se presion—
                 */
                HashMap<String, String> entry = data.get(position);

                /**
                 * Preparamos el intent ACTION_VIEW y luego iniciamos la actividad (navegador en este caso)
                 */
                Intent browserAction = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(entry.get(DATA_LINK)));
                startActivity(browserAction);
            }
        });
    }
    /**
     * Funci—n auxiliar que recibe una lista de mapas, y utilizando esta data crea un adaptador
     * para poblar al ListView del dise–o
     * */
    private void setData(LinkedList<HashMap<String, String>> data){
        SimpleAdapter sAdapter = new SimpleAdapter(getApplicationContext(), data,
                R.layout.fila,
                new String[] { DATA_TITLE, DATA_LINK },
                new int[] { R.id.textView1, R.id.textView2 });
        ListView lv = (ListView) findViewById(R.id.lstData);
        lv.setAdapter(sAdapter);
    }
    /**
     * Funci—n auxiliar que inicia la carga de datos, muestra al usuario un di‡logo de que
     * se est‡n cargando los datos y levanta un thread para lograr la carga.
     */
    private void loadData() {
        progressDialog = ProgressDialog.show(
                MainActivity.this,
                "",
                "Por favor espere mientras se cargan los datos...",
                true);

        new Thread(new Runnable(){
            @Override
            public void run() {
                XMLParser parser = new XMLParser(feedUrl);
                Message msg = progressHandler.obtainMessage();
                msg.obj = parser.parse();
                progressHandler.sendMessage(msg);
            }}).start();
    }

}
