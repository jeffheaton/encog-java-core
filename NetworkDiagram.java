package org.encog.workbench.frames.network;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import org.encog.EncogError;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.Layer;
import org.encog.neural.networks.synapse.DirectSynapse;
import org.encog.neural.networks.synapse.OneToOneSynapse;
import org.encog.neural.networks.synapse.Synapse;
import org.encog.neural.networks.synapse.SynapseType;
import org.encog.neural.networks.synapse.WeightedSynapse;
import org.encog.neural.networks.synapse.WeightlessSynapse;
import org.encog.workbench.EncogWorkBench;
import org.encog.workbench.WorkBenchError;
import org.encog.workbench.WorkbenchFonts;
import org.encog.workbench.frames.network.NetworkTool.Type;
import org.encog.workbench.util.MouseUtil;

public class NetworkDiagram extends JPanel implements MouseListener, MouseMotionListener, ActionListener {

	enum Side
	{
		Top,
		Bottom,
		Left,
		Right
	}
	
	public static final int LAYER_WIDTH = 96;
	public static final int LAYER_HEIGHT = 64;
	public static final int SELECTION_WIDTH = 10;
	public static final int VIRTUAL_WIDTH = 2000;
	public static final int VIRTUAL_HEIGHT = 2000;
	public static final int ARROWHEAD_WIDTH = 10;
	private final NetworkFrame parent;
	private Layer selected;
	private Layer fromLayer;
	private int dragOffsetX;
	private int dragOffsetY;
	private Image offscreen;
	private Graphics offscreenGraphics;
	private List<Layer> layers = new ArrayList<Layer>();
	private List<Layer> orphanLayers = new ArrayList<Layer>();
	private JPopupMenu popupNetworkLayer;
	private JMenuItem popupNetworkLayerDelete;
	private JMenuItem popupNetworkLayerEdit;
	private JMenuItem popupEditMatrix;
	
	public NetworkDiagram(NetworkFrame parent)
	{
		this.parent = parent;
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		this.setPreferredSize(new Dimension(VIRTUAL_HEIGHT,VIRTUAL_WIDTH));
		getLayers();
		
		this.popupNetworkLayer = new JPopupMenu();
		this.popupNetworkLayerEdit = addItem(this.popupNetworkLayer,
				"Edit Layer", 'e');
		this.popupEditMatrix = addItem(this.popupNetworkLayer,
				"Edit Matrix", 'm');
		this.popupNetworkLayerDelete = addItem(this.popupNetworkLayer,
				"Delete", 'd');
		
	}
	
