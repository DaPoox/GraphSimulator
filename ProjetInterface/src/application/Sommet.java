package application;

import java.util.ArrayList;

import javafx.animation.FadeTransition;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Callback;
import javafx.util.Duration;

public class Sommet extends Group {
	/* Constantes designants la forme du sommet */
	public static final int  SHAPE_CERCLE = 1;
	public static final int SHAPE_RECTANGLE = 0;
	
	private final int TAILLE = 10;
	
	private final int FORME_X = 0;
	private final int FORME_Y = 0;
	
	private final int NOM_X = 10;
	private final int NOM_Y = -20;
	
	private final int DISTANCE_X = 0;
	private final int DISTANCE_Y = 10;
	
	private final int POIDS_X = 0;
	private final int POIDS_Y = 7;
	
	//Liste des attributs de la classe:
	public Point2D position;
	private String nom;
	private int poids;
	private ArrayList<Sommet> listeVoisins;
	private BooleanProperty isSelected;
	private Color couleur;
	
	private boolean dragged;

	public Shape forme;
	public Label labelNom;
	public Label labelPoids;
	public TextField field;
	
	private long time1 =0;
	private long time2 = 0;
	
	public DoubleProperty centerX;
	public DoubleProperty centerY;
	
	//Liste des arretes: 
	ArrayList<Sommet> listeSomVoisin;
	ArrayList <Arrete> listeArreteVoisin;
	
	//Valeurs utilisées pour les algo: 
	private int distance = Integer.MAX_VALUE;
	private Label labelDistance;
	public boolean done = false;
	private Sommet pred = null;
	
	//Constructeur:
	public Sommet(Point2D position, String nom, int cstShape, int poids, Color couleur){

		super();
		
		this.dragged = false;
		this.position = new Point2D(position.getX(), position.getY());
		this.nom = nom;
		this.poids = poids;
		this.couleur = couleur;
		
		this.setLayoutX(position.getX());
		this.setLayoutY(position.getY());
		
		this.isSelected = new SimpleBooleanProperty(false);
			
		switch(cstShape){
			//Faire en sorte que le centre du carré/cercle soit dans la position du click:
			case SHAPE_CERCLE: this.forme = new Circle(FORME_X, FORME_Y,  TAILLE);
			break;
			case SHAPE_RECTANGLE: this.forme = new Rectangle(FORME_X-TAILLE, FORME_Y-TAILLE, TAILLE*2, TAILLE*2);
			break;
		}
		
		centerX = new SimpleDoubleProperty(position.getX());
		centerY = new SimpleDoubleProperty(position.getY());
		
		this.forme.setFill(couleur);

		this.labelNom = new Label(this.nom);
		this.labelNom.setLayoutX(NOM_X);
		this.labelNom.setLayoutY(NOM_Y);
		labelNom.setTextFill(Color.BLACK);
		labelNom.setFont(Font.font(null, FontWeight.NORMAL, 12));

		this.labelPoids = new Label(String.valueOf(this.poids));
		this.labelPoids.setLayoutX(POIDS_X);
		this.labelPoids.setLayoutY(POIDS_Y);
		labelPoids.setTextFill(Color.BLACK);
		labelPoids.setFont(Font.font(null, FontWeight.NORMAL, 12));
		
		this.getChildren().addAll(this.labelNom, this.forme, this.labelPoids);
		
		//Gérer l'evenement DoubleClick de la souris sur le sommet:
		this.addDoubleClickHandler();
	
		//Déplacer le sommet: MousePressed + Mouse Dragged + Mouse Release:
		this.makeDraggable(this);
		
		//Ajouter un change listener sur isSelected:
		this.isSelectedListener();
		
		//Initialiser la liste des voisins:
		this.listeSomVoisin = new ArrayList<Sommet>();
		this.listeArreteVoisin = new ArrayList<Arrete>();
		
		//petite animation lors de la création:
		FadeTransition ft1 = new FadeTransition(Duration.millis(250), this);
		ft1.fromValueProperty().setValue(0);
		ft1.toValueProperty().setValue(1);
		ft1.play();
	}	
	
