/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.labo4_decorateur;

import java.util.Random;
import java.util.Date;
import java.text.SimpleDateFormat;
/**
 *
 * @author Marc-Andre Couturier
 */
public class Labo4_Decorateur 
{
    enum Algo
    {
        LEIBNIZ,
        MONTECARLO
    }
    
    static class Repeteur // je voulais juste une struc
    {
        boolean estActif_;
        int nbRepetition_;
        
        public Repeteur(boolean estActif, int nbRepetition )
        {
            estActif_ = estActif;
            nbRepetition_ = nbRepetition;
        }           
    }
    
    

    public static void main(String[] args) 
    {
        ISortie sortie= new SortieConsole(); 
        ICommande leibniz = assembleurDeCommande(Algo.LEIBNIZ, 2_100_000_000, true, true,true,new Repeteur(true,10));
        ICommande monteCarlo = assembleurDeCommande(Algo.MONTECARLO, 210_000_000, true, true,true,new Repeteur(true,10)); // iterateur /10  sinon ca prenait 35 secondes
        leibniz.executer(sortie);
        monteCarlo.executer(sortie);   
    }
    

    
    public static ICommande assembleurDeCommande(Algo algo, int iterateur, boolean compteur, boolean chrono, boolean date, Repeteur repeteur )
    {
        ICommande retour;
        switch(algo)
        {
            case LEIBNIZ:
                retour = new Leibniz(iterateur);
                break;
            default:
                retour = new MonteCarlo(iterateur);
        }
        if(compteur)
        {
            retour = new DecorateurCompteur(retour);
        }
        if(chrono)
        {
            retour = new DecorateurChronometreur(retour);
        }
        if(date)
        {
            retour = new DecorateurDate(retour);
        }
        if(repeteur.estActif_)
        {
            retour = new DecorateurRepeteur(retour, repeteur.nbRepetition_);
        }
        return  retour;       
    }
}


interface ISortie
{
    void ecrire( String txt );
}

class  SortieConsole implements ISortie
{
   @Override
   public void ecrire(String txt)
    {
        System.out.println(txt);
    }
}

interface ICommande
{ 
    void executer(ISortie sortie);
}

class Leibniz implements ICommande
{
    int nombreIteration_;
    public Leibniz(int n)
    {
        nombreIteration_ = n;
    }
            
    @Override
    public void executer(ISortie sortie)
    {
        double approximationPi = 0.0;
        
        for (int i = 0; i < nombreIteration_; i++) 
        {
            double terme = 1.0 / (2 * i + 1);
            if (i % 2 == 0) {
                approximationPi += terme;
            } else {
                approximationPi -= terme;

            }
        }

        approximationPi *= 4.0;
        sortie.ecrire("\nAlgo:\tLeibniz\nPi\t" + String.valueOf(approximationPi));
    }
}



class MonteCarlo implements ICommande
{
    int nombrePoint_;
    public MonteCarlo (int n)
    {
       nombrePoint_ = n; 
    }
    
    @Override
    public void executer(ISortie sortie)
    {
        Random rand = new Random();
        int nombrePointsDansCercle = 0;

        for (int i = 0; i < nombrePoint_; i++) {
            double x = rand.nextDouble();
            double y = rand.nextDouble(); 
            double distance = x * x + y * y;


            if (distance <= 1) {
                nombrePointsDansCercle++;
            }
        }
        
        sortie.ecrire(String.valueOf("\nAlgo:\tMonte Carlo\nPi:\t" +(double) nombrePointsDansCercle / nombrePoint_ * 4.0));
    }
}

class DecorateurCommande implements ICommande
{
    ICommande commande_;
    
    public DecorateurCommande(ICommande commande)
    {
        commande_ = commande;
    }
    
    @Override
    public void executer(ISortie sortie)
    {
        commande_.executer(sortie);
    }
}

class DecorateurChronometreur extends DecorateurCommande
{
    public DecorateurChronometreur (ICommande commande)
    {
        super(commande);
    }
    
    @Override
    public void executer(ISortie sortie)
    {
        long tempsDebut = System.nanoTime();
        super.executer(sortie);
        long tempsFin = System.nanoTime();
        sortie.ecrire("temps:\t" + String.valueOf((tempsFin - tempsDebut)/ 1_000_000_000.0) + 's');
    }
}

class DecorateurDate extends DecorateurCommande
{
    public DecorateurDate (ICommande commande)
    {
        super(commande);
    }
    
    @Override
    public void executer(ISortie sortie)
    {
        SimpleDateFormat gabarit = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        super.executer(sortie);
        sortie.ecrire("Date:\t" + gabarit.format(new Date()));
    }
}

class DecorateurRepeteur extends DecorateurCommande
{
    int nbRepetition_;
    
    public DecorateurRepeteur(ICommande commande, int n)
    {
        super(commande);
        nbRepetition_ = n;
    }
    
    @Override
    public void executer(ISortie sortie)
    {
        for(int i = 0; i < nbRepetition_; i++)
        {
            super.executer(sortie);
        }
    }
}

class DecorateurCompteur extends DecorateurCommande
{
    int nbRepetition_ = 0;
    
    public DecorateurCompteur(ICommande commande)
    {
        super(commande);
    }
    
    @Override
    public void executer(ISortie sortie)
    {
        super.executer(sortie);
        sortie.ecrire("test:\t" + String.valueOf(nbRepetition_));
        nbRepetition_++;
    }
}

