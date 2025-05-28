package com.example.triviyaaas.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.triviyaaas.R;

public class CategoriesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private Cursor cursor;
    private boolean isGrid;

    public CategoriesAdapter(Context context, Cursor cursor, boolean isGrid) {
        this.context = context;
        this.cursor = cursor;
        this.isGrid = isGrid;
    }

    public void setIsGrid(boolean isGrid) {
        this.isGrid = isGrid;
    }

    public interface OnCategoryClickListener {
        void onCategoryClick(int categoryId);
    }

    private OnCategoryClickListener listener;

    public void setOnCategoryClickListener(OnCategoryClickListener listener) {
        this.listener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        return isGrid ? 0 : 1; // 0 for grid, 1 for list
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == 0) {
            view = LayoutInflater.from(context).inflate(R.layout.categories_grid_item, parent, false);
            return new GridViewHolder(view);
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.categories_item_list, parent, false);
            return new ListViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (cursor != null && cursor.moveToPosition(position)) {
            int columnIndex = cursor.getColumnIndex("id");
            if (columnIndex != -1) {
                @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex("name"));
                @SuppressLint("Range") int quizCount = cursor.getInt(cursor.getColumnIndex("quiz_count"));
                @SuppressLint("Range") String imageName = cursor.getString(cursor.getColumnIndex("image_name"));
                @SuppressLint("Range") int categoryId = cursor.getInt(columnIndex);

                int imageResId = getImageResourceIdByName(imageName);

                if (holder instanceof GridViewHolder) {
                    ((GridViewHolder) holder).bind(name, quizCount, imageResId);
                } else if (holder instanceof ListViewHolder) {
                    ((ListViewHolder) holder).bind(name, quizCount, imageResId);
                }

                holder.itemView.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onCategoryClick(categoryId);
                    }
                });
            } else {
                Log.e("CategoryAdapter", "Column 'id' not found in cursor.");
            }
        }
    }


    @Override
    public int getItemCount() {
        return cursor != null ? cursor.getCount() : 0;
    }

    public class GridViewHolder extends RecyclerView.ViewHolder {
        TextView categoryTitle, quizNumberTV;
        ImageView categoryImage;

        public GridViewHolder(View itemView) {
            super(itemView);
            categoryTitle = itemView.findViewById(R.id.courseTitle);
            categoryImage = itemView.findViewById(R.id.courseIV);
            quizNumberTV = itemView.findViewById(R.id.quizNumberTV);
        }

        public void bind(String name, int quizCount, int imageResId) {
            categoryTitle.setText(name);
            categoryImage.setImageResource(imageResId);
            quizNumberTV.setText(quizCount + " Available Quiz");
        }
    }

    public class ListViewHolder extends RecyclerView.ViewHolder {
        AppCompatTextView categoryTitle, quizNumberTV;
        AppCompatImageView categoryImage;

        public ListViewHolder(View itemView) {
            super(itemView);
            categoryTitle = itemView.findViewById(R.id.courseTitle);
            categoryImage = itemView.findViewById(R.id.courseIV);
            quizNumberTV = itemView.findViewById(R.id.quizNumberTV);
        }

        public void bind(String name, int quizCount, int imageResId) {
            categoryTitle.setText(name);
            categoryImage.setImageResource(imageResId);
            quizNumberTV.setText(quizCount + " Available Quiz");
        }
    }

    private int getImageResourceIdByName(String imageName) {
        switch (imageName) {
            case "general":
                return R.drawable.general_knowledge;
            case "ic_science":
                return R.drawable.science;
            case "ic_computer":
                return R.drawable.computer;
            case "ic_history":
                return R.drawable.history;
            default:
                return R.drawable.app_logo_small_transparent;
        }
    }

    public void swapCursor(Cursor newCursor) {
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        cursor = newCursor;
        if (newCursor != null) {
            notifyDataSetChanged();
        }
    }

    public void closeCursor() {
        if (cursor != null) {
            cursor.close();
        }
    }
}
