package com.example.quizapp_main.model;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quizapp_main.R;

import java.text.NumberFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class RankingAdapter extends RecyclerView.Adapter<RankingAdapter.RankingViewHolder> {

    private List<Player> playerList;

    public RankingAdapter(List<Player> playerList) {
        this.playerList = playerList;
        // Sắp xếp danh sách theo số câu hỏi giảm dần
        Collections.sort(this.playerList, new Comparator<Player>() {
            @Override
            public int compare(Player p1, Player p2) {
                return Integer.compare(p2.getMoney(), p1.getMoney());
            }
        });
    }

    @NonNull
    @Override
    public RankingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ranking_item, parent, false);
        return new RankingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RankingViewHolder holder, int position) {
        Player player = playerList.get(position);

        // Set vị trí (thứ hạng)
        holder.rankPosition.setText(String.valueOf(position + 1));
        // Set tên người chơi
        holder.rankName.setText(player.getName());
        // Set số câu trả lời đúng (đã có đơn vị "câu" trong layout)
        holder.rankQuestionNumber.setText(String.valueOf(player.getQuestionNumber()));
        // Set số tiền (định dạng và có đơn vị "VND" trong layout)
        String formattedMoney = NumberFormat.getNumberInstance(Locale.US).format(player.getMoney());
        holder.rankMoney.setText(formattedMoney);
    }

    @Override
    public int getItemCount() {
        return playerList.size();
    }

    public static class RankingViewHolder extends RecyclerView.ViewHolder {
        TextView rankPosition, rankName, rankQuestionNumber, rankMoney;

        public RankingViewHolder(View itemView) {
            super(itemView);
            rankPosition = itemView.findViewById(R.id.rankPosition);
            rankName = itemView.findViewById(R.id.rankName);
            rankQuestionNumber = itemView.findViewById(R.id.rankQuestionNumber);
            rankMoney = itemView.findViewById(R.id.rankMoney);
        }
    }
}