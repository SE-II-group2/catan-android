package com.group2.catan_android.adapter;

import android.content.ClipData;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.group2.catan_android.R;
import com.group2.catan_android.databinding.GameItemBinding;
import com.group2.catan_android.networking.dto.Game;

import java.util.List;

public class GameListAdapter extends RecyclerView.Adapter<GameListAdapter.GameListViewHolder> {

    private List<Game> games;
    private final ItemClickListener listener;

    private int selectedPos = RecyclerView.NO_POSITION;

    public GameListAdapter(List<Game> games, ItemClickListener listener){
        this.games = games;
        this.listener = listener;
    }

    public GameListAdapter(ItemClickListener listener){
        this.games = List.of();
        this.listener = listener;
    }

    public void setGames(List<Game> games){
        this.games = games;
        selectedPos = RecyclerView.NO_POSITION;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public GameListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        GameItemBinding itemBinding = GameItemBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new GameListViewHolder(itemBinding);
    }


    @Override
    public void onBindViewHolder(@NonNull GameListViewHolder holder, int position) {
        holder.setGameData(games.get(position));
        holder.bindListener(games.get(position), listener);

        // TODO: maybe refactor sometime
        if (selectedPos == position){
            holder.binding.getRoot().setBackgroundColor(Color.BLACK);
            //holder.gameCardView.setCardBackgroundColor(ContextCompat.getColor(holder.gameCardView.getContext(), R.color.GrassGreenHighlighted));
            //holder.gameCardView.setCardBackgroundColor(Color.BLUE);
        } else {
            holder.binding.getRoot().setBackgroundColor(Color.TRANSPARENT);
            //holder.gameCardView.setCardBackgroundColor(ContextCompat.getColor(holder.gameCardView.getContext(), R.color.GrassGreen));
            //holder.gameCardView.setCardBackgroundColor(Color.GREEN);
        }
    }

    @Override
    public int getItemCount() {
        return games.size();
    }

    public class GameListViewHolder extends RecyclerView.ViewHolder{

        GameItemBinding binding;
        CardView gameCardView;
        GameListViewHolder(GameItemBinding gameItemBinding){
            super(gameItemBinding.getRoot());
            binding = gameItemBinding;
            gameCardView = binding.getRoot().findViewById(R.id.gameCardView);
        }

        void bindListener(Game game, ItemClickListener listener){
            binding.getRoot().setOnClickListener(v -> {
                int previousSelectedPos = selectedPos;
                selectedPos = getAdapterPosition();
                Log.d("GameListAdapter", "Clicked position: " + selectedPos + ", Previous position: " + previousSelectedPos);
                notifyItemChanged(previousSelectedPos);
                notifyItemChanged(selectedPos);
                listener.onItemClicked(game);
            });
        }

        void setGameData(Game game){
            binding.gameID.setText(game.getGameID());
            binding.playersConnected.setText(Integer.toString(game.getPlayerCount()));
        }
    }

    public interface ItemClickListener{
        void onItemClicked(Game game);
    }
}
