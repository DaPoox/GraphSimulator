package application;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Selection extends Group{

	private Point2D debut;
	private Point2D fin;
	private BooleanProperty finished;
	private BooleanProperty moved;
	private Rectangle rect;
	
	public Selection(){
		debut = new Point2D(0, 0);
		fin = new Point2D(0, 0);
		
		finished = new SimpleBooleanProperty(false);
		moved = new SimpleBooleanProperty(false);
		
		this.rect = new Rectangle(0, 0, 0, 0);
		Main.pane.getChildren().add(rect);
		
		this.makeDraggable(this);

		finished.addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, 
					Boolean oldValue, Boolean newValue) {
				// TODO Auto-generated method stub
				if(finished.getValue() == true){
					//Ajouter les sommets qui sont dans la selection, et mettre à jours leur couleur
					ajouterSommetGroupe();
					//Supprimer le rectangle de la selection:
					removeRectangle();
					if(getChildren().isEmpty()){
						Main.boutonSupprimerSommet.setDisable(true);
					}else Main.boutonSupprimerSommet.setDisable(false);
				}
			}
		});
		
		moved.addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, 
					Boolean oldValue, Boolean newValue) {
				// TODO Auto-generated method stub
				if(moved.getValue() == true){
					rect.setStroke(Color.GREEN);
					//Dessiner le rectangle qui représente les bordures de la selection:
					drawRect();
					//Ajouter les sommets qui sont dans la selection, et mettre à jours leur couleur
					updateColorSommet();
				}
			}			
		});		
	}
	public void removeRectangle(){
		rect.setHeight(0);
        rect.setWidth(0);
        rect.setX(0);
        rect.setY(0);
	}
	
	public void drawRect(){
		double a = Math.min(debut.getX(), fin.getX());		        
        double b = Math.min(debut.getY(), fin.getY());
        double w = Math.abs(debut.getX() - fin.getX());
        double h = Math.abs(debut.getY() - fin.getY());
            
        rect.setHeight(h);
        rect.setWidth(w);
        rect.setX(a);
        rect.setY(b);
		
		rect.setFill(Color.TRANSPARENT);
	}

	public void updateColorSommet(){
		//Parcourire la liste des sommets et vérifier chacun s'il est dans la région ou pas:
		for(Sommet s : Main.graphe.listeSommets){
			if(s.getBoundsInParent().intersects(rect.getBoundsInParent())){
				s.forme.setFill(Color.YELLOW);
			}else{
				s.forme.setFill(s.getCouleur());
			}
		}
	}
	
	public boolean verifierIntersection(Sommet s){
		if(this.rect == null) return false;
		if(s.getBoundsInParent().intersects(this.rect.getBoundsInParent())){
			return true;
		}return false;
	}
	public void supprimerTout(){
		for(Sommet s: Main.graphe.listeSommets){
			this.supprimerSommetGroupe(s);
		}
	}	
	public void supprimerSommetGroupe(Sommet s){
		this.getChildren().remove(s);
		try{
			s.forme.setFill(s.getCouleur());
			Main.pane.getChildren().add(s);
		}catch(java.lang.IllegalArgumentException e){
			//Do nothing.
		}

	}

	public void ajouterSommetGroupe(){
		for(Sommet s : Main.graphe.listeSommets){
			if(s.getBoundsInParent().intersects(rect.getBoundsInParent())){
				ajouterSommetGroupe(s);
			}
		}
	}
	public void ajouterSommetGroupe(Sommet s){
		Main.pane.getChildren().remove(this);
		this.getChildren().add(s);
		Main.pane.getChildren().add(this);
		System.out.println("size: "+Main.pane.getChildren().size());
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
	            	System.out.println("Handler in groupe !! ");
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
	                Sommet s;
	                //Mise à jours des position des sommet (layout, et non pas s.position)
	                System.out.println("Children : "+getChildren().size());
	                for(Node n: getChildren()){
	                	s = (Sommet)n;	     
	                	//Trouver la nouvelle position pour redessiner le sommet:
	                	Point2D newPosition= new Point2D(s.position.getX() +(point.getX() - t.anchorX),
	                					s.position.getY() +( point.getY() - t.anchorY));
	                	//Modifier la position de CenterX/Y pour deplacer les arretes (gràace à bind)
	                	s.updatePosition(newPosition);
	                	
	                	//Mettre à jours les position de dessin:
	                	s.setLayoutX(newPosition.getX());
	                	s.setLayoutY(newPosition.getY());
	                }
	                event.consume();
	            }
	        };
	        
	       EventHandler<MouseEvent> releaseHandler = new EventHandler<MouseEvent>(){
				@Override
				public void handle(MouseEvent event) {
					// TODO Auto-generated method stub	
					Point2D point = node.localToParent(event.getX(), event.getY());
					Sommet s;
					//Modifier la position finale du sommet (s.position), avec les coordonnée du layout + centerXY
					for(Node n: getChildren()){
	                	s = (Sommet)n;	                	
	                	s.position = new Point2D(s.position.getX() +(point.getX() - t.anchorX),
	                					s.position.getY() +( point.getY() - t.anchorY));
	                	s.updatePosition(s.position);
	                	s.setLayoutX(s.position.getX());
	                	s.setLayoutY(s.position.getY());
	                }
					event.consume();
				}	        	
	        };	        
	        this.addEventFilter(MouseEvent.MOUSE_PRESSED, pressHandler);
	        this.addEventFilter(MouseEvent.MOUSE_DRAGGED, dragHandler);
	        this.addEventFilter(MouseEvent.MOUSE_RELEASED, releaseHandler);
	    }		 
		 
	public Point2D getDebut() {
		return debut;
	}

	public void setDebut(Point2D debut) {
		this.debut = debut;
	}

	public Point2D getFin() {
		return fin;
	}

	public void setFin(Point2D fin) {
		this.fin = fin;
	}

	public boolean isMoving(){
		return this.moved.getValue();
	}
	public void setMoved(boolean d){
		this.moved.setValue(d);
	}
	
	public boolean isFinished() {
		return finished.getValue();
	}

	public void setFinished(boolean finished) {
		this.finished.setValue(finished);;
	}
}