	public Color getCouleur(){
		return this.couleur;
	}
	
//****** Ajouter le listener sur isSelecetd: 
	public void isSelectedListener(){
		this.isSelected.addListener(new ChangeListener<Boolean>(){
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				// TODO Auto-generated method stub
				if(isSelected.getValue() == true){
					Main.selectedNodes.supprimerTout();
					forme.setFill(Color.RED);
					labelNom.setTextFill(Color.BLACK);
					labelNom.setFont(Font.font(null, FontWeight.NORMAL, 15));
					labelPoids.setTextFill(Color.BLACK);
					labelPoids.setFont(Font.font(null, FontWeight.NORMAL, 15));
					Main.boutonSupprimerSommet.setDisable(false);
				}else{
					labelNom.setTextFill(Color.BLACK);
					labelNom.setFont(Font.font(null, FontWeight.NORMAL, 12));
					labelPoids.setTextFill(Color.BLACK);
					labelPoids.setFont(Font.font(null, FontWeight.NORMAL, 12));
					forme.setFill(couleur);
				}
			}
		});
	}

//***** Ajouter l'evenement double click sur le sommet (forme):
	public void addDoubleClickHandler(){
		EventHandler<MouseEvent> clickHandler = new EventHandler<MouseEvent>(){
			@Override
			public void handle(MouseEvent event) {
				// TODO Auto-generated method stub
					ArrayList<Sommet> listSommetSelected = Main.graphe.getSelected();
					//1 seul element séléctionné:
					if(!listSommetSelected.isEmpty() && listSommetSelected.size()<2){
						Sommet s = listSommetSelected.get(0);
						if(verifierSommet(s) == true && verifierSommetVoisin(s) == false){
							ajouterArrete(s);
							Main.graphe.setAllSelected(false);
						}
					}else{
							Main.graphe.setAllSelected(false);
							isSelected.setValue(true);
					}
					Main.graphe.setAllArreteSelected(false);

				//Vérifier si c'est un double click:
				if(time1 == 0){
					//On a clicker la première fois
					time1 = System.currentTimeMillis();
				}else{
					time2 = System.currentTimeMillis();
					long dif = time2 - time1;
					if(dif<600){//Double click!
						//Ouvire dialog p10roprietés
						openDialogProperties();
					}
					isSelected.setValue(false);
					time1 = 0;
				}
				event.consume();
			}
		};
		this.forme.addEventFilter(MouseEvent.MOUSE_CLICKED, clickHandler);
		System.out.println(" --------Debut------------------------------ ");
    	System.out.println("\t- LayoutX:"+this.getLayoutX()+" LayoutY:"+this.getLayoutY());
    	System.out.println("\t- PositionX:"+this.position.getX()+" positionY: "+this.position.getY());
    	System.out.println("\t CenterX: "+this.centerX.doubleValue()+" centerY: "+this.centerY.doubleValue());
    	System.out.println(" ------------------------------------------------ ");
	}
	
//***** Deplacement du sommet: 
	 private void makeDraggable(final Node node) {
	        class T {
	            double initialTranslateX, initialTranslateY, anchorX, anchorY;
	        }

	        final T t = new T();

	        EventHandler<MouseEvent> pressHandler = new EventHandler<MouseEvent>() {
	            @Override
	            public void handle(final MouseEvent event) {
	            	System.out.println("Handler in sommet !! ");
	                /* trouver et memoriser la translation initiale de Node */
	                t.initialTranslateX = node.getTranslateX();
	                t.initialTranslateY = node.getTranslateY();
	                /* trouver la position initial de la souris dans les coordonnees 
	                 * du parent de Node
	                 * et les memoriser dans t.anchor
	                 */
	                Point2D point = node.localToParent(event.getX(), event.getY());
	                t.anchorX = point.getX();
	                t.anchorY = point.getY();
	               // Main.graphe.setAllSelected(false);
	               // isSelected.setValue(true);
	                event.consume();
	            }
	        };

	        EventHandler<MouseEvent> dragHandler = new EventHandler<MouseEvent>() {
	            @Override
	            public void handle(final MouseEvent event) {
	                /* trouver la position de la souris dans les coordonnees du parent
	                 * effectuer le deplacement de Node pour suivre les mouvement 
	                 * de la souris
	                 */
	                Point2D point = node.localToParent(event.getX(), event.getY());
	                node.setTranslateX(t.initialTranslateX - t.anchorX + point.getX());
	                node.setTranslateY(t.initialTranslateY - t.anchorY + point.getY());
	                position = point;
	                updatePosition(position);
	                dragged = true;
	                event.consume();
	            }
	        };
	        
	       EventHandler<MouseEvent> releaseHandler = new EventHandler<MouseEvent>(){
				@Override
				public void handle(MouseEvent event) {
					// TODO Auto-generated method stub
					Point2D point = node.localToParent(event.getX(), event.getY());
					position = point;
					event.consume();
				}	        	
	        };	        
	        forme.addEventFilter(MouseEvent.MOUSE_PRESSED, pressHandler);
	        forme.addEventFilter(MouseEvent.MOUSE_DRAGGED, dragHandler);
	        forme.addEventFilter(MouseEvent.MOUSE_RELEASED, releaseHandler);
	    }
	 
	 
	 public void updatePosition(Point2D pos){
		 this.centerX.setValue(pos.getX());
		 this.centerY.setValue(pos.getY());
	 }
	 
