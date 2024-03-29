package nl.hypothermic.windesmemes.android;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
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
import com.squareup.picasso.Picasso;

import java.util.List;

import nl.hypothermic.windesmemes.android.auth.AuthenticationManager;
import nl.hypothermic.windesmemes.android.data.MemeViewModel;
import nl.hypothermic.windesmemes.android.ui.ActivityTheme;
import nl.hypothermic.windesmemes.android.ui.ModelResourceMappings;
import nl.hypothermic.windesmemes.android.ui.recycler.InfiniteScrollListener;
import nl.hypothermic.windesmemes.android.ui.recycler.MemeAdapter;
import nl.hypothermic.windesmemes.android.util.CircleCropTransformation;
import nl.hypothermic.windesmemes.android.util.LocaleCompat;
import nl.hypothermic.windesmemes.model.Meme;
import nl.hypothermic.windesmemes.model.MemeMode;
import nl.hypothermic.windesmemes.model.User;
import nl.hypothermic.windesmemes.retrofit.WindesMemesAPI;

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
                        int oldListSize = adapter.getMemes().size();
                        adapter.getMemes().addAll(memes);
                        adapter.notifyItemRangeInserted(oldListSize, memes.size());
                    }
                } else {
                    cardView.setAdapter(new MemeAdapter(memes, activity, cardView));
                }
                ActionBar supportActionBar = activity.getSupportActionBar();
                if (supportActionBar != null) {
                    supportActionBar.setTitle(String.format(activity.getString(R.string.actionbar_title_format), activity.getString(ModelResourceMappings.getModeResource(mode))));
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
        final NavigationView navigationView = findViewById(R.id.nav_view);

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
        final ImageView accountAvatar      = headerView.findViewById(R.id.account_avatar);
        AuthenticationManager.acquire(this).registerProfileObserver(new Observer<User>() {
            @Override
            public void onChanged(User user) {
                // On user logged in
                if (user != null) {
                    if (user.username != null) {
                        accountTitleView.setText(user.username);
                    }
                    // API returns null when no avatar, it gets parsed to string (ex. "null")
                    if (user.avatar_id != null && !user.avatar_id.equals("null")) {
                        Picasso.get().load(WindesMemesAPI.API_URL + "get_avatar?id=" + user.avatar_id)
                                .transform(CircleCropTransformation.getInstance())
                                .error(ContextCompat.getDrawable(MainActivity.this, R.mipmap.ic_launcher_round))
                                .fit().centerInside().into(accountAvatar);
                    }
                    accountSubtitleView.setText(String.format(LocaleCompat.getDefaultLocale(MainActivity.this),
                                                                "%d %s", user.totalKarma, getString(R.string.common_karma)));
                    navigationView.getMenu().findItem(R.id.nav_account_login).setVisible(false);
                    navigationView.getMenu().findItem(R.id.nav_account_register).setVisible(false);
                    navigationView.getMenu().findItem(R.id.nav_account_logout).setVisible(true);
                // On user logged out
                } else {
                    accountAvatar.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, R.mipmap.ic_launcher_round));
                    accountTitleView.setText("");
                    accountSubtitleView.setText(R.string.account_mail_placeholder);
                    navigationView.getMenu().findItem(R.id.nav_account_login).setVisible(true);
                    navigationView.getMenu().findItem(R.id.nav_account_register).setVisible(true);
                    navigationView.getMenu().findItem(R.id.nav_account_logout).setVisible(false);
                }
            }
        });

        ConnectivityManager cm = ContextCompat.getSystemService(this, ConnectivityManager.class);
        if (!(cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isAvailable() && cm.getActiveNetworkInfo().isConnected())) {
            // TODO show "no internet connection" screen
        }

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

        MemeMode defaultMode = MemeMode.fromSerialized(sharedPref.getString("default-mode", MemeMode.DEFAULT_MODE.getAsString()));
        refreshMemes(this, cardView, viewModel, defaultMode);
        navigationView.setCheckedItem(ModelResourceMappings.getNavigationItem(defaultMode));
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, appBarConfig) || super.onSupportNavigateUp();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();

        switch (id) {
            case R.id.nav_account_login:
                if (!AuthenticationManager.acquire(MainActivity.this).isUserAuthenticated()) {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                } else {
                    // TODO show user options??
                    Snackbar.make(cardView, getString(R.string.login_error_success), Snackbar.LENGTH_LONG).show();
                }
                break;
            case R.id.nav_account_register:
                // Temporary code. TODO register in-app
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://windesmemes.nl/signup")));
                break;
            case R.id.nav_account_logout:
                AuthenticationManager.acquire(this).clear();
                break;
            case R.id.nav_mode_hot:
                refreshMemes(this, cardView, viewModel, MemeMode.HOT);
                break;
            case R.id.nav_mode_trending:
                refreshMemes(this, cardView, viewModel, MemeMode.TRENDING);
                break;
            case R.id.nav_mode_fresh:
                refreshMemes(this, cardView, viewModel, MemeMode.FRESH);
                break;
            case R.id.nav_mode_best:
                refreshMemes(this, cardView, viewModel, MemeMode.BEST);
                break;
            case R.id.nav_preference_theme:
                SharedPreferences preferences = getSharedPreferences(SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE);
                int currentTheme = preferences.getInt(PREFS_KEY_THEME, DEFAULT_THEME.getIndex());

                // Gebruik commit i.p.v. apply want we willen zeker weten dat de value
                // opgeslagen is zodra de activity opnieuw wordt geladen.
                preferences.edit().putInt(PREFS_KEY_THEME, currentTheme == 0 ? 1 : 0).commit();
                recreate();
                break;
            case R.id.nav_preference_policy:
                // Temporary code. TODO view in-app + view HT policy
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://windesmemes.nl/contact/privacy")));
                break;
        }

        ((DrawerLayout) findViewById(R.id.drawer_layout)).closeDrawer(GravityCompat.START);
        return true;
    }
}
