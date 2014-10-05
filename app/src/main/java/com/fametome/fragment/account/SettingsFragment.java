package com.fametome.fragment.account;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.fametome.Dialog.FTDialog;
import com.fametome.FTFragment;
import com.fametome.R;
import com.fametome.activity.member.MainActivity;
import com.fametome.activity.register.RegisterLoginActivity;
import com.fametome.adapter.DisabledListArrayAdapter;
import com.fametome.database.DatabaseQuery;
import com.fametome.fragment.account.AccountFragment;
import com.fametome.object.User;
import com.fametome.util.ParseConsts;
import com.parse.ParseInstallation;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONObject;

public class SettingsFragment extends FTFragment {

    private ListView accountList;
    private ListView aboutList;
    private ListView logOutList;

    public SettingsFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ((MainActivity)getActivity()).setNavigationDrawerEnabled(false);
        setHasOptionsMenu(true);

        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);

        accountList = (ListView)rootView.findViewById(R.id.accountList);
        aboutList = (ListView)rootView.findViewById(R.id.aboutList);
        logOutList = (ListView)rootView.findViewById(R.id.logOutList);

        String[] accountStrings = {
                User.getInstance().getUsername(),
                ParseUser.getCurrentUser().getEmail()
        };

        String[] aboutStrings = {
                getString(R.string.settings_privacy_policy),
                getString(R.string.settings_terms_of_use)
        };

        String[] logOutStrings = {
                getString(R.string.settings_sign_out)
        };

        DisabledListArrayAdapter<String> accountListAdapter = new DisabledListArrayAdapter<String>(getActivity().getApplicationContext(), R.layout.item_settings_list, accountStrings);
        ArrayAdapter<String> aboutListAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), R.layout.item_settings_list, aboutStrings);
        ArrayAdapter<String> logOutListAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), R.layout.item_settings_list, logOutStrings);

        accountList.setAdapter(accountListAdapter);
        aboutList.setAdapter(aboutListAdapter);
        logOutList.setAdapter(logOutListAdapter);

        aboutList.setOnItemClickListener(clickItemAbout);
        logOutList.setOnItemClickListener(clickItemLogOut);

        return rootView;
    }

    private AdapterView.OnItemClickListener clickItemAbout = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if(position == 0){
                final FTDialog dialog = new FTDialog(((MainActivity)getActivity()).getContext());
                dialog.setTitle(R.string.settings_privacy_policy_title);
                dialog.setMessage(R.string.settings_privacy_policy_message);
                dialog.show();

            }else if(position == 1){

                final FTDialog dialog = new FTDialog(((MainActivity)getActivity()).getContext());
                dialog.setTitle(R.string.settings_terms_of_use_title);
                dialog.setMessage(R.string.settings_terms_of_use_message);
                dialog.show();
            }
        }
    };

    private AdapterView.OnItemClickListener clickItemLogOut = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if(position == 0){
                ParseUser.logOut();
                User.deleteInstance();
                ParseQuery.clearAllCachedResults();
                startActivity(new Intent(getActivity(), RegisterLoginActivity.class));
                getActivity().finish();
                ParseInstallation.getCurrentInstallation().put(ParseConsts.INSTALLATION_USER, JSONObject.NULL);
                ParseInstallation.getCurrentInstallation().saveEventually();

                /* Clear the initialisation */
                DatabaseQuery databaseQuery = new DatabaseQuery(getActivity().getApplicationContext());
                databaseQuery.removeInitialisation();
            }
        }
    };

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();

        final ActionBar actionBar = getActivity().getActionBar();
        actionBar.setIcon(R.drawable.ic_account_shadow);
        actionBar.setTitle(R.string.settings_title);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            AccountFragment accountFragment = new AccountFragment();
            ((MainActivity) getActivity()).showFragment(accountFragment);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