//***** Methode pour lancer les proprietés d'un sommet:		
	public void openDialogProperties(){
		Dialog<Sommet> dialog = new Dialog<Sommet>();
		dialog.setTitle("Proprietés");
		dialog.setHeaderText("Proprietés du sommet selectionné, modifier puis appuyez sur Ok");
		dialog.setResizable(false);
		
		Label label1 = new Label("Nom du sommet:");
		TextField field1 = new TextField(this.nom);
		Label label2 = new Label("Poids du sommet:");
		TextField field2 = new TextField(String.valueOf(this.poids));
		Label label3 = new Label("Couleur: ");
		ColorPicker picker = new ColorPicker(this.couleur);
		
		GridPane grid = new GridPane();
		grid.add(label1, 1, 1);
		grid.add(field1, 2, 1);
		grid.add(label2, 1, 2);
		grid.add(field2, 2, 2);
		grid.add(label3, 1, 3);
		grid.add(picker, 2, 3);
		
		dialog.getDialogPane().setContent(grid);
		ButtonType buttonTypeOk = new ButtonType("Valider", ButtonData.OK_DONE);
		ButtonType buttonTypeAnnuler = new ButtonType("Annuler", ButtonData.CANCEL_CLOSE);
		
		dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);
		dialog.getDialogPane().getButtonTypes().add(buttonTypeAnnuler);
		
		dialog.setResultConverter(new Callback<ButtonType, Sommet>() {

			@Override
			public Sommet call(ButtonType b) {
				// TODO Auto-generated method stub
				if(b == buttonTypeOk){
					nom = field1.getText();
					labelNom.setText(nom);
					poids = Integer.valueOf(field2.getText());
					labelPoids.setText(String.valueOf(poids));
					couleur = picker.getValue();
					forme.setFill(couleur);
				}
				return null;
			}

		});

		dialog.showAndWait();
		
	}
	//Vérifier si le sommet S n'est pas "THIS", et non null: (Méthode utile pour éviter d'ajouter des arretes avec soit meme...)
	public boolean verifierSommet(Sommet s){
			if(s != null && s != this){
				return true;
			}
			return false;
	}
	
	//Méthodes utiles:
	public void setSelected(boolean selected){
		this.isSelected.set(selected);
	}
	
	//Ajouter arrete entre nous et sommet s:
	public void ajouterArrete(Sommet s){
		//Création de la nouvelle arrete:
		Arrete a = new Arrete(s, this, 10, 0);
		Main.pane.getChildren().add(a);
		
		//Mise à jour de la liste des voisins (des deux sommets extems.):
		this.listeSomVoisin.add(s);
		s.listeSomVoisin.add(this);
		
		//Mise a jour de la liste des arretes voisins (des deux sommets extrems.): 
		this.listeArreteVoisin.add(a);
		s.listeArreteVoisin.add(a);
		
		//Mise à jours du pane principale: 
			/*
			 * Supprimer les deux sommet, ajouter l'arrete, ajouter les deux sommets: (pour éviter de déssiner l'arrete en dessus des sommets)
			 */
		Main.pane.getChildren().remove(s);
		Main.pane.getChildren().remove(this);
		Main.graphe.ajouterArrete(a);
		Main.pane.getChildren().add(this);
		Main.pane.getChildren().add(s);
	}
	//Vérifier si le sommet s est notre voisins: 
	public boolean verifierSommetVoisin(Sommet s){
		for(Sommet sommet : this.listeSomVoisin){
			if(sommet == s){
				return true;
			}
		}
		return false;
	}
	
	public boolean getSelected(){
		return isSelected.getValue();
	}

	public void setDistance(int distance){
		this.distance = distance;
	}
	public void setPred(Sommet s){
		this.pred = s;
	}
	public int getDistance(){
		return this.distance;
	}
	public Sommet getPred(){
		return this.pred;
	}
	public int getPoids(){
		return this.poids;
	}
}