	private void obtainOffScreen()
	{
		if( this.offscreen==null)
		{
			this.offscreen = this.createImage(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
			this.offscreenGraphics = this.offscreen.getGraphics();
		}
	}
	
	public void paint(Graphics g)
	{
		obtainOffScreen();
		offscreenGraphics.setColor(Color.WHITE);
		offscreenGraphics.fillRect(0,0,getWidth(),getHeight());
		offscreenGraphics.setColor(Color.BLACK);
		
		BasicNetwork network = (BasicNetwork)this.parent.getEncogObject();
		for(Layer layer: this.layers)
		{
			// draw any synapse arrows
			for(Synapse synapse: layer.getNext() )
			{
				if( synapse.isSelfConnected() )
					drawSelfConnectedSynapse(offscreenGraphics,synapse);
				else
					drawSynapse(offscreenGraphics,synapse);
			}
			
			// draw the actual layer
			drawLayer(offscreenGraphics,layer);
			
			if(network.isInput(layer))
			{
				drawInput(offscreenGraphics,layer);
			}
			else if(network.isOutput(layer))
			{
				drawOutput(offscreenGraphics,layer);
			}
			
			if( this.selected==layer)
			{
				drawSelection(offscreenGraphics,layer);
			}
			if( this.fromLayer==layer)
			{
				drawFromSelection(offscreenGraphics,layer);
			}
		}
		
		g.drawImage(this.offscreen, 0,0,this);
		
	}
	
	private void drawSelfConnectedSynapse(Graphics g,
			Synapse synapse) {
		DrawArrow.drawSelfArrow(g, synapse);
		g.drawString(this.getSynapseText(synapse), 
				synapse.getToLayer().getX()+100, 
				synapse.getFromLayer().getY()-20);
		
	}
	
	private String getSynapseText(Synapse synapse)
	{
		if( synapse instanceof WeightedSynapse )
		{
			return "Weighted";
		}
		else if( synapse instanceof WeightlessSynapse )
		{
			return "Weightless";
		}
		else if( synapse instanceof OneToOneSynapse )
		{
			return "1:1";
		}
		else if( synapse instanceof DirectSynapse )
		{
			return "Direct";
		}
		else
			return "Unknown";
	}

	private void drawSynapse(Graphics g, Synapse synapse) {
		DrawArrow.drawArrow(g,synapse);
		
		String type = getSynapseText(synapse);
		
		int labelX = Math.min(synapse.getFromLayer().getX(), synapse.getToLayer().getX());
		int labelY = Math.min(synapse.getFromLayer().getY(), synapse.getToLayer().getY());
		int distX = Math.abs(synapse.getFromLayer().getX() - synapse.getToLayer().getX())/2;
		int distY = Math.abs(synapse.getFromLayer().getY() - synapse.getToLayer().getY())/2;
		g.setColor(Color.BLACK);
		g.setFont(WorkbenchFonts.getTextFont());
		g.drawString(type, labelX+distX, labelY+distY+40);
		
	}

	private NetworkTool findTool(Layer layer)
	{
		for(NetworkTool tool: this.parent.getTools())
		{
			if( tool.getClassType() == layer.getClass() )
			{
				return tool;
			}
		}
		return null;
	}
	
	private void drawSelection(Graphics g, Layer layer)
	{
		g.setColor(Color.CYAN);
		g.drawRect(layer.getX(), layer.getY(), LAYER_WIDTH, LAYER_HEIGHT);
		g.fillRect(layer.getX(), layer.getY(), SELECTION_WIDTH, SELECTION_WIDTH);
		g.fillRect(layer.getX()+LAYER_WIDTH-SELECTION_WIDTH, layer.getY(), SELECTION_WIDTH, SELECTION_WIDTH);
		g.fillRect(layer.getX()+LAYER_WIDTH-SELECTION_WIDTH, layer.getY()+LAYER_HEIGHT-SELECTION_WIDTH, SELECTION_WIDTH, SELECTION_WIDTH);
		g.fillRect(layer.getX(), layer.getY()+LAYER_HEIGHT-SELECTION_WIDTH, SELECTION_WIDTH, SELECTION_WIDTH);
	}
	
	private void drawFromSelection(Graphics g, Layer layer)
	{
		g.setColor(Color.RED);
		g.setFont(WorkbenchFonts.getTextFont());
		g.drawString("Choose a layer to build a synapse to", layer.getX(), layer.getY());
	}
	
	private void drawLayer(Graphics g, Layer layer)
	{
		NetworkTool tool = findTool(layer);
		g.setColor(Color.WHITE);
		g.fillRect(layer.getX(), layer.getY(), LAYER_WIDTH, LAYER_HEIGHT);
		g.setColor(Color.BLACK);
		tool.getIcon().paintIcon(this, g, layer.getX(), layer.getY());
		g.drawRect(layer.getX(), layer.getY(), NetworkTool.WIDTH, NetworkTool.HEIGHT);
		g.drawRect(layer.getX(), layer.getY(), LAYER_WIDTH, LAYER_HEIGHT);
		g.drawRect(layer.getX()-1, layer.getY()-1, LAYER_WIDTH, LAYER_HEIGHT);
		g.setFont(WorkbenchFonts.getTitle2Font());
		FontMetrics fm = g.getFontMetrics();
		int y = layer.getY()+fm.getHeight()+NetworkTool.HEIGHT;
		g.drawString(tool.getName() + " Layer", layer.getX()+2, y);
		y+=fm.getHeight();
		g.setFont(WorkbenchFonts.getTextFont());
		g.drawString(layer.getNeuronCount() + " Neuron" + ((layer.getNeuronCount()>1)?"s":""), layer.getX()+2, y);
		
		//g.fillRect(layer.getX(), layer.getY(), 50,50);
	}
	
	private Layer findLayer(MouseEvent e)
	{
		// was a layer something clicked
		Layer clickedLayer = null;
		for(int i=layers.size()-1;i>=0;i--)
		{
			Layer layer = layers.get(i);
			if( contains(layer,e.getX(),e.getY()))
			{
				clickedLayer = layer;
			}
		}
		return clickedLayer;
	}

	public void mouseClicked(MouseEvent e) {
		
		Layer clickedLayer = findLayer(e);
		
		if (MouseUtil.isRightClick(e)) {
			rightClick(e,clickedLayer);
		}
		else
		if (e.getClickCount() == 2) {

			doubleClick(e,clickedLayer);
		}
		
	}
	
	private void rightClick(MouseEvent e,Layer clickedLayer)
	{
		this.popupNetworkLayer.show(e.getComponent(), e.getX(), e.getY());
	}
	
	private void doubleClick(MouseEvent e,Layer clickedLayer)
	{
		
	}

	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@SuppressWarnings("unchecked")
	public void mousePressed(MouseEvent e) {
		
		Layer clickedLayer = findLayer(e);
		BasicNetwork network = (BasicNetwork)this.parent.getEncogObject();
		
		// is a synapse connection about to start or end
		if( this.parent.getNetworkToolbar().getSelected()!=null)
		{
			// is it a synapse
			if( this.parent.getNetworkToolbar().getSelected().getType()==Type.synapse)
			{
				// about to start
				if( this.fromLayer==null )
				{
					this.selected = this.fromLayer = clickedLayer;
					repaint();
					return;
				}
				else
				{
				// about to create
					createSynapse(clickedLayer);
					return;
				}
			}
		}
		
		
		// was something deselected or selected
		
		if(clickedLayer!=null)
		{
			if( selected==clickedLayer)
			{
				// deselected
				selected = null;
				repaint();
				return;
			}
			else
			{
				// selected
				selected = clickedLayer;
				dragOffsetX = e.getX()-clickedLayer.getX();
				dragOffsetY = e.getY()-clickedLayer.getY();
				repaint();
				return;
			}
		}
		
		
		// nothing was selected, is there a toolbar item that needs to be added
		if( this.parent.getNetworkToolbar().getSelected()!=null && 
				this.parent.getNetworkToolbar().getSelected().getType()==Type.synapse )
		{
			EncogWorkBench.displayError("Error", "Can't drop a synapse there, chose a 'from layer'\nthen a 'to layer'.");
		}
		else if( this.parent.getNetworkToolbar().getSelected()!=null)
		{
			try {
				Class<? extends Layer> c = this.parent.getNetworkToolbar().getSelected().getClassType();
				Layer layer = (Layer)c.newInstance();
				this.parent.getNetworkToolbar().setSelected(null);
				
				if( network.getInputLayer()==null )
					network.addLayer(layer);
				else
					this.orphanLayers.add(layer);
				
				layer.setX(e.getX());
				layer.setY(e.getY());
				this.getLayers();
				this.selected = layer;
				this.parent.getNetworkToolbar().setSelected(null);
				repaint();
			} catch (InstantiationException e1) {
				throw new WorkBenchError(e1);
			} catch (IllegalAccessException e1) {
				throw new WorkBenchError(e1);
			}
			
		}
		
		// nothing was selected, deselect if something was previously
		if( this.selected!=null )
		{
			this.selected = null;
			repaint();
		}
		
	}
	
	private void createSynapse(Layer clickedLayer)
	{
		// validate any obvious errors
		if( this.fromLayer.isConnectedTo(clickedLayer))
		{
			EncogWorkBench.displayError("Can't Create Synapse", "There is already a synapse between these two layers.\nYou must delete it first.");
			this.parent.clearSelection();
			return;
		}
		
		// try to create it
		try
		{
		NetworkTool tool = this.parent.getNetworkToolbar().getSelected();
		if( tool.getClassType() == WeightedSynapse.class )
		{
			this.fromLayer.addNext(clickedLayer,SynapseType.Weighted);
		}
		else if( tool.getClassType() == WeightlessSynapse.class )
		{
			this.fromLayer.addNext(clickedLayer,SynapseType.Weightless);
		}
		else if( tool.getClassType() == DirectSynapse.class )
		{
			this.fromLayer.addNext(clickedLayer,SynapseType.Direct);
		}
		else if( tool.getClassType() == OneToOneSynapse.class )
		{
			this.fromLayer.addNext(clickedLayer,SynapseType.OneToOne);
		}
		}
		catch(EncogError e)
		{
			EncogWorkBench.displayError("Synapse Error", e.getMessage());
		}
		
		BasicNetwork network = (BasicNetwork)this.parent.getEncogObject();
		network.getStructure().finalizeStructure();
		this.parent.clearSelection();
		repaint();
	}
	
	private boolean contains(Layer layer,int x, int y)
	{
		return( x>layer.getX() && (x<layer.getX()+LAYER_WIDTH) &&
			y>layer.getY() && (y<layer.getY()+LAYER_HEIGHT) );
	}

	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	private void getLayers()
	{
		BasicNetwork network = (BasicNetwork)this.parent.getEncogObject();
		
		network.getStructure().finalizeStructure();
		
		// first remove any orphans that may have made it into the "real" list
		for(Layer layer: network.getStructure().getLayers() )
		{
			this.orphanLayers.remove(layer);
		}
		
		// now build the layer list
		this.layers.clear();
		this.layers.addAll(network.getStructure().getLayers());
		this.layers.addAll(this.orphanLayers);
	}

	public void mouseDragged(MouseEvent e) {
		
		if(this.selected!=null)
		{
			this.selected.setX(e.getX()-dragOffsetX);
			this.selected.setY(e.getY()-dragOffsetY);
			repaint();
		}
		
	}

	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void clearSelection() {
		if(this.selected!=null)
		{
			this.fromLayer = null;
			this.selected=null;
			repaint();
		}
		
	}
	

	public void actionPerformed(final ActionEvent action) {
		
		if (action.getSource() == this.popupNetworkLayerEdit) {
			//performEditLayer();
		} else if (action.getSource() == this.popupNetworkLayerDelete) {
			performDelete();
		}  
	}
		
	public JMenuItem addItem(final JPopupMenu m, final String s,
			final int key) {

		final JMenuItem mi = new JMenuItem(s, key);
		mi.addActionListener(this);
		m.add(mi);
		return mi;
	}
	
	private void drawInput(Graphics g, Layer layer)
	{
		String str = ">>>Input>>>";
		
		g.setFont(WorkbenchFonts.getTextFont());
		FontMetrics fm = g.getFontMetrics();
		
		int height = fm.getHeight();
		int width = fm.stringWidth(str);
		int x = layer.getX();
		int y = layer.getY()-height;
		
		int center = (LAYER_WIDTH/2)-(width/2);
		g.drawString(str, x+center,y+height-3);
		g.drawRect(x, y, LAYER_WIDTH, height);
	}
	
	private void drawOutput(Graphics g, Layer layer)
	{
		String str = "<<<Output<<<";
		
		g.setFont(WorkbenchFonts.getTextFont());
		FontMetrics fm = g.getFontMetrics();
		
		int height = fm.getHeight();
		int width = fm.stringWidth(str);
		int x = layer.getX();
		int y = layer.getY();
		
		int center = (LAYER_WIDTH/2)-(width/2);
		g.drawString(str, x+center,y+height-3+LAYER_HEIGHT);
		g.drawRect(x, y+LAYER_HEIGHT, LAYER_WIDTH, height);
	}
	
	public void performDelete()
	{
		if( EncogWorkBench.askQuestion("Are you sure?", "Do you want to delete this layer?") )
		{
			BasicNetwork network = (BasicNetwork)this.parent.getEncogObject();
			network.deleteLayer(this.selected);
		}
	}
	
	
}
