package application;

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
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.Bloom;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.QuadCurve;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Callback;
import javafx.util.Duration;

public class Arrete extends Group{

	public static final int  SHAPE_ARROW = 1;
	public static final int SHAPE_NORMAL = 0;
	
	private Point2D debut;	
	private Point2D fin;		
	private Point2D controle;	
	private Point2D mid;
	
	public Circle cercleControle;	
	public QuadCurve curve;
	private Color couleur;
	
	private int poids;	
	Label labelPoids;
	
	private BooleanProperty selected;
	
	Sommet somDebut;
	Sommet somFin;
	
	DoubleProperty debPropertyX;
	DoubleProperty debPropertyY;
	
	DoubleProperty finPropertyX;
	DoubleProperty finPropertyY;
	
	DoubleProperty controlPropertyX;
	DoubleProperty controlPropertyY;
	
	DoubleProperty cercleControlpropertyX;
	DoubleProperty cercleControlpropertyY; 
	
	DoubleProperty poidPropertyX;
	DoubleProperty poidPropertyY;
	
	private long time1 =0;
	private long time2 = 0;
	
	public Arrete(Sommet s1, Sommet s2, int poids, int type){
		this.poids = poids;
		this.selected = new SimpleBooleanProperty(false);
		this.labelPoids = new Label(String.valueOf(poids));
		labelPoids.setTextFill(Color.BLACK);
		labelPoids.setFont(Font.font(null, FontWeight.NORMAL, 15));
		this.couleur = Color.DARKGREEN;
		
		//Les deux sommets de l'arrete
		this.somDebut = s1;
		this.somFin = s2;
		
		//Point de debut de l'arrete:
		this.debut = new Point2D(somDebut.centerX.getValue(), 
								somDebut.centerY.getValue());
		//Point de fin de l'arrete:
		this.fin = new Point2D(somFin.centerX.getValue(),
								somFin.centerY.getValue());
		
		//Initialiser les 'property' de Debut et Fin: 
		debPropertyX = new SimpleDoubleProperty(this.debut.getX());
		debPropertyY = new SimpleDoubleProperty(this.debut.getY());
		finPropertyX = new SimpleDoubleProperty(this.fin.getX());
		finPropertyY = new SimpleDoubleProperty(this.fin.getY());
		
		//Binder les proprietés innitialisé, avec les deux sommets de l'arret: Deplacer sommet = deplacer debut/fin
		debPropertyX.bind(somDebut.centerX);
		debPropertyY.bind(somDebut.centerY);
		finPropertyX.bind(somFin.centerX);
		finPropertyY.bind(somFin.centerY);

		//Création du point de control de Curve: initialisé à la position du millieu:
		this.controle = new Point2D((debPropertyX.getValue()+finPropertyX.getValue()) / 2,
				(debPropertyY.getValue()+finPropertyY.getValue())/2);
		
		//Créer les propriétés du point de controle, pour le faire deplacer avec les différentes changement possibles:
		controlPropertyX = new SimpleDoubleProperty(this.controle.getX());
		controlPropertyY = new SimpleDoubleProperty(this.controle.getY());
		
		//Ajouter un changeListener au point de debut/fin, pour déplacer le point de controle avec eux:
		// -----> On ne peut pas utiliser un bind avec les deux points à la fois --> il faut faire un listener:
		ChangeListener <Number> updateControl = new ChangeListener<Number>(){
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				// TODO Auto-generated method stub
				//Mise a jout de la position du poids:
				updateLabelPosition();
			}		
		};
		//Ajouter le listener aux points de debut/fin:
		debPropertyX.addListener(updateControl);
		debPropertyY.addListener(updateControl);
		finPropertyX.addListener(updateControl);
		finPropertyY.addListener(updateControl);
		
		
		//varibales temporaires, utile pour le calcule du centre du cercle qui va etre déssiné sur la ligne
		double temp1, temp2;
		temp1 = (debPropertyX.getValue() + controlPropertyX.getValue()) /2;
		temp2 = (finPropertyX.getValue() + controlPropertyX.getValue()) /2;
		cercleControlpropertyX = new SimpleDoubleProperty((temp1+temp2) /2);
		temp1 = (debPropertyY.getValue() + controlPropertyY.getValue()) /2;
		temp2 = (finPropertyY.getValue() + controlPropertyY.getValue()) /2;
		cercleControlpropertyY = new SimpleDoubleProperty((temp1+temp2) /2);
			
		//Dessiner l'arrete: 
		dessinerArrete();
		
		//Listener sur Selected: 
		addSelectedListener();
		
		//petite animation lors de la création:
		FadeTransition ft1 = new FadeTransition(Duration.millis(250), this);
		ft1.fromValueProperty().setValue(0);
		ft1.toValueProperty().setValue(1);
		ft1.play();
	}

