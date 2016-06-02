package com.cardwatch.g1.CardWatch.utils;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

/**
 *  Réalisé par nicolassalleron le 01/05/16.
 */
public class Comparaison {


    private ArrayList<String> listeCarte;
    public double exactitude;
    public double rappel;
    public double précision;
    public double harmonique;
    public double reconnaissanceClasse;
    public double reconnaissanceNombre;

    /**
     *
     * @param fichierselectionner   Path du fichier
     */
    public Comparaison( String fichierselectionner, boolean isFile) {
        if(isFile)
            initListFile(fichierselectionner);
        else
            initListString(fichierselectionner);

    }
    public void initListString(String string){
        listeCarte = new ArrayList<>();
        String [] stringLine = string.split("\\n");
        Collections.addAll(listeCarte, stringLine);

    }

    public void initListFile(String fichierselectionner){
        listeCarte = new ArrayList<>();
        BufferedReader read;
        try {
            read = new BufferedReader(new FileReader(fichierselectionner));
            String strLine;
            //On stock toutes les carte du fichier dans une liste
            while ((strLine = read.readLine()) != null)   {
                listeCarte.add(strLine);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    public void compute(ArrayList<String> result){
        rappel(result);
        precision(result);
        moyenneHarmonique();
        reconnaissanceClasse(result);
        reconnaissanceNombre(result);

    }

    private void reconnaissanceNombre(ArrayList<String> result) {

        double nbReconnu = 0;

        for (int i = 0; i < result.size(); i++) {
            String[] stringResult = result.get(i).split(";");
            String[] stringFileResult = listeCarte.get(i).split(";");
            if (!stringResult[0].equals("false")) { //Nous avons détecté une carte dans l'image.
                if (stringResult[1].equals(stringFileResult[1])) {
                    nbReconnu +=1;
                }
            }else
                if(stringResult[0].equals(stringFileResult[0]))
                    nbReconnu +=1;

        }

        reconnaissanceNombre = (nbReconnu / (double)result.size()) *100;

    }

    private void reconnaissanceClasse(ArrayList<String> result) {

        double nbReconnu = 0;

        for (int i = 0; i < result.size(); i++) {
            String[] stringResult = result.get(i).split(";");
            String[] stringFileResult = listeCarte.get(i).split(";");
            if (!stringResult[0].equals("false")) { //Nous avons détecté une carte dans l'image.
                if (stringResult[0].equals(stringFileResult[0])) {
                    nbReconnu +=1;
                }
            }else
                if(stringResult[0].equals(stringFileResult[0]))
                    nbReconnu +=1;

        }

        reconnaissanceClasse = (nbReconnu / (double)result.size()) *100;

    }

    public void rappel(ArrayList<String> result){


        ArrayList<Integer> listeTruePositive = new ArrayList<>();
        ArrayList<Integer> listeFalseNegative = new ArrayList<>();
        ArrayList<Integer> listeNombre = new ArrayList<>();
        ArrayList<Integer> listeClasse = new ArrayList<>();


        for (int i = 0; i < result.size(); i++) {
            String[] stringResult = result.get(i).split(";");
            String[] stringFileResult = listeCarte.get(i).split(";");
            if (!stringResult[0].equals("false")) { //Nous avons détecté une carte dans l'image.

                if (stringResult[0].equals(stringFileResult[0])) {
                    listeClasse.add(1);
                } else {
                    listeClasse.add(0);
                }
                if (stringResult[1].equals(stringFileResult[1])) {
                    listeNombre.add(1);
                } else {
                    listeNombre.add(0);
                }
            } else {  //Nous n'avons pas détecté de carte.
                if (!stringFileResult[0].equals("false")) {   //Si dans le résultat il y avait bien une carte
                    listeClasse.add(0);
                    listeNombre.add(0);
                } else {      //Sinon, cela signifie que le faux était bon !
                    listeClasse.add(1);
                    listeNombre.add(1);
                }
            }
        }

        //Si a l'indice i le nombre =1 et la classe 1 alors la carte est bien reconnue et fais partie des TruePositive
        //Sinon elle fait partie des FalseNegative
        for(int i=0;i<listeNombre.size();i++){
            if(listeNombre.get(i) == 1 && listeClasse.get(i) ==1){
                listeTruePositive.add(1);
            }else{
                listeFalseNegative.add(1);
            }
        }

        //Rappel
        // VP / (VP + FN)
        rappel = ((double) listeTruePositive.size()/(listeTruePositive.size()+listeFalseNegative.size())) *100;




    }
    /*
    * La precision sert a mesurer les performances de l'algorithme en matiére de classification
    * En fonction de la présence ou non d'une garde dans l'image
     */
    public void precision(ArrayList<String> result){

        float VP = 0; //Vrai Positif
        float FP = 0; //Faux Positif
        float VN = 0; //Vrai Négatif
        float FN = 0; //Faux Négatif


        for(int i = 0;i<result.size();i++){
            String[] stringResult = result.get(i).split(";");
            String[] stringFileResult = listeCarte.get(i).split(";");
            //Si l'algo dit qu'il y a une carte et qu'il y en a vraiment une
            //On ajoute 1 dans la liste des vrais Positif
            if(!stringResult[0].equals("false") && !stringFileResult[0].equals("false"))
                VP +=1;
            //Si l'algo dit qu'il y a une carte alors qu'il n'y en a pas
            //On ajoute 1 dans les faux positif
            if(!stringResult[0].equals("false") && stringFileResult[0].equals("false"))
                FP+=1;
            //Si l'algo dit qu'il n'y a pas de carte et qu'il n'y en a pas
            //On ajoute 1 dans les vrai negatif
            if(stringResult[0].equals("false") && stringFileResult[0].equals("false"))
                VN +=1;
            //Si l'algo dit qu'il n'y pas de carte alors qu'il y en a une
            //On ajoute 1 dans les faux negatif
            if(stringResult[0].equals("false") && !stringFileResult[0].equals("false"))
                FN+=1;

        }
        //Précision
        // VP  /  (VP + FP)
        précision = VP/(VP+FP) * 100;
        exactitude = (VP + VN)/(VP+VN+FN+FP) *100;

    }





    public void moyenneHarmonique(){
        if(précision != 0 && rappel != 0)
            harmonique = (2*précision*rappel)/(précision+rappel);


    }
}
