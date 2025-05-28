package com.example.triviyaaas.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;

import com.example.triviyaaas.R;
import com.example.triviyaaas.model.SearchItem;
import com.example.triviyaaas.ui.dashboard.CategoriesActivity;
import com.example.triviyaaas.ui.dashboard.SearchActivity;

import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchViewHolder> {
    private Context context;
    private List<SearchItem> searchItems;

    public SearchAdapter(Context context, List<SearchItem> searchItems) {
        this.context = context;
        this.searchItems = searchItems;
    }

    @Override
    public SearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.search_row_item, parent, false);
        return new SearchViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SearchViewHolder holder, int position) {
        SearchItem item = searchItems.get(position);
        holder.nameTextView.setText(item.getName());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, CategoriesActivity.class);
            intent.putExtra("CATEGORY_ID", item.getId());
            context.startActivity(intent);

            if (context instanceof SearchActivity) {
                ((SearchActivity) context).finish();
            }
        });
    }

    @Override
    public int getItemCount() {
        return searchItems.size();
    }

    public void updateData(List<SearchItem> newItems) {
        this.searchItems = newItems;
        notifyDataSetChanged();
    }

    public static class SearchViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;

        public SearchViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.title);
        }
    }
}
