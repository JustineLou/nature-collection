package fr.justine.naturecollection.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import fr.justine.naturecollection.MainActivity
import fr.justine.naturecollection.PlantModel
import fr.justine.naturecollection.PlantRepository.Singleton.plantList
import fr.justine.naturecollection.R
import fr.justine.naturecollection.adapter.PlantAdapter
import fr.justine.naturecollection.adapter.PlantItemDecoration

class HomeFragment(
        private val context: MainActivity
) : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        //on injecte le layout dans le fragment
        val view = inflater?.inflate(R.layout.fragment_home, container, false)

/*       N'EST PLUS UTILE AVEC LA DB -> on impote le plantList qui fera référence à celui du singleton
         //Créer une liste qui va stocker les plantes
         val plantList = arrayListOf<PlantModel>()*/

/*
        ENREGISTREMENT EN LOCAL DES DONNEES (N'EST PLUS UTILE AVEC LA DB)

        //Enregistrer une première plante
        plantList.add(PlantModel(
                name = "Pissenlit",
                description = "jaune soleil",
                imageUrl = "https://cdn.pixabay.com/photo/2016/07/30/13/16/dandelion-1557110__340.jpg",
                liked = false)
        )

        //Enregistrer une deuxième plante
        plantList.add(PlantModel(
                name = "Rose",
                description = "ça pique",
                imageUrl = "https://cdn.pixabay.com/photo/2018/02/09/21/46/rose-3142529_960_720.jpg",
                liked = false)
        )

        //Enregistrer une troisième plante
        plantList.add(PlantModel(
                name = "Cactus",
                description = "ça pique fort",
                imageUrl = "https://cdn.pixabay.com/photo/2014/07/29/08/55/cactus-404362_960_720.jpg",
                liked = false)
        )

        //Enregistrer une quatrième plante
        plantList.add(PlantModel(
                name = "Tulipe",
                description = "c'est beau",
                imageUrl = "https://cdn.pixabay.com/photo/2017/03/30/18/38/tulip-2189317_960_720.jpg",
                liked = false)
        )*/

        //récupérer le recyclerview :
        val horizontalRecyclerView = view.findViewById<RecyclerView>(R.id.horizontal_recycler_view)
        horizontalRecyclerView.adapter = PlantAdapter(context, plantList.filter { !it.liked }, R.layout.item_horizontal_plant)

        //récupérer le second recycler view
        val verticalRecyclerView = view.findViewById<RecyclerView>(R.id.vertical_recycler_view)
        verticalRecyclerView.adapter = PlantAdapter(context, plantList, R.layout.item_vertical_plant)
        verticalRecyclerView.addItemDecoration(PlantItemDecoration())

        return view
    }
}