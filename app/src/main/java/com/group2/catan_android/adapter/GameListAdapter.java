package com.group2.catan_android.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.group2.catan_android.data.model.AvailableGame;
import com.group2.catan_android.databinding.GameItemBinding;

import java.util.List;

public class GameListAdapter extends RecyclerView.Adapter<GameListAdapter.GameListViewHolder> {

    private List<AvailableGame> games;
    private final ItemClickListener listener;

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
    }

    @Override
    public int getItemCount() {
        return games.size();
    }

    public static class GameListViewHolder extends RecyclerView.ViewHolder{

        GameItemBinding binding;
        GameListViewHolder(GameItemBinding gameItemBinding){
            super(gameItemBinding.getRoot());
            binding = gameItemBinding;
        }

        void bindListener(AvailableGame game, ItemClickListener listener){
            binding.getRoot().setOnClickListener(v ->
                    listener.onItemClicked(game)
            );
        }

        void setGameData(AvailableGame game){
            binding.gameID.setText(game.getGameID());
            binding.playersConnected.setText(Integer.toString(game.getPlayerCount()));
        }
    }

    public interface ItemClickListener{
        void onItemClicked(AvailableGame game);
    }
}
