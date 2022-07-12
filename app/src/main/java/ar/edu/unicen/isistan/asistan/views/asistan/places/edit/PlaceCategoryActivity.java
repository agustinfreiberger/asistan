package ar.edu.unicen.isistan.asistan.views.asistan.places.edit;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;

import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatDrawableManager;
import androidx.appcompat.widget.Toolbar;
import ar.edu.unicen.isistan.asistan.R;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.places.AndFilter;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.places.Filter;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.places.FilterByWord;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.places.FilterLeaf;
import ar.edu.unicen.isistan.asistan.storage.database.mobility.places.PlaceCategory;

public class PlaceCategoryActivity extends AppCompatActivity {

    public static final String RESULT = "category";

    TreeNode.TreeNodeClickListener clickListener;
    private LinearLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_category);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        this.getSupportActionBar().setTitle("Categoría de lugar");

        this.clickListener = new TreeNode.TreeNodeClickListener() {
            @Override
            public void onClick(TreeNode node, Object value) {
                PlaceCategory place_category = (PlaceCategory) node.getValue();
                Intent data = new Intent();
                data.putExtra(RESULT,place_category.getCode());
                setResult(RESULT_OK, data);
                finish();
            }
        };

        SearchView searchView = this.findViewById(R.id.search_bar);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Filter filter = new AndFilter(new FilterLeaf(true),new FilterByWord(query.split(" ")));
                ArrayList<PlaceCategory> categories = new ArrayList<>();
                PlaceCategory.getRoot().filter(filter,categories);
                TreeNode root = generateTree(0,categories);
                AndroidTreeView treeView = new AndroidTreeView(PlaceCategoryActivity.this, root);
                PlaceCategoryActivity.this.layout.removeAllViews();
                PlaceCategoryActivity.this.layout.addView(treeView.getView());

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    PlaceCategoryActivity.this.layout.removeAllViews();
                    TreeNode root = generateTree(0,PlaceCategory.getRoot());
                    AndroidTreeView tree = new AndroidTreeView(PlaceCategoryActivity.this, root);
                    PlaceCategoryActivity.this.layout.addView(tree.getView());
                } else {
                    Filter filter = new AndFilter(new FilterLeaf(true),new FilterByWord(newText.split(" ")));
                    ArrayList<PlaceCategory> categories = new ArrayList<>();
                    PlaceCategory.getRoot().filter(filter,categories);
                    TreeNode root = generateTree(0,categories);
                    AndroidTreeView treeView = new AndroidTreeView(PlaceCategoryActivity.this, root);
                    PlaceCategoryActivity.this.layout.removeAllViews();
                    PlaceCategoryActivity.this.layout.addView(treeView.getView());
                }
                return false;
            }
        });

        this.layout = this.findViewById(R.id.categories_list);

        TreeNode root = generateTree(0,PlaceCategory.getRoot());
        AndroidTreeView tree = new AndroidTreeView(this, root);
        this.layout.addView(tree.getView());
    }

    private TreeNode generateTree(int level, ArrayList<PlaceCategory> categories) {
        TreeNode node = TreeNode.root();

        for (PlaceCategory category : categories)
            node.addChild(generateTree(level+1,category));

        return node;
    }

    private TreeNode generateTree(int level, PlaceCategory category) {
        TreeNode node;
        if (level == 0)
            node = TreeNode.root();
        else
            node = new TreeNode(category);

        if (category.getSubTypes().isEmpty()) {
            node.setViewHolder(new ItemViewHolder(this, level));
            node.setClickListener(this.clickListener);
        }
        else {
            node.setViewHolder(new GroupViewHolder(this,level));
            for (PlaceCategory childCategory : category.getSubTypes())
                node.addChild(generateTree(level+1,childCategory));

        }
        return node;
    }

    private class ItemViewHolder extends TreeNode.BaseNodeViewHolder<PlaceCategory> {

        private int level;

        private ItemViewHolder(Context context, int level) {
            super(context);
            this.level = level;
        }

        @Override
        public View createNodeView(TreeNode node, PlaceCategory value) {
            final LayoutInflater inflater = LayoutInflater.from(this.context);

            final View view = inflater.inflate(R.layout.place_category_item, null, false);

            view.setPadding(this.level * 24,view.getPaddingTop(),view.getPaddingRight(),view.getPaddingBottom());

            TextView name = view.findViewById(R.id.name);
            name.setText(value.getName());

            TextView description = view.findViewById(R.id.description);
            description.setText(value.getDescription());
            return view;
        }

    }

    private class GroupViewHolder extends TreeNode.BaseNodeViewHolder<PlaceCategory> {

        private int level;

        private GroupViewHolder(Context context, int level) {
            super(context);
            this.level = level;
        }

        @Override
        public View createNodeView(TreeNode node, PlaceCategory value) {
            final LayoutInflater inflater = LayoutInflater.from(context);

            final View view = inflater.inflate(R.layout.place_category_group, null, false);

            view.setPadding(this.level * 24,view.getPaddingTop(),view.getPaddingRight(),view.getPaddingBottom());

            TextView name = view.findViewById(R.id.title);
            name.setText(value.getDescription());

            ImageView icon = view.findViewById(R.id.icon);
            icon.setImageDrawable(AppCompatDrawableManager.get().getDrawable(view.getContext(),value.getIconSrc()));

            return view;
        }

    }

}

