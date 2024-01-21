package iestrassierra.jlcamunas.trasstarea.preferencias;

import static androidx.core.content.ContentProviderCompat.requireContext;

import static com.google.android.material.internal.ContextUtils.getActivity;
import static java.security.AccessController.getContext;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import iestrassierra.jlcamunas.trasstarea.R;

//Clase donde tenemos las preferncias
public class SettingsActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    private boolean theme = true;
    String tamañoLetraIndex = "";
    String tamañoLetra = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        theme = sharedPreferences.getBoolean("tema",true);



    }



    @Override
    protected void onResume() {
        super.onResume();
        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);
    }

    //Metodo que cada vez que se cambia una preferencia se ejecuta y cambia el tema y el tamaño de la letra
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, @Nullable String key) {

            if(key == null){

            }else if(key.equalsIgnoreCase("tema")){

                     boolean switchTema = sharedPreferences.getBoolean("tema", false);

                    //Cambiamos a tema oscuro dependiendo del switch
                    if (switchTema) {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    } else {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    }
            }else if(key.equalsIgnoreCase("tamañoLetra")){
                String fontSize = sharedPreferences.getString("tamañoLetra", "Mediana");
                // float size = getResources().getConfiguration().fontScale;
                if(fontSize.equalsIgnoreCase("1")){
                    ajustarTamanoLetraEnTodaLaApp(getResources(),0.8f);
                }else if (fontSize.equalsIgnoreCase("2") ){
                    ajustarTamanoLetraEnTodaLaApp(getResources(),1.2f);
                }else if (fontSize.equalsIgnoreCase("3") ){
                    ajustarTamanoLetraEnTodaLaApp(getResources(),1.5f);
                }
            }
        recreate();
    }

    //Mismo metodo para cambiar el tamaño de la letra que en el MAIN.
    public static void ajustarTamanoLetraEnTodaLaApp(Resources resources, float nuevoTamano) {
        Configuration configuration = resources.getConfiguration();

        // Crear una nueva configuración basada en la configuración actual
        Configuration newConfig = new Configuration(configuration);

        // Modificar la escala de fuente en la nueva configuración
        newConfig.fontScale = nuevoTamano;

        // Aplicar la nueva configuración al recurso
        resources.updateConfiguration(newConfig, null);

        // Actualizar la densidad de píxeles en función de la nueva configuración
        DisplayMetrics metrics = resources.getDisplayMetrics();
        resources.updateConfiguration(newConfig, metrics);
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
            Preference botonReset = findPreference("resetBoton");
            botonReset.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(@NonNull Preference preference) {
                    resetPreferencias();
                    return true;
                }
            });
        }

        private void resetPreferencias(){
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            getActivity().recreate();
        }
    }




    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        if (item.getItemId() == android.R.id.home){

            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}