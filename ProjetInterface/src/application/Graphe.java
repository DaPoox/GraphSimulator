package application;

import java.util.ArrayList;

import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class Graphe extends Pane{
	
	//Liste des sommet: 
	ArrayList<Sommet> listeSommets;
	
	//liste des arretes: 
	ArrayList<Arrete> listeArretes;
	
	public Graphe(){
		this.listeSommets = new ArrayList<Sommet>();
		this.listeArretes = new ArrayList<Arrete>();	
	}
	
//************** Arretes: ***********************	
	public int getCpt(){
		return this.listeSommets.size();
	}
	public int getCptArrete(){
		return this.listeArretes.size();
	}
	//Avoir une arrete i: 
	public Arrete getArrete(int i){
		if(i>listeArretes.size()){
			System.out.println("Erreur i<taille");
			return null;
		}else
			return listeArretes.get(i);
	}
	
	//Ajouter une arrete à la liste:
	public void ajouterArrete(Arrete a){
		this.listeArretes.add(a);
	}
	
	//Supprimer une arrete du graphhe:
	public void deleteArrete(Arrete a){
		//Mise à jours des voisins des sommets: 
		a.getSommetDebut().listeSomVoisin.remove(a.getSommetFin());
		a.getSommetFin().listeSomVoisin.remove(a.getSommetDebut());
		//Supprimer l'arrete du graphe:
		this.listeSommets.remove(a);
	}
	
	public Arrete getArrete(Sommet s1, Sommet s2){
		for(Arrete a: s1.listeArreteVoisin){
			//System.out.println("Arrete: "+a.toString()+" S1: "+s1.labelNom.getText()+" s2: "+s2.labelNom.getText());
			if((a.somDebut == s1 && a.somFin == s2) || (a.somDebut == s2 && a.somFin == s1)){
				System.out.println("Trouvé !");
				return a;
			}
		}
		return null;
	}

	//Méthode utiles: 
	public void setAllArreteSelected(boolean selected){
		for(Arrete a:this.listeArretes){
				a.setSelected(selected);
		}
	}
		
	//Récuperer le sommet selectionné: 
	public Arrete getArreteSelected(){
			for(Arrete a:this.listeArretes){
				if(a.getSelected()) return a;
			}
			return null;
		}
		
	//Taille du grpahe: nbSommet:
	public int getArreteTaille(){
		return this.listeSommets.size();
	}
	
//******************** SOMMETS *******************
	
//Avoir un sommet i:	
	public Sommet getSommet(int i){
		if(i>listeSommets.size()){
			System.out.println("Erreur i<taille");
			return null;
		}else
			return listeSommets.get(i);
	}
	
//Ajouter un sommet dans la liste:	
	public void ajouterSommet(Sommet sommet){
		this.listeSommets.add(sommet);
	}
	public void ajouterSommet(Sommet sommet, int i){
		this.listeSommets.add(i, sommet);
	}
	
//Supprimer un sommet du graphhe:
	public void deleteSommet(Sommet s){
		this.listeSommets.remove(s);
	}
//Méthode utiles: 
	public void setAllSelected(boolean selected){
		for(Sommet s:this.listeSommets){
			s.setSelected(selected);
		}
	}
	
//Récuperer le sommet selectionné: 
	public ArrayList<Sommet> getSelected(){
		ArrayList<Sommet> listSelected = new ArrayList<Sommet>();
		for(Sommet s:this.listeSommets){
			if(s.getSelected()) listSelected.add(s);
		}
		return listSelected;
	}
//Taille du grpahe: nbSommet:
	public int getTaille(){
		return this.listeSommets.size();
	}
}
