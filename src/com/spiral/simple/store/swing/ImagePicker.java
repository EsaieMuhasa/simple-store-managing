/**
 * 
 */
package com.spiral.simple.store.swing;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.EmptyBorder;

import com.spiral.simple.store.tools.Config;

import jnafilechooser.api.JnaFileChooser;

/**
 * @author Esaie MUHASA
 *
 */
public class ImagePicker extends JPanel {
	private static final long serialVersionUID = -8188839974929150048L;
	private static final Dimension PREFERRED_SIZE = new Dimension(310, 300);
	private static final String [] EXT = {"png", "jpg", "jpeg"};
	
	private JLabel title = new JLabel("", JLabel.CENTER);
	
	private JSlider slider = new JSlider(JSlider.VERTICAL);
	private ImagePickerRender render = new ImagePickerRender();
	private JButton btnChoose = new JButton("Choisir", new ImageIcon(Config.getIcon("edit")));
	
	private final static JnaFileChooser FILE_CHOOSER = new JnaFileChooser();
	static {
		FILE_CHOOSER.setMultiSelectionEnabled(false);
		FILE_CHOOSER.addFilter("Image", "png", "jpg", "jpeg");
		FILE_CHOOSER.setTitle("Sélectionné une photo");
	}
	private Frame mainFrame;
	
	private String file;//pour afficher une image x dans l'image picker
	
	private final MouseAdapter mouseListener = new MouseAdapter() {
		@Override
		public void mouseClicked(MouseEvent e) {
			if(e.getClickCount() == 2) {
				btnChoose.doClick();
			}
		}
	};

	/**
	 * 
	 */
	public ImagePicker() {
		super(new BorderLayout());
		init();
	}
	
	public ImagePicker(String label) {
		this();
		title.setText(label);
	}
	
	public void setOwnerFrame (Frame frame) {
		mainFrame = frame;
	}
	
	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		btnChoose.setEnabled(enabled);
		render.setEnabled(enabled);
		