//*******************************************************************************************************
	public void updateLabelPosition(){
		mid = new Point2D((debPropertyX.getValue() + finPropertyX.getValue()) / 2, 
				(debPropertyY.getValue() + finPropertyY.getValue()) / 2);
		labelPoids.setLayoutX(((cercleControle.getCenterX() + mid.getX())/ 2)+5);
		labelPoids.setLayoutY(((cercleControle.getCenterY() + mid.getY()) / 2)+5);
	}
	
	public void dessinerArrete(){
		curve = new QuadCurve();
		curve.setStroke(this.couleur);
		curve.setStrokeWidth(4);
		curve.setFill(null);
		
		//Création du cercle qui va nous servire comme point de control:
		cercleControle = new Circle(cercleControlpropertyX.getValue(), cercleControlpropertyY.getValue(), 5);
		cercleControle.setFill(Color.YELLOW);
		
		//Création du handler pout le cercle: 
		EventHandler<MouseEvent> handler = new EventHandler<MouseEvent>() {
			@Override
	        public void handle(MouseEvent event) {
				//Deplacer cercle
				cercleControle.setCenterX(event.getX());
				cercleControle.setCenterY(event.getY());
				//Mise a jour points de poid:
				updateLabelPosition();
	        }
	    };
	    cercleControle.setOnMousePressed(handler);
	    cercleControle.setOnMouseDragged(handler);
	        
		//initialisé la Curve:
		curve.setStartX(debPropertyX.getValue());
		curve.setStartY(debPropertyY.getValue());	
		curve.setEndX(finPropertyX.getValue());
		curve.setEndY(finPropertyY.getValue());
		
		//Binder les deux extémités de la curve: 
		curve.startXProperty().bind(debPropertyX);
		curve.startYProperty().bind(debPropertyY);
		
		curve.endXProperty().bind(finPropertyX);
		curve.endYProperty().bind(finPropertyY);
		
		//BInder le point de controle de la curve avec le cercle designant le point de controle:
		controlPropertyX.bind(cercleControle.centerXProperty());
		controlPropertyY.bind(cercleControle.centerYProperty());
		
		curve.controlXProperty().bind(controlPropertyX);
		curve.controlYProperty().bind(controlPropertyY);
		
		labelPoids.setLayoutX(controlPropertyX.getValue());
		labelPoids.setLayoutY(controlPropertyY.getValue());
		
		this.getChildren().addAll(curve, labelPoids);
		
		/* Click listener pour:
		 * 		- Selectionner l'arrete
				- ouvrire la boite de dialogue et modifier les propriétés de l'arrete
		*/
		this.setOnMouseClicked(new EventHandler<MouseEvent>(){
			@Override
			public void handle(MouseEvent event) {
				// TODO Auto-generated method stub
				Main.graphe.setAllArreteSelected(false);
				Main.graphe.setAllSelected(false);	
				Main.selectedNodes.supprimerTout();

				selected.setValue(true);
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
					time1 = 0;
				}
				event.consume();
			}
		});
	}
//*******************************************************************************************************
	
	// Afficher l'arrete quand celle ci est séléctionnée (Méthode appelée dans le constructeur)
	public void addSelectedListener(){
		this.selected.addListener(new ChangeListener<Boolean>(){
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				// TODO Auto-generated method stub
				if(selected.getValue() == true){
					curve.setStrokeWidth(4);
					curve.setStroke(Color.RED);
					labelPoids.setTextFill(Color.BLUE);
					labelPoids.setFont(Font.font(null, FontWeight.BOLD, 20));
					getChildren().remove(cercleControle);
					getChildren().add(cercleControle);
					Main.boutonSupprimerSommet.setDisable(false);
				}else{
					curve.setStrokeWidth(4);
					curve.setStroke(couleur);
					labelPoids.setTextFill(Color.BLACK);
					labelPoids.setFont(Font.font(null, FontWeight.NORMAL, 15));
					getChildren().remove(cercleControle);
				}
			}			
		});
	}
	
//***** Methode pour lancer les proprietés d'un sommet:		
	public void openDialogProperties(){
			Dialog<Sommet> dialog = new Dialog<Sommet>();
			dialog.setTitle("Proprietés");
			dialog.setHeaderText("Proprietés du sommet selectionné, modifier puis appuyez sur Ok");
			dialog.setResizable(false);
			
			Label label2 = new Label("Poids de l'arrete:");
			TextField field2 = new TextField(String.valueOf(this.poids));
			Label label3 = new Label("Couleur: ");
			ColorPicker picker = new ColorPicker(this.couleur);
			
			GridPane grid = new GridPane();
			grid.add(label2, 1, 1);
			grid.add(field2, 2, 1);
			grid.add(label3, 1, 2);
			grid.add(picker, 2, 2);
			
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
						selected.setValue(false);
						poids = Integer.valueOf(field2.getText());
						labelPoids.setText(String.valueOf(poids));
						couleur = picker.getValue();
						curve.setStroke(couleur);
					}
					return null;
				}
			});
			dialog.showAndWait();
		}
//***************************************************		
	public Sommet getSommetDebut(){
		return somDebut;
	}
	public Sommet getSommetFin(){
		return somFin;
	}
	
	public boolean getSelected(){
		return this.selected.getValue();
	}
	public void setSelected(boolean s){
		this.selected.setValue(s);
	}
	public int getPoid(){
		return this.poids;
	}
	public String toString(){
		return ""+somDebut.labelNom.getText()+" ---> "+somFin.labelNom.getText()+" Poids: "+this.poids;
	}
	public Color getCouleur(){
		return this.couleur;
	}
	public void setCouleur(Color c){
		this.couleur = c;
		curve.setFill(couleur);
	}
}
