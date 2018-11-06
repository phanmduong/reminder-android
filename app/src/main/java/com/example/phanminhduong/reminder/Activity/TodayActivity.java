package com.example.phanminhduong.reminder.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.example.phanminhduong.reminder.Data;
import com.example.phanminhduong.reminder.GetGroupsQuery;
import com.example.phanminhduong.reminder.GetUserQuery;
import com.example.phanminhduong.reminder.LoginUserMutation;
import com.example.phanminhduong.reminder.TodoListMutation;
import com.example.phanminhduong.reminder.graphql.MyApolloClient;
import com.facebook.login.LoginManager;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import com.example.phanminhduong.reminder.Adapter.WorkAdapter;
import com.example.phanminhduong.reminder.Model.Work;
import com.example.phanminhduong.reminder.R;

import java.io.InputStream;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;

public class TodayActivity extends AppCompatActivity {
    private ListView listView, doneWorkListView;
    List<Work> listWork, listDoneWork;
    WorkAdapter workAdapter;
    private int ADD_WORK = 198;
    Bitmap bt;
    GetUserQuery.User user;

    TextView tvNameUser;
    ImageView imgAvatarUser;
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_today);
        init();


        listView = findViewById(R.id.workListView);
        listWork = new LinkedList<>();
        listWork.add(new Work("Viec 1","note", "1997-11-12", 0));
        listWork.add(new Work("Viec 2", "note", "1997-11-12", 1));
        listWork.add(new Work("Viec 3","note",  "1997-11-12", 0));
        listWork.add(new Work("Viec 4","note",  "1997-11-12", 1));
        listWork.add(new Work("Viec 1","note",  "1997-11-12", 0));
        listWork.add(new Work("Viec 2","note",  "1997-11-12", 1));
        workAdapter = new WorkAdapter(this, listWork);
        listView.setAdapter(workAdapter);

        doneWorkListView = findViewById(R.id.doneWorkListView);
        listDoneWork = new LinkedList<>();
        listDoneWork.add(new Work("Viec xong 1","note",  "1997-11-12", 0));
        listDoneWork.add(new Work("Viec xong 2", "note", "1997-11-12", 1));
        listDoneWork.add(new Work("Viec xong 3","note",  "1997-11-12", 0));
        listDoneWork.add(new Work("Viec xong 4","note", "1997-11-12", 1));
        workAdapter = new WorkAdapter(this, listDoneWork);
        doneWorkListView.setAdapter(workAdapter);

        getUserServer();
        getTodoList();
    }

    private void getTodoList() {
        Menu menu = navigationView.getMenu();
        GetGroupsQuery getGroupsQuery = GetGroupsQuery.builder().token(Data.token).build();
        MyApolloClient.getApolloClient().query(getGroupsQuery).enqueue(new ApolloCall.Callback<GetGroupsQuery.Data>() {
            @Override
            public void onResponse(@NotNull final Response<GetGroupsQuery.Data> response) {
                final List<GetGroupsQuery.Group> list = response.data().groups();
                TodayActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        for (final GetGroupsQuery.Group group : list) {
                            Menu menu = navigationView.getMenu();
                            menu.add(group.name()).setIcon(R.drawable.ic_menu_camera).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(MenuItem item) {
                                    Toast.makeText(TodayActivity.this, group.name(), Toast.LENGTH_LONG).show();
                                    return true;
                                }
                            });
                        }
                    }
                });

            }

            @Override
            public void onFailure(@NotNull ApolloException e) {

            }
        });
    }

    private void init() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        tvNameUser = headerView.findViewById(R.id.nav_header_name_user);
        imgAvatarUser = headerView.findViewById(R.id.nav_header_avatar_user);

        setTodayMenu();
    }

    private void setTodayMenu() {
        Menu menu = navigationView.getMenu();
        menu.add("Today").setIcon(R.drawable.ic_menu_camera).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Toast.makeText(TodayActivity.this, "Today", Toast.LENGTH_LONG).show();
                return true;
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void getUserServer() {
        GetUserQuery getUserQuery = GetUserQuery.builder().token(Data.token).build();
        MyApolloClient.getApolloClient().query(getUserQuery).enqueue(new ApolloCall.Callback<GetUserQuery.Data>() {
            @Override
            public void onResponse(@NotNull Response<GetUserQuery.Data> response) {
                user = response.data().user();
                Log.e("user", user.name());
                TodayActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvNameUser.setText(user.name());
                        Picasso.get().load(user.avatar_url()).resize(180, 180)
                                .centerCrop().into(imgAvatarUser);
                    }
                });
            }

            @Override
            public void onFailure(@NotNull ApolloException e) {

            }
        });
    }

    public void addWork(View v) {
        Intent i = new Intent(this, AddWorkActivity.class);
        startActivityForResult(i, ADD_WORK);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == ADD_WORK) {
            String time = data.getStringExtra("time");
            String image = data.getStringExtra("image");
            String note = data.getStringExtra("note");
            String name = data.getStringExtra("title");

            try {
                //parse image
                if (bt != null) {
                    bt.recycle();
                }

                InputStream stream = getContentResolver().openInputStream(Uri.parse(image));

                bt = BitmapFactory.decodeStream(stream);

                stream.close();

                //format date
                java.util.Date parse = new SimpleDateFormat("a hh:mm:ss  dd-MM-yyyy").parse(time);
                String formatted_time = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").format(parse);
                Log.e("time",formatted_time);
                time = formatted_time;

            } catch (Exception e) {
                e.printStackTrace();
            }
            final Work work = new Work(name, note, time, 0, Data.groupId);

            Log.e("Check",work.toString());
            TodoListMutation tm = TodoListMutation.builder().token(Data.token).name(name).note(note).deadline(time).group_id(Data.groupId).build();
            MyApolloClient.getApolloClient().mutate(tm).enqueue(new ApolloCall.Callback<TodoListMutation.Data>() {
                @Override
                public void onResponse(@NotNull Response<TodoListMutation.Data> response) {
                    Object obj = response.data();
                    Log.e("OBJ", obj.toString());
                    Toast.makeText(TodayActivity.this, "Thêm thành công!!!", Toast.LENGTH_LONG).show();
                    listWork.add(0,work);
                }

                @Override
                public void onFailure(@NotNull ApolloException e) {
//                    LoginManager.getInstance().logOut();
                    TodayActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            Toast.makeText(TodayActivity.this, "Thất bại!!!", Toast.LENGTH_LONG).show();
                        }
                    });


                }
            });


            doneWorkListView.setAdapter(new WorkAdapter(this, listDoneWork));


        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
