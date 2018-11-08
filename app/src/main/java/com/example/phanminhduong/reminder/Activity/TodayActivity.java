package com.example.phanminhduong.reminder.Activity;

import android.content.DialogInterface;
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
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.example.phanminhduong.reminder.AddGroupMutation;
import com.example.phanminhduong.reminder.Data;
import com.example.phanminhduong.reminder.GetGroupsQuery;
import com.example.phanminhduong.reminder.GetTodoListQuery;
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
import java.util.Random;

public class TodayActivity extends AppCompatActivity {
    private ListView listView, doneWorkListView;
    List<Work> listWork, listDoneWork;
    WorkAdapter workAdapter;
    private int ADD_WORK = 198;
    Bitmap bt;
    GetUserQuery.User user;
    List<GetTodoListQuery.TodoList> todoList;

    TextView tvNameUser;
    ImageView imgAvatarUser;
    NavigationView navigationView;
    SubMenu menuGroups;
    boolean hideMenu = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_today);
        init();


        listView = findViewById(R.id.workListView);
        listWork = new LinkedList<>();
//        listWork.add(new Work("Viec 1", "note", "1997-11-12", 0));
//        listWork.add(new Work("Viec 2", "note", "1997-11-12", 1));
//        listWork.add(new Work("Viec 3", "note", "1997-11-12", 0));
//        listWork.add(new Work("Viec 4", "note", "1997-11-12", 1));
//        listWork.add(new Work("Viec 1", "note", "1997-11-12", 0));
//        listWork.add(new Work("Viec 2", "note", "1997-11-12", 1));
        getTodoList();



        doneWorkListView = findViewById(R.id.doneWorkListView);
        listDoneWork = new LinkedList<>();
//        listDoneWork.add(new Work("Viec xong 1", "note", "1997-11-12", 0));
//        listDoneWork.add(new Work("Viec xong 2", "note", "1997-11-12", 1));
//        listDoneWork.add(new Work("Viec xong 3", "note", "1997-11-12", 0));
//        listDoneWork.add(new Work("Viec xong 4", "note", "1997-11-12", 1));
        workAdapter = new WorkAdapter(this, listDoneWork);
        doneWorkListView.setAdapter(workAdapter);

        getUserServer();
        setMenu();
    }

    public void getTodoList(){
        GetTodoListQuery gtdlq = GetTodoListQuery.builder().token(Data.token).build();
        MyApolloClient.getApolloClient().query(gtdlq).enqueue(new ApolloCall.Callback<GetTodoListQuery.Data>() {
            @Override
            public void onResponse(@NotNull Response<GetTodoListQuery.Data> response) {
                 todoList = response.data().todoLists();
                 Log.e("TDL","" + todoList.size());
            }

            @Override
            public void onFailure(@NotNull ApolloException e) {
                e.printStackTrace();
                TodayActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        Toast.makeText(TodayActivity.this, "ERR", Toast.LENGTH_LONG).show();

                    }
                });
            }
        });
        workAdapter = new WorkAdapter(this, listWork);
        listView.setAdapter(workAdapter);
    }

    private void dialogAddGroup() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Create group");

        // Set up the input
        final EditText input = new EditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        final AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addGroup(input.getText().toString(), dialog);
            }
        });
    }

    private void addGroup(String name, final DialogInterface dialog) {
        AddGroupMutation addGroupMutation = AddGroupMutation.builder().name(name).token(Data.token).build();
        MyApolloClient.getApolloClient().mutate(addGroupMutation).enqueue(new ApolloCall.Callback<AddGroupMutation.Data>() {
            @Override
            public void onResponse(@NotNull final Response<AddGroupMutation.Data> response) {
                TodayActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                        final AddGroupMutation.Group group = response.data().group();
                        menuGroups.add(group.name()).setIcon(R.drawable.ic_listing).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                groupClick(group.id());
                                return true;
                            }
                        });
                        ;
                    }
                });
            }

            @Override
            public void onFailure(@NotNull ApolloException e) {
                Toast.makeText(TodayActivity.this, "Error. Again", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void groupClick(int groupID) {
        hideMenu = false;
        invalidateOptionsMenu();
        Toast.makeText(TodayActivity.this, groupID + "", Toast.LENGTH_LONG).show();
    }

    private void getGroups() {
        final Menu menu = navigationView.getMenu();
        menuGroups = menu.addSubMenu("Groups");
        GetGroupsQuery getGroupsQuery = GetGroupsQuery.builder().token(Data.token).build();
        MyApolloClient.getApolloClient().query(getGroupsQuery).enqueue(new ApolloCall.Callback<GetGroupsQuery.Data>() {
            @Override
            public void onResponse(@NotNull final Response<GetGroupsQuery.Data> response) {
                final List<GetGroupsQuery.Group> list = response.data().groups();
                TodayActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        for (final GetGroupsQuery.Group group : list) {
                            menuGroups.add(group.name()).setIcon(R.drawable.ic_listing).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(MenuItem item) {
                                    groupClick(group.id());
                                    return true;
                                }
                            });
                        }
                        menu.add("Add group").setIcon(R.drawable.ic_add).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                dialogAddGroup();
                                return true;
                            }
                        });
                    }
                });
            }

            @Override
            public void onFailure(@NotNull ApolloException e) {

            }
        });
    }

    private void setMenu() {
        Menu menu = navigationView.getMenu();
        menu.clear();
        setTodayMenu();
        getGroups();
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


    }

    private void setTodayMenu() {
        Menu menu = navigationView.getMenu();
        menu.add("Today").setIcon(R.drawable.ic_calendar).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Toast.makeText(TodayActivity.this, "Today", Toast.LENGTH_LONG).show();
                hideMenu = true;
                invalidateOptionsMenu();
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

            listWork.add(0,new Work(name, note, time, 0, Data.groupId));

            Log.e("TOKEN",Data.token);
            TodoListMutation tm = TodoListMutation.builder().token(Data.token).name(name).note(note).deadline(time).group_id(Data.groupId).build();
            MyApolloClient.getApolloClient().mutate(tm).enqueue(new ApolloCall.Callback<TodoListMutation.Data>() {
                @Override
                public void onResponse(@NotNull Response<TodoListMutation.Data> response) {
                    Object obj = response.data();
                    Log.e("OBJ", obj.toString());
                    Toast.makeText(TodayActivity.this, "Thêm thành công!!!", Toast.LENGTH_LONG).show();

                }

                @Override
                public void onFailure(@NotNull final ApolloException e) {
//                    LoginManager.getInstance().logOut();
//                    e.printStackTrace();
//                    Toast.makeText(TodayActivity.this, "Thất bại!!!", Toast.LENGTH_LONG).show();
                    TodayActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            e.printStackTrace();
                            Toast.makeText(TodayActivity.this, "Thêm thành công!!!", Toast.LENGTH_LONG).show();

                        }
                    });

                }
            });


            doneWorkListView.setAdapter(new WorkAdapter(this, listDoneWork));


        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_action_toolbar, menu);

        MenuItem deleteItem = menu.findItem(R.id.menu_delete);
        MenuItem editItem = menu.findItem(R.id.menu_edit);

        deleteItem.setVisible(!hideMenu);
        editItem.setVisible(!hideMenu);

        return true;
    }

    public void logout(View view) {
        LoginManager.getInstance().logOut();
        Data.token = "";
        finish();
        Intent intent = new Intent(TodayActivity.this, MainActivity.class);
        startActivity(intent);

    }
}
