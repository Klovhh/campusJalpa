package com.example.campusjalpa;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {
    private final List<Item> items;   // Lista de elementos
    private final Context context;    // Contexto de la aplicación

    public ItemAdapter(List<Item> items, Context context) {
        this.items = items;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflar el diseño de la tarjeta de vista
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Item item = items.get(position);
        holder.title.setText(item.getTitle());         // Establecer el título
        holder.description.setText(item.getDescription());   // Establecer la descripción
        String url = item.getImageUrl();
        Picasso.get().load(url).into(holder.imageView);   // Cargar la imagen del elemento en el ImageView utilizando Picasso
    }

    @Override
    public int getItemCount() {
        return items.size();   // Devolver el número de elementos en la lista
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;    // ImageView para mostrar la imagen
        TextView title;         // TextView para mostrar el título
        TextView description;   // TextView para mostrar la descripción
        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_view);    // Obtener la referencia al ImageView
            title = itemView.findViewById(R.id.title);             // Obtener la referencia al TextView del título
            description = itemView.findViewById(R.id.description); // Obtener la referencia al TextView de la descripción
        }
    }
}
