package com.example.dell_5548.eventmusicpestyah_hunyi.Activities;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.dell_5548.eventmusicpestyah_hunyi.Adapters.EventAdapterListener;
import com.example.dell_5548.eventmusicpestyah_hunyi.Adapters.EventsAdapter;
import com.example.dell_5548.eventmusicpestyah_hunyi.ClassManagers.EventManager;
import com.example.dell_5548.eventmusicpestyah_hunyi.Models.EventModel;
import com.example.dell_5548.eventmusicpestyah_hunyi.Fragments.LoginOrRegisterFragment;
import com.example.dell_5548.eventmusicpestyah_hunyi.R;
import com.example.dell_5548.eventmusicpestyah_hunyi.Fragments.UserWelcomeFragment;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class MainActivity extends AppCompatActivity implements EventAdapterListener,
        LoginOrRegisterFragment.OnButtonClickedListener,
        UserWelcomeFragment.OnButtonClickedListener {

    Context ctx = this;
    private final int CREATE_EVENT_REQUEST_CODE = 0;
    private final int LOGIN_REQUEST_CODE = 1;
    private final int SHOW_EVENT_REQUEST_CODE = 2;
    private final String NEW_EVENT_CREATED = "NEW_EVENT_CREATED";
    private final String EVENT_KEY = "EVENT_KEY";

    private List<EventModel> eventList;
    private RecyclerView recyclerView;
    private EventsAdapter mAdapter;
    private android.support.v7.widget.SearchView searchView;
    private String M_NODE_EVENT;

    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mDatabaseReference;
    private DatabaseReference mEventsReference;

    private Button addEventButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        eventList = new ArrayList<>();

        //Set up others

        M_NODE_EVENT = getResources().getString(R.string.M_NODE_EVENT);

        //Set up firebase
        mFirebaseAuth = FirebaseAuth.getInstance();
        if (AuthUI.getInstance() != null) {
            boolean result = AuthUI.getInstance()
                    .signOut(this)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        public void onComplete(@NonNull com.google.android.gms.tasks.Task<Void> task) {
                            // ...

                        }
                    }).isSuccessful();
//
        }

        mFirebaseAuth.signInWithEmailAndPassword("testhun@email.com", "testHun");

        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mEventsReference = mDatabaseReference.child(M_NODE_EVENT);



        //Set up RecyclerView
        recyclerView = (RecyclerView) findViewById(R.id.event_recycler_view);
        mAdapter = new EventsAdapter(ctx, mEventsReference, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(ctx);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(ctx, LinearLayoutManager.VERTICAL));


        recyclerView.setAdapter(mAdapter);

        //prepareTestEventData();


        //Set up topmost fragment

        LoginOrRegisterFragment loginOrRegisterFragment = new LoginOrRegisterFragment();
        android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.loginOrWelcomeFrameContainer, loginOrRegisterFragment);
        transaction.addToBackStack(null);
        transaction.commit();

        addEventButton = (Button) findViewById(R.id.addNewEventButton);
        addEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mFirebaseAuth.getCurrentUser() == null) {
                    Toast.makeText(ctx, "You need to log in first", Toast.LENGTH_SHORT).show();
                } else {
                    Intent addNewEventIntent = new Intent(ctx, NewEventActivity.class);
                    startActivityForResult(addNewEventIntent, CREATE_EVENT_REQUEST_CODE);
                }
            }
        });

    }



    @Override
    protected void onResume() {
        super.onResume();
        if (mFirebaseAuth.getCurrentUser() != null) {
            UserWelcomeFragment userWelcomeFragment = new UserWelcomeFragment();
            android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.loginOrWelcomeFrameContainer, userWelcomeFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == CREATE_EVENT_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                boolean result = data.getBooleanExtra(NEW_EVENT_CREATED, false);
                if (result) {
                    Toast.makeText(ctx, "Event created successfully!", Toast.LENGTH_SHORT).show();
                }
            }
        }

    }

    /**
     * <h2>Description:</h2><br>
     * <ul>
     * <li>Inflates and sets up a menu-bar.</li>
     * <li>When the user submits something on it, the adapter's filter function is getting called, filtering the data
     * it contains.</li>
     * </ul>
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (android.support.v7.widget.SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        searchView.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                mAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                mAdapter.getFilter().filter(query);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        // close search view on back button pressed
        if (!searchView.isIconified()) {
            searchView.setIconified(true);
            return;
        }
        super.onBackPressed();
    }


    @Override
    public void onEventSelected(EventModel event) {
        Intent showEventIntent = new Intent(ctx, ShowEventActivity.class);
        showEventIntent.putExtra("EVENT_KEY",event.getKey());
        startActivityForResult(showEventIntent, SHOW_EVENT_REQUEST_CODE);
    }

    @Override
    public void onButtonClicked(int buttonCode) {
        switch (buttonCode) {
            case 1:
                Intent loginIntent = new Intent(ctx, LoginActivity.class);
                startActivityForResult(loginIntent, LOGIN_REQUEST_CODE);

                break;

            case 2:
                Intent registerIntent = new Intent(ctx, RegisterActivity.class);
                startActivityForResult(registerIntent, LOGIN_REQUEST_CODE);
                break;
        }

    }


    @Override
    public void onButtonClickedTop(int buttonCode) {
        switch (buttonCode) {
            case 1:
                if (AuthUI.getInstance() != null) {
                    boolean result = AuthUI.getInstance()
                            .signOut(this)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                public void onComplete(@NonNull com.google.android.gms.tasks.Task<Void> task) {
                                    // ...

                                }
                            }).isSuccessful();

                    LoginOrRegisterFragment loginOrRegisterFragment = new LoginOrRegisterFragment();
                    android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.loginOrWelcomeFrameContainer, loginOrRegisterFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
//
                }

                break;

            case 2:
                Intent profileIntent = new Intent(ctx, UserProfileActivity.class);
                startActivityForResult(profileIntent, LOGIN_REQUEST_CODE);
                break;
        }
    }
}
