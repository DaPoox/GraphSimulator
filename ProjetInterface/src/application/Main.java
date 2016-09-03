package application;
	
import java.util.ArrayList;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.ToolBar;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Callback;

public class Main extends Application {

	//La fenetre principale: 
	Scene scene;
	BorderPane root;
	static Pane pane;//Espace de dessin
	
	//Outils de dessins: 
	ColorPicker colorPicker;
	
	ToggleButton boutonCercle;
	ToggleButton boutonRect;
	ToggleButton checkSelectionner;
	
	static Button boutonSupprimerSommet;//On en a besoin pour activer/desactiver dans la classe sommet
	
	//Attributs de dessin:
	Color couleur;
	
	//Sommet selectionné: 
	Sommet selectedSom;
	
	//Le graphe:
	static Graphe graphe;
	
	//Outils de selection: 
	static Selection selectedNodes;
	
	Button boutonLancer;

	//Algorithme Dijkstra;
	Dijkstra dijkstra;
	
	Button boutonSuivant;
	
//************** Start: ***************************
	@Override
	public void start(Stage primaryStage) {
		try {
			dijkstra = new Dijkstra();
			
			graphe = new Graphe();
			root = new BorderPane();
			scene = new Scene(root,800,600);
			
			//Ajouter la bar de menus: 
			ajouterMenu();
			//Ajouter la barre d'outils:
			ajouterBarreOutilsDessin();
			ajouterBarreOutilsAlgorithme();
			
			//Initialiser Pane, avec la création du rectangle (clipping area):
			pane = new Pane();
			selectedNodes = new Selection();

			//**** TO DELETE **** background de pane
			pane.setBackground(new Background(new BackgroundFill(Color.GRAY, CornerRadii.EMPTY, Insets.EMPTY)));
			//********			
			Rectangle clip = new Rectangle(pane.getHeight(), pane.getWidth());
		    pane.setClip(clip);
		    //Ajustter la taille de "clip" avec la taille du pane:
		    clip.heightProperty().bind(pane.heightProperty());
		    clip.widthProperty().bind(pane.widthProperty());
		
		    //**** TO DELETE **** (border de pane)
		    pane.setBorder(new Border(new BorderStroke(
	                Color.RED,
	                //new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, stops),
	                BorderStrokeStyle.SOLID,
	                CornerRadii.EMPTY,
	                new BorderWidths(3))));
		    //*****************
		    
		    //****** Handler: *************
		    //Clicked: 
		    EventHandler<MouseEvent> clickHandler = new EventHandler<MouseEvent>(){
				@Override
				public void handle(MouseEvent event) {
					// TODO Auto-generated method stub
					System.out.println("Click MAIN \n");
					if(!checkSelectionner.isSelected())
						ajouterSommet((int)event.getX(), (int)event.getY());
					graphe.setAllSelected(false);
					checkSelectionner.setSelected(false);
				}
		    };
		    //******* Pressed: Fixer le point de debut de la selection: 
		    EventHandler<MouseEvent> pressHandler = new EventHandler<MouseEvent>(){
				@Override
				public void handle(MouseEvent event) {
					// TODO Auto-generated method stub
					System.out.println("Mouse pressed !");
					if(checkSelectionner.isSelected()){
						selectedNodes.setDebut(new Point2D(event.getX(), event.getY()));
						selectedNodes.setFin(new Point2D(event.getX(), event.getY()));
						selectedNodes.supprimerTout();
						selectedNodes.setFinished(false);
						selectedNodes.setMoved(true);
					}
					event.consume();
				}
		    };
		    //******* Dragged: faire la selection:
		    EventHandler<MouseEvent> dragEvent = new EventHandler<MouseEvent>(){
				@Override
				public void handle(MouseEvent event) {
					// TODO Auto-generated method stub
					if(checkSelectionner.isSelected()){
						selectedNodes.setMoved(true);
						selectedNodes.setFin(new Point2D(event.getX(), event.getY()));
						selectedNodes.setMoved(false);
					}
				}
		    };
		    
		    //******* Released: Terminer la selection: 
		    EventHandler<MouseEvent> releaseEvent = new EventHandler<MouseEvent>(){
				@Override
				public void handle(MouseEvent event) {
					// TODO Auto-generated method stub
					System.out.println("Mouse release");
					if(checkSelectionner.isSelected()){
						selectedNodes.setFinished(true);
						selectedNodes.setMoved(false);
					}
					event.consume();
				}	
		    };
		    //Ajouter les handlers au pane:
		    pane.addEventHandler(MouseEvent.MOUSE_CLICKED, clickHandler);
		    pane.addEventHandler(MouseEvent.MOUSE_PRESSED, pressHandler);
		    pane.addEventHandler(MouseEvent.MOUSE_DRAGGED, dragEvent);
		    pane.addEventHandler(MouseEvent.MOUSE_RELEASED, releaseEvent);
		    
		    root.setCenter(pane);			
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
//********** Methode pour ajouter la bar des menus, avec ses menus: 
	public void ajouterMenu(){
		MenuBar barMenu = new MenuBar();
		//1. Menu Fichier:
		Menu Fichier = new Menu("Fichier");
		
		MenuItem itemQuitter = new MenuItem("Quitter");
		itemQuitter.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub
				System.exit(0);
			}
		});
		//Ajouter les items au menu "Fichier"
		Fichier.getItems().add(itemQuitter);
		
