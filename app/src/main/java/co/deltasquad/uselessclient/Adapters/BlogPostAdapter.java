package co.deltasquad.uselessclient.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import co.deltasquad.uselessclient.Objects.BlogPostObject;
import co.deltasquad.uselessclient.R;

public class BlogPostAdapter extends ArrayAdapter<BlogPostObject> {

    private List<BlogPostObject> items;

    Context contextGlobal;

    private final LayoutInflater mInflater;

    public BlogPostAdapter(Context context) {
        super(context, R.layout.blog_post);
        contextGlobal = context;

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

        authorHandle.setText(currentPost.getAuthor_handle());
        postTitle.setText(currentPost.getTitle());
        blogContent.setText(currentPost.getContent());

        return view;
    }
}
