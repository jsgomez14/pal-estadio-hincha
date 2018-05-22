package com.nn.palestadio.android_java;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.support.annotation.NonNull;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.nfc.NfcAdapter;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


public class HomeActivity extends AppCompatActivity {

    public static final String MIME_TEXT_PLAIN = "text/plain";
    public static final String TAG = "NfcDemo";


    private float heightFab;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    TextView textViewVerified, textViewName, textViewCedula;
    FloatingActionButton floatingButton;
    private static ProgressBar progressBar;


    private final static String PREF_NAME = "prefs";
    private final static String KEY_CEDULA = "cedula";
    private static final String KEY_USERUID = "useruid";
    private final static String KEY_BOLETAS = "boletas";

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    FirebaseUser user;
    DatabaseReference myRef;
    UserInformation userInfo;

    private NfcAdapter mNfcAdapter;

    private FirebaseFirestore db;
    private static ArrayList<MatchInformation> boletas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        userInfo = new UserInformation();

        sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        db = FirebaseFirestore.getInstance();
        boletas = new ArrayList<>();

        textViewName = findViewById(R.id.textViewName);
        textViewVerified = findViewById(R.id.textViewVerified);
        textViewCedula = findViewById(R.id.textViewCedula);
        progressBar = (ProgressBar) findViewById(R.id.progressBarBoleta);
        user = FirebaseAuth.getInstance().getCurrentUser();
        myRef = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());
        loadUserInformation();

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, 0);
        }

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);

        drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.setVisibility(View.GONE);
        navigationView = findViewById(R.id.navigationView);
        navigationView.setCheckedItem(R.id.nav_home);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.nav_home:
                        drawerLayout.closeDrawers();
                        Intent intentHome = new Intent(HomeActivity.this, HomeActivity.class);
                        startActivity(intentHome);
                        drawerLayout.setVisibility(View.GONE);
                        return true;

                    case R.id.nav_camera:
                        drawerLayout.closeDrawers();
                        if(verificarConexion()){
                            if (user.isEmailVerified()) {
                                Intent intentCamera = new Intent(HomeActivity.this, ScanTicketActivity.class);
                                startActivity(intentCamera);
                            } else {
                                setSnackBar(findViewById(R.id.layoutHome), "Para esto requieres verificación de correo.");
                            }
                        } else {
                            setSnackBar(findViewById(R.id.layoutHome),"Para registrar una boleta requieres de conexión a internet.");
                        }
                        drawerLayout.setVisibility(View.GONE);
                        return false;

                    case R.id.nav_map:
                        drawerLayout.closeDrawers();
                        if(verificarConexion()) {
                            drawerLayout.closeDrawers();
                            Intent intentMap = new Intent(HomeActivity.this, MapsActivity.class);
                            startActivity(intentMap);
                        } else {
                            setSnackBar(findViewById(R.id.fab),"Para ver como llegar al estadio requieres de conexión a internet.");
                        }
                        drawerLayout.setVisibility(View.GONE);
                     return false;

                    case R.id.nav_exit:
                        FirebaseAuth.getInstance().signOut();
                        drawerLayout.closeDrawers();
                        drawerLayout.setVisibility(View.GONE);
                        finish();
                        Intent intentLog = new Intent(HomeActivity.this, MainActivity.class);
                        startActivity(intentLog);
                        setSnackBar(findViewById(R.id.fab),"Has finalizado tu sesión con éxito");
                        return false;

                }
                return false;
            }
        });



        floatingButton = (FloatingActionButton) findViewById(R.id.fab);
        floatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(verificarConexion()){
                    if (user.isEmailVerified()) {
                        Intent intent = new Intent(HomeActivity.this, ScanTicketActivity.class);
                        startActivity(intent);
                    } else {
                        setSnackBar(findViewById(R.id.layoutHome), "Para esto requieres verificación de correo.");
                    }
                } else {
                    setSnackBar(findViewById(R.id.layoutHome),"Para registrar una boleta requieres de conexión a internet.");
                }
                
            }
        });


        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if(mNfcAdapter == null)
        {
            return;
        }
        handleIntent(getIntent());

    }

    private void loadUserInformation() {
        if(user != null)
        {
            String uidAnterior = sharedPreferences.getString(KEY_USERUID, "");
            if (!sharedPreferences.getString(KEY_CEDULA, "").isEmpty() && user.getUid().equals(uidAnterior)) {
                textViewCedula.setText("Cédula: " + sharedPreferences.getString(KEY_CEDULA, ""));
                String cedula = sharedPreferences.getString(KEY_CEDULA, "");
                crearBoletas(cedula);
            } else {
                if (!verificarConexion()) {
                    textViewCedula.setText("Cédula: " + sharedPreferences.getString(KEY_CEDULA, ""));
                    crearBoletas(sharedPreferences.getString(KEY_CEDULA, ""));
                } else {
                    myRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            showData(dataSnapshot);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

            if(user.getDisplayName() != null)
            {
                textViewName.setText(user.getDisplayName());
            }
            if(user.isEmailVerified())
            {
                textViewVerified.setVisibility(View.GONE);

            }else{
                textViewVerified.setTextColor(Color.parseColor("#b20000"));
                textViewVerified.setText("Verifica tu correo para acceder al resto de funcionalidades.");
            }


        }

    }

    public void crearBoletas(String cedula) {
        if(verificarConexion()) {
        db.collection("boleteria")
                .whereEqualTo("cedula",cedula)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if(task.getResult().isEmpty()){
                                setSnackBar(findViewById(R.id.fab),"No hay boletas guardadas.");
                            } else {
                                for (DocumentSnapshot document : task.getResult()) {

                                    editor.putString(KEY_BOLETAS, document.getData().toString());
                                    editor.apply();
                                    String[] array = document.getData().toString().split(",");

                                    //Asiento
                                    String[] asiento = array[5].split("=");

                                    //Equipo1
                                    String[] equipo1 = array[6].split("=");
                                    //Equipo2
                                    String[] equipo2 = array[0].split("=");
                                    //Fecha
                                    String[] fecha = array[8].split("=");
                                    //Hora
                                    String[] hora = array[1].split("=");
                                    //Tribuna
                                    String[] tribuna = array[4].split("=");

                                    MatchInformation boleta = new MatchInformation(asiento[1],equipo1[1],equipo2[1],fecha[1].substring(0,10),hora[1],tribuna[1],document.getData().toString());
                                    boletas.add(boleta);
                                    initRecyclerView();
                                }
                            }

                        } else {
                            Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Existen problemas con la base de datos", Snackbar.LENGTH_SHORT);
                            snackbar.show();
                        }
                    }
                });
        } else {
            Log.d("Conexion", "No hay conexion");
            if(!sharedPreferences.getString(KEY_BOLETAS,"").isEmpty()) {
                String[] array = sharedPreferences.getString(KEY_BOLETAS,"").split(",");

                String[] asiento = array[5].split("=");
                Log.d("Asiento", "El asiento es: " + asiento[1]);
                //Equipo1
                String[] equipo1 = array[6].split("=");
                //Equipo2
                String[] equipo2 = array[0].split("=");
                //Fecha
                String[] fecha = array[8].split("=");
                //Hora
                String[] hora = array[1].split("=");
                //Tribuna
                String[] tribuna = array[4].split("=");

                MatchInformation boleta = new MatchInformation(asiento[1],equipo1[1],equipo2[1],fecha[1].substring(0,10),hora[1],tribuna[1],sharedPreferences.getString(KEY_BOLETAS,""));
                boletas.add(boleta);
                initRecyclerView();
            } else {
                Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "No existen boletas en el almacenamiento interno", Snackbar.LENGTH_SHORT);
                snackbar.show();
            }

        }

    }

    private void showData(DataSnapshot dataSnapshot) {
        userInfo.setCedula(dataSnapshot.getValue(UserInformation.class).getCedula());
        textViewCedula.setText("Cédula: "+userInfo.getCedula());
        crearBoletas(userInfo.getCedula());
        editor.putString(KEY_CEDULA, userInfo.getCedula());
        editor.apply();

    }

    private void initRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(layoutManager);
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(this, boletas);
        recyclerView.setAdapter(adapter);
    }

    public static void createQR(Context context, int position){
        progressBar.setVisibility(View.VISIBLE);
        Intent QRCode = new Intent(context, QRCodeGenerated.class);
        QRCode.putExtra("EXTRA_BARCODE_SCANNED", boletas.get(position).getInfo());
        context.startActivity(QRCode);

    }

    public static void stopProgressBar() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    protected void onStart() {
        super.onStart();

        user.reload();
        if(user == null)
        {
            finish();
            startActivity(new Intent(this, MainActivity.class));
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        /**
         * This method gets called, when a new Intent gets associated with the current activity instance.
         * Instead of creating a new activity, onNewIntent will be called. For more information have a look
         * at the documentation.
         *
         * In our case this method gets called, when the user attaches a Tag to the device.
         */
        if(mNfcAdapter == null)
        {
            return;
        }

        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        String action = intent.getAction();
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {

            String type = intent.getType();
            if (MIME_TEXT_PLAIN.equals(type)) {

                Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                new NdefReaderTask(this).execute(tag);

            } else {
                Log.d(TAG, "Wrong mime type: " + type);
            }
        } else if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {

            // In case we would still use the Tech Discovered Intent
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            String[] techList = tag.getTechList();
            String searchedTech = Ndef.class.getName();

            for (String tech : techList) {
                if (searchedTech.equals(tech)) {
                    new NdefReaderTask(this).execute(tag);
                    break;
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        /**
         * It's important, that the activity is in the foreground (resumed). Otherwise
         * an IllegalStateException is thrown.
         */
        if(mNfcAdapter != null) setupForegroundDispatch(this, mNfcAdapter);
    }

    @Override
    protected void onPause() {
        /**
         * Call this before onPause, otherwise an IllegalArgumentException is thrown as well.
         */
        if(mNfcAdapter != null) stopForegroundDispatch(this, mNfcAdapter);

        super.onPause();
    }

    /**
     * @param activity The corresponding {@link Activity} requesting the foreground dispatch.
     * @param adapter The {@link NfcAdapter} used for the foreground dispatch.
     * Took from: https://code.tutsplus.com/tutorials/reading-nfc-tags-with-android--mobile-17278
     */
    public static void setupForegroundDispatch(final Activity activity, NfcAdapter adapter) {
        final Intent intent = new Intent(activity.getApplicationContext(), activity.getClass());
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        final PendingIntent pendingIntent = PendingIntent.getActivity(activity.getApplicationContext(), 0, intent, 0);

        IntentFilter[] filters = new IntentFilter[1];
        String[][] techList = new String[][]{};

        // Notice that this is the same filter as in our manifest.
        filters[0] = new IntentFilter();
        filters[0].addAction(NfcAdapter.ACTION_NDEF_DISCOVERED);
        filters[0].addCategory(Intent.CATEGORY_DEFAULT);
        try {
            filters[0].addDataType(MIME_TEXT_PLAIN);
        } catch (IntentFilter.MalformedMimeTypeException e) {
            throw new RuntimeException("Check your mime type.");
        }

        adapter.enableForegroundDispatch(activity, pendingIntent, filters, techList);
    }

    /**
     * @param activity The corresponding {@link HomeActivity} requesting to stop the foreground dispatch.
     * @param adapter The {@link NfcAdapter} used for the foreground dispatch.
     * Took from: https://code.tutsplus.com/tutorials/reading-nfc-tags-with-android--mobile-17278
     */
    public static void stopForegroundDispatch(final Activity activity, NfcAdapter adapter) {
        adapter.disableForegroundDispatch(activity);
    }

    public void setSnackBar(View coordinatorLayout, String snackTitle) {
        final Snackbar snackbar = Snackbar.make(coordinatorLayout, snackTitle, Snackbar.LENGTH_SHORT);
        snackbar.addCallback(new BaseTransientBottomBar.BaseCallback<Snackbar>() {
            @Override
            public void onShown(Snackbar transientBottomBar) {
                float heightSnack = transientBottomBar.getView().getHeight();
                heightFab = floatingButton.getY();
                floatingButton.setY(heightFab-heightSnack);
                super.onShown(transientBottomBar);
            }

            @Override
            public void onDismissed(Snackbar transientBottomBar, int event) {
                floatingButton.setY(heightFab);

                super.onDismissed(transientBottomBar, event);
            }
        });
        snackbar.show();
        View view = snackbar.getView();
        TextView txtv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
        txtv.setGravity(Gravity.CENTER_HORIZONTAL);

    }

    public boolean verificarConexion() {
        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if(netInfo != null && netInfo.isConnectedOrConnecting())
            return true;
        else
            return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.setVisibility(View.VISIBLE);
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                drawerLayout.openDrawer(Gravity.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
