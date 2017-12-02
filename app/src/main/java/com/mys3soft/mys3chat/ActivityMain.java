package com.mys3soft.mys3chat;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.mys3soft.mys3chat.Models.Message;
import com.mys3soft.mys3chat.Models.StaticInfo;
import com.mys3soft.mys3chat.Models.User;
import com.mys3soft.mys3chat.Services.DataContext;
import com.mys3soft.mys3chat.Services.IFireBaseAPI;
import com.mys3soft.mys3chat.Services.LocalUserService;
import com.mys3soft.mys3chat.Services.Tools;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import retrofit2.Call;

public class ActivityMain extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    private User user;
    Firebase refUser;
    private DataContext db;
    private ProgressDialog pd;
    private List<Message> userLastChatList;
    private List<User> userFriednList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Firebase.setAndroidContext(this);

        db = new DataContext(this, null, null, 1);

        pd = new ProgressDialog(this);
        pd.setMessage("Refreshing...");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        // check if user exists in local db
        user = LocalUserService.getLocalUserFromPreferences(this);
        if (user.Email == null) {
            // send to activitylogin
//            Intent intent = new Intent(this, ActivityLogin.class);
//            startActivityForResult(intent, 100);
//
        } else {
            startService(new Intent(this, AppService.class));
            if (refUser == null) {
                refUser = new Firebase(StaticInfo.UsersURL + "/" + user.Email);
            }

        }
    }


    @Override
    protected void onStart() {
        super.onStart();

        // check if user exists in local db
        user = LocalUserService.getLocalUserFromPreferences(this);
        if (user.Email == null) {
            // send to activitylogin
            Intent intent = new Intent(this, ActivityLogin.class);
            startActivityForResult(intent, 100);
            return;
        }

        // refresh last chat
        userLastChatList = db.getUserLastChatList(user.Email);
        ListAdapter lastChatAdp = new AdapterLastChat(this, userLastChatList);
        final ListView lv_LastChatList = (ListView) findViewById(R.id.lv_LastChatList);
        if (lv_LastChatList != null) {
            lv_LastChatList.setAdapter(lastChatAdp);
            // reset listener

            lv_LastChatList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    if (userLastChatList.size() <= position) return false;
                    final Message selectedMessageItem = userLastChatList.get(position);
                    final CharSequence options[] = new CharSequence[]{"Delete Chat"};
                    AlertDialog.Builder builder = new AlertDialog.Builder(ActivityMain.this);
                    builder.setTitle(selectedMessageItem.FriendFullName);
                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int index) {
                            // the user clicked on list[index]
                            if (index == 0) {
                                // Delete Chat
                                new AlertDialog.Builder(ActivityMain.this)
                                        .setTitle(selectedMessageItem.FriendFullName)
                                        .setMessage("Are you sure to delete this chat?")
                                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                db.deleteChat(user.Email, selectedMessageItem.FromMail);
                                                Toast.makeText(getApplicationContext(), "Chat deleted successfully", Toast.LENGTH_SHORT).show();
                                                userLastChatList = db.getUserLastChatList(user.Email);
                                                ListAdapter adp = new AdapterLastChat(getApplicationContext(), userLastChatList);
                                                lv_LastChatList.setAdapter(adp);
                                            }
                                        })
                                        .setNegativeButton(android.R.string.no, null)
                                        .show();
                            }
                        }
                    });

                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.show();
                    return true;
                }
            });

        }

        // refresh contacts
        userFriednList = db.getUserFriendList();
        ListAdapter adp = new FriendListAdapter(this, userFriednList);
        final ListView lv_FriendList = (ListView) findViewById(R.id.lv_FriendList);
        if (lv_FriendList != null) {
            lv_FriendList.setAdapter(adp);
            lv_FriendList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    if (userFriednList.size() <= position) return false;
                    final User selectedUser = userFriednList.get(position);
                    final CharSequence options[] = new CharSequence[]{"Profile", "Delete Contact"};
                    AlertDialog.Builder builder = new AlertDialog.Builder(ActivityMain.this);
                    builder.setTitle(selectedUser.FirstName + " " + selectedUser.LastName);
                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int index) {
                            // the user clicked on list[index]
                            if (index == 0) {
                                // Profile
                                Intent intent = new Intent(ActivityMain.this, ActivityFriendProfile.class);
                                intent.putExtra("Email", selectedUser.Email);
                                startActivityForResult(intent, StaticInfo.ChatAciviityRequestCode);
                            } else {
                                // Delete Contact
                                new AlertDialog.Builder(ActivityMain.this)
                                        .setTitle(selectedUser.FirstName + " " + selectedUser.LastName)
                                        .setMessage("Are you sure to delete this contact?")
                                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Firebase ref = new Firebase(StaticInfo.EndPoint + "/friends/" + user.Email + "/" + selectedUser.Email);
                                                ref.removeValue();
                                                // delete from local database
                                                db.deleteFriendByEmailFromLocalDB(selectedUser.Email);
                                                Toast.makeText(ActivityMain.this, "Contact deleted successfully", Toast.LENGTH_SHORT).show();
                                                userFriednList = db.getUserFriendList();
                                                ListAdapter adp = new FriendListAdapter(ActivityMain.this, userFriednList);
                                                lv_FriendList.setAdapter(adp);
                                            }
                                        })
                                        .setNegativeButton(android.R.string.no, null)
                                        .show();
                            }
                        }
                    });

                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    builder.show();


                    return true;
                }
            });
        }

        // set online status
        user = LocalUserService.getLocalUserFromPreferences(this);
        if (user.Email != null) {
            if (refUser == null) {
                refUser = new Firebase(StaticInfo.UsersURL + "/" + user.Email);
            }
        }
        if (refUser != null)
            refUser.child("Status").setValue("Online");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // set last seen
        DateFormat dateFormat = new SimpleDateFormat("dd MM yy hh:mm a");
        Date date = new Date();
        refUser.child("Status").setValue(dateFormat.format(date));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_logout) {
            new AlertDialog.Builder(this)
                    .setTitle("Logout?")
                    .setMessage("Are you sure to logout, you will no longer receive notifications.")
                    .setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // set last seen
                            DateFormat dateFormat = new SimpleDateFormat("dd MM yy hh:mm a");
                            Date date = new Date();
                            refUser.child("Status").setValue(dateFormat.format(date));
                            if (LocalUserService.deleteLocalUserFromPreferences(getApplicationContext())) {
                                db.deleteAllFriendsFromLocalDB();
                                // stopService(new Intent(getApplicationContext(), AppService.class));
                                Toast.makeText(getApplicationContext(), "Logout Success", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), ActivityLogin.class);
                                startActivityForResult(intent, 100);
                            } else {
                                Toast.makeText(getApplicationContext(), "Logout Success", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), ActivityLogin.class);
                                startActivityForResult(intent, 100);
                            }
                        }
                    })
                    .setNegativeButton(android.R.string.no, null)
                    .show();
            return true;
        }
        if (id == R.id.menu_profile) {
            startActivity(new Intent(this, ActivityProfile.class));

        }

        if (id == R.id.menu_addContacts) {
            startActivity(new Intent(this, ActivityAddContact.class));
            return true;
        }

        if (id == R.id.menu_notification) {
            startActivity(new Intent(this, ActivityNotifications.class));
            return true;
        }

        if (id == R.id.menu_refresh) {
            
            if (Tools.isNetworkAvailable(this)){

                FriendListTask t = new FriendListTask();
                t.execute();
            }else {
                Toast.makeText(this, "Please check your internet connection.", Toast.LENGTH_SHORT).show();
            }
            
        }

        return super.onOptionsItemSelected(item);
    }

    public static class PlaceholderFragment extends Fragment {

        private static final String ARG_SECTION_NUMBER = "section_number";
        private View rootView;
        private ListView lv_LastChatList;
        private DataContext db;
        User user;
        private List<User> userFriendList;
        private List<Message> userLastChatList;


        public PlaceholderFragment() {
        }


        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            if (user == null) {
                user = LocalUserService.getLocalUserFromPreferences(getActivity());
            }
            db = new DataContext(getActivity(), null, null, 1);
            // Chat tab
            if (getArguments().getInt(ARG_SECTION_NUMBER) == 1) {
                rootView = inflater.inflate(R.layout.fragment_chat, container, false);
                userLastChatList = db.getUserLastChatList(user.Email);
                ListAdapter adp = new AdapterLastChat(getActivity(), userLastChatList);
                lv_LastChatList = (ListView) rootView.findViewById(R.id.lv_LastChatList);
                lv_LastChatList.setAdapter(adp);
                lv_LastChatList.setOnItemClickListener(
                        new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                TextView email = (TextView) view.findViewById(R.id.tv_lastChat_HiddenEmail);
                                TextView tv_Name = (TextView) view.findViewById(R.id.tv_lastChat_FriendFullName);
                                Intent intend = new Intent(getActivity(), ActivityChat.class);
                                intend.putExtra("FriendEmail", email.getText().toString());
                                intend.putExtra("FriendFullName", tv_Name.getText().toString());
                                startActivity(intend);
                            }
                        }
                );

                lv_LastChatList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                        if (userLastChatList.size() <= position) return false;
                        final Message selectedMessageItem = userLastChatList.get(position);
                        final CharSequence options[] = new CharSequence[]{"Delete Chat"};
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle(selectedMessageItem.FriendFullName);
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int index) {
                                // the user clicked on list[index]
                                if (index == 0) {
                                    // Delete Chat
                                    new AlertDialog.Builder(getActivity())
                                            .setTitle(selectedMessageItem.FriendFullName)
                                            .setMessage("Are you sure to delete this chat?")
                                            .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    db.deleteChat(user.Email, selectedMessageItem.FromMail);
                                                    Toast.makeText(getActivity(), "Chat deleted successfully", Toast.LENGTH_SHORT).show();
                                                    userLastChatList = db.getUserLastChatList(user.Email);
                                                    ListAdapter adp = new AdapterLastChat(getActivity(), userLastChatList);
                                                    lv_LastChatList = (ListView) rootView.findViewById(R.id.lv_LastChatList);
                                                    lv_LastChatList.setAdapter(adp);
                                                }
                                            })
                                            .setNegativeButton(android.R.string.no, null)
                                            .show();
                                }
                            }
                        });

                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                        builder.show();

                        return true;
                    }
                });

                return rootView;
            }
            // Contacts tab
            else {
                rootView = inflater.inflate(R.layout.fragment_contact, container, false);
                userFriendList = db.getUserFriendList();
                ListAdapter adp = new FriendListAdapter(getActivity(), userFriendList);
                ListView lv_FriendList = (ListView) rootView.findViewById(R.id.lv_FriendList);
                lv_FriendList.setAdapter(adp);
                lv_FriendList.setOnItemClickListener(
                        new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                TextView email = (TextView) view.findViewById(R.id.tv_HiddenEmail);
                                TextView tv_Name = (TextView) view.findViewById(R.id.tv_FriendFullName);
                                Intent intend = new Intent(getActivity(), ActivityChat.class);
                                intend.putExtra("FriendEmail", email.getText().toString());
                                intend.putExtra("FriendFullName", tv_Name.getText().toString());
                                startActivity(intend);
                            }
                        }

                );

                lv_FriendList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                        if (userFriendList.size() <= position) return false;
                        final User selectedUser = userFriendList.get(position);
                        final CharSequence options[] = new CharSequence[]{"Profile", "Delete Contact"};
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle(selectedUser.FirstName + " " + selectedUser.LastName);
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int index) {
                                // the user clicked on list[index]
                                if (index == 0) {
                                    // Profile
                                    Intent intent = new Intent(getActivity(), ActivityFriendProfile.class);
                                    intent.putExtra("Email", selectedUser.Email);
                                    startActivityForResult(intent, StaticInfo.ChatAciviityRequestCode);
                                } else {
                                    // Delete Contact
                                    new AlertDialog.Builder(getActivity())
                                            .setTitle(selectedUser.FirstName + " " + selectedUser.LastName)
                                            .setMessage("Are you sure to delete this contact?")
                                            .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    Firebase ref = new Firebase(StaticInfo.EndPoint + "/friends/" + user.Email + "/" + selectedUser.Email);
                                                    ref.removeValue();
                                                    // delete from local database
                                                    db.deleteFriendByEmailFromLocalDB(selectedUser.Email);
                                                    Toast.makeText(getActivity(), "Contact deleted successfully", Toast.LENGTH_SHORT).show();
                                                    userFriendList = db.getUserFriendList();
                                                    ListAdapter adp = new FriendListAdapter(getActivity(), userFriendList);
                                                    ListView lv_FriendList = (ListView) rootView.findViewById(R.id.lv_FriendList);
                                                    lv_FriendList.setAdapter(adp);
                                                }
                                            })
                                            .setNegativeButton(android.R.string.no, null)
                                            .show();
                                }
                            }
                        });

                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                        builder.show();


                        return true;
                    }
                });
                return rootView;
            }

        }
    }


    public class SectionsPagerAdapter extends FragmentPagerAdapter {


        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "CHATS";
                case 1:
                    return "CONTACTS";
            }
            return null;
        }
    }


    public class FriendListTask extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            pd.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            user = LocalUserService.getLocalUserFromPreferences(getApplicationContext());
            IFireBaseAPI api = Tools.makeRetroFitApi();
            Call<String> call = api.getUserFriendsListAsJsonString(StaticInfo.FriendsURL + "/" + user.Email + ".json");
            try {
                return call.execute().body();
            } catch (IOException e) {
                e.printStackTrace();
                pd.hide();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String jsonListString) {

            try {
                user = LocalUserService.getLocalUserFromPreferences(getApplicationContext());
                List<User> friendList = new ArrayList<>();
                JSONObject userFriendTree = new JSONObject(jsonListString);
                for (Iterator iterator = userFriendTree.keys(); iterator.hasNext(); ) {
                    String key = (String) iterator.next();
                    User friend = new User();
                    JSONObject friendJson = userFriendTree.getJSONObject(key);
                    friend.Email = friendJson.getString("Email");
                    friend.FirstName = friendJson.getString("FirstName");
                    friend.LastName = friendJson.getString("LastName");
                    friendList.add(friend);
                }

                // refresh local database
                db = new DataContext(getApplicationContext(), null, null, 1);
                db.refreshUserFriendList(friendList);

                // set to adapter
                ListAdapter adp = new FriendListAdapter(getApplicationContext(), db.getUserFriendList());
                ListView lv_FriendList = (ListView) findViewById(R.id.lv_FriendList);
                lv_FriendList.setAdapter(adp);
                pd.hide();
                Toast.makeText(ActivityMain.this, "Contact list is updated", Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                pd.hide();
                e.printStackTrace();
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        user = LocalUserService.getLocalUserFromPreferences(this);
        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            if (refUser == null) {
                refUser = new Firebase(StaticInfo.UsersURL + "/" + user.Email);
            }
            startService(new Intent(getApplicationContext(), AppService.class));
            FriendListTask t = new FriendListTask();
            t.execute();
        }
    }


}
