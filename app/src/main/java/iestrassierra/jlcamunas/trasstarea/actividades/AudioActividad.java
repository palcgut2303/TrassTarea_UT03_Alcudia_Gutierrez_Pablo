package iestrassierra.jlcamunas.trasstarea.actividades;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import iestrassierra.jlcamunas.trasstarea.R;
import iestrassierra.jlcamunas.trasstarea.adaptadores.TareaViewModel;
import iestrassierra.jlcamunas.trasstarea.modelo.Tarea;

public class AudioActividad extends AppCompatActivity {

    private Tarea tareaReproducir;
    private ProgressBar progressBar;
    private MediaPlayer mp;
    private TareaViewModel audioViewModel;
    private ImageButton btPlay,btPause,btRecord;

    private int posicion = 0;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);
        audioViewModel =
                new ViewModelProvider(this).get(TareaViewModel.class);
        progressBar = findViewById(R.id.progressBar);

        btPlay = findViewById(R.id.ibReproducir);
        btPause = findViewById(R.id.ibPausar);
        btRecord = findViewById(R.id.ibStop);


        Bundle bundle = getIntent().getExtras();
        try {
            if (bundle != null) {
                this.tareaReproducir = bundle.getParcelable("TareaDetallada");
            }
        }catch (NullPointerException e){
            Log.e("Bundle recibido nulo", e.toString());
        }

        String CadenaUriAudio = tareaReproducir.getURL_aud();
        Uri uriAudio = Uri.parse(CadenaUriAudio);
        audioViewModel.setURL_aud(uriAudio);
        audioViewModel.getURL_aud().observe(this, Uri -> {
            mp = MediaPlayer.create(this,Uri);
            progressBar.setMax(mp.getDuration());
        });

        //Handler para comunicar con el hilo principal
        final Handler handler = new Handler(Looper.getMainLooper());
        //Executor para ejecutar en hilos secundarios
        Executor executor = Executors.newSingleThreadExecutor();

        btPlay.setOnClickListener(V -> {
            if (mp != null){
                //En un hilo secundario reproducimos el audio
                executor.execute(()->{
                    if(!mp.isPlaying()) {
                        mp.setLooping(false);
                        mp.start();
                    }
                });
                //En otro hilo secundario avanzamos el ProgressBar
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        if (mp != null) {
                            int currentPosition = mp.getCurrentPosition();
                            progressBar.setProgress(currentPosition);
                        }
                        handler.postDelayed(this, 500);
                    }
                });
            }else{
                Toast.makeText(this, "No hay grabación para reproducir", Toast.LENGTH_SHORT).show();
            }
        });

        btPause.setOnClickListener(v -> {
            if(mp!=null){
                if(mp.isPlaying()){
                    posicion = mp.getCurrentPosition();
                    mp.pause();
                }
                else {
                    mp.seekTo(posicion);
                    mp.start();
                }

            }
        });

        btRecord.setOnClickListener(v -> {
            if(mp!=null){
                if(mp.isPlaying()){
                    mp.stop();
                }
                mp.release();
            }
        });

    }
}
