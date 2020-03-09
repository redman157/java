package com.jvit.companycoin.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.jvit.companycoin.api.ApiService;
import com.jvit.companycoin.object.FindUser;
import com.jvit.companycoin.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class FindUserAdapter extends ArrayAdapter<FindUser> {
    private Context context;
    private List<FindUser> findUserList, filterUserObjectList;
    private int layout;

    public FindUserAdapter(@NonNull Context context, int layout, @NonNull List<FindUser> findUserList) {
        super(context, layout, findUserList);
        this.context = context;
        this.layout = layout;

        /* @NOTE: tạo 2 mảng, một mảng dùng chứa dữ liệu cho filter, và một mảng để chứ các data sau
         * khi filter */
        this.findUserList = new ArrayList<>();
        this.filterUserObjectList = findUserList;

        for (FindUser item: findUserList) {
            this.findUserList.add(item);
        }

    }

    public View getView(int position, View convertView, ViewGroup viewGroup){
        View view = convertView;
        if (view == null){
            view = LayoutInflater.from(context).inflate(layout, null);
        }
        FindUser findUser = filterUserObjectList.get(position);
        if (findUser != null){
            ImageView avatar = view.findViewById(R.id.imgAvataFindUser);
            TextView name = view.findViewById(R.id.textNameFindUser);
            TextView email = view.findViewById(R.id.textEmailFindUser);

            Picasso.get()
                    .load(ApiService.url_path+ findUser.getAvatar())
                    .fit()
                    .into(avatar);
            name.setText(findUser.getName());
            email.setText(findUser.getEmail());
        }
        return view;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            public CharSequence convertResultToString(Object resultValue) {
                String email = ((FindUser) (resultValue)).getEmail();
                return email;
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                if (constraint != null) {
                    ArrayList<FindUser> suggestions = new ArrayList<>();

                    for (FindUser findUser : findUserList) {
                        if (findUser.getEmail().toLowerCase().contains(constraint.toString().toLowerCase())) {
                            suggestions.add(findUser);
                        }
                    }

                    FilterResults filterResults = new FilterResults();
                    filterResults.values = suggestions;
                    filterResults.count = suggestions.size();
                    return filterResults;
                } else {
                    return new FilterResults();
                }
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                ArrayList<FindUser> filteredList = (ArrayList<FindUser>) results.values;
                if (results.count > 0) {
                    /* @NOTE: cap nhat du lieu cho list dung de hien thi */
                    filterUserObjectList.clear();
                    filterUserObjectList.addAll(filteredList);
                    notifyDataSetChanged();
                }
            }
        };
    }
}
