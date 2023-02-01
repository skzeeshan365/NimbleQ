package com.reiserx.nimbleq.Adapters;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;
import com.reiserx.nimbleq.Constants.CONSTANTS;
import com.reiserx.nimbleq.Models.UserData;
import com.reiserx.nimbleq.R;
import com.reiserx.nimbleq.Utils.dialogs;
import com.reiserx.nimbleq.databinding.LtHomeListAdminBinding;

import java.util.ArrayList;
import java.util.List;

public class LearnerListAdapter extends RecyclerView.Adapter<LearnerListAdapter.HomeViewHolder> {

    Context context;
    List<UserData> data;
    String ratings;
    dialogs dialogs;

    public void setData(List<UserData> data) {
        this.data = data;
    }

    public void clear() {
        if (data != null && !data.isEmpty())
            data.clear();
    }

    public List<UserData> getData() {
        return data;
    }

    public LearnerListAdapter(Context context, dialogs dialogs) {
        this.context = context;
        this.dialogs = dialogs;
    }

    @NonNull
    @Override
    public LearnerListAdapter.HomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.lt_home_list_admin, parent, false);
        return new LearnerListAdapter.HomeViewHolder(view);
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    @Override
    public void onBindViewHolder(@NonNull LearnerListAdapter.HomeViewHolder holder, int position) {
        UserData model = data.get(position);
        holder.binding.textView25.setText(model.getUserName());

        holder.binding.getRoot().setOnClickListener(view -> {
            AlertDialog.Builder alert = new AlertDialog.Builder(context);
            alert.setTitle(model.getUserName());
            String grade = context.getString(R.string.grade2) + " " + model.getUserDetails().getGrade();
            String stateCity = "\n" + context.getString(R.string.lives_in_1) + " " + model.getUserDetails().getState() + ", " + model.getUserDetails().getCity();
            String gender = "\n" + context.getString(R.string.gender_2) + " " + model.getUserDetails().getGender();
            String schoolname = "\n" + context.getString(R.string.school_2) + " " + model.getUserDetails().getSchoolName();

            if (model.getRating() > 0) {
                String rating = String.format("%.1f", model.getRating());
                ratings = "\n" + context.getString(R.string.ratings) + " " + rating;
            } else {
                ratings = "\n" + context.getString(R.string.ratings) + " 0";
            }

            alert.setMessage(grade + schoolname + stateCity + gender + ratings);
            alert.setPositiveButton(context.getString(R.string.chat_with_learner), (dialogInterface, i) -> {
                String[] permissions = {Manifest.permission.WRITE_CONTACTS, Manifest.permission.READ_CONTACTS};
                Permissions.check(context, permissions, null, null, new PermissionHandler() {
                    @Override
                    public void onGranted() {
                        if (contactExists(model.getPhoneNumber()))
                            openWhatsappContact(model.getPhoneNumber());
                        else
                            addContact(model.getUserName(), model.getPhoneNumber());
                    }
                });
            });
            alert.setNegativeButton(context.getString(R.string.menu_send_notification), (dialogInterface, i) -> dialogs.sendNotification(model.getFCM_TOKEN(), model.getUserName()));
            alert.setNeutralButton(context.getString(R.string.close), null);
            alert.show();
        });
    }

    @Override
    public int getItemCount() {
        if (data == null)
            return 0;
        else
            return data.size();
    }

    public class HomeViewHolder extends RecyclerView.ViewHolder {

        LtHomeListAdminBinding binding;

        public HomeViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = LtHomeListAdminBinding.bind(itemView);
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setFilter(List<UserData> FilteredDataList) {
        data = FilteredDataList;
        notifyDataSetChanged();
    }

    private void addContact(String username, String phone) {
        ArrayList<ContentProviderOperation> op_list = new ArrayList<>();
        op_list.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                //.withValue(RawContacts.AGGREGATION_MODE, RawContacts.AGGREGATION_MODE_DEFAULT)
                .build());

        op_list.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, username)
                .build());

        op_list.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, phone)
                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_HOME)
                .build());

        try {
            ContentProviderResult[] results = context.getContentResolver().applyBatch(ContactsContract.AUTHORITY, op_list);
            androidx.appcompat.app.AlertDialog.Builder alert = new androidx.appcompat.app.AlertDialog.Builder(context);
            alert.setTitle(context.getString(R.string.chat_with_learner));
            alert.setMessage(context.getString(R.string.chat_with_learner_msg));
            alert.setPositiveButton(context.getString(R.string.open_whatsapp), (dialogInterface, i) -> openWhatsappContact(phone));
            alert.setNegativeButton(context.getString(R.string.cancel), null);
            alert.show();
        } catch (Exception e) {
            Log.d(CONSTANTS.TAG2, e.toString());
            e.printStackTrace();
        }
    }

    public boolean contactExists(String number) {
        if (number != null) {
            Uri lookupUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
            String[] mPhoneNumberProjection = {ContactsContract.PhoneLookup._ID, ContactsContract.PhoneLookup.NUMBER, ContactsContract.PhoneLookup.DISPLAY_NAME};
            try (Cursor cur = context.getContentResolver().query(lookupUri, mPhoneNumberProjection, null, null, null)) {
                if (cur.moveToFirst()) {
                    return true;
                }
            }
        }
        return false;
    }

    public void openWhatsappContact(String numberWithCountryCode) {
        Uri uri = Uri.parse("https://api.whatsapp.com/send?phone=" + numberWithCountryCode + "&text=" + "");
        Intent sendIntent = new Intent(Intent.ACTION_VIEW, uri);
        context.startActivity(sendIntent);
    }
}