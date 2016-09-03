package application;

public class Etape {
	private Sommet courant;
	private Sommet modifie;
	private int nouveauPoids;
	private Etape suiv;
	private Etape pred;
	
	public Etape(Sommet c, Sommet m, int nvpoid, Etape pred){
		this.courant = c;
		this.modifie = m;
		this.nouveauPoids = nvpoid;
		this.pred = pred;
		this.suiv = null;
	}
	public Etape(Sommet c, Sommet m, int nvpoid){
		this.courant = c;
		this.modifie = m;
		this.nouveauPoids = nvpoid;
		this.suiv = null;
		this.pred = null;
	}
	public String toString(){
		return "Courant: "+this.courant.labelNom.getText()+" Modifie: "+modifie.labelNom.getText()+" new Poids: "+this.nouveauPoids;
		
	}
	public Sommet getCourant() {
		return courant;
	}
	public void setCourant(Sommet courant) {
		this.courant = courant;
	}
	public Sommet getModifie() {
		return modifie;
	}
	public void setModifie(Sommet modifie) {
		this.modifie = modifie;
	}
	public int getNouveauPoids() {
		return nouveauPoids;
	}
	public void setNouveauPoids(int nouveauPoids) {
		this.nouveauPoids = nouveauPoids;
	}
	public Etape getSuiv() {
		return suiv;
	}
	public void setSuiv(Etape suiv) {
		this.suiv = suiv;
	}
	public Etape getPred() {
		return pred;
	}
	public void setPred(Etape pred) {
		this.pred = pred;
	}
	
}