		//Ajouter les menus dans la barre de menus: 
		barMenu.getMenus().add(Fichier);
		
		//Ajouter la barre de menus dans le Root:
		root.setTop(barMenu);
	}

//*********** Methode pour ajouter la barre d'outils: 
	public void ajouterBarreOutilsDessin(){
		//Créer Toolbar; 
		ToolBar toolBar = new ToolBar();
		toolBar.setOrientation(Orientation.VERTICAL);
		toolBar.setPadding(new Insets(15));
		
		//******** Titre: *****************
		Text titreToolBar = new Text("Bar d'outils:\n");
		
		//******** Color Picker ***********
		couleur = Color.BLACK;
        final ColorPicker picker = new ColorPicker(couleur);
        picker.setPrefWidth(100);
        picker.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                Color c = picker.getValue();
                if (c != null) {
                    couleur = c;
                }
            }
        });      
        //******** Bouton Supprimer ***********
        Canvas supCanvas = new Canvas(25, 20);
        GraphicsContext context = supCanvas.getGraphicsContext2D();
        context.setFill(Color.BLACK);
        context.strokeLine(5, 5, 20, 15);
        context.strokeLine(20, 5, 5, 15);
        this.boutonSupprimerSommet = new Button("Supprimer", supCanvas);
        boutonSupprimerSommet.setOnAction(e ->{
           	//Supprimer un sommet
        	ArrayList<Sommet> listSommetSelected = graphe.getSelected();
        	if(!listSommetSelected.isEmpty()){
        		//supprimer le sommet:
        		supprimerSommet(listSommetSelected);
        	}else if(!selectedNodes.getChildren().isEmpty()){
        			supprimerGroupe();
        	}else{ 
        		Arrete a = graphe.getArreteSelected();
        		if(a != null){
        			supprimerArrete(a);
        		}
        	}
        	boutonSupprimerSommet.setDisable(true);
        });

        ToggleGroup toggleGroup = new ToggleGroup();
        
        //***** Bouton select *********
        Canvas selectCanvas = new Canvas(25, 20);
        context = selectCanvas.getGraphicsContext2D();
        context.setFill(Color.BLACK);
        context.strokeRect(5, 5, 15, 10);
        checkSelectionner= new ToggleButton("Selectionner", selectCanvas);
        this.checkSelectionner.setToggleGroup(toggleGroup);
        
        Canvas rectCanvas = new Canvas(25, 20);
        context = rectCanvas.getGraphicsContext2D();
        context.setFill(Color.BLACK);
        context.fillRect(5, 5, 15, 10);
        this.boutonRect = new ToggleButton(null, rectCanvas);
        this.boutonRect.setToggleGroup(toggleGroup);
        
        Canvas cercCanvas = new Canvas(25, 20);
        context = cercCanvas.getGraphicsContext2D();
        context.setFill(Color.BLACK);
        context.fillOval(5, 5, 15, 10);
        this.boutonCercle = new ToggleButton(null, cercCanvas);
        this.boutonCercle.setToggleGroup(toggleGroup);   
        
        HBox buttonBox = new HBox(this.boutonCercle, this.boutonRect);
        buttonBox.setSpacing(10);
        
        //************************************************
		toolBar.getItems().addAll(titreToolBar, buttonBox, checkSelectionner, boutonSupprimerSommet, picker);
		root.setLeft(toolBar);
	}
	
