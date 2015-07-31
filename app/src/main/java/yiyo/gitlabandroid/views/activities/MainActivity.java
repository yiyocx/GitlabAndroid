package yiyo.gitlabandroid.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import yiyo.gitlabandroid.views.fragments.HomeFragment;
import butterknife.Bind;
import butterknife.ButterKnife;
import yiyo.gitlabandroid.R;
import yiyo.gitlabandroid.utils.Configuration;
import yiyo.gitlabandroid.views.fragments.NavigationViewFragment;

public class MainActivity extends AppCompatActivity implements NavigationViewFragment.NavigationDrawerCallbacks {

    @Bind(R.id.toolbar) Toolbar mToolbar;
    @Bind(R.id.tab_layout) TabLayout mTabLayout;
    private NavigationViewFragment mNavigationViewFragment;
    private Configuration configuration;
    private Fragment mCurrentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        configuration = new Configuration(MainActivity.this);

        if (!configuration.isLoggedIn()) {
            logoutUser();
        }

        ButterKnife.bind(this);
        setupToolbar();
        setupNavigationView();
        setupTabLayout();
    }

    private void setupToolbar() {
        setSupportActionBar(mToolbar);

        // Show menu icon
        final ActionBar ab = getSupportActionBar();
        assert ab != null;
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);
    }

    public void setupNavigationView() {
        mNavigationViewFragment = (NavigationViewFragment) getFragmentManager()
                .findFragmentById(R.id.navigation_fragment);

        mNavigationViewFragment.setUp(R.id.navigation_fragment,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    private void setupTabLayout() {
        mTabLayout.addTab(mTabLayout.newTab().setText("News"));
        mTabLayout.addTab(mTabLayout.newTab().setText("Repositories"));
    }

    private void logoutUser() {
        configuration.closeSession();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Para que al presionar el boton back el drawer se oculte
     */
    @Override
    public void onBackPressed() {

        if(mNavigationViewFragment.isDrawerOpen()){
            mNavigationViewFragment.closeDrawer();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                mNavigationViewFragment.openDrawer();
                return true;
            case R.id.action_logout:
                logoutUser();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /** Pasando la opción del menú elegida para mostrar el Fragment Correspondiente */
    @Override
    public void onNavigationDrawerItemSelected(MenuItem menuItem) {

        setTitle(menuItem.getTitle());
        Class fragmentClass = null;

        // Actualizar el contenido principal reemplazando los fragments
        switch (menuItem.getItemId()) {
            case R.id.navigation_item_1:
                fragmentClass = HomeFragment.class;
                Toast.makeText(this, menuItem.getTitle(), Toast.LENGTH_SHORT).show();
                break;
            case R.id.navigation_item_2:
                Toast.makeText(this, menuItem.getTitle(), Toast.LENGTH_SHORT).show();
                break;
            case R.id.navigation_item_3:
                Toast.makeText(this, menuItem.getTitle(), Toast.LENGTH_SHORT).show();
                break;
            default:
                fragmentClass = HomeFragment.class;
                break;
        }

        try {
            mCurrentFragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.main_content, mCurrentFragment).commit();
    }
}
