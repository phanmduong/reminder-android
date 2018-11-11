package com.example.phanminhduong.reminder.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.example.phanminhduong.reminder.ActionCode;
import com.example.phanminhduong.reminder.AddGroupMutation;
import com.example.phanminhduong.reminder.ChangeStatusTodoListMutation;
import com.example.phanminhduong.reminder.Data;
import com.example.phanminhduong.reminder.DeleteGroupMutation;
import com.example.phanminhduong.reminder.GetGroupsQuery;
import com.example.phanminhduong.reminder.GetTodoListQuery;
import com.example.phanminhduong.reminder.GetUserQuery;
import com.example.phanminhduong.reminder.TodoListMutation;
import com.example.phanminhduong.reminder.adapter.DoneWorkAdapter;
import com.example.phanminhduong.reminder.graphql.MyApolloClient;
import com.facebook.login.LoginManager;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import com.example.phanminhduong.reminder.adapter.WorkAdapter;
import com.example.phanminhduong.reminder.model.Work;
import com.example.phanminhduong.reminder.R;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;

public class TodayActivity extends AppCompatActivity {
    private ListView listView, doneWorkListView;
    List<Work> listWork, listDoneWork;
    WorkAdapter workAdapter;
    DoneWorkAdapter doneWorkAdapter;
    ScrollView scrollView;
    GetUserQuery.User user;
    List<GetTodoListQuery.TodoList> todoList;
    Button btnShowDoneWork;
    TextView tvNameUser;
    ImageView imgAvatarUser;
    NavigationView navigationView;
    SubMenu menuGroups;
    boolean hideMenu = true;
    Toolbar toolbar;
    DrawerLayout drawer;
    ProgressDialog prgDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_today);
        init();
        getUserServer();
        setMenu();
        getTodoList(Data.groupId);
    }

    public void getTodoList(int groupId) {
        prgDialog.show();

        Data.groupId = groupId;
        GetTodoListQuery gtdlq = GetTodoListQuery.builder().token(Data.token).groupId(groupId).build();
        MyApolloClient.getApolloClient().query(gtdlq).enqueue(new ApolloCall.Callback<GetTodoListQuery.Data>() {
            @Override
            public void onResponse(@NotNull Response<GetTodoListQuery.Data> response) {
                todoList = response.data().todoLists();
                Log.e("TDL", "" + todoList.size());

                listWork = new LinkedList<>();
                listDoneWork = new LinkedList<>();
                for (GetTodoListQuery.TodoList w : todoList) {
                    Log.e(w.id() + "", w.name() + " - " + w.note() + " - " + w.status());
                    Work element = new Work(w.name(), w.note(), w.deadline(), w.status(), w.id());
                    if (element.getStatus() != 0) {
                        listDoneWork.add(element);
                    } else {
                        listWork.add(element);
                    }

                }

                TodayActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        workAdapter = new WorkAdapter(TodayActivity.this, listWork);
                        listView.setAdapter(workAdapter);

                        doneWorkAdapter = new DoneWorkAdapter(TodayActivity.this, listDoneWork);
                        doneWorkListView.setAdapter(doneWorkAdapter);
                        prgDialog.hide();
                        scrollView.post(new Runnable() {
                            @Override
                            public void run() {
                                scrollView.smoothScrollTo(0, scrollView.getTop());
                            }
                        });
                    }
                });
            }


            @Override
            public void onFailure(@NotNull ApolloException e) {
                e.printStackTrace();
                TodayActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        prgDialog.hide();
                        Toast.makeText(TodayActivity.this, "Có lỗi xảy ra", Toast.LENGTH_LONG).show();

                    }
                });
            }
        });

    }

    private void closeDrawer() {
        drawer.closeDrawers();
    }

    private void dialogStoreGroup(final int groupID, final String groupName) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);


        // Set up the input
        final EditText input = new EditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setTitle("Tạo nhóm");
        if (groupID > 0) {
            input.setText(groupName);
            builder.setTitle("Sửa tên nhóm");
        }
        builder.setView(input);


        // Set up the buttons
        builder.setPositiveButton("Lưu", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setNegativeButton("Thoát", new DialogInterface.OnClickListener() {
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
                if (groupID > 0) {
                    editGroup(groupID, input.getText().toString(), dialog);
                } else {
                    addGroup(input.getText().toString(), dialog);
                }
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
                                groupClick(group.id(), group.name());
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

    private void editGroup(int id, String name, final DialogInterface dialog) {
        AddGroupMutation addGroupMutation = AddGroupMutation.builder().name(name).token(Data.token).id(id).build();
        MyApolloClient.getApolloClient().mutate(addGroupMutation).enqueue(new ApolloCall.Callback<AddGroupMutation.Data>() {
            @Override
            public void onResponse(@NotNull final Response<AddGroupMutation.Data> response) {
                TodayActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                        final AddGroupMutation.Group group = response.data().group();
                        menuGroups.findItem(group.id()).setTitle(group.name());
                        getSupportActionBar().setTitle(group.name());
                    }
                });
            }

            @Override
            public void onFailure(@NotNull ApolloException e) {
                Toast.makeText(TodayActivity.this, "Error. Again", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void groupClick(int groupID, String name) {
        hideMenu = false;
        invalidateOptionsMenu();
        getSupportActionBar().setTitle(name);
//        Toast.makeText(TodayActivity.this, groupID + "", Toast.LENGTH_LONG).show();
        getTodoList(groupID);
        closeDrawer();

    }

    private void getGroups() {
        final Menu menu = navigationView.getMenu();
        menuGroups = menu.addSubMenu("Nhóm công việc");
        GetGroupsQuery getGroupsQuery = GetGroupsQuery.builder().token(Data.token).build();
        MyApolloClient.getApolloClient().query(getGroupsQuery).enqueue(new ApolloCall.Callback<GetGroupsQuery.Data>() {
            @Override
            public void onResponse(@NotNull final Response<GetGroupsQuery.Data> response) {
                final List<GetGroupsQuery.Group> list = response.data().groups();
                if (list != null)
                    TodayActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            for (final GetGroupsQuery.Group group : list) {
                                menuGroups.add(100, group.id(), group.id(), group.name()).setIcon(R.drawable.ic_listing).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                                    @Override
                                    public boolean onMenuItemClick(MenuItem item) {
                                        groupClick(group.id(), group.name());
                                        return true;
                                    }
                                });

                            }
                            menu.add("Thêm nhóm").setIcon(R.drawable.ic_add).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(MenuItem item) {
                                    dialogStoreGroup(0, "");
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
        setNoGroup();
        getGroups();
    }

    private void init() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Hôm nay");

        View layoutToday = findViewById(R.id.layoutToday);
        layoutToday.getBackground().setAlpha(150);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        tvNameUser = headerView.findViewById(R.id.nav_header_name_user);
        imgAvatarUser = headerView.findViewById(R.id.nav_header_avatar_user);
        listView = findViewById(R.id.workListView);
        doneWorkListView = findViewById(R.id.doneWorkListView);
        scrollView = findViewById(R.id.scrollView);
        btnShowDoneWork = findViewById(R.id.showDoneWorkButton);

        listWork = new LinkedList<>();

        listDoneWork = new LinkedList<>();

        prgDialog = new ProgressDialog(this);
        prgDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        prgDialog.setMessage("Đang tải...");
        prgDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFD4D9D0")));
        prgDialog.setIndeterminate(false);
        prgDialog.setCancelable(false);
    }

    private void setTodayMenu() {
        Menu menu = navigationView.getMenu();
        menu.add("Hôm nay").setIcon(R.drawable.ic_calendar).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                todayMenuOnClick();
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }

    private void setNoGroup() {
        Menu menu = navigationView.getMenu();
        menu.add("Không nhóm").setIcon(R.drawable.ic_calendar).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                hideMenu = true;
                invalidateOptionsMenu();
                getSupportActionBar().setTitle("Không nhóm");
                getTodoList(0);
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }

    private void todayMenuOnClick() {
        hideMenu = true;
        invalidateOptionsMenu();
        getSupportActionBar().setTitle("Hôm nay");
        getTodoList(-1);
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
        startActivityForResult(i, ActionCode.ADD_WORK);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == ActionCode.ADD_WORK) {
//            String time = data.getStringExtra("time");
//            String image = data.getStringExtra("image");
//            String note = data.getStringExtra("note");
//            String name = data.getStringExtra("title");
//            listWork.add(0, new Work(name, note, time, 0, Data.groupId));
//            listView.setAdapter(new WorkAdapter(this, listWork));
            getTodoList(Data.groupId);
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

    public void toggleDoneList(View v) {
        int status = doneWorkListView.getVisibility();
        if (status == View.GONE) {
            doneWorkListView.setVisibility(View.VISIBLE);
        } else
            doneWorkListView.setVisibility(View.GONE);
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.smoothScrollTo(0, doneWorkListView.getTop());
            }
        });
    }

    public void changeStatusWork(final int pos, final boolean checked) {
        Work work = null;
        int isChecked = checked ? 1 : 0;

        if (checked && pos < listWork.size()) {
            work = listWork.get(pos);
        }
        if (!checked && pos < listDoneWork.size()) {
            work = listDoneWork.get(pos);
        }
        if (work == null) {
            return;
        }
        prgDialog.show();
        ChangeStatusTodoListMutation csttm = ChangeStatusTodoListMutation.builder().token(Data.token).id(work.getId()).status(isChecked).build();
        MyApolloClient.getApolloClient().mutate(csttm).enqueue(new ApolloCall.Callback<ChangeStatusTodoListMutation.Data>() {
            @Override
            public void onResponse(@NotNull Response<ChangeStatusTodoListMutation.Data> response) {
                Log.e("mutation", "aukay");
                TodayActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        moveWork(pos, checked);
                        prgDialog.hide();
                    }
                });
            }

            @Override
            public void onFailure(@NotNull ApolloException e) {
                Log.e("mutation", "fail");
                TodayActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        prgDialog.hide();
                    }
                });

            }
        });

    }

    public void moveWork(int pos, boolean checked) {
        Log.e(pos + "", checked + "");
        if (checked) {
            Work work = listWork.get(pos);
            work.setStatus(checked ? 1 : 0);
            listDoneWork.add(work);
            listWork.remove(pos);
        } else {
            Work work = listDoneWork.get(pos);
            work.setStatus(checked ? 1 : 0);
            listWork.add(work);
            listDoneWork.remove(pos);
        }
        workAdapter = new WorkAdapter(TodayActivity.this, listWork);
        listView.setAdapter(workAdapter);
        doneWorkAdapter = new DoneWorkAdapter(TodayActivity.this, listDoneWork);
        doneWorkListView.setAdapter(doneWorkAdapter);
    }

    private void confirmDeleteGroup() {
        final android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(this);
        alert.setTitle("Xóa nhóm");
        alert.setMessage("Bạn có chắc chắn muốn xóa nhóm này không?");
        alert.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                deleteGroup(Data.groupId);
            }
        });
        alert.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // close dialog
                dialog.cancel();
            }
        });
        alert.show();
    }

    private void deleteGroup(final int groupID) {
        prgDialog.show();
        DeleteGroupMutation deleteGroupMutation = DeleteGroupMutation.builder().token(Data.token).id(groupID).build();
        MyApolloClient.getApolloClient().mutate(deleteGroupMutation).enqueue(new ApolloCall.Callback<DeleteGroupMutation.Data>() {
            @Override
            public void onResponse(@NotNull Response<DeleteGroupMutation.Data> response) {
                TodayActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        prgDialog.dismiss();
                        menuGroups.removeItem(groupID);
                        todayMenuOnClick();
                    }
                });
            }

            @Override
            public void onFailure(@NotNull ApolloException e) {

            }
        });
    }

    private void editGroup() {
        MenuItem menuItem = menuGroups.findItem(Data.groupId);
        menuItem.getTitle();
        dialogStoreGroup(Data.groupId, menuItem.getTitle().toString());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_delete:
                confirmDeleteGroup();
                return true;
            case R.id.menu_edit:
                editGroup();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
}