		if(render.fileName != null)
			slider.setEnabled(enabled);
		else {
			slider.setEnabled(false);
		}
	}
	
	/**
	 * Demande d'affichage d'une image
	 * @param imageFileName
	 */
	public void show (String imageFileName) {
		this.file = imageFileName;
		this.render.setFileName(this.file, slider.getValue());
	}
	
	private void init() {
		final JPanel center = new JPanel(new BorderLayout());
		final Box box = Box.createVerticalBox();
		setPreferredSize(PREFERRED_SIZE);
		setMaximumSize(PREFERRED_SIZE);
		setMinimumSize(PREFERRED_SIZE);
		setBorder(BorderFactory.createLineBorder(Color.WHITE));
		
		slider.setMaximum(200);
		slider.setMinimum(1);
		
		box.add(slider);
		box.add(Box.createVerticalStrut(20));
		
		center.add(render, BorderLayout.CENTER);
		center.setBorder(new EmptyBorder(0, 5, 0, 0));
		
		JPanel bottom = new JPanel();
		bottom.add(btnChoose);
		
		title.setForeground(Color.BLACK);
		add(title, BorderLayout.NORTH);
		add(center, BorderLayout.CENTER);
		add(bottom, BorderLayout.SOUTH);
		add(box, BorderLayout.EAST);
		
		slider.setEnabled(false);

		btnChoose.addActionListener(event -> {
			boolean result = FILE_CHOOSER.showOpenDialog(mainFrame);
			
			if(result) {
				File file = FILE_CHOOSER.getSelectedFile();
				render.setFileName(file.getAbsolutePath(), slider.getValue());
				slider.setEnabled(true);
			} else {
				render.setFileName(null, slider.getValue());
				slider.setEnabled(false);
			}
		});
		
		render.addMouseListener(mouseListener);
		slider.addChangeListener(event -> {
			if(!render.setRation(slider.getValue())) {
				slider.setEnabled(false);
				slider.setValue(render.getCurrentRation());
				slider.setEnabled(true);
			}
		});
	}
	
	/**
	 * Modification du zoom max possible
	 * @param ration
	 */
	public void setMaxZoom (int ration) {
		slider.setMaximum(ration);
		render.setRationInterval(render.minRation, ration);
	}
	
	/**
	 * Modification du zoom min possible
	 * @param ration
	 */
	public void setMinZoom (int ration) {
		slider.setMinimum(ration);
		render.setRationInterval(ration, render.maxRation);
	}
	
	/**
	 * modification du ration actuel 
	 * doit etre une valeur comptix entre zoomMin et zoomMax
	 * @param ration
	 */
	public void setRation (int ration) {
		slider.setValue(ration);
	}
	
	public int getRation () {
		return slider.getValue();
	}
	
	/**
	 * Renvoie le chemain absolut vers l'image sur le HDD
	 * @return
	 */
	public String getSelectedFileName () {
		return render.getFileName();
	}
	
	/**
	 * Renvoie le type de l'image.
	 * typiquement une petite chaine de caractere comme png, jpg ou jpeg
	 * @return
	 */
	public String getImageType () {
		if(getSelectedFileName() == null)
			return null;
		for (String e : EXT)
			if(getSelectedFileName().toLowerCase().matches(".+\\."+e))
				return e;
		
		return null;
	}
	
	/**
	 * Renvoie l'image deja redimensionner
	 * @return
	 */
	public BufferedImage getImage () {
		return render.cropImage();
	}
	
	/**
	 * est-ce que l'image afficher dans le rendue est redimensionnable??
	 * @return
	 */
	public boolean isCropableImage () {
		return render.isCropable();
	}
	
	/**
	 * 
	 * @author Esaie MUHASA
	 * Rendu de l'image choisie
	 */
	private static class ImagePickerRender extends JComponent {
		private static final long serialVersionUID = 7268721008490974405L;
		private static final BasicStroke BORDER_STROK = new BasicStroke(2f);
		private static final Color BK_COLOR = new Color(0x88000000, true);
		private static int IMAGE_RECT_CROP_WIDTH = 250;
		private static final String DEFAULT_FILE_NAME = Config.getIcon("personne");
		private static BufferedImage defaultImage;
		
		private String fileName;
		private BufferedImage image;
		
		//coordonnee de l'outil pour crop l'image
		private int xRect = 0;
		private int yRect = 0;
		
		//dimension de l'image
		private int xImg = 0;
		private int yImg = 0;
		private int wImg;//largeur de l'image (apres alcul du ration)
		private int hImg;//hauteur de l'image (apres calcul du ration)
		
		private int currentRation = 100;//la ration actuelement prise en compte
		private int minRation = 1;
		private int maxRation = 200;
		
		private final MouseAdapter listener = new MouseAdapter() {
			
			private Point start;
			
			public void mouseDragged(MouseEvent e) {
				if(!imageReady())
					return;
				
				Point mouse = e.getPoint();
				
				if(start == null) {
					start = mouse;
					return;
				}
				
				int distX = (int) (mouse.getX() - start.getX()), distY = (int) (mouse.getY() - start.getY());
				move (xImg+distX, yImg+distY);
				
				start = mouse;
			};
			
			public void mousePressed(MouseEvent e) {	
				if(!imageReady())
					return;
				
				start = e.getPoint();
				ImagePickerRender.this.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
			};
			
			public void mouseReleased(MouseEvent e) {
				if(!imageReady())
					return;
				
				ImagePickerRender.this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				start = null;
			};
			
			/**
			 * Deplacement du cadre de crop
			 * @param x
			 * @param y
			 */
			private void move (int x, int y) {
				int xP = x + wImg, yP = y + hImg;//position attendue du coin doit de l'image
				xImg = x >= xRect ? (xRect) : (xP <= (xRect + IMAGE_RECT_CROP_WIDTH)? xImg : x);
				yImg = y >= yRect ? (yRect) : (yP <= (yRect + IMAGE_RECT_CROP_WIDTH)? yImg : y) ;
				ImagePickerRender.this.repaint();
			}
			
			/**
			 * verification si l'image est deja selectionner
			 * @return
			 */
			protected boolean imageReady () {
				return fileName != null && image != null;
			}
			
		};
		
		public ImagePickerRender() {
			super();
			addMouseListener(listener);
			addMouseMotionListener(listener);
		}
		
		/**
		 * Initalisation l'intervale de varation de la ration
		 * @param min
		 * @param max
		 */
		public void setRationInterval (int min, int max) {
			minRation = min;
			maxRation = max;
		}
		
		@Override
		public void doLayout() {
			super.doLayout();
			
			initCropLook();
			
			if(defaultImage == null) {
				try {
					 defaultImage = ImageIO.read(new File(DEFAULT_FILE_NAME));
				} catch (IOException e) {
				}
			}
		}
		
		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			
			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
			
			
			if (fileName != null) {				
				g2.drawImage(image, xImg, yImg, wImg, hImg, null);
			} else {
				initCropLook();
				g2.drawImage(defaultImage, xRect, yRect, IMAGE_RECT_CROP_WIDTH, IMAGE_RECT_CROP_WIDTH, null);			
			}
			
			
			g2.setColor(BK_COLOR);
			
			Area flue = new Area(new Rectangle(getWidth(), getHeight()));
			flue.subtract(new Area(new Rectangle(xRect, yRect, IMAGE_RECT_CROP_WIDTH, IMAGE_RECT_CROP_WIDTH)));
			
			g2.fill(flue);
			
			g2.setStroke(BORDER_STROK);
			g2.drawRect(1, 1, getWidth()-2, getHeight()-2);

			g2.setColor(Color.ORANGE);
			g2.drawRect(xRect, yRect, IMAGE_RECT_CROP_WIDTH, IMAGE_RECT_CROP_WIDTH);
		}

		/**
		 * @return the fileName
		 */
		public String getFileName() {
			return fileName;
		}

		/**
		 * @param fileName the fileName to set
		 */
		public void setFileName (String fileName, int ration) {
			this.fileName = fileName;
			if(fileName != null) {
				try {
					image = ImageIO.read(new File(fileName));
					if(image.getWidth() <= IMAGE_RECT_CROP_WIDTH || image.getHeight() <= IMAGE_RECT_CROP_WIDTH) {
						xImg = xRect;
						yImg = yRect;
						setRation(100);
					} else {
						int w = (int)(image.getWidth() * (ration/100.0)),
							h = (int)(image.getHeight() * (ration/100.0));
						xImg = (w - getWidth()) / -2;
						yImg = (h - getHeight()) / -2;
						setRation(ration);
					}
				} catch (IOException e) {}
			} else {
				image = null;
			}
			repaint();
		}
		
		private void initCropLook () {
			xRect = getWidth()/2 - IMAGE_RECT_CROP_WIDTH/2;
			yRect = getHeight()/2 - IMAGE_RECT_CROP_WIDTH/2;
		}
		
		/**
		 * Pour manipuler la ration d'affichage de l'image
		 * @param ration une valeur entre 1 et 100
		 * @return {@link Boolean} true if ration succefuly applicated, otherways false
		 */
		public boolean setRation (int ration) {
			boolean accept = ration >= minRation && ration <= maxRation;
			if (accept)	{				
				BigDecimal bigW = new BigDecimal(image.getWidth() * (ration/100.0)).setScale(0, RoundingMode.HALF_UP),
						bigH = new BigDecimal(image.getHeight() * (ration/100.0)).setScale(0, RoundingMode.HALF_UP);
				
				int wPropozed = bigW.intValue(), hPropozed = bigH.intValue();
				if (wPropozed >= IMAGE_RECT_CROP_WIDTH && hPropozed >= IMAGE_RECT_CROP_WIDTH) {				
					
					int xP = wPropozed + xImg, yP = hPropozed + yImg;
					
					xImg = xP < (xRect + IMAGE_RECT_CROP_WIDTH)? xRect : xImg;
					yImg = yP < (yRect + IMAGE_RECT_CROP_WIDTH)? yRect : yImg;
					
					wImg = wPropozed;
					hImg = hPropozed;
					
					repaint();
					accept = true;
					currentRation = ration;
				} else 
					accept = false;
			}		
			
			return accept;
		}
		
		/**
		 * @return the currentRation
		 */
		public int getCurrentRation() {
			return currentRation;
		}
		
		/**
		 * y a-t-il moyen de crooper l'image??
		 * @return
		 */
		public boolean isCropable () {
			if(image != null && (image.getWidth() >= IMAGE_RECT_CROP_WIDTH && image.getHeight() >= IMAGE_RECT_CROP_WIDTH)) {
				return true;
			}
			
			return false;
		}

		/**
		 * renvoie l'image cropper
		 * @return
		 */
		public BufferedImage cropImage () {
			
			if(!isCropable())
				return image;
			
			double diffX = Math.abs(xImg - xRect),
					diffY = Math.abs(yImg - yRect);
			
			Image resize = image.getScaledInstance(wImg, hImg, Image.SCALE_DEFAULT);
			BufferedImage buffer = new BufferedImage(wImg, hImg, BufferedImage.TYPE_INT_BGR);
			buffer.createGraphics().drawImage(resize, 0, 0, null);
			
			BigDecimal bigX = new BigDecimal(diffX).setScale(0, RoundingMode.HALF_UP),
					bigY = new BigDecimal(diffY).setScale(0, RoundingMode.HALF_UP);
			
			int x = bigX.intValue(), y = bigY.intValue(), size = IMAGE_RECT_CROP_WIDTH;
			BufferedImage crop = buffer.getSubimage(x, y, size, size);
			return crop;
		}
	}

}