//*********** Methode pour ajouter la barre d'outils des algorithmes:
	public void ajouterBarreOutilsAlgorithme(){
	    HBox southBar = new HBox();
	    
		ObservableList<String> options
         = FXCollections.observableArrayList(
                 "Dijkstra",
                 "Kech wahed"
         );
		ComboBox<String> choixAlgorithme = new ComboBox<String>(options);
		choixAlgorithme.setValue("Dijkstra");
	
		boutonLancer = new Button("Lancer");
		boutonLancer.setDisable(true);
		boutonLancer.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub				
				/*Ouvrire une boite de dialogue pour choisir le sommet de debut et le sommet de fin
				 	du chemin 
				 */

				boutonLancer.setDisable(true);
				openDialogConfiguration();

				dijkstra.appliquerAlgo();

				//Afficher le résultat dans le graphe:
				
				Sommet courant = dijkstra.getFin();;
			
				if(courant == null){
					//Aucun sommet n'a été séléctionné pour le déroulement de l'algorithme
					System.out.println("Aucun sommet n'a été selectionné pour le déroulement de l'algorithme");
					return;
				}
				if(!dijkstra.getEtapeParEtape()){
					dijkstra.afficherChemin();
					dijkstra.reinitialisation();
				}else{
					boutonSuivant.setDisable(false);
					afficherEtape(null, dijkstra.getEtapeCourante());
				}
				boutonLancer.setDisable(false);
			}
		});
		
		VBox boxAlgo = new VBox(choixAlgorithme, boutonLancer);
		boxAlgo.setSpacing(10);;
		boxAlgo.setPadding(new Insets(0,0,10,150));
		
		Button boutonRetour = new Button("Retour");
		boutonRetour.setDisable(true);	
		boutonSuivant = new Button("Suivant");
		boutonSuivant.setDisable(true);
		boutonRetour.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub		
				Etape avant = dijkstra.getEtapeCourante();
				Etape etape = dijkstra.getEtapePred();
				afficherEtape(avant, etape);
				if(etape.getPred() == null){
					boutonRetour.setDisable(true);
				}
				if(etape.getSuiv() != null){
					boutonSuivant.setDisable(false);
				}
			}	
			
		});

		boutonSuivant.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub
				Etape avant = dijkstra.getEtapeCourante();
				if(avant == dijkstra.listEtape.get(dijkstra.listEtape.size()-1)){
					dijkstra.afficherChemin();
					boutonSuivant.setDisable(true);
					dijkstra.reinitialisation();
					return;
				}
				Etape etape = dijkstra.getEtapeSuivante();
				afficherEtape(avant, etape);
				if(etape.getPred() != null){
					boutonRetour.setDisable(false);
				}
				if(etape.getSuiv() == null){
					boutonRetour.setDisable(true);
				}
			}
			
		});
		
		Slider sliderVitesse = new Slider(0, 100, 30);
		sliderVitesse.setBlockIncrement(5);
		sliderVitesse.setShowTickLabels(true);
		
		Label vitesseLabel = new Label("Vitesse déroulement: "+sliderVitesse.getValue());
		
		HBox boutons = new HBox(boutonRetour, boutonSuivant);
		boutons.setSpacing(10);
		
		HBox vitesseBox = new HBox(vitesseLabel, sliderVitesse);
		vitesseBox.setSpacing(10);
		
		VBox boxControle = new VBox(boutons, vitesseBox);
		boxControle.setSpacing(10);
		
		southBar.setSpacing(40);
		southBar.setPadding(new Insets(10, 0,10, 0));
		southBar.getChildren().addAll(boxAlgo, boxControle);
		root.setBottom(southBar);;
	}
	
	public void afficherEtape(Etape avant, Etape etape){
		if(avant != null){
			//Remettre AVANT comme par defaut:
			avant.getCourant().forme.setFill(avant.getCourant().getCouleur());
			avant.getModifie().forme.setFill(avant.getModifie().getCouleur());
			System.out.println("Avant.courant = "+avant.getCourant().labelNom.getText()
				+"Avant.modofie: "+avant.getModifie().labelNom.getText());
			Arrete a = graphe.getArrete(avant.getCourant(), avant.getModifie());
			a.curve.setStroke(a.getCouleur());
		}
		
		//Affichage de la nouvelle etape: *
		etape.getCourant().forme.setFill(Color.CHARTREUSE);
		etape.getModifie().forme.setFill(Color.CYAN);
		graphe.getArrete(etape.getCourant(), etape.getModifie()).curve.setStroke(Color.DARKVIOLET);
		etape.getModifie().labelPoids.setText(""+etape.getNouveauPoids());
	}
	
	/* Boite de dialogue pour configurer l'algorithme */
	//***** Methode pour lancer les proprietés d'un sommet:		
	public void openDialogConfiguration(){
		Dialog<Void> dialog = new Dialog<Void>();
		dialog.setTitle("Configuration de l'algorithme");
		dialog.setHeaderText("Choisir les points de debut/fin du chemin; puis appuyez sur Ok");
		dialog.setResizable(false);
		
		Label label1 = new Label("Sommet de debut:");
		ObservableList<String> options
	        = FXCollections.observableArrayList();
		for(Sommet s:this.graphe.listeSommets){
			options.add(s.labelNom.getText());
		}
		ComboBox<String> listSommetComboBox1 = new ComboBox<String>(options);
		listSommetComboBox1.setValue(options.get(0));
		Label label2 = new Label("Sommet de fin:");
		ComboBox<String> listSommetComboBox2 = new ComboBox<String>(options);
		listSommetComboBox2.setValue(options.get(options.size()-1));
		
		CheckBox check = new CheckBox("Executer etape par etape");
		check.selectedProperty().addListener(new ChangeListener<Boolean>(){
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				// TODO Auto-generated method stub
				dijkstra.setEtapeParEtape(newValue.booleanValue());
			}
		});
		
		GridPane grid = new GridPane();
		grid.add(label1, 1, 1);
		grid.add(listSommetComboBox1, 2, 1);
		grid.add(label2, 1, 2);
		grid.add(listSommetComboBox2, 2, 2);
		grid.add(check, 1, 3);
		
		dialog.getDialogPane().setContent(grid);
		ButtonType buttonTypeOk = new ButtonType("Valider", ButtonData.OK_DONE);
		ButtonType buttonTypeAnnuler = new ButtonType("Annuler", ButtonData.CANCEL_CLOSE);
		
		dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);
		dialog.getDialogPane().getButtonTypes().add(buttonTypeAnnuler);
		
		dialog.setResultConverter(new Callback<ButtonType, Void>() {
				@Override
			public Void call(ButtonType b) {
				// TODO Auto-generated method stub
				if(b == buttonTypeOk){
					if(listSommetComboBox1.getSelectionModel().getSelectedIndex() == listSommetComboBox2.getSelectionModel().getSelectedIndex()){
							//Sommet deb = sommet fin on ne peut pas appliquer l'algorithme
							Alert alert = new Alert(AlertType.ERROR);
					        alert.setTitle("Erreur");
					        alert.setHeaderText("Erreur selection des sommets");
					        alert.setContentText("Il faut selectionner des sommets différents");
					        alert.showAndWait();
					}
					dijkstra.setDebut(graphe.listeSommets.get(listSommetComboBox1.getSelectionModel().getSelectedIndex()));
					dijkstra.setFin(graphe.listeSommets.get(listSommetComboBox2.getSelectionModel().getSelectedIndex()));
					}
					return null;
				}
		});

		dialog.showAndWait();
			
	}
	
	
