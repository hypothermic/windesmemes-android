package nl.hypothermic.windesmemes.android;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.util.Consumer;
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

import nl.hypothermic.windesmemes.android.auth.AuthenticationManager;
import nl.hypothermic.windesmemes.android.data.MemeViewModel;
import nl.hypothermic.windesmemes.android.ui.ActivityTheme;
import nl.hypothermic.windesmemes.android.ui.I18NMappings;
import nl.hypothermic.windesmemes.android.ui.recycler.InfiniteScrollListener;
import nl.hypothermic.windesmemes.android.ui.recycler.MemeAdapter;
import nl.hypothermic.windesmemes.model.Meme;
import nl.hypothermic.windesmemes.model.MemeMode;
import nl.hypothermic.windesmemes.model.User;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String SHARED_PREFERENCES_KEY = "nl.hypothermic.windesmemes.WM.USER_PREFS";
    private static final String PREFS_KEY_THEME        = "theme";
    private static final ActivityTheme DEFAULT_THEME   = ActivityTheme.LIGHT;

    private AppBarConfiguration appBarConfig;
    private DrawerLayout drawerLayout;
    private RecyclerView cardView;

    private MemeViewModel viewModel;
    private volatile MemeMode lastMode;

    public static void refreshMemes(final MainActivity activity, final RecyclerView cardView, MemeViewModel model, MemeMode mode) {
        refreshMemes(activity, cardView, model, mode, null, 0, false);
    }

    public static void refreshMemes(final MainActivity activity, final RecyclerView cardView, final MemeViewModel model,
                                    final MemeMode mode, final Consumer<Void> callback, int start, final boolean append) {
        model.clearCache().getData(mode, start).observe(activity, new Observer<List<Meme>>() {
            @Override
            public void onChanged(List<Meme> memes) {
                if (append) {
                    MemeAdapter adapter = (MemeAdapter) cardView.getAdapter();
                    if (adapter != null) {
                        adapter.getMemes().addAll(memes);
                        // TODO mAdapter.notifyItemRangeInserted for better performance
                        adapter.notifyDataSetChanged();
                    }
                } else {
                    cardView.setAdapter(new MemeAdapter(memes, activity, cardView));
                }
                ActionBar supportActionBar = activity.getSupportActionBar();
                if (supportActionBar != null) {
                    supportActionBar.setTitle(String.format(activity.getString(R.string.actionbar_title_format), activity.getString(I18NMappings.getModeResource(mode))));
                }
                if (callback != null) {
                    callback.accept(null);
                }
            }
        });
        activity.lastMode = mode;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPref = getSharedPreferences(SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE);
        setTheme(ActivityTheme.fromIndex(sharedPref.getInt(PREFS_KEY_THEME, DEFAULT_THEME.getIndex())).getStyleId());

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton floating = findViewById(R.id.fab);
        floating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                refreshMemes(MainActivity.this, cardView, viewModel, lastMode, new Consumer<Void>() {
                    @Override
                    public void accept(Void ignore) {
                        Snackbar.make(view, getString(R.string.message_refreshed), Snackbar.LENGTH_LONG).show();
                    }
                }, 0, false);
            }
        });

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        appBarConfig = new AppBarConfiguration.Builder(R.id.nav_home, R.id.nav_mode, R.id.nav_util)
                                                .setDrawerLayout(drawerLayout)
                                                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfig);
        NavigationUI.setupWithNavController(navigationView, navController);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.bringToFront();

        View headerView = navigationView.getHeaderView(0);
        headerView.findViewById(R.id.nav_header_container).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!AuthenticationManager.acquire(MainActivity.this).isUserAuthenticated()) {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                } else {
                    // TODO show user options??
                    Snackbar.make(v, getString(R.string.login_error_success), Snackbar.LENGTH_LONG).show();
                }
            }
        });

        final TextView accountTitleView    = headerView.findViewById(R.id.account_name);
        final TextView accountSubtitleView = headerView.findViewById(R.id.account_mail);
        AuthenticationManager.acquire(this).registerProfileObserver(new Observer<User>() {
            @Override
            public void onChanged(User user) {
                if (user != null) {
                    // API returns null when no avatar, it gets parsed to string
                    if (!user.avatar_id.equals("null")) {

                    }
                    if (user.username != null) {
                        accountTitleView.setText(user.username);
                    }
                    accountSubtitleView.setText(user.totalKarma + " karma"); // TODO string res i18n
                }
            }
        });

        viewModel = ViewModelProviders.of(this).get(MemeViewModel.class);

        cardView = findViewById(R.id.main_cards); // TODO butterknife or view binding (studio canary 11+)
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        cardView.setLayoutManager(layoutManager);
        cardView.addOnScrollListener(new InfiniteScrollListener(cardView, layoutManager, new Observer<InfiniteScrollListener.ObserverData<Void>>() {
            @Override
            public void onChanged(final InfiniteScrollListener.ObserverData<Void> observerData) {
                refreshMemes(MainActivity.this, cardView, viewModel, lastMode, new Consumer<Void>() {
                    @Override
                    public void accept(Void aVoid) {
                        observerData.getOnLoadingDoneCallback().onChanged(null);
                    }
                }, observerData.getTotalItemCount(), true);
            }
        }));

        AuthenticationManager.acquire(this).refreshSession(null);
        refreshMemes(this, cardView, viewModel, MemeMode.fromSerialized(sharedPref.getString("default-mode", MemeMode.DEFAULT_MODE.getAsString())));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, appBarConfig) || super.onSupportNavigateUp();
    }

    // TODO switch is cleaner ??
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();

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
            refreshMemes(this, cardView, viewModel, newMode);
        }

        if (id == R.id.nav_preferences) {
            SharedPreferences preferences = getSharedPreferences(SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE);
            int currentTheme = preferences.getInt(PREFS_KEY_THEME, DEFAULT_THEME.getIndex());

            // Gebruik commit i.p.v. apply want we willen zeker weten dat de value
            // opgeslagen is zodra de activity opnieuw wordt geladen.
            preferences.edit().putInt(PREFS_KEY_THEME, currentTheme == 0 ? 1 : 0).commit();
            recreate();
        }
        if (id == R.id.nav_account_logout) {
            AuthenticationManager.acquire(this).clear();
        }

        ((DrawerLayout) findViewById(R.id.drawer_layout)).closeDrawer(GravityCompat.START);
        return true;
    }
}
