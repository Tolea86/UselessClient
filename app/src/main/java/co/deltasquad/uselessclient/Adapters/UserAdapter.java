package co.deltasquad.uselessclient.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import co.deltasquad.uselessclient.BlogActivity;
import co.deltasquad.uselessclient.Objects.BlogPostObject;
import co.deltasquad.uselessclient.Objects.UserObject;
import co.deltasquad.uselessclient.R;

public class UserAdapter extends ArrayAdapter<UserObject> {

    private List<UserObject> items;

    Context contextGlobal;

    private final LayoutInflater mInflater;


    public UserAdapter(Context context) {
        super(context, R.layout.user);
        contextGlobal = context;

        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setData(List<UserObject> data) {

        clear();
        if (data != null) {
            addAll(data);
            notifyDataSetChanged();
        }
        items = data;
    }

    public UserObject getItem(int position) {
        return items.get(position);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View view;
        if (convertView == null) {
            view =  mInflater.inflate(R.layout.user, parent, false);
        } else {
            view = convertView;
        }

        final UserObject currentPost = items.get(position);

        TextView userHandle = view.findViewById(R.id.userHandle);
        TextView userName = view.findViewById(R.id.userName);

        userHandle.setText(currentPost.getHandle());
        userName.setText(currentPost.getUsername());

        return view;
    }
}
