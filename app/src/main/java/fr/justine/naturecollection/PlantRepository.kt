package fr.justine.naturecollection

import android.net.Uri
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import fr.justine.naturecollection.PlantRepository.Singleton.databaseRef
import fr.justine.naturecollection.PlantRepository.Singleton.downloadUri
import fr.justine.naturecollection.PlantRepository.Singleton.plantList
import fr.justine.naturecollection.PlantRepository.Singleton.storageReference
import java.net.URI
import java.util.*

class PlantRepository {
    object Singleton {
        //donner le lien pour accéder au bucket
        private val BUCKET_URL: String = "gs://naturecollection-97959.appspot.com"

        //se connecter à notre espace de stockage
        val storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(BUCKET_URL)

        //se connecter à la référence "plants"
        val databaseRef = FirebaseDatabase.getInstance("https://naturecollection-97959-default-rtdb.firebaseio.com/").getReference("plants")

        //créer une liste qui va contenir nos plantes
        val plantList = arrayListOf<PlantModel>()

        //contenir le lien de l'image courante
        var downloadUri: Uri? = null
    }

    fun updateData(callback: () -> Unit) {
        //absorber les données depuis la databaseRef pour les donner à la liste de plante
        databaseRef.addValueEventListener(object : ValueEventListener {
            // On récupère les données qui ont été récoltées sous la forme d'une liste
            override fun onDataChange(snapshot: DataSnapshot) {
                //retirer les anciennes qui ont été précédemment enregistrées pour maj la base
                plantList.clear()

                //On parcourre les éléments de la liste pour construire une nouvelle plante par rapport à ce qu'on aura récolté
                for (ds in snapshot.children) {

                    //construire un objet plante en récoltant les données avec le ds
                    val plant = ds.getValue(PlantModel::class.java)

                    //vérifier que la plante n'est pas nulle et à bien été chargée
                    if (plant != null) {
                        //ajouter la plante à notre liste
                        plantList.add(plant)
                    }
                }

                //actionner le call back (sinon les composants chargent avant les images)
                callback()
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    //Créer une fonction pour envoyer des fichiers sur le storage
    fun uploadImage(file: Uri, callback: () -> Unit) {
        //vérifiquer que ce fichier n'est pas nuel
        if(file != null){
            //on nomem le fichier
            val fileName = UUID.randomUUID().toString() + ".jpg"
            //on définit l'endroit de la bdd où on veut le ranegr
            val ref = storageReference.child(fileName)
            //on lui associe quel est el contenu à soumettre
            val uploadTask = ref.putFile(file)

            //démarrer la tâche d'envoi
            uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task <Uri>> { task ->
                // s'il y a eu un pb lors de l'envoi du fichier
                if(task.isSuccessful){
                    task.exception?.let{ throw it }
                }

                return@Continuation ref.downloadUrl
            }).addOnCompleteListener { task ->
                //vérifier si tout à bien fonctionné
                if(task.isSuccessful) {
                    //récupérr l'image
                    downloadUri = task.result
                    callback()
                }
            }
        }
    }

    //mettre à jour un objet plante en bdd
    fun updatePlant(plant: PlantModel) = databaseRef.child(plant.id).setValue(plant)

    //insérer une nouvelle plante en bdd
    fun insertPlant(plant: PlantModel) = databaseRef.child(plant.id).setValue(plant)

    //supprimer une plante de la base
    fun deletePlant(plant: PlantModel) = databaseRef.child(plant.id).removeValue()



}