//*********** Ajouter un sommet au graphe:
	public void ajouterSommet(int x, int y){
		Sommet sommet;
		if(this.boutonCercle.isSelected()){
			sommet = new Sommet(new Point2D(x, y),
					"Sommet "+(graphe.getCpt()+1), Sommet.SHAPE_CERCLE, graphe.getCpt()+1, couleur);
			this.boutonCercle.setSelected(false);
			boutonLancer.setDisable(false);
		}else if(this.boutonRect.isSelected()){
			sommet = new Sommet(new Point2D(x, y),
					"Sommet "+(graphe.getCpt()+1), Sommet.SHAPE_RECTANGLE, graphe.getCpt()+1, couleur);
			this.boutonRect.setSelected(false);
			boutonLancer.setDisable(false);
		}else return;
		
		//Ajouter le sommet au graphe:
		graphe.ajouterSommet(sommet);
		//Ajouter le sommet au pane: 
		pane.getChildren().add(sommet);
	}
	
//*********** Méthode pour supprimer un sommet et autre pour une arrete:
	public void supprimerSommet(ArrayList<Sommet> listSommet){
		for(Sommet s : listSommet){
			supprimerSommet(s);
		}
	}
	public void supprimerSommet(Sommet s){
		//Supprimer toutes les arretes qui sont reliées à ce sommet:
		for(Arrete a : s.listeArreteVoisin){
			supprimerArrete(a);
		}
		pane.getChildren().remove(s);
    	graphe.deleteSommet(s);
	}
	public void supprimerGroupe(){
		pane.getChildren().remove(selectedNodes);
		for(Node s:selectedNodes.getChildren()){
			supprimerSommet((Sommet)s);
		}
		selectedNodes.getChildren().removeAll(selectedNodes.getChildren());
	}
	public void supprimerArrete(Arrete a){
		pane.getChildren().remove(a);
		graphe.deleteArrete(a);
	}

//*********** Méthode de selection avec la souris: 
	public void select(Point2D deb, Point2D fin){
		
	}
	public static void main(String[] args) {
		launch(args);
	}
}
