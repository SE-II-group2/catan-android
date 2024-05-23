package com.group2.catan_android.adapter;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.group2.catan_android.data.model.AvailableGame;
import com.group2.catan_android.R;
import com.group2.catan_android.databinding.GameItemBinding;

import java.util.List;

public class GameListAdapter extends RecyclerView.Adapter<GameListAdapter.GameListViewHolder> {

    private List<AvailableGame> games;
    private final ItemClickListener listener;
    private int selectedPos = RecyclerView.NO_POSITION;

    public GameListAdapter(List<AvailableGame> games, ItemClickListener listener){
        this.games = games;
        this.listener = listener;
    }

    public GameListAdapter(ItemClickListener listener){
        this.games = List.of();
        this.listener = listener;
    }

    public void setGames(List<AvailableGame> games){
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

    // fixme dont use inner classes, especially public ones
    public class GameListViewHolder extends RecyclerView.ViewHolder{

        GameItemBinding binding;
        CardView gameCardView;
        GameListViewHolder(GameItemBinding gameItemBinding){
            super(gameItemBinding.getRoot());
            binding = gameItemBinding;
            gameCardView = binding.getRoot().findViewById(R.id.gameCardView);
        }

        void bindListener(AvailableGame game, ItemClickListener listener){
            binding.getRoot().setOnClickListener(v -> {
                int previousSelectedPos = selectedPos;
                selectedPos = getAdapterPosition();
                if(selectedPos == previousSelectedPos)
                    selectedPos = RecyclerView.NO_POSITION;
                Log.d("GameListAdapter", "Clicked position: " + selectedPos + ", Previous position: " + previousSelectedPos);
                notifyItemChanged(previousSelectedPos);
                notifyItemChanged(selectedPos);
                listener.onItemClicked(game);
            });
        }

        void setGameData(AvailableGame game){
            binding.gameID.setText(game.getGameID());
            binding.playersConnected.setText(Integer.toString(game.getPlayerCount()));
        }
    }

    // fixme dont use inner interfaces either
    public interface ItemClickListener{
        void onItemClicked(AvailableGame game);
    }
}
