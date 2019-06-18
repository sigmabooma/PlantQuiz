package com.example.plantquiz.model

import org.json.JSONArray
import org.json.JSONObject

class ParsePlantUtility {

    fun parsePlantObjectsFromJSONData(): List<Plant>?{

        val allPlantObjects: ArrayList<Plant> = ArrayList()

        val downloadingObject = DownloadingObject()
        val topLevelPlantJSONData = downloadingObject.downloadJSONDataFromLink("http://plantplaces.com/perl/mobile/flashcard.pl")
        val topLevelJSONObject = JSONObject(topLevelPlantJSONData)
        val plantObjectsArray: JSONArray = topLevelJSONObject.getJSONArray("values")

        var index = 0
        while (index < plantObjectsArray.length()){
            val plantObject = Plant()
            val jsonObject = plantObjectsArray.getJSONObject(index)

            /*
            (genus: String, species: String, cultivar: String, common: String,
            pictureName: String, description: String, difficulty: Int, id: Int = 0)
             */

            with(jsonObject) {
                plantObject.genus = getString("genus")
                plantObject.species = getString("species")
                plantObject.cultivar = getString("cultivar")
                plantObject.common = getString("common")
                plantObject.pictureName = getString("picture_name")
                plantObject.description = getString("description")
                plantObject.difficulty = getInt("difficulty")
                plantObject.id = getInt("id")
            }

            allPlantObjects.add(plantObject)
            index++
        }
        return allPlantObjects
    }
}

