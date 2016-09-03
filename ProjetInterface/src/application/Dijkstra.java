package application;

import java.util.ArrayList;

import javafx.scene.paint.Color;

public class Dijkstra{
	
	public ArrayList<Sommet> resultat;
	public ArrayList<Integer> distance;
	public ArrayList<Sommet> rencontre;
	public ArrayList<Etape> listEtape;
	
	Sommet source;
	Sommet destination;
	
	Etape etapeCourante;
	private boolean etapeParEtape;
	
	public Dijkstra(){
		resultat = new ArrayList<Sommet>();
		distance = new ArrayList<Integer>();
		rencontre = new ArrayList<Sommet>();
		listEtape = new ArrayList<Etape>();
		source = null;
		destination = null;
		etapeCourante = null;
		this.etapeParEtape = false;
	}
	
	public void setEtapeParEtape(boolean bool){
		this.etapeParEtape = bool;
	}
	public boolean getEtapeParEtape(){
		return this.etapeParEtape;
	}
	public void setDebut(Sommet deb){
		this.source = deb;
	}
	public Sommet getDebut(){
		return this.source;
	}
	public void setFin(Sommet fin){
		this.destination = fin;
	}
	public Sommet getFin(){
		return this.destination;
	}
	public void appliquerAlgo(){
		if(this.source == null || this.destination == null){
			//Aucun sommet n'est selectionné!
			return;
		}
		System.out.println("Source = "+source.labelNom.getText());
		source.setDistance(0);
		source.setPred(null);
		this.rencontre.clear();
		this.rencontre.add(source);
		for(Sommet s: Main.graphe.listeSommets){
			s.setDistance(Integer.MAX_VALUE-1);
			s.setPred(null);
			s.done = false;
		}
		
		this.rencontre.get(0).setDistance(0);
		Sommet som;
		System.out.println("Source = "+source.labelNom.getText()+" Destination = "+destination.labelNom.getText());
		
		this.listEtape.clear();
		Etape etape = new Etape(source, source, 0);
		this.listEtape.add(etape);
		this.etapeCourante = etape;
		
		while(!rencontre.isEmpty()){
			som = getMin();
			
			if(som == null){
				System.out.println("Som null");
				return;
			}
			System.out.println("Sommet courant = "+som.labelNom.getText()+" Distance: "+som.getDistance());
			if(som == destination){
				this.afficherResultat(destination);
				afficherEtapes();
				this.etapeCourante = listEtape.get(1);
				return;//FIN. CHEMIN TROUVE:
			}
			for(Sommet s:som.listeSomVoisin){
				System.out.println(""+som.labelNom.getText()+" a comme voisin: "+s.labelNom.getText());
				if(s.done == false)
					updateDistance(som, s);
			}
			som.done = true;
		}
	}
	public void afficherChemin(){
		Sommet courant = this.destination;
		for(Sommet s:Main.graphe.listeSommets){
			s.forme.setFill(s.getCouleur());
		}
		for(Arrete a: Main.graphe.listeArretes){
			a.curve.setStroke(a.getCouleur());
		}
		while(courant.getPred() != null){
			courant.forme.setFill(Color.ORANGE);
			Main.graphe.getArrete(courant, courant.getPred()).curve.setStroke(Color.BLACK);
			courant = courant.getPred();
		}
	}
	public Etape getEtapeCourante(){
		return this.etapeCourante;
	}
	public Etape getEtapeSuivante(){
		this.etapeCourante = etapeCourante.getSuiv();
		return this.etapeCourante;
	}
	public Etape getEtapePred(){
		this.etapeCourante = etapeCourante.getPred();
		return this.etapeCourante;
	}
	public void afficherEtapes(){
		int i=0;
		System.out.println("La liste des étapes: ");
		for(Etape e:this.listEtape){
			System.out.print("Etape "+i+": "+e.toString()+"\n");
			i ++;
		}
	}
	public void reinitialisation(){
		this.source = null;
		this.destination = null;
	}
	
	public Sommet getMin(){
		int min = Integer.MAX_VALUE;
		Sommet sMin = null;
		for(Sommet s: this.rencontre){
			System.out.println("Sommet ----> "+s.labelNom.getText()+" Distance: "+s.getDistance());
			System.out.println("Min Courant: "+min);
			if(s.getDistance() < min && s.done != true){
				min = s.getDistance();
				sMin = s;
			}
		}
		this.rencontre.remove(sMin);
		return sMin;
	}

	public void updateDistance(Sommet s1, Sommet s2){
		int poid = getPoid(s1, s2);
		if(poid == -1){
			//Arrete n'existe pas, ne rien faire.
			return;
		}
		int newDist = s1.getDistance() + getPoid(s1, s2);
		System.out.println("NewDist: "+newDist+" s2.distance "+s2.getDistance()+" getPoid "+getPoid(s1, s2));
		if(s2.getDistance() > newDist && s2.done != true){
			s2.setDistance(newDist);
			s2.setPred(s1);

			System.out.println(""+s2.labelNom.getText()+" new Dist"+ s2.getDistance()+" pred: "+s2.getPred().labelNom.getText());
			this.rencontre.add(s2);
		}			
		Etape etape = new Etape(s1, s2, s2.getDistance(), listEtape.get(listEtape.size()-1));
		this.listEtape.get(listEtape.size()-1).setSuiv(etape);
		this.listEtape.add(etape);
		
	}
	public int getPoid(Sommet s1, Sommet s2){
		System.out.println("Trouver arrete entre "+s1.labelNom.getText()+" et "+s2.labelNom.getText());
		
		for(Arrete a: s1.listeArreteVoisin){
			//System.out.println("Arrete: "+a.toString()+" S1: "+s1.labelNom.getText()+" s2: "+s2.labelNom.getText());
			if((a.somDebut == s1 && a.somFin == s2) || (a.somDebut == s2 && a.somFin == s1)){
				System.out.println("Trouvé ! poids = "+a.getPoid());
				return a.getPoid();
			}
		}
		return -1;
	}
	
	public void afficherResultat(Sommet dest){
		Sommet courant  = dest;
		while(courant != null){
			System.out.println("- "+courant.labelNom.getText());;
			courant = courant.getPred();
		}
	}
}
