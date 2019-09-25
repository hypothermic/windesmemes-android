package nl.hypothermic.windesmemes.android;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import nl.hypothermic.windesmemes.android.data.MemeViewModel;
import nl.hypothermic.windesmemes.android.ui.ActivityTheme;
import nl.hypothermic.windesmemes.android.ui.recycler.MemeAdapter;
import nl.hypothermic.windesmemes.model.Meme;
import nl.hypothermic.windesmemes.model.MemeMode;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String SHARED_PREFERENCES_KEY = "nl.hypothermic.windesmemes.WM.USER_PREFS";
    private static final ActivityTheme DEFAULT_THEME = ActivityTheme.LIGHT;

    private AppBarConfiguration appBarConfig;

    private MemeViewModel viewModel;
    private RecyclerView cardView;

    public static void refreshMemes(final AppCompatActivity activity, final RecyclerView cardView, MemeViewModel model, MemeMode mode) {
        model.getData(mode).observe(activity, new Observer<List<Meme>>() {
            @Override
            public void onChanged(List<Meme> memes) {
                cardView.setAdapter(new MemeAdapter(memes, activity));
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPref = getSharedPreferences(SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE);
        //setTheme(ActivityTheme.fromIndex(sharedPref.getInt("theme", DEFAULT_THEME.getIndex())).getStyleId());

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, getString(R.string.message_refresh), Snackbar.LENGTH_LONG).show();
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        appBarConfig = new AppBarConfiguration.Builder(R.id.nav_home, R.id.nav_mode, R.id.nav_util)
                                                .setDrawerLayout(drawer)
                                                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfig);
        NavigationUI.setupWithNavController(navigationView, navController);

        cardView = findViewById(R.id.main_cards); // TODO butterknife or view binding (studio canary 11+)
        cardView.setLayoutManager(new LinearLayoutManager(this));

        viewModel = ViewModelProviders.of(this).get(MemeViewModel.class);
        refreshMemes(this, cardView, viewModel, MemeMode.fromSerialized(sharedPref.getString("default-mode", MemeMode.DEFAULT_MODE.getAsString())));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, appBarConfig) || super.onSupportNavigateUp();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();
        LogWrapper.error(this, "X ITEM SELECT " + id);
        MemeMode newMode = null;
        if (id == R.id.nav_mode_hot) {
            newMode = MemeMode.HOT;
        }
        if (id == R.id.nav_mode_trending) {
            newMode = MemeMode.TRENDING;
        }
        if (id == R.id.nav_mode_fresh) {
            newMode = MemeMode.FRESH;
        }
        if (id == R.id.nav_mode_best) {
            newMode = MemeMode.BEST;
        }
        if (newMode != null) {
            LogWrapper.error(this, "REFRESH %s", newMode.getAsString());
            refreshMemes(this, cardView, viewModel, newMode);
        }
        ((DrawerLayout) findViewById(R.id.drawer_layout)).closeDrawer(GravityCompat.START);
        return true;
    }
}
