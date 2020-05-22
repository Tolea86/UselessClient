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
import co.deltasquad.uselessclient.R;

public class BlogPostAdapter extends ArrayAdapter<BlogPostObject> {

    private List<BlogPostObject> items;

    Context contextGlobal;

    BlogActivity currentActivity;

    SharedPreferences sPref;

    private final LayoutInflater mInflater;



    public BlogPostAdapter(Context context, BlogActivity activity) {
        super(context, R.layout.blog_post);
        contextGlobal = context;

        currentActivity = activity;

        sPref = context.getSharedPreferences("USELESS", Context.MODE_PRIVATE);

        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setData(List<BlogPostObject> data) {

        clear();
        if (data != null) {
            addAll(data);
            notifyDataSetChanged();
        }
        items = data;
    }

    public BlogPostObject getItem(int position) {
        return items.get(position);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View view;
        if (convertView == null) {
            view =  mInflater.inflate(R.layout.blog_post, parent, false);
        } else {
            view = convertView;
        }

        final BlogPostObject currentPost = items.get(position);

        TextView authorHandle = view.findViewById(R.id.authorHandle);
        TextView postTitle = view.findViewById(R.id.postTitle);
        TextView blogContent = view.findViewById(R.id.blogContent);

        LinearLayout myPostButtons = view.findViewById(R.id.myPostButtons);

        RelativeLayout deleteButton = view.findViewById(R.id.deleteButton);
        RelativeLayout editButton = view.findViewById(R.id.editButton);

        authorHandle.setText(currentPost.getAuthor_handle());
        postTitle.setText(currentPost.getTitle());
        blogContent.setText(currentPost.getContent());

        authorHandle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentActivity.authorHandleClicked(currentPost.getAuthor_handle());
            }
        });

        if(currentPost.getAuthor_handle().equals("@" + sPref.getString("current_user_name", ""))){
            myPostButtons.setVisibility(View.VISIBLE);

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentActivity.deletePostClicked(currentPost.getSlug());
                }
            });
            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentActivity.editPostClicked(currentPost.getSlug());
                }
            });
        }
        else{
            myPostButtons.setVisibility(View.GONE);
        }

        return view;
    }
}
