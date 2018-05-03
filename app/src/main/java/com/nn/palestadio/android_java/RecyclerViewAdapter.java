package com.nn.palestadio.android_java;


import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{

    private ArrayList<MatchInformation> boletas = new ArrayList<>();
    private Context context;

    public RecyclerViewAdapter(Context nContext, ArrayList<MatchInformation> nBoletas) {
        boletas = nBoletas;
        context = nContext;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.match_item, parent,false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        MatchInformation m = boletas.get(position);
        holder.fecha.setText(m.getFecha());
        holder.silla.setText("Silla: " + m.getAsiento());
        holder.hora.setText("HORA OFICIAL DEL PARTIDO " + m.getHora());
        holder.tribuna.setText("Tribuna: " + m.getTribuna());
        holder.equipo1.setImageResource(setImageEquipo(m.getEquipo1()));
        holder.equipo2.setImageResource(setImageEquipo(m.getEquipo2()));
        holder.enviarQR.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                HomeActivity.createQR(context, position);
            }
        });
    }

    public int setImageEquipo(String equipo) {
        if(equipo.equals("america"))
            return R.mipmap.ic_america;
        else if(equipo.equals("deportivo_cali"))
            return R.mipmap.ic_deportivo_cali;
        else if(equipo.equals("millonarios"))
            return R.mipmap.ic_millonarios;
        else if(equipo.equals("medellin"))
            return R.mipmap.ic_medellin;
        else if(equipo.equals("nacional"))
            return R.mipmap.ic_nacional;
        else if(equipo.equals("santa_fe"))
            return R.mipmap.ic_santa_fe;
        else
            return -1;

    }

    @Override
    public int getItemCount() {
        return boletas.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView fecha,hora,silla,tribuna;
        ImageView equipo1, equipo2;
        Button enviarQR;

        public ViewHolder(View itemView) {
            super(itemView);
            fecha = itemView.findViewById(R.id.textViewFecha);
            hora = itemView.findViewById(R.id.textViewHora);
            silla = itemView.findViewById(R.id.textViewSilla);
            tribuna = itemView.findViewById(R.id.textViewTribuna);
            equipo1 = itemView.findViewById(R.id.imageViewEquipo1);
            equipo2 = itemView.findViewById(R.id.imageViewEquipo2);
            enviarQR = itemView.findViewById(R.id.buttonQR);
        }
    }
}